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

package me.itzsomebody.radon.transformers.obfuscators.references;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;
import me.itzsomebody.radon.Main;
import me.itzsomebody.radon.asm.ClassWrapper;
import me.itzsomebody.radon.utils.ASMUtils;
import me.itzsomebody.radon.utils.StringUtils;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InvokeDynamicInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;

/**
 * Hides method invocations with invokedynamic instructions.
 *
 * @author ItzSomebody
 */
public class FastInvokedynamicTransformer extends ReferenceObfuscation {
    @Override
    public void transform() {
        MemberNames memberNames = new MemberNames();
        AtomicInteger counter = new AtomicInteger();

        Handle bootstrapHandle = new Handle(H_INVOKESTATIC, memberNames.className, memberNames.bootstrapMethodName,
                "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;", false);

        getClassWrappers().stream().filter(cw -> !excluded(cw) && !"java/lang/Enum".equals(cw.getSuperName())
                && cw.allowsIndy()).forEach(classWrapper ->
                classWrapper.getMethods().stream().filter(mw -> !excluded(mw) && hasInstructions(mw)).forEach(mw -> {
                    InsnList insns = mw.getInstructions();

                    Stream.of(insns.toArray()).forEach(insn -> {
                        if (insn instanceof MethodInsnNode) {
                            MethodInsnNode m = (MethodInsnNode) insn;

                            if (m.name.startsWith("<"))
                                return;

                            String newDesc = m.desc;
                            if (m.getOpcode() != INVOKESTATIC)
                                newDesc = newDesc.replace("(", "(Ljava/lang/Object;");

                            newDesc = ASMUtils.getGenericMethodDesc(newDesc);
                            String name = m.owner.replace('/', '.') + "\u0000\u0000" + m.name + "\u0000\u0000"
                                    + m.desc + "\u0000\u0000" + ((insn.getOpcode() == INVOKESTATIC) ? "a" : "b");

                            InvokeDynamicInsnNode indy = new InvokeDynamicInsnNode(
                                    encrypt(name, memberNames),
                                    newDesc,
                                    bootstrapHandle
                            );

                            insns.set(m, indy);

                            counter.incrementAndGet();
                        }
                    });
                }));

        ClassNode decryptor = createBootstrap(memberNames);
        getClasses().put(decryptor.name, new ClassWrapper(decryptor, false));

        Main.info("Hid API " + counter.get() + " references using fast invokedynamic");
    }

    private static String encrypt(String encrypted, MemberNames memberNames) {
        char[] encryptedChars = encrypted.toCharArray();
        char[] decryptedChars = new char[encryptedChars.length];

        for (int i = 0; i < encryptedChars.length; i++) {
            switch (i % 3) {
                case 0:
                    decryptedChars[i] = (char) (encryptedChars[i] ^ memberNames.className.replace('/', '.').hashCode());
                    break;
                case 1:
                    decryptedChars[i] = (char) (encryptedChars[i] ^ memberNames.bootstrapMethodName.hashCode());
                    break;
                default:
                    decryptedChars[i] = (char) (encryptedChars[i] ^ memberNames.decryptMethodName.hashCode());
                    break;
            }
        }

        return new String(decryptedChars);
    }

    @SuppressWarnings("Duplicates")
    private static ClassNode createBootstrap(MemberNames memberNames) {
        ClassNode cw = new ClassNode();
        MethodVisitor mv;

        cw.visit(V1_7, ACC_PUBLIC + ACC_SUPER, memberNames.className, null, "java/lang/Object", null);

        {
            mv = cw.visitMethod(ACC_PRIVATE + ACC_STATIC, memberNames.decryptMethodName, "(Ljava/lang/String;)Ljava/lang/String;", null, null);
            mv.visitCode();
            Label l0 = new Label();
            mv.visitLabel(l0);
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Thread", "currentThread", "()Ljava/lang/Thread;", false);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Thread", "getStackTrace", "()[Ljava/lang/StackTraceElement;", false);
            mv.visitVarInsn(ASTORE, 1);
            Label l1 = new Label();
            mv.visitLabel(l1);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "toCharArray", "()[C", false);
            mv.visitVarInsn(ASTORE, 2);
            Label l2 = new Label();
            mv.visitLabel(l2);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitInsn(ARRAYLENGTH);
            mv.visitIntInsn(NEWARRAY, T_CHAR);
            mv.visitVarInsn(ASTORE, 3);
            Label l3 = new Label();
            mv.visitLabel(l3);
            mv.visitInsn(ICONST_0);
            mv.visitVarInsn(ISTORE, 4);
            Label l4 = new Label();
            mv.visitLabel(l4);
            mv.visitVarInsn(ILOAD, 4);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitInsn(ARRAYLENGTH);
            Label l5 = new Label();
            mv.visitJumpInsn(IF_ICMPGE, l5);
            Label l6 = new Label();
            mv.visitLabel(l6);
            mv.visitVarInsn(ILOAD, 4);
            mv.visitInsn(ICONST_3);
            mv.visitInsn(IREM);
            Label l7 = new Label();
            Label l8 = new Label();
            Label l9 = new Label();
            Label l10 = new Label();
            mv.visitTableSwitchInsn(0, 2, l10, l7, l8, l9);
            mv.visitLabel(l7);
            mv.visitVarInsn(ALOAD, 3);
            mv.visitVarInsn(ILOAD, 4);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitVarInsn(ILOAD, 4);
            mv.visitInsn(CALOAD);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitInsn(ICONST_2);
            mv.visitInsn(AALOAD);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StackTraceElement", "getClassName", "()Ljava/lang/String;", false);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "hashCode", "()I", false);
            mv.visitInsn(IXOR);
            mv.visitInsn(I2C);
            mv.visitInsn(CASTORE);
            Label l11 = new Label();
            mv.visitLabel(l11);
            mv.visitJumpInsn(GOTO, l10);
            mv.visitLabel(l8);
            mv.visitVarInsn(ALOAD, 3);
            mv.visitVarInsn(ILOAD, 4);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitVarInsn(ILOAD, 4);
            mv.visitInsn(CALOAD);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitInsn(ICONST_2);
            mv.visitInsn(AALOAD);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StackTraceElement", "getMethodName", "()Ljava/lang/String;", false);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "hashCode", "()I", false);
            mv.visitInsn(IXOR);
            mv.visitInsn(I2C);
            mv.visitInsn(CASTORE);
            Label l12 = new Label();
            mv.visitLabel(l12);
            mv.visitJumpInsn(GOTO, l10);
            mv.visitLabel(l9);
            mv.visitVarInsn(ALOAD, 3);
            mv.visitVarInsn(ILOAD, 4);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitVarInsn(ILOAD, 4);
            mv.visitInsn(CALOAD);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitInsn(ICONST_1);
            mv.visitInsn(AALOAD);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StackTraceElement", "getMethodName", "()Ljava/lang/String;", false);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "hashCode", "()I", false);
            mv.visitInsn(IXOR);
            mv.visitInsn(I2C);
            mv.visitInsn(CASTORE);
            mv.visitLabel(l10);
            mv.visitIincInsn(4, 1);
            mv.visitJumpInsn(GOTO, l4);
            mv.visitLabel(l5);
            mv.visitTypeInsn(NEW, "java/lang/String");
            mv.visitInsn(DUP);
            mv.visitVarInsn(ALOAD, 3);
            mv.visitMethodInsn(INVOKESPECIAL, "java/lang/String", "<init>", "([C)V", false);
            mv.visitInsn(ARETURN);
            Label l13 = new Label();
            mv.visitLabel(l13);
            mv.visitMaxs(5, 5);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PRIVATE + ACC_STATIC, memberNames.getMethodHandleMethodName, "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;C)Ljava/lang/invoke/MethodHandle;", null, new String[]{"java/lang/Exception"});
            mv.visitCode();
            Label l0 = new Label();
            mv.visitLabel(l0);
            mv.visitVarInsn(ILOAD, 4);
            mv.visitIntInsn(BIPUSH, 97);
            Label l1 = new Label();
            mv.visitJumpInsn(IF_ICMPNE, l1);
            Label l2 = new Label();
            mv.visitLabel(l2);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ALOAD, 1);
            Label l3 = new Label();
            mv.visitLabel(l3);
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Class", "forName", "(Ljava/lang/String;)Ljava/lang/Class;", false);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitVarInsn(ALOAD, 3);
            mv.visitLdcInsn(Type.getType("L" + memberNames.className + ";"));
            Label l4 = new Label();
            mv.visitLabel(l4);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Class", "getClassLoader", "()Ljava/lang/ClassLoader;", false);
            Label l5 = new Label();
            mv.visitLabel(l5);
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/invoke/MethodType", "fromMethodDescriptorString", "(Ljava/lang/String;Ljava/lang/ClassLoader;)Ljava/lang/invoke/MethodType;", false);
            Label l6 = new Label();
            mv.visitLabel(l6);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/invoke/MethodHandles$Lookup", "findStatic", "(Ljava/lang/Class;Ljava/lang/String;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/MethodHandle;", false);
            mv.visitInsn(ARETURN);
            mv.visitLabel(l1);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ALOAD, 1);
            Label l7 = new Label();
            mv.visitLabel(l7);
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Class", "forName", "(Ljava/lang/String;)Ljava/lang/Class;", false);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitVarInsn(ALOAD, 3);
            mv.visitLdcInsn(Type.getType("L" + memberNames.className + ";"));
            Label l8 = new Label();
            mv.visitLabel(l8);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Class", "getClassLoader", "()Ljava/lang/ClassLoader;", false);
            Label l9 = new Label();
            mv.visitLabel(l9);
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/invoke/MethodType", "fromMethodDescriptorString", "(Ljava/lang/String;Ljava/lang/ClassLoader;)Ljava/lang/invoke/MethodType;", false);
            Label l10 = new Label();
            mv.visitLabel(l10);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/invoke/MethodHandles$Lookup", "findVirtual", "(Ljava/lang/Class;Ljava/lang/String;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/MethodHandle;", false);
            mv.visitInsn(ARETURN);
            Label l11 = new Label();
            mv.visitLabel(l11);
            mv.visitMaxs(5, 5);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC + ACC_STATIC, memberNames.bootstrapMethodName, "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;", null, null);
            mv.visitCode();
            Label l0 = new Label();
            Label l1 = new Label();
            Label l2 = new Label();
            mv.visitTryCatchBlock(l0, l1, l2, "java/lang/Exception");
            mv.visitLabel(l0);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitMethodInsn(INVOKESTATIC, memberNames.className, memberNames.decryptMethodName, "(Ljava/lang/String;)Ljava/lang/String;", false);
            mv.visitLdcInsn("\u0000\u0000");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "split", "(Ljava/lang/String;)[Ljava/lang/String;", false);
            mv.visitVarInsn(ASTORE, 3);
            Label l3 = new Label();
            mv.visitLabel(l3);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ALOAD, 3);
            mv.visitInsn(ICONST_0);
            mv.visitInsn(AALOAD);
            mv.visitVarInsn(ALOAD, 3);
            mv.visitInsn(ICONST_1);
            mv.visitInsn(AALOAD);
            mv.visitVarInsn(ALOAD, 3);
            mv.visitInsn(ICONST_2);
            mv.visitInsn(AALOAD);
            mv.visitVarInsn(ALOAD, 3);
            mv.visitInsn(ICONST_3);
            mv.visitInsn(AALOAD);
            mv.visitInsn(ICONST_0);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "charAt", "(I)C", false);
            mv.visitMethodInsn(INVOKESTATIC, memberNames.className, memberNames.getMethodHandleMethodName, "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;C)Ljava/lang/invoke/MethodHandle;", false);
            mv.visitVarInsn(ASTORE, 4);
            Label l4 = new Label();
            mv.visitLabel(l4);
            mv.visitTypeInsn(NEW, "java/lang/invoke/ConstantCallSite");
            mv.visitInsn(DUP);
            mv.visitVarInsn(ALOAD, 4);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/invoke/MethodHandle", "asType", "(Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/MethodHandle;", false);
            mv.visitMethodInsn(INVOKESPECIAL, "java/lang/invoke/ConstantCallSite", "<init>", "(Ljava/lang/invoke/MethodHandle;)V", false);
            mv.visitLabel(l1);
            mv.visitInsn(ARETURN);
            mv.visitLabel(l2);
            mv.visitVarInsn(ASTORE, 3);
            Label l5 = new Label();
            mv.visitLabel(l5);
            mv.visitVarInsn(ALOAD, 3);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Exception", "printStackTrace", "()V", false);
            Label l6 = new Label();
            mv.visitLabel(l6);
            mv.visitInsn(ACONST_NULL);
            mv.visitInsn(ARETURN);
            Label l7 = new Label();
            mv.visitLabel(l7);
            mv.visitMaxs(6, 5);
            mv.visitEnd();
        }
        cw.visitEnd();

        return cw;
    }

    private class MemberNames {
        private String className = StringUtils.randomClassName(getClasses().keySet());
        private String decryptMethodName = uniqueRandomString();
        private String getMethodHandleMethodName = uniqueRandomString();
        private String bootstrapMethodName = uniqueRandomString();
    }
}
