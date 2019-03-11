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
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

/**
 * Encrypts string literals using very basic multi-threading, a key, caller and name context.
 *
 * @author ItzSomebody
 */
public class HeavyStringEncryption extends StringEncryption {
    public HeavyStringEncryption(StringEncryptionSetup setup) {
        super(setup);
    }

    @Override
    public void transform() {
        AtomicInteger counter = new AtomicInteger();
        MemberNames memberNames = new MemberNames();

        getClassWrappers().parallelStream().filter(classWrapper ->
                !excluded(classWrapper)).forEach(classWrapper ->
                classWrapper.methods.parallelStream().filter(methodWrapper -> !excluded(methodWrapper)
                        && hasInstructions(methodWrapper.methodNode)).forEach(methodWrapper -> {
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
                                    int callerClassHC = classWrapper.classNode.name.replace("/", ".").hashCode();
                                    int callerMethodHC = methodNode.name.hashCode();
                                    int decryptorClassHC = memberNames.className.replace("/", ".").hashCode();
                                    int decryptorMethodHC = memberNames.decryptorMethodName.hashCode();
                                    methodNode.instructions.insert(insn, new MethodInsnNode(INVOKESTATIC,
                                            memberNames.className, memberNames.decryptorMethodName,
                                            "(Ljava/lang/Object;I)Ljava/lang/String;", false));
                                    methodNode.instructions.insert(insn, new InsnNode(POP));
                                    methodNode.instructions.insert(insn, new InsnNode(DUP_X1));
                                    methodNode.instructions.insertBefore(insn, BytecodeUtils.getNumberInsn(extraKey));

                                    String encryptedString = encrypt(cst, callerClassHC, callerMethodHC, decryptorClassHC,
                                            decryptorMethodHC, extraKey);
                                    BytecodeUtils.replaceInsn(methodNode.instructions, insn, getSafeStringInsnList(encryptedString));

                                    leeway -= 10;
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

    @Override
    public String getName() {
        return "Heavy string encryption";
    }

    private static String encrypt(String msg, int callerClassHC, int callerMethodHC, int decryptorClassHC,
                                  int decryptorMethodHC, int extraKey) {
        StringBuilder sb = new StringBuilder();
        char[] chars = msg.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            switch (i % 4) {
                case 0: {
                    sb.append((char) (extraKey ^ callerClassHC ^ chars[i]));
                    break;
                }
                case 1: {
                    sb.append((char) (extraKey ^ callerMethodHC ^ chars[i]));
                    break;
                }
                case 2: {
                    sb.append((char) (extraKey ^ decryptorClassHC ^ chars[i]));
                    break;
                }
                case 3: {
                    sb.append((char) (extraKey ^ decryptorMethodHC ^ chars[i]));
                    break;
                }
            }
        }

        return sb.toString();
    }

    private static ClassNode createDecryptor(MemberNames memberNames) {
        ClassNode cw = new ClassNode();
        FieldVisitor fv;
        MethodVisitor mv;

        cw.visit(V1_5, ACC_PUBLIC + ACC_SUPER, memberNames.className, null, "java/lang/Thread", null);

        {
            fv = cw.visitField(ACC_PRIVATE + ACC_STATIC + ACC_VOLATILE, memberNames.infoFieldName,
                    "[Ljava/lang/Object;", null, null);
            fv.visitEnd();
        }
        {
            fv = cw.visitField(ACC_PRIVATE + ACC_STATIC, memberNames.cacheFieldName, "Ljava/util/HashMap;",
                    null, null);
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
            mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Thread", "<init>", "()V", false);
            mv.visitInsn(RETURN);
            Label l1 = new Label();
            mv.visitLabel(l1);
            mv.visitMaxs(1, 1);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC, "run", "()V", null, null);
            mv.visitCode();
            Label l0 = new Label();
            mv.visitLabel(l0);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKESPECIAL, memberNames.className, memberNames.populateMethodName, "()V", false);
            Label l1 = new Label();
            mv.visitLabel(l1);
            mv.visitInsn(RETURN);
            Label l2 = new Label();
            mv.visitLabel(l2);
            mv.visitMaxs(1, 1);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PRIVATE, memberNames.populateMethodName, "()V", null, null);
            mv.visitCode();
            Label l0 = new Label();
            mv.visitLabel(l0);
            mv.visitInsn(ICONST_5);
            mv.visitTypeInsn(ANEWARRAY, "java/lang/Object");
            mv.visitFieldInsn(PUTSTATIC, memberNames.className, memberNames.infoFieldName, "[Ljava/lang/Object;");
            Label l1 = new Label();
            mv.visitLabel(l1);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKEVIRTUAL, memberNames.className, "getStackTrace",
                    "()[Ljava/lang/StackTraceElement;", false);
            mv.visitVarInsn(ASTORE, 1);
            Label l2 = new Label();
            mv.visitLabel(l2);
            mv.visitLdcInsn(Type.getType("L" + memberNames.className + ";"));
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Class", "getDeclaredMethods", "()[Ljava/lang/reflect/Method;",
                    false);
            mv.visitVarInsn(ASTORE, 2);
            Label l3 = new Label();
            mv.visitLabel(l3);
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Runtime", "getRuntime", "()Ljava/lang/Runtime;", false);
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
            mv.visitVarInsn(ALOAD, 2);
            mv.visitVarInsn(ILOAD, 4);
            mv.visitInsn(AALOAD);
            mv.visitVarInsn(ASTORE, 5);
            Label l8 = new Label();
            mv.visitLabel(l8);
            mv.visitVarInsn(ALOAD, 5);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/reflect/Method", "getReturnType", "()Ljava/lang/Class;",
                    false);
            mv.visitLdcInsn(Type.getType("Ljava/lang/String;"));
            Label l9 = new Label();
            mv.visitJumpInsn(IF_ACMPNE, l9);
            mv.visitVarInsn(ALOAD, 5);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/reflect/Method", "getParameterTypes", "()[Ljava/lang/Class;",
                    false);
            mv.visitInsn(ICONST_2);
            mv.visitTypeInsn(ANEWARRAY, "java/lang/Class");
            mv.visitInsn(DUP);
            mv.visitInsn(ICONST_0);
            mv.visitLdcInsn(Type.getType("Ljava/lang/Object;"));
            mv.visitInsn(AASTORE);
            mv.visitInsn(DUP);
            mv.visitInsn(ICONST_1);
            mv.visitFieldInsn(GETSTATIC, "java/lang/Integer", "TYPE", "Ljava/lang/Class;");
            mv.visitInsn(AASTORE);
            mv.visitMethodInsn(INVOKESTATIC, "java/util/Arrays", "equals", "([Ljava/lang/Object;[Ljava/lang/Object;)Z",
                    false);
            mv.visitJumpInsn(IFEQ, l9);
            Label l10 = new Label();
            mv.visitLabel(l10);
            mv.visitFieldInsn(GETSTATIC, memberNames.className, memberNames.infoFieldName, "[Ljava/lang/Object;");
            mv.visitInsn(ICONST_4);
            mv.visitVarInsn(ALOAD, 5);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/reflect/Method", "getName", "()Ljava/lang/String;", false);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "hashCode", "()I", false);
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
            mv.visitInsn(AASTORE);
            Label l11 = new Label();
            mv.visitLabel(l11);
            Label l12 = new Label();
            mv.visitJumpInsn(GOTO, l12);
            mv.visitLabel(l9);
            mv.visitIincInsn(4, 1);
            mv.visitLabel(l6);
            mv.visitVarInsn(ILOAD, 4);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitInsn(ARRAYLENGTH);
            mv.visitJumpInsn(IF_ICMPLT, l7);
            mv.visitLabel(l12);
            mv.visitVarInsn(ALOAD, 3);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Runtime", "availableProcessors", "()I", false);
            mv.visitVarInsn(ISTORE, 4);
            Label l13 = new Label();
            mv.visitLabel(l13);
            mv.visitFieldInsn(GETSTATIC, memberNames.className, memberNames.infoFieldName, "[Ljava/lang/Object;");
            mv.visitInsn(ICONST_0);
            mv.visitVarInsn(ILOAD, 4);
            mv.visitInsn(ICONST_1);
            mv.visitInsn(IADD);
            mv.visitVarInsn(ILOAD, 4);
            mv.visitInsn(IREM);
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
            mv.visitInsn(AASTORE);
            Label l14 = new Label();
            mv.visitLabel(l14);
            mv.visitFieldInsn(GETSTATIC, memberNames.className, memberNames.infoFieldName, "[Ljava/lang/Object;");
            mv.visitInsn(ICONST_1);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitFieldInsn(GETSTATIC, memberNames.className, memberNames.infoFieldName, "[Ljava/lang/Object;");
            mv.visitInsn(ICONST_0);
            mv.visitInsn(AALOAD);
            mv.visitTypeInsn(CHECKCAST, "java/lang/Integer");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Integer", "intValue", "()I", false);
            mv.visitInsn(AALOAD);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StackTraceElement", "getClassName", "()Ljava/lang/String;",
                    false);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "hashCode", "()I", false);
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
            mv.visitInsn(AASTORE);
            Label l15 = new Label();
            mv.visitLabel(l15);
            mv.visitFieldInsn(GETSTATIC, memberNames.className, memberNames.infoFieldName, "[Ljava/lang/Object;");
            mv.visitInsn(ICONST_2);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitFieldInsn(GETSTATIC, memberNames.className, memberNames.infoFieldName, "[Ljava/lang/Object;");
            mv.visitInsn(ICONST_0);
            mv.visitInsn(AALOAD);
            mv.visitTypeInsn(CHECKCAST, "java/lang/Integer");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Integer", "intValue", "()I", false);
            mv.visitInsn(AALOAD);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StackTraceElement", "getMethodName", "()Ljava/lang/String;",
                    false);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "hashCode", "()I", false);
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
            mv.visitInsn(AASTORE);
            Label l16 = new Label();
            mv.visitLabel(l16);
            mv.visitFieldInsn(GETSTATIC, memberNames.className, memberNames.infoFieldName, "[Ljava/lang/Object;");
            mv.visitInsn(ICONST_3);
            mv.visitFieldInsn(GETSTATIC, memberNames.className, memberNames.infoFieldName, "[Ljava/lang/Object;");
            mv.visitInsn(ICONST_0);
            mv.visitInsn(AALOAD);
            mv.visitTypeInsn(CHECKCAST, "java/lang/Integer");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Integer", "intValue", "()I", false);
            mv.visitInsn(ICONST_1);
            mv.visitInsn(ISHL);
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
            mv.visitInsn(AASTORE);
            Label l17 = new Label();
            mv.visitLabel(l17);
            mv.visitInsn(RETURN);
            Label l18 = new Label();
            mv.visitLabel(l18);
            mv.visitMaxs(5, 6);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PRIVATE + ACC_STATIC, memberNames.createInfoMethodName, "()V", null,
                    new String[]{"java/lang/InterruptedException"});
            mv.visitCode();
            Label l0 = new Label();
            mv.visitLabel(l0);
            mv.visitTypeInsn(NEW, memberNames.className);
            mv.visitInsn(DUP);
            mv.visitMethodInsn(INVOKESPECIAL, memberNames.className, "<init>", "()V", false);
            mv.visitVarInsn(ASTORE, 0);
            Label l1 = new Label();
            mv.visitLabel(l1);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKEVIRTUAL, memberNames.className, "start", "()V", false);
            Label l2 = new Label();
            mv.visitLabel(l2);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKEVIRTUAL, memberNames.className, "join", "()V", false);
            Label l3 = new Label();
            mv.visitLabel(l3);
            mv.visitInsn(RETURN);
            Label l4 = new Label();
            mv.visitLabel(l4);
            mv.visitMaxs(2, 1);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PRIVATE + ACC_STATIC, memberNames.setCacheMethodName,
                    "(Ljava/lang/String;Ljava/lang/String;)V", null, null);
            mv.visitCode();
            Label l0 = new Label();
            mv.visitLabel(l0);
            mv.visitFieldInsn(GETSTATIC, memberNames.className, memberNames.cacheFieldName, "Ljava/util/HashMap;");
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/util/HashMap", "put",
                    "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", false);
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
            mv = cw.visitMethod(ACC_PRIVATE + ACC_STATIC, memberNames.getCacheMethodName,
                    "(Ljava/lang/String;)Ljava/lang/String;", null, null);
            mv.visitCode();
            Label l0 = new Label();
            mv.visitLabel(l0);
            mv.visitFieldInsn(GETSTATIC, memberNames.className, memberNames.cacheFieldName, "Ljava/util/HashMap;");
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/util/HashMap", "get",
                    "(Ljava/lang/Object;)Ljava/lang/Object;", false);
            mv.visitTypeInsn(CHECKCAST, "java/lang/String");
            mv.visitInsn(ARETURN);
            Label l1 = new Label();
            mv.visitLabel(l1);
            mv.visitMaxs(2, 1);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PRIVATE + ACC_STATIC, memberNames.cacheContainsMethodName, "(Ljava/lang/String;)Z",
                    null, null);
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
            mv = cw.visitMethod(ACC_PUBLIC + ACC_STATIC, memberNames.decryptorMethodName,
                    "(Ljava/lang/Object;I)Ljava/lang/String;", null, null);
            mv.visitCode();
            Label l0 = new Label();
            Label l1 = new Label();
            Label l2 = new Label();
            mv.visitTryCatchBlock(l0, l1, l2, "java/lang/Throwable");
            Label l3 = new Label();
            Label l4 = new Label();
            Label l5 = new Label();
            mv.visitTryCatchBlock(l3, l4, l5, "java/lang/Throwable");
            Label l6 = new Label();
            Label l7 = new Label();
            mv.visitTryCatchBlock(l6, l7, l5, "java/lang/Throwable");
            mv.visitLabel(l3);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitTypeInsn(CHECKCAST, "java/lang/String");
            mv.visitVarInsn(ASTORE, 2);
            Label l8 = new Label();
            mv.visitLabel(l8);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitMethodInsn(INVOKESTATIC, memberNames.className, memberNames.cacheContainsMethodName,
                    "(Ljava/lang/String;)Z", false);
            mv.visitJumpInsn(IFEQ, l6);
            Label l9 = new Label();
            mv.visitLabel(l9);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitMethodInsn(INVOKESTATIC, memberNames.className, memberNames.getCacheMethodName,
                    "(Ljava/lang/String;)Ljava/lang/String;", false);
            mv.visitLabel(l4);
            mv.visitInsn(ARETURN);
            mv.visitLabel(l6);
            mv.visitFieldInsn(GETSTATIC, memberNames.className, memberNames.infoFieldName, "[Ljava/lang/Object;");
            Label l10 = new Label();
            mv.visitJumpInsn(IFNONNULL, l10);
            Label l11 = new Label();
            mv.visitLabel(l11);
            mv.visitMethodInsn(INVOKESTATIC, memberNames.className, memberNames.createInfoMethodName, "()V", false);
            mv.visitLabel(l10);
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Thread", "currentThread", "()Ljava/lang/Thread;", false);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Thread", "getStackTrace", "()[Ljava/lang/StackTraceElement;",
                    false);
            mv.visitVarInsn(ASTORE, 3);
            Label l12 = new Label();
            mv.visitLabel(l12);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "toCharArray", "()[C", false);
            mv.visitVarInsn(ASTORE, 4);
            Label l13 = new Label();
            mv.visitLabel(l13);
            mv.visitTypeInsn(NEW, "java/lang/StringBuilder");
            mv.visitInsn(DUP);
            mv.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "()V", false);
            mv.visitVarInsn(ASTORE, 5);
            Label l14 = new Label();
            mv.visitLabel(l14);
            mv.visitInsn(ICONST_0);
            mv.visitVarInsn(ISTORE, 6);
            mv.visitLabel(l0);
            mv.visitVarInsn(ILOAD, 6);
            mv.visitInsn(ICONST_4);
            mv.visitInsn(IREM);
            Label l15 = new Label();
            Label l16 = new Label();
            Label l17 = new Label();
            Label l18 = new Label();
            mv.visitTableSwitchInsn(0, 3, l1, l15, l16, l17, l18);
            mv.visitLabel(l15);
            mv.visitVarInsn(ALOAD, 5);
            mv.visitVarInsn(ALOAD, 4);
            mv.visitVarInsn(ILOAD, 6);
            mv.visitInsn(CALOAD);
            mv.visitVarInsn(ALOAD, 3);
            mv.visitFieldInsn(GETSTATIC, memberNames.className, memberNames.infoFieldName, "[Ljava/lang/Object;");
            mv.visitInsn(ICONST_3);
            mv.visitInsn(AALOAD);
            mv.visitTypeInsn(CHECKCAST, "java/lang/Integer");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Integer", "intValue", "()I", false);
            mv.visitIntInsn(SIPUSH, 255);
            mv.visitInsn(IAND);
            mv.visitInsn(AALOAD);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StackTraceElement", "getClassName", "()Ljava/lang/String;",
                    false);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "hashCode", "()I", false);
            mv.visitInsn(IXOR);
            mv.visitVarInsn(ILOAD, 1);
            mv.visitInsn(IXOR);
            mv.visitInsn(I2C);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(C)Ljava/lang/StringBuilder;",
                    false);
            mv.visitInsn(POP);
            Label l19 = new Label();
            mv.visitLabel(l19);
            Label l20 = new Label();
            mv.visitJumpInsn(GOTO, l20);
            mv.visitLabel(l16);
            mv.visitVarInsn(ALOAD, 5);
            mv.visitVarInsn(ALOAD, 4);
            mv.visitVarInsn(ILOAD, 6);
            mv.visitInsn(CALOAD);
            mv.visitVarInsn(ALOAD, 3);
            mv.visitFieldInsn(GETSTATIC, memberNames.className, memberNames.infoFieldName, "[Ljava/lang/Object;");
            mv.visitInsn(ICONST_3);
            mv.visitInsn(AALOAD);
            mv.visitTypeInsn(CHECKCAST, "java/lang/Integer");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Integer", "intValue", "()I", false);
            mv.visitIntInsn(SIPUSH, 255);
            mv.visitInsn(IAND);
            mv.visitInsn(AALOAD);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StackTraceElement", "getMethodName", "()Ljava/lang/String;",
                    false);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "hashCode", "()I", false);
            mv.visitInsn(IXOR);
            mv.visitVarInsn(ILOAD, 1);
            mv.visitInsn(IXOR);
            mv.visitInsn(I2C);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(C)Ljava/lang/StringBuilder;",
                    false);
            mv.visitInsn(POP);
            Label l21 = new Label();
            mv.visitLabel(l21);
            mv.visitJumpInsn(GOTO, l20);
            mv.visitLabel(l17);
            mv.visitVarInsn(ALOAD, 5);
            mv.visitVarInsn(ALOAD, 4);
            mv.visitVarInsn(ILOAD, 6);
            mv.visitInsn(CALOAD);
            mv.visitFieldInsn(GETSTATIC, memberNames.className, memberNames.infoFieldName, "[Ljava/lang/Object;");
            mv.visitInsn(ICONST_1);
            mv.visitInsn(AALOAD);
            mv.visitTypeInsn(CHECKCAST, "java/lang/Integer");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Integer", "intValue", "()I", false);
            mv.visitInsn(IXOR);
            mv.visitVarInsn(ILOAD, 1);
            mv.visitInsn(IXOR);
            mv.visitInsn(I2C);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(C)Ljava/lang/StringBuilder;",
                    false);
            mv.visitInsn(POP);
            Label l22 = new Label();
            mv.visitLabel(l22);
            mv.visitJumpInsn(GOTO, l20);
            mv.visitLabel(l18);
            mv.visitVarInsn(ALOAD, 5);
            mv.visitVarInsn(ALOAD, 4);
            mv.visitVarInsn(ILOAD, 6);
            mv.visitInsn(CALOAD);
            mv.visitFieldInsn(GETSTATIC, memberNames.className, memberNames.infoFieldName, "[Ljava/lang/Object;");
            mv.visitInsn(ICONST_4);
            mv.visitInsn(AALOAD);
            mv.visitTypeInsn(CHECKCAST, "java/lang/Integer");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Integer", "intValue", "()I", false);
            mv.visitInsn(IXOR);
            mv.visitVarInsn(ILOAD, 1);
            mv.visitInsn(IXOR);
            mv.visitInsn(I2C);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(C)Ljava/lang/StringBuilder;",
                    false);
            mv.visitInsn(POP);
            mv.visitLabel(l1);
            mv.visitJumpInsn(GOTO, l20);
            mv.visitLabel(l2);
            mv.visitVarInsn(ASTORE, 7);
            Label l23 = new Label();
            mv.visitLabel(l23);
            Label l24 = new Label();
            mv.visitJumpInsn(GOTO, l24);
            mv.visitLabel(l20);
            mv.visitIincInsn(6, 1);
            Label l25 = new Label();
            mv.visitLabel(l25);
            mv.visitJumpInsn(GOTO, l0);
            mv.visitLabel(l24);
            mv.visitVarInsn(ALOAD, 5);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false);
            mv.visitVarInsn(ASTORE, 7);
            Label l26 = new Label();
            mv.visitLabel(l26);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitVarInsn(ALOAD, 7);
            mv.visitMethodInsn(INVOKESTATIC, memberNames.className, memberNames.setCacheMethodName,
                    "(Ljava/lang/String;Ljava/lang/String;)V", false);
            Label l27 = new Label();
            mv.visitLabel(l27);
            mv.visitVarInsn(ALOAD, 7);
            mv.visitLabel(l7);
            mv.visitInsn(ARETURN);
            mv.visitLabel(l5);
            mv.visitVarInsn(ASTORE, 2);
            Label l28 = new Label();
            mv.visitLabel(l28);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Throwable", "printStackTrace", "()V", false);
            Label l29 = new Label();
            mv.visitLabel(l29);
            mv.visitInsn(ACONST_NULL);
            mv.visitInsn(ARETURN);
            Label l30 = new Label();
            mv.visitLabel(l30);
            mv.visitMaxs(5, 8);
            mv.visitEnd();
        }
        cw.visitEnd();

        return cw;
    }

    private class MemberNames {
        private String className;
        private String infoFieldName;
        private String cacheFieldName;
        private String populateMethodName;
        private String createInfoMethodName;
        private String setCacheMethodName;
        private String getCacheMethodName;
        private String cacheContainsMethodName;
        private String decryptorMethodName;

        private MemberNames() {
            this.className = StringUtils.randomClassName(getClasses().keySet());
            this.infoFieldName = randomString(4);
            this.cacheFieldName = randomString(4);
            this.populateMethodName = randomString(4);
            this.createInfoMethodName = randomString(4);
            this.setCacheMethodName = randomString(4);
            this.getCacheMethodName = randomString(4);
            this.cacheContainsMethodName = randomString(4);
            this.decryptorMethodName = randomString(4);
        }
    }
}
