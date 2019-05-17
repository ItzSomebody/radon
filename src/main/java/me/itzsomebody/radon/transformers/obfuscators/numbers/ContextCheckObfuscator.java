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

package me.itzsomebody.radon.transformers.obfuscators.numbers;

import java.util.concurrent.atomic.AtomicInteger;
import me.itzsomebody.radon.Main;
import me.itzsomebody.radon.asm.ClassWrapper;
import me.itzsomebody.radon.utils.ASMUtils;
import me.itzsomebody.radon.utils.RandomUtils;
import me.itzsomebody.radon.utils.StringUtils;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.TypeInsnNode;

/**
 * XORs number constants using stacktrace variables as keys. Be super careful with this because it WILL
 * SLOW STUFF DOWN BY A LOT.
 *
 * @author ItzSomebody
 */
public class ContextCheckObfuscator extends NumberObfuscation {
    @Override
    public void transform() {
        MemberNames memberNames = new MemberNames();
        AtomicInteger counter = new AtomicInteger();

        getClassWrappers().stream().filter(classWrapper -> !excluded(classWrapper)).forEach(classWrapper ->
                classWrapper.getMethods().stream().filter(methodWrapper -> !excluded(methodWrapper)
                        && hasInstructions(methodWrapper)).forEach(methodWrapper -> {
                    int leeway = getSizeLeeway(methodWrapper);
                    InsnList methodInstructions = methodWrapper.getInstructions();

                    for (AbstractInsnNode insn : methodInstructions.toArray()) {
                        if (leeway < 10000)
                            break;

                        if (ASMUtils.isIntInsn(insn) && master.isIntegerTamperingEnabled()) {
                            int originalNum = ASMUtils.getIntegerFromInsn(insn);
                            int encodedInt = encodeInt(originalNum, methodWrapper.getName().hashCode());

                            InsnList insnList = new InsnList();
                            insnList.add(ASMUtils.getNumberInsn(encodedInt));
                            insnList.add(new MethodInsnNode(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false));
                            insnList.add(new InsnNode(ICONST_0));
                            insnList.add(new MethodInsnNode(INVOKESTATIC, memberNames.className, memberNames.decodeConstantMethodName, "(Ljava/lang/Object;I)Ljava/lang/Object;", false));
                            insnList.add(new TypeInsnNode(CHECKCAST, "java/lang/Integer"));
                            insnList.add(new MethodInsnNode(INVOKEVIRTUAL, "java/lang/Integer", "intValue", "()I", false));

                            methodInstructions.insertBefore(insn, insnList);
                            methodInstructions.remove(insn);
                            leeway -= 20;
                            counter.incrementAndGet();
                        } else if (ASMUtils.isLongInsn(insn) && master.isLongTamperingEnabled()) {
                            long originalNum = ASMUtils.getLongFromInsn(insn);
                            long encodedLong = encodeLong(originalNum, methodWrapper.getName().hashCode());

                            InsnList insnList = new InsnList();
                            insnList.add(ASMUtils.getNumberInsn(encodedLong));
                            insnList.add(new MethodInsnNode(INVOKESTATIC, "java/lang/Long", "valueOf", "(J)Ljava/lang/Long;", false));
                            insnList.add(new InsnNode(ICONST_0));
                            insnList.add(new MethodInsnNode(INVOKESTATIC, memberNames.className, memberNames.decodeConstantMethodName, "(Ljava/lang/Object;I)Ljava/lang/Object;", false));
                            insnList.add(new TypeInsnNode(CHECKCAST, "java/lang/Long"));
                            insnList.add(new MethodInsnNode(INVOKEVIRTUAL, "java/lang/Long", "longValue", "()J", false));

                            methodInstructions.insertBefore(insn, insnList);
                            methodInstructions.remove(insn);
                            leeway -= 25;
                            counter.incrementAndGet();
                        } else if (ASMUtils.isFloatInsn(insn) && master.isFloatTamperingEnabled()) {
                            float originalNum = ASMUtils.getFloatFromInsn(insn);
                            int encodedFloat = encodeFloat(originalNum, methodWrapper.getName().hashCode());

                            InsnList insnList = new InsnList();
                            insnList.add(ASMUtils.getNumberInsn(encodedFloat));
                            insnList.add(new MethodInsnNode(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false));
                            insnList.add(ASMUtils.getNumberInsn(RandomUtils.getRandomInt()));
                            insnList.add(new MethodInsnNode(INVOKESTATIC, memberNames.className, memberNames.decodeConstantMethodName, "(Ljava/lang/Object;I)Ljava/lang/Object;", false));
                            insnList.add(new TypeInsnNode(CHECKCAST, "java/lang/Float"));
                            insnList.add(new MethodInsnNode(INVOKEVIRTUAL, "java/lang/Float", "floatValue", "()F", false));

                            methodInstructions.insertBefore(insn, insnList);
                            methodInstructions.remove(insn);

                            leeway -= 20;
                            counter.incrementAndGet();
                        } else if (ASMUtils.isDoubleInsn(insn) && master.isDoubleTamperingEnabled()) {
                            double originalNum = ASMUtils.getDoubleFromInsn(insn);
                            long encodedLong = encodeDouble(originalNum, methodWrapper.getName().hashCode());

                            InsnList insnList = new InsnList();
                            insnList.add(ASMUtils.getNumberInsn(encodedLong));
                            insnList.add(new MethodInsnNode(INVOKESTATIC, "java/lang/Long", "valueOf", "(J)Ljava/lang/Long;", false));
                            insnList.add(ASMUtils.getNumberInsn(RandomUtils.getRandomInt()));
                            insnList.add(new MethodInsnNode(INVOKESTATIC, memberNames.className, memberNames.decodeConstantMethodName, "(Ljava/lang/Object;I)Ljava/lang/Object;", false));
                            insnList.add(new TypeInsnNode(CHECKCAST, "java/lang/Double"));
                            insnList.add(new MethodInsnNode(INVOKEVIRTUAL, "java/lang/Double", "doubleValue", "()D", false));

                            methodInstructions.insertBefore(insn, insnList);
                            methodInstructions.remove(insn);
                            leeway -= 25;
                            counter.incrementAndGet();
                        }
                    }
                }));

        ClassNode decoder = createConstantDecoder(memberNames);
        getClasses().put(decoder.name, new ClassWrapper(decoder, false));

        Main.info("Enabled " + counter.get() + " number context checks");
    }

    private static int encodeInt(int n, int hashCode) {
        int xorVal = n ^ hashCode;
        int[] arr = new int[4];
        for (int i = 0; i < 4; i++) {
            arr[i] = (xorVal >>> (i * 8)) & 0xFF;
        }
        int value = 0;
        for (int i = 0; i < arr.length; i++) {
            value |= arr[i] << (i * 8);
        }
        return value;
    }

    private static int encodeFloat(float f, int hashCode) {
        return encodeInt(Float.floatToIntBits(f), hashCode);
    }

    private static long encodeLong(long n, int hashCode) {
        long xorVal = n ^ hashCode;
        long[] arr = new long[8];
        for (int i = 0; i < 8; i++) {
            arr[i] = (xorVal >>> (i * 8)) & 0xFF;
        }
        long value = 0;
        for (int i = 0; i < arr.length; i++) {
            value |= arr[i] << (i * 8);
        }

        return value;
    }

    private static long encodeDouble(double d, int hashCode) {
        return encodeLong(Double.doubleToLongBits(d), hashCode);
    }

    @SuppressWarnings("Duplicates")
    private static ClassNode createConstantDecoder(MemberNames memberNames) {
        ClassNode cw = new ClassNode();
        MethodVisitor mv;
        FieldVisitor fv;

        cw.visit(V1_5, ACC_PUBLIC + ACC_SUPER, memberNames.className, null, "java/lang/Thread", null);

        {
            fv = cw.visitField(ACC_PRIVATE + ACC_STATIC + ACC_VOLATILE, memberNames.constantFieldName, "Ljava/lang/Object;", null, null);
            fv.visitEnd();
        }
        {
            fv = cw.visitField(ACC_PRIVATE + ACC_STATIC + ACC_VOLATILE, memberNames.indicatorFieldName, "Ljava/lang/Object;", null, null);
            fv.visitEnd();
        }
        {
            fv = cw.visitField(ACC_PRIVATE + ACC_STATIC, memberNames.elementFieldName, "Ljava/lang/StackTraceElement;", null, null);
            fv.visitEnd();
        }
        {
            fv = cw.visitField(ACC_PRIVATE + ACC_FINAL + ACC_STATIC, memberNames.indexFieldName, "I", null, null);
            fv.visitEnd();
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
            mv = cw.visitMethod(ACC_PRIVATE + ACC_STATIC, memberNames.threadStarterMethodName, "()V", null, null);
            mv.visitCode();
            Label l0 = new Label();
            Label l1 = new Label();
            Label l3 = new Label();
            mv.visitLabel(l3);
            mv.visitTypeInsn(NEW, memberNames.className);
            mv.visitInsn(DUP);
            mv.visitMethodInsn(INVOKESPECIAL, memberNames.className, "<init>", "()V", false);
            mv.visitVarInsn(ASTORE, 0);
            Label l4 = new Label();
            mv.visitLabel(l4);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKEVIRTUAL, memberNames.className, "start", "()V", false);
            mv.visitLabel(l0);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKEVIRTUAL, memberNames.className, "join", "()V", false);
            mv.visitLabel(l1);
            mv.visitInsn(RETURN);
            Label l7 = new Label();
            mv.visitLabel(l7);
            mv.visitMaxs(2, 2);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC, "run", "()V", null, null);
            mv.visitCode();
            Label l0 = new Label();
            Label l1 = new Label();
            Label l2 = new Label();
            mv.visitTryCatchBlock(l0, l1, l2, "java/lang/Throwable");
            mv.visitLabel(l0);
            mv.visitFieldInsn(GETSTATIC, memberNames.className, memberNames.constantFieldName, "Ljava/lang/Object;");
            mv.visitTypeInsn(INSTANCEOF, "java/lang/Integer");
            Label l3 = new Label();
            mv.visitJumpInsn(IFEQ, l3);
            Label l4 = new Label();
            mv.visitLabel(l4);
            mv.visitFieldInsn(GETSTATIC, memberNames.className, memberNames.indicatorFieldName, "Ljava/lang/Object;");
            mv.visitTypeInsn(CHECKCAST, "java/lang/Integer");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Integer", "intValue", "()I", false);
            Label l5 = new Label();
            mv.visitJumpInsn(IFNE, l5);
            Label l6 = new Label();
            mv.visitLabel(l6);
            mv.visitMethodInsn(INVOKESTATIC, memberNames.className, memberNames.decodeWordMethodName, "()I", false);
            mv.visitFieldInsn(GETSTATIC, memberNames.className, memberNames.elementFieldName, "Ljava/lang/StackTraceElement;");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StackTraceElement", "getMethodName", "()Ljava/lang/String;", false);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "hashCode", "()I", false);
            mv.visitInsn(IXOR);
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
            mv.visitFieldInsn(PUTSTATIC, memberNames.className, memberNames.constantFieldName, "Ljava/lang/Object;");
            Label l7 = new Label();
            mv.visitJumpInsn(GOTO, l7);
            mv.visitLabel(l5);
            mv.visitMethodInsn(INVOKESTATIC, memberNames.className, memberNames.decodeWordMethodName, "()I", false);
            mv.visitFieldInsn(GETSTATIC, memberNames.className, memberNames.elementFieldName, "Ljava/lang/StackTraceElement;");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StackTraceElement", "getMethodName", "()Ljava/lang/String;", false);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "hashCode", "()I", false);
            mv.visitInsn(IXOR);
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Float", "intBitsToFloat", "(I)F", false);
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Float", "valueOf", "(F)Ljava/lang/Float;", false);
            mv.visitFieldInsn(PUTSTATIC, memberNames.className, memberNames.constantFieldName, "Ljava/lang/Object;");
            mv.visitLabel(l7);
            mv.visitTypeInsn(NEW, "java/lang/NullPointerException");
            mv.visitInsn(DUP);
            mv.visitMethodInsn(INVOKESPECIAL, "java/lang/NullPointerException", "<init>", "()V", false);
            mv.visitInsn(ATHROW);
            mv.visitLabel(l3);
            mv.visitFieldInsn(GETSTATIC, memberNames.className, memberNames.constantFieldName, "Ljava/lang/Object;");
            mv.visitTypeInsn(INSTANCEOF, "java/lang/Long");
            mv.visitJumpInsn(IFEQ, l1);
            Label l8 = new Label();
            mv.visitLabel(l8);
            mv.visitFieldInsn(GETSTATIC, memberNames.className, memberNames.indicatorFieldName, "Ljava/lang/Object;");
            mv.visitTypeInsn(CHECKCAST, "java/lang/Integer");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Integer", "intValue", "()I", false);
            Label l9 = new Label();
            mv.visitJumpInsn(IFNE, l9);
            Label l10 = new Label();
            mv.visitLabel(l10);
            mv.visitMethodInsn(INVOKESTATIC, memberNames.className, memberNames.decodeDwordMethodName, "()J", false);
            mv.visitFieldInsn(GETSTATIC, memberNames.className, memberNames.elementFieldName, "Ljava/lang/StackTraceElement;");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StackTraceElement", "getMethodName", "()Ljava/lang/String;", false);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "hashCode", "()I", false);
            mv.visitInsn(I2L);
            mv.visitInsn(LXOR);
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Long", "valueOf", "(J)Ljava/lang/Long;", false);
            mv.visitFieldInsn(PUTSTATIC, memberNames.className, memberNames.constantFieldName, "Ljava/lang/Object;");
            Label l11 = new Label();
            mv.visitJumpInsn(GOTO, l11);
            mv.visitLabel(l9);
            mv.visitMethodInsn(INVOKESTATIC, memberNames.className, memberNames.decodeDwordMethodName, "()J", false);
            mv.visitFieldInsn(GETSTATIC, memberNames.className, memberNames.elementFieldName, "Ljava/lang/StackTraceElement;");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StackTraceElement", "getMethodName", "()Ljava/lang/String;", false);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "hashCode", "()I", false);
            mv.visitInsn(I2L);
            mv.visitInsn(LXOR);
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Double", "longBitsToDouble", "(J)D", false);
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Double", "valueOf", "(D)Ljava/lang/Double;", false);
            mv.visitFieldInsn(PUTSTATIC, memberNames.className, memberNames.constantFieldName, "Ljava/lang/Object;");
            mv.visitLabel(l11);
            mv.visitTypeInsn(NEW, "java/lang/NullPointerException");
            mv.visitInsn(DUP);
            mv.visitMethodInsn(INVOKESPECIAL, "java/lang/NullPointerException", "<init>", "()V", false);
            mv.visitInsn(ATHROW);
            mv.visitLabel(l1);
            Label l12 = new Label();
            mv.visitJumpInsn(GOTO, l12);
            mv.visitLabel(l2);
            mv.visitVarInsn(ASTORE, 1);
            Label l13 = new Label();
            mv.visitLabel(l13);
            mv.visitInsn(RETURN);
            mv.visitLabel(l12);
            mv.visitInsn(RETURN);
            Label l14 = new Label();
            mv.visitLabel(l14);
            mv.visitMaxs(4, 2);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PRIVATE + ACC_STATIC, memberNames.decodeWordMethodName, "()I", null, null);
            mv.visitCode();
            Label l0 = new Label();
            mv.visitLabel(l0);
            mv.visitFieldInsn(GETSTATIC, memberNames.className, memberNames.constantFieldName, "Ljava/lang/Object;");
            mv.visitTypeInsn(CHECKCAST, "java/lang/Integer");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Integer", "intValue", "()I", false);
            mv.visitVarInsn(ISTORE, 0);
            Label l1 = new Label();
            mv.visitLabel(l1);
            mv.visitFieldInsn(GETSTATIC, memberNames.className, memberNames.indexFieldName, "I");
            mv.visitInsn(ICONST_1);
            mv.visitInsn(ISHL);
            mv.visitIntInsn(NEWARRAY, T_INT);
            mv.visitVarInsn(ASTORE, 1);
            Label l2 = new Label();
            mv.visitLabel(l2);
            mv.visitInsn(ICONST_0);
            mv.visitVarInsn(ISTORE, 2);
            Label l3 = new Label();
            mv.visitLabel(l3);
            mv.visitVarInsn(ILOAD, 2);
            mv.visitFieldInsn(GETSTATIC, memberNames.className, memberNames.indexFieldName, "I");
            mv.visitInsn(ICONST_2);
            mv.visitInsn(IMUL);
            Label l4 = new Label();
            mv.visitJumpInsn(IF_ICMPGE, l4);
            Label l5 = new Label();
            mv.visitLabel(l5);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitVarInsn(ILOAD, 2);
            mv.visitVarInsn(ILOAD, 0);
            mv.visitVarInsn(ILOAD, 2);
            mv.visitIntInsn(BIPUSH, 8);
            mv.visitInsn(IMUL);
            mv.visitInsn(ISHR);
            mv.visitIntInsn(SIPUSH, 255);
            mv.visitInsn(IAND);
            mv.visitInsn(IASTORE);
            Label l6 = new Label();
            mv.visitLabel(l6);
            mv.visitIincInsn(2, 1);
            mv.visitJumpInsn(GOTO, l3);
            mv.visitLabel(l4);
            mv.visitInsn(ICONST_0);
            mv.visitVarInsn(ISTORE, 2);
            Label l7 = new Label();
            mv.visitLabel(l7);
            mv.visitInsn(ICONST_0);
            mv.visitVarInsn(ISTORE, 3);
            Label l8 = new Label();
            mv.visitLabel(l8);
            mv.visitVarInsn(ILOAD, 3);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitInsn(ARRAYLENGTH);
            Label l9 = new Label();
            mv.visitJumpInsn(IF_ICMPGE, l9);
            Label l10 = new Label();
            mv.visitLabel(l10);
            mv.visitVarInsn(ILOAD, 2);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitVarInsn(ILOAD, 3);
            mv.visitInsn(IALOAD);
            mv.visitVarInsn(ILOAD, 3);
            mv.visitFieldInsn(GETSTATIC, memberNames.className, memberNames.indexFieldName, "I");
            mv.visitInsn(ICONST_2);
            mv.visitInsn(ISHL);
            mv.visitInsn(IMUL);
            mv.visitInsn(ISHL);
            mv.visitInsn(IOR);
            mv.visitVarInsn(ISTORE, 2);
            Label l11 = new Label();
            mv.visitLabel(l11);
            mv.visitIincInsn(3, 1);
            mv.visitJumpInsn(GOTO, l8);
            mv.visitLabel(l9);
            mv.visitVarInsn(ILOAD, 2);
            mv.visitInsn(IRETURN);
            Label l12 = new Label();
            mv.visitLabel(l12);
            mv.visitMaxs(5, 4);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PRIVATE + ACC_STATIC, memberNames.decodeDwordMethodName, "()J", null, null);
            mv.visitCode();
            Label l0 = new Label();
            mv.visitLabel(l0);
            mv.visitFieldInsn(GETSTATIC, memberNames.className, memberNames.constantFieldName, "Ljava/lang/Object;");
            mv.visitTypeInsn(CHECKCAST, "java/lang/Long");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Long", "longValue", "()J", false);
            mv.visitVarInsn(LSTORE, 0);
            Label l1 = new Label();
            mv.visitLabel(l1);
            mv.visitFieldInsn(GETSTATIC, memberNames.className, memberNames.indexFieldName, "I");
            mv.visitInsn(ICONST_2);
            mv.visitInsn(ISHL);
            mv.visitIntInsn(NEWARRAY, T_LONG);
            mv.visitVarInsn(ASTORE, 2);
            Label l2 = new Label();
            mv.visitLabel(l2);
            mv.visitInsn(ICONST_0);
            mv.visitVarInsn(ISTORE, 3);
            Label l3 = new Label();
            mv.visitLabel(l3);
            mv.visitVarInsn(ILOAD, 3);
            mv.visitFieldInsn(GETSTATIC, memberNames.className, memberNames.indexFieldName, "I");
            mv.visitInsn(ICONST_4);
            mv.visitInsn(IMUL);
            Label l4 = new Label();
            mv.visitJumpInsn(IF_ICMPGE, l4);
            Label l5 = new Label();
            mv.visitLabel(l5);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitVarInsn(ILOAD, 3);
            mv.visitVarInsn(LLOAD, 0);
            mv.visitVarInsn(ILOAD, 3);
            mv.visitIntInsn(BIPUSH, 8);
            mv.visitInsn(IMUL);
            mv.visitInsn(LSHR);
            mv.visitLdcInsn(255L);
            mv.visitInsn(LAND);
            mv.visitInsn(LASTORE);
            Label l6 = new Label();
            mv.visitLabel(l6);
            mv.visitIincInsn(3, 1);
            mv.visitJumpInsn(GOTO, l3);
            mv.visitLabel(l4);
            mv.visitInsn(LCONST_0);
            mv.visitVarInsn(LSTORE, 3);
            Label l7 = new Label();
            mv.visitLabel(l7);
            mv.visitInsn(ICONST_0);
            mv.visitVarInsn(ISTORE, 5);
            Label l8 = new Label();
            mv.visitLabel(l8);
            mv.visitVarInsn(ILOAD, 5);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitInsn(ARRAYLENGTH);
            Label l9 = new Label();
            mv.visitJumpInsn(IF_ICMPGE, l9);
            Label l10 = new Label();
            mv.visitLabel(l10);
            mv.visitVarInsn(LLOAD, 3);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitVarInsn(ILOAD, 5);
            mv.visitInsn(LALOAD);
            mv.visitVarInsn(ILOAD, 5);
            mv.visitFieldInsn(GETSTATIC, memberNames.className, memberNames.indexFieldName, "I");
            mv.visitInsn(ICONST_2);
            mv.visitInsn(ISHL);
            mv.visitInsn(IMUL);
            mv.visitInsn(LSHL);
            mv.visitInsn(LOR);
            mv.visitVarInsn(LSTORE, 3);
            Label l11 = new Label();
            mv.visitLabel(l11);
            mv.visitIincInsn(5, 1);
            mv.visitJumpInsn(GOTO, l8);
            mv.visitLabel(l9);
            mv.visitVarInsn(LLOAD, 3);
            mv.visitInsn(LRETURN);
            Label l12 = new Label();
            mv.visitLabel(l12);
            mv.visitMaxs(7, 6);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC + ACC_STATIC, memberNames.decodeConstantMethodName, "(Ljava/lang/Object;I)Ljava/lang/Object;", null, null);
            mv.visitCode();
            Label l0 = new Label();
            mv.visitLabel(l0);
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Thread", "currentThread", "()Ljava/lang/Thread;", false);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Thread", "getStackTrace", "()[Ljava/lang/StackTraceElement;", false);
            mv.visitFieldInsn(GETSTATIC, memberNames.className, memberNames.indexFieldName, "I");
            mv.visitInsn(AALOAD);
            mv.visitFieldInsn(PUTSTATIC, memberNames.className, memberNames.elementFieldName, "Ljava/lang/StackTraceElement;");
            Label l1 = new Label();
            mv.visitLabel(l1);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(PUTSTATIC, memberNames.className, memberNames.constantFieldName, "Ljava/lang/Object;");
            Label l2 = new Label();
            mv.visitLabel(l2);
            mv.visitVarInsn(ILOAD, 1);
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
            mv.visitFieldInsn(PUTSTATIC, memberNames.className, memberNames.indicatorFieldName, "Ljava/lang/Object;");
            Label l3 = new Label();
            mv.visitLabel(l3);
            mv.visitMethodInsn(INVOKESTATIC, memberNames.className, memberNames.threadStarterMethodName, "()V", false);
            Label l4 = new Label();
            mv.visitLabel(l4);
            mv.visitFieldInsn(GETSTATIC, memberNames.className, memberNames.constantFieldName, "Ljava/lang/Object;");
            mv.visitInsn(ARETURN);
            Label l5 = new Label();
            mv.visitLabel(l5);
            mv.visitMaxs(2, 2);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_STATIC, "<clinit>", "()V", null, null);
            mv.visitCode();
            Label l0 = new Label();
            mv.visitLabel(l0);
            mv.visitMethodInsn(INVOKESTATIC, "java/util/concurrent/ThreadLocalRandom", "current", "()Ljava/util/concurrent/ThreadLocalRandom;", false);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/util/concurrent/ThreadLocalRandom", "nextInt", "()I", false);
            mv.visitVarInsn(ISTORE, 0);
            Label l1 = new Label();
            mv.visitLabel(l1);
            mv.visitVarInsn(ILOAD, 0);
            mv.visitInsn(ICONST_4);
            mv.visitInsn(IUSHR);
            mv.visitVarInsn(ISTORE, 0);
            Label l2 = new Label();
            mv.visitLabel(l2);
            mv.visitVarInsn(ILOAD, 0);
            mv.visitIntInsn(SIPUSH, 255);
            mv.visitInsn(IAND);
            mv.visitVarInsn(ISTORE, 0);
            Label l3 = new Label();
            mv.visitLabel(l3);
            mv.visitVarInsn(ILOAD, 0);
            mv.visitInsn(ICONST_3);
            mv.visitInsn(IREM);
            mv.visitVarInsn(ISTORE, 0);
            Label l4 = new Label();
            mv.visitLabel(l4);
            mv.visitVarInsn(ILOAD, 0);
            mv.visitLdcInsn("000010");
            mv.visitInsn(ICONST_2);
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(Ljava/lang/String;I)Ljava/lang/Integer;", false);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Integer", "intValue", "()I", false);
            mv.visitInsn(IOR);
            mv.visitVarInsn(ISTORE, 0);
            Label l5 = new Label();
            mv.visitLabel(l5);
            mv.visitVarInsn(ILOAD, 0);
            mv.visitLdcInsn("000010");
            mv.visitInsn(ICONST_2);
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(Ljava/lang/String;I)Ljava/lang/Integer;", false);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Integer", "intValue", "()I", false);
            mv.visitInsn(IAND);
            mv.visitVarInsn(ISTORE, 0);
            Label l6 = new Label();
            mv.visitLabel(l6);
            mv.visitVarInsn(ILOAD, 0);
            mv.visitFieldInsn(PUTSTATIC, memberNames.className, memberNames.indexFieldName, "I");
            Label l7 = new Label();
            mv.visitLabel(l7);
            mv.visitInsn(RETURN);
            mv.visitMaxs(3, 1);
            mv.visitEnd();
        }
        cw.visitEnd();

        return cw;
    }

    private class MemberNames {
        private String className;
        private String constantFieldName;
        private String indicatorFieldName;
        private String elementFieldName;
        private String indexFieldName;
        private String threadStarterMethodName;
        private String decodeWordMethodName;
        private String decodeDwordMethodName;
        private String decodeConstantMethodName;

        private MemberNames() {
            this.className = StringUtils.randomClassName(getClasses().keySet());
            this.constantFieldName = uniqueRandomString();
            this.indicatorFieldName = uniqueRandomString();
            this.elementFieldName = uniqueRandomString();
            this.indexFieldName = uniqueRandomString();
            this.threadStarterMethodName = uniqueRandomString();
            this.decodeWordMethodName = uniqueRandomString();
            this.decodeDwordMethodName = uniqueRandomString();
            this.decodeConstantMethodName = uniqueRandomString();
        }
    }
}
