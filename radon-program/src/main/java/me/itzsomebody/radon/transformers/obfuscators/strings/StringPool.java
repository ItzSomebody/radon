/*
 * Copyright (C) 2018 ItzSomebody
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package me.itzsomebody.radon.transformers.obfuscators.strings;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import me.itzsomebody.radon.exclusions.ExclusionType;
import me.itzsomebody.radon.utils.BytecodeUtils;
import me.itzsomebody.radon.utils.LoggerUtils;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

/**
 * Transformer that takes all the strings in a class and pools them into a field. When the string is needed, the string
 * pool field is called with an index number.
 *
 * @author ItzSomebody
 */
public class StringPool extends StringEncryption {
    public StringPool(StringEncryptionSetup setup) {
        super(setup);
    }

    @Override
    public void transform() {
        AtomicInteger counter = new AtomicInteger();

        getClassWrappers().parallelStream().filter(classWrapper -> !excluded(classWrapper)).forEach(classWrapper -> {
            ArrayList<String> strList = new ArrayList<>();
            String methodName = randomString(4);
            String fieldName = randomString(4);

            classWrapper.methods.parallelStream().filter(methodWrapper -> !excluded(methodWrapper)
                    && hasInstructions(methodWrapper.methodNode)).forEach(methodWrapper -> {
                MethodNode methodNode = methodWrapper.methodNode;

                for (AbstractInsnNode insn : methodNode.instructions.toArray()) {
                    if (insn instanceof LdcInsnNode) {
                        Object cst = ((LdcInsnNode) insn).cst;

                        if (cst instanceof String) {
                            String str = (String) cst;

                            if (!excludedString(str)) {
                                strList.add(str);

                                int indexNumber = strList.size() - 1;

                                methodNode.instructions.insertBefore(insn, new FieldInsnNode(GETSTATIC,
                                        classWrapper.classNode.name, fieldName, "[Ljava/lang/String;"));
                                methodNode.instructions.insertBefore(insn, BytecodeUtils.getNumberInsn(indexNumber));
                                methodNode.instructions.set(insn, new InsnNode(AALOAD));
                                counter.incrementAndGet();
                            }
                        }
                    }
                }
            });

            if (strList.size() != 0) {
                classWrapper.classNode.methods.add(stringPool(classWrapper.classNode.name, methodName, fieldName,
                        strList));

                MethodNode clinit = classWrapper.classNode.methods.stream().filter(methodNode ->
                        "<clinit>".equals(methodNode.name)).findFirst().orElse(null);
                if (clinit == null) {
                    clinit = new MethodNode(ACC_PRIVATE + ACC_STATIC + ACC_SYNTHETIC, "<clinit>", "()V", null, null);
                    InsnList insns = new InsnList();
                    insns.add(new MethodInsnNode(INVOKESTATIC, classWrapper.classNode.name, methodName, "()V", false));
                    insns.add(new InsnNode(RETURN));
                    clinit.instructions = insns;
                    classWrapper.classNode.methods.add(clinit);
                } else {
                    clinit.instructions.insertBefore(clinit.instructions.getFirst(), new MethodInsnNode(INVOKESTATIC,
                            classWrapper.classNode.name, methodName, "()V", false));
                }
                FieldNode fieldNode = new FieldNode(ACC_PRIVATE + ACC_STATIC + ACC_SYNTHETIC, fieldName,
                        "[Ljava/lang/String;", null, null);
                if (classWrapper.classNode.fields == null)
                    classWrapper.classNode.fields = new ArrayList<>();
                classWrapper.classNode.fields.add(fieldNode);
            }
        });
        LoggerUtils.stdOut(String.format("Pooled %d strings.", counter.get()));
    }

    private MethodNode stringPool(String className, String methodName, String fieldName, ArrayList<String> strings) {
        MethodNode method = new MethodNode(ACC_PRIVATE + ACC_STATIC + ACC_SYNTHETIC + ACC_BRIDGE, methodName, "()V",
                null, null);

        method.visitCode();
        int numberOfStrings = strings.size();
        if (numberOfStrings <= 5) {
            method.visitInsn(numberOfStrings + 3);
        } else if (numberOfStrings <= 127) {
            method.visitIntInsn(BIPUSH, strings.size());
        } else if (numberOfStrings <= 32767) {
            method.visitIntInsn(SIPUSH, strings.size());
        } else {
            method.visitLdcInsn(strings.size());
        }

        method.visitTypeInsn(ANEWARRAY, "java/lang/String");

        for (int i = 0; i < strings.size(); i++) {
            method.visitInsn(DUP);

            if (i <= 5) {
                method.visitInsn(i + 3);
            } else if (i <= 127) {
                method.visitIntInsn(BIPUSH, i);
            } else if (i <= 32767) {
                method.visitIntInsn(SIPUSH, i);
            } else {
                method.visitLdcInsn(i);
            }

            method.visitLdcInsn(strings.get(i));
            method.visitInsn(AASTORE);
        }
        method.visitFieldInsn(PUTSTATIC, className, fieldName, "[Ljava/lang/String;");
        method.visitInsn(RETURN);
        method.visitMaxs(3, 0);
        method.visitEnd();

        return method;
    }

    @Override
    protected ExclusionType getExclusionType() {
        return ExclusionType.STRING_POOL;
    }

    @Override
    public String getName() {
        return "String pool";
    }
}
