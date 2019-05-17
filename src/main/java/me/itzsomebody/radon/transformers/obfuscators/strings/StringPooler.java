/*
 * Radon - An open-source Java obfuscator
 * Copyright (C) 2019 ItzSomebody
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
import java.util.stream.Stream;
import me.itzsomebody.radon.Main;
import me.itzsomebody.radon.utils.ASMUtils;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

public class StringPooler extends StringEncryption {
    private StringEncryption master;

    public StringPooler(StringEncryption master) {
        this.master = master;
    }

    @Override
    public void transform() {
        AtomicInteger counter = new AtomicInteger();

        getClassWrappers().stream().filter(cw -> !excluded(cw)).forEach(cw -> {
            ArrayList<String> strList = new ArrayList<>();
            String methodName = uniqueRandomString();
            String fieldName = uniqueRandomString();

            cw.getMethods().stream().filter(methodWrapper -> !excluded(methodWrapper)
                    && hasInstructions(methodWrapper.getMethodNode())).forEach(methodWrapper -> {
                InsnList insns = methodWrapper.getInstructions();

                Stream.of(insns.toArray()).filter(insn -> insn instanceof LdcInsnNode
                        && ((LdcInsnNode) insn).cst instanceof String).forEach(insn -> {
                    String str = (String) ((LdcInsnNode) insn).cst;

                    if (!master.excludedString(str)) {
                        strList.add(str);

                        int indexNumber = strList.size() - 1;

                        insns.insertBefore(insn, new FieldInsnNode(GETSTATIC, cw.getName(), fieldName, "[Ljava/lang/String;"));
                        insns.insertBefore(insn, ASMUtils.getNumberInsn(indexNumber));
                        insns.set(insn, new InsnNode(AALOAD));
                        counter.incrementAndGet();
                    }
                });
            });

            if (strList.size() != 0) {
                cw.addMethod(stringPool(cw.getName(), methodName, fieldName, strList));

                MethodNode clinit = cw.getClassNode().methods.stream().filter(methodNode ->
                        "<clinit>".equals(methodNode.name)).findFirst().orElse(null);
                if (clinit == null) {
                    clinit = new MethodNode(ACC_PRIVATE + ACC_STATIC + ACC_SYNTHETIC, "<clinit>", "()V", null, null);
                    InsnList insns = new InsnList();
                    insns.add(new MethodInsnNode(INVOKESTATIC, cw.getName(), methodName, "()V", false));
                    insns.add(new InsnNode(RETURN));
                    clinit.instructions = insns;
                    cw.getClassNode().methods.add(clinit);
                } else
                    clinit.instructions.insertBefore(clinit.instructions.getFirst(), new MethodInsnNode(INVOKESTATIC,
                            cw.getName(), methodName, "()V", false));
                FieldNode fieldNode = new FieldNode(ACC_PRIVATE + ACC_STATIC + ACC_SYNTHETIC, fieldName, "[Ljava/lang/String;", null, null);
                cw.getClassNode().fields.add(fieldNode);
            }
        });

        Main.info(String.format("Pooled %d strings.", counter.get()));
    }

    private MethodNode stringPool(String className, String methodName, String fieldName, ArrayList<String> strings) {
        MethodNode method = new MethodNode(ACC_PRIVATE + ACC_STATIC + ACC_SYNTHETIC + ACC_BRIDGE, methodName, "()V",
                null, null);

        method.visitCode();
        int numberOfStrings = strings.size();
        if (numberOfStrings <= 5)
            method.visitInsn(numberOfStrings + 3);
        else if (numberOfStrings <= 127)
            method.visitIntInsn(BIPUSH, strings.size());
        else if (numberOfStrings <= 32767)
            method.visitIntInsn(SIPUSH, strings.size());
        else
            method.visitLdcInsn(strings.size());


        method.visitTypeInsn(ANEWARRAY, "java/lang/String");

        for (int i = 0; i < strings.size(); i++) {
            method.visitInsn(DUP);

            if (i <= 5)
                method.visitInsn(i + 3);
            else if (i <= 127)
                method.visitIntInsn(BIPUSH, i);
            else if (i <= 32767)
                method.visitIntInsn(SIPUSH, i);
            else
                method.visitLdcInsn(i);

            method.visitLdcInsn(strings.get(i));
            method.visitInsn(AASTORE);
        }
        method.visitFieldInsn(PUTSTATIC, className, fieldName, "[Ljava/lang/String;");
        method.visitInsn(RETURN);
        method.visitMaxs(3, 0);
        method.visitEnd();

        return method;
    }
}
