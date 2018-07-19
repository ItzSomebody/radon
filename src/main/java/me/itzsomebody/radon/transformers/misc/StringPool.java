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

package me.itzsomebody.radon.transformers.misc;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import me.itzsomebody.radon.transformers.AbstractTransformer;
import me.itzsomebody.radon.utils.BytecodeUtils;
import me.itzsomebody.radon.utils.LoggerUtils;
import me.itzsomebody.radon.utils.StringUtils;
import org.objectweb.asm.Label;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

/**
 * Transformer that takes all the strings in a class and pools them into a
 * field. When the string is needed, the string pool field is called with
 * an index number.
 *
 * @author ItzSomebody
 */
public class StringPool extends AbstractTransformer {
    /**
     * Path to pool method.
     */
    private String randName;

    /**
     * Array of {@link String}s that will be pooled
     */
    private String[] strings;

    /**
     * Field path.
     */
    private String[] fieldName = new String[2];

    /**
     * Applies obfuscation.
     */
    public void obfuscate() {
        AtomicInteger counter = new AtomicInteger();
        long current = System.currentTimeMillis();
        this.logStrings.add(LoggerUtils.stdOut("------------------------------------------------"));
        this.logStrings.add(LoggerUtils.stdOut("Started string pool transformer."));
        this.classNodes().stream().filter(classNode -> !this.exempted(classNode.name, "StringPool")).forEach(classNode -> {
            this.randName = StringUtils.randomString(this.dictionary);
            this.fieldName[0] = classNode.name;
            this.fieldName[1] = StringUtils.randomString(this.dictionary);
            List<String> stringslist = new ArrayList<>();
            classNode.methods.stream().filter(methodNode ->
                    !this.exempted(classNode.name + '.' + methodNode.name + methodNode.name, "StringPool")
                            && hasInstructions(methodNode)).forEach(methodNode -> {
                for (AbstractInsnNode insn : methodNode.instructions.toArray()) {
                    if (insn instanceof LdcInsnNode) {
                        Object cst = ((LdcInsnNode) insn).cst;

                        if (cst instanceof String) {
                            stringslist.add((String) cst);

                            int indexNumber = stringslist.size() - 1;

                            methodNode.instructions.insertBefore(insn, new FieldInsnNode(GETSTATIC, classNode.name, this.fieldName[1], "[Ljava/lang/String;"));
                            methodNode.instructions.insertBefore(insn, BytecodeUtils.createNumberInsn(indexNumber));
                            methodNode.instructions.set(insn, new InsnNode(AALOAD));
                            counter.incrementAndGet();
                        }
                    }
                }
            });
            if (stringslist.size() != 0) {
                this.strings = new String[stringslist.size()];
                for (int i = 0; i < stringslist.size(); i++) {
                    this.strings[i] = stringslist.get(i);
                }
                classNode.methods.add(stringPool());

                MethodNode clinit = classNode.methods.stream().filter(methodNode -> methodNode.name.equals("<clinit>")).findFirst().orElse(null);
                if (clinit == null) {
                    clinit = new MethodNode(ACC_PRIVATE + ACC_STATIC + ACC_SYNTHETIC, "<clinit>", "()V", null, null);
                    InsnList insns = new InsnList();
                    insns.add(new MethodInsnNode(INVOKESTATIC, classNode.name, randName, "()V", false));
                    insns.add(new InsnNode(RETURN));
                    clinit.instructions = insns;
                    classNode.methods.add(clinit);
                } else {
                    clinit.instructions.insertBefore(clinit.instructions.getFirst(), new MethodInsnNode(INVOKESTATIC, classNode.name, randName, "()V", false));
                }
                FieldNode fieldNode = new FieldNode(ACC_PRIVATE + ACC_STATIC + ACC_SYNTHETIC, this.fieldName[1], "[Ljava/lang/String;", null, null);
                if (classNode.fields == null)
                    classNode.fields = new ArrayList<>();
                classNode.fields.add(fieldNode);
            }
        });
        this.logStrings.add(LoggerUtils.stdOut("Pooled  " + counter + " strings."));
        this.logStrings.add(LoggerUtils.stdOut("Finished. [" + tookThisLong(current) + "ms]"));
    }

    /**
     * String pool method which contains all the strings.
     *
     * @return string pool method which contains all the strings.
     */
    private MethodNode stringPool() {
        MethodNode method = new MethodNode(ACC_PRIVATE + ACC_STATIC + ACC_SYNTHETIC + ACC_BRIDGE, randName, "()V", null, null);

        method.visitCode();

        Label l0 = new Label();
        method.visitLabel(l0);
        int numberOfStrings = this.strings.length;
        if (numberOfStrings <= 5) {
            method.visitInsn(numberOfStrings + 3);
        } else if (numberOfStrings <= 127) {
            method.visitIntInsn(BIPUSH, this.strings.length);
        } else if (numberOfStrings <= 32767) {
            method.visitIntInsn(SIPUSH, this.strings.length);
        } else {
            method.visitLdcInsn(this.strings.length);
        }

        method.visitTypeInsn(ANEWARRAY, "java/lang/String");

        for (int i = 0; i < this.strings.length; i++) {
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

            method.visitLdcInsn(this.strings[i]);
            method.visitInsn(AASTORE);
        }
        method.visitFieldInsn(PUTSTATIC, this.fieldName[0], this.fieldName[1], "[Ljava/lang/String;");
        method.visitInsn(RETURN);

        method.visitMaxs(3, 0);

        method.visitEnd();

        return method;
    }
}
