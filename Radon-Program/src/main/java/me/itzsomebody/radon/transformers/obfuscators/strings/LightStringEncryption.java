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

import java.util.concurrent.atomic.AtomicInteger;
import me.itzsomebody.radon.asm.ClassWrapper;
import me.itzsomebody.radon.utils.BytecodeUtils;
import me.itzsomebody.radon.utils.LoggerUtils;
import me.itzsomebody.radon.utils.RandomUtils;
import me.itzsomebody.radon.utils.StringUtils;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

public class LightStringEncryption extends StringEncryption {
    public LightStringEncryption(StringEncryptionSetup setup) {
        super(setup);
    }

    @Override
    public void transform() {
        AtomicInteger counter = new AtomicInteger();
        MemberNames memberNames = new MemberNames();

        this.getClassWrappers().parallelStream().filter(classWrapper -> !excluded(classWrapper)).forEach(classWrapper -> classWrapper.methods.parallelStream().filter(methodWrapper -> !excluded(methodWrapper) && hasInstructions(methodWrapper.methodNode)).forEach(methodWrapper -> {
            MethodNode methodNode = methodWrapper.methodNode;
            int leeway = getSizeLeeway(methodNode);

            for (AbstractInsnNode insn : methodNode.instructions.toArray()) {
                if (leeway < 10000) {
                    break;
                }
                if (insn instanceof LdcInsnNode) {
                    LdcInsnNode ldc = (LdcInsnNode) insn;
                    if (ldc.cst instanceof String) {
                        String cst = (String) ldc.cst;

                        if (!excludedString(cst)) {
                            int extraKey = RandomUtils.getRandomInt();
                            ldc.cst = encrypt(cst, extraKey);
                            methodNode.instructions.insert(insn, new MethodInsnNode(INVOKESTATIC, memberNames.className, memberNames.decryptMethodName, "(Ljava/lang/String;I)Ljava/lang/String;", false));
                            methodNode.instructions.insert(insn, BytecodeUtils.getNumberInsn(extraKey));
                            leeway -= 7;
                            counter.incrementAndGet();
                        }
                    }
                }
            }
        }));
        // Add decrypt method
        ClassNode decryptor = createDecryptor(memberNames);
        getClasses().put(decryptor.name, new ClassWrapper(decryptor, false));

        LoggerUtils.stdOut(String.format("Encrypted %d strings.", counter.get()));
    }

    private static String encrypt(String msg, int key) {
        StringBuilder sb = new StringBuilder();
        for (char c : msg.toCharArray()) {
            sb.append((char) (c ^ key));
        }

        return sb.toString();
    }

    @Override
    public String getName() {
        return "Light string encryption";
    }

    private ClassNode createDecryptor(MemberNames memberNames) {
        ClassNode cw = new ClassNode(0);
        FieldVisitor fv;
        MethodVisitor mv;

        cw.visit(V1_5, ACC_PUBLIC + ACC_SUPER, memberNames.className, null, "java/lang/Object", null);

        {
            fv = cw.visitField(ACC_PRIVATE + ACC_STATIC, memberNames.cacheFieldName, "Ljava/util/HashMap;", "Ljava/util/HashMap<Ljava/lang/String;Ljava/lang/String;>;", null);
            fv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_STATIC, "<clinit>", "()V", null, null);
            mv.visitCode();
            Label l0 = new Label();
            mv.visitLabel(l0);
            mv.visitTypeInsn(NEW, "java/util/HashMap");
            mv.visitInsn(DUP);
            mv.visitMethodInsn(INVOKESPECIAL, "java/util/HashMap", "<init>", "()V", false);
            mv.visitFieldInsn(PUTSTATIC, memberNames.className, memberNames.cacheFieldName, "Ljava/util/HashMap;");
            mv.visitInsn(RETURN);
            mv.visitMaxs(2, 0);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
            mv.visitCode();
            Label l0 = new Label();
            mv.visitLabel(l0);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
            mv.visitInsn(RETURN);
            Label l1 = new Label();
            mv.visitLabel(l1);
            mv.visitMaxs(1, 1);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PRIVATE + ACC_STATIC, memberNames.cacheStringMethodName, "(Ljava/lang/String;Ljava/lang/String;)V", null, null);
            mv.visitCode();
            Label l0 = new Label();
            mv.visitLabel(l0);
            mv.visitFieldInsn(GETSTATIC, memberNames.className, memberNames.cacheFieldName, "Ljava/util/HashMap;");
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/util/HashMap", "put", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", false);
            mv.visitInsn(POP);
            Label l1 = new Label();
            mv.visitLabel(l1);
            mv.visitInsn(RETURN);
            Label l2 = new Label();
            mv.visitLabel(l2);
            mv.visitMaxs(3, 2);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PRIVATE + ACC_STATIC, memberNames.returnCacheMethodName, "(Ljava/lang/String;)Ljava/lang/String;", null, null);
            mv.visitCode();
            Label l0 = new Label();
            mv.visitLabel(l0);
            mv.visitFieldInsn(GETSTATIC, memberNames.className, memberNames.cacheFieldName, "Ljava/util/HashMap;");
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/util/HashMap", "get", "(Ljava/lang/Object;)Ljava/lang/Object;", false);
            mv.visitTypeInsn(CHECKCAST, "java/lang/String");
            mv.visitInsn(ARETURN);
            Label l1 = new Label();
            mv.visitLabel(l1);
            mv.visitMaxs(2, 1);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PRIVATE + ACC_STATIC, memberNames.cacheContainsMethodName, "(Ljava/lang/String;)Z", null, null);
            mv.visitCode();
            Label l0 = new Label();
            mv.visitLabel(l0);
            mv.visitFieldInsn(GETSTATIC, memberNames.className, memberNames.cacheFieldName, "Ljava/util/HashMap;");
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/util/HashMap", "containsKey", "(Ljava/lang/Object;)Z", false);
            mv.visitInsn(IRETURN);
            Label l1 = new Label();
            mv.visitLabel(l1);
            mv.visitMaxs(2, 1);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC + ACC_STATIC, memberNames.decryptMethodName, "(Ljava/lang/String;I)Ljava/lang/String;", null, null);
            mv.visitCode();
            Label l0 = new Label();
            mv.visitLabel(l0);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKESTATIC, memberNames.className, memberNames.cacheContainsMethodName, "(Ljava/lang/String;)Z", false);
            Label l1 = new Label();
            mv.visitJumpInsn(IFEQ, l1);
            Label l2 = new Label();
            mv.visitLabel(l2);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKESTATIC, memberNames.className, memberNames.returnCacheMethodName, "(Ljava/lang/String;)Ljava/lang/String;", false);
            mv.visitInsn(ARETURN);
            mv.visitLabel(l1);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "toCharArray", "()[C", false);
            mv.visitVarInsn(ASTORE, 2);
            Label l3 = new Label();
            mv.visitLabel(l3);
            mv.visitTypeInsn(NEW, "java/lang/StringBuilder");
            mv.visitInsn(DUP);
            mv.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "()V", false);
            mv.visitVarInsn(ASTORE, 3);
            Label l4 = new Label();
            mv.visitLabel(l4);
            mv.visitInsn(ICONST_0);
            mv.visitVarInsn(ISTORE, 4);
            Label l5 = new Label();
            mv.visitLabel(l5);
            Label l6 = new Label();
            mv.visitJumpInsn(GOTO, l6);
            Label l7 = new Label();
            mv.visitLabel(l7);
            mv.visitVarInsn(ALOAD, 3);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitVarInsn(ILOAD, 4);
            mv.visitInsn(CALOAD);
            mv.visitVarInsn(ILOAD, 1);
            mv.visitInsn(IXOR);
            mv.visitInsn(I2C);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(C)Ljava/lang/StringBuilder;", false);
            mv.visitInsn(POP);
            Label l8 = new Label();
            mv.visitLabel(l8);
            mv.visitIincInsn(4, 1);
            mv.visitLabel(l6);
            mv.visitVarInsn(ILOAD, 4);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitInsn(ARRAYLENGTH);
            mv.visitJumpInsn(IF_ICMPLT, l7);
            Label l9 = new Label();
            mv.visitLabel(l9);
            mv.visitVarInsn(ALOAD, 3);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false);
            mv.visitVarInsn(ASTORE, 4);
            Label l10 = new Label();
            mv.visitLabel(l10);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ALOAD, 4);
            mv.visitMethodInsn(INVOKESTATIC, memberNames.className, memberNames.cacheStringMethodName, "(Ljava/lang/String;Ljava/lang/String;)V", false);
            Label l11 = new Label();
            mv.visitLabel(l11);
            mv.visitVarInsn(ALOAD, 3);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false);
            mv.visitInsn(ARETURN);
            Label l12 = new Label();
            mv.visitLabel(l12);
            mv.visitMaxs(3, 5);
            mv.visitEnd();
        }
        cw.visitEnd();

        return cw;
    }

    private class MemberNames {
        private String className;
        private String cacheFieldName;
        private String cacheStringMethodName;
        private String returnCacheMethodName;
        private String cacheContainsMethodName;
        private String decryptMethodName;

        private MemberNames() {
            className = StringUtils.randomClassName(getClasses().keySet());
            cacheFieldName = StringUtils.randomSpacesString(RandomUtils.getRandomInt(10));
            cacheStringMethodName = StringUtils.randomSpacesString(RandomUtils.getRandomInt(10));
            returnCacheMethodName = StringUtils.randomSpacesString(RandomUtils.getRandomInt(10));
            cacheContainsMethodName = StringUtils.randomSpacesString(RandomUtils.getRandomInt(10));
            decryptMethodName = StringUtils.randomSpacesString(RandomUtils.getRandomInt(10));
        }
    }
}
