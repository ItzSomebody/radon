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

package me.itzsomebody.radon.methods;

import me.itzsomebody.radon.transformers.stringencryption.LightStringEncryption;
import me.itzsomebody.radon.transformers.stringencryption.NormalStringEncryption;
import me.itzsomebody.radon.transformers.stringencryption.SuperLightStringEncryption;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.MethodNode;

/**
 * Class containing {@link MethodNode}s needed to decrypt a {@link String} for the
 * appropriate StringEncryption transformer.
 *
 * @author ItzSomebody
 * @author ASMifier by OW2
 */
public class StringEncryption implements Opcodes {
    /**
     * Returns a {@link MethodNode} that returns a {@link String} needed to
     * decrypt strings encrypted by {@link SuperLightStringEncryption}.
     *
     * @param decryptionMethodName used to determine the name of the
     *                             generated {@link MethodNode}.
     * @return a {@link MethodNode} that returns a {@link String} needed to
     * decrypt strings encrypted by {@link SuperLightStringEncryption}.
     */
    public static MethodNode superLightMethod(String decryptionMethodName) {
        MethodNode mv = new MethodNode(ACC_PUBLIC + ACC_STATIC
                + ACC_SYNTHETIC + ACC_BRIDGE, decryptionMethodName,
                "(Ljava/lang/String;I)Ljava/lang/String;", null,
                null);
        mv.visitCode();
        Label l0 = new Label();
        mv.visitLabel(l0);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "toCharArray",
                "()[C", false);
        mv.visitVarInsn(ASTORE, 2);
        Label l1 = new Label();
        mv.visitLabel(l1);
        mv.visitVarInsn(ALOAD, 2);
        mv.visitInsn(ARRAYLENGTH);
        mv.visitIntInsn(NEWARRAY, T_CHAR);
        mv.visitVarInsn(ASTORE, 3);
        Label l2 = new Label();
        mv.visitLabel(l2);
        mv.visitInsn(ICONST_0);
        mv.visitVarInsn(ISTORE, 4);
        Label l3 = new Label();
        mv.visitLabel(l3);
        Label l4 = new Label();
        mv.visitJumpInsn(GOTO, l4);
        Label l5 = new Label();
        mv.visitLabel(l5);
        mv.visitFrame(Opcodes.F_APPEND, 3, new Object[]{"[C", "[C",
                Opcodes.INTEGER}, 0, null);
        mv.visitVarInsn(ALOAD, 3);
        mv.visitVarInsn(ILOAD, 4);
        mv.visitVarInsn(ALOAD, 2);
        mv.visitVarInsn(ILOAD, 4);
        mv.visitInsn(CALOAD);
        mv.visitVarInsn(ILOAD, 1);
        mv.visitInsn(IXOR);
        mv.visitInsn(I2C);
        mv.visitInsn(CASTORE);
        Label l6 = new Label();
        mv.visitLabel(l6);
        mv.visitIincInsn(4, 1);
        mv.visitLabel(l4);
        mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
        mv.visitVarInsn(ILOAD, 4);
        mv.visitVarInsn(ALOAD, 3);
        mv.visitInsn(ARRAYLENGTH);
        mv.visitJumpInsn(IF_ICMPLT, l5);
        Label l7 = new Label();
        mv.visitLabel(l7);
        mv.visitTypeInsn(NEW, "java/lang/String");
        mv.visitInsn(DUP);
        mv.visitVarInsn(ALOAD, 3);
        mv.visitMethodInsn(INVOKESPECIAL, "java/lang/String", "<init>",
                "([C)V", false);
        mv.visitInsn(ARETURN);
        Label l8 = new Label();
        mv.visitLabel(l8);
        mv.visitMaxs(4, 5);
        mv.visitEnd();

        return mv;
    }

    /**
     * Returns a {@link MethodNode} that returns a {@link String} needed to
     * decrypt strings encrypted by {@link LightStringEncryption}.
     *
     * @param decryptMethodName used to determine the name of the generated
     *                          {@link MethodNode}.
     * @return a {@link MethodNode} that returns a {@link String} needed to
     * decrypt strings encrypted by {@link LightStringEncryption}.
     */
    public static MethodNode lightMethod(String decryptMethodName) {
        MethodNode mv = new MethodNode(ACC_PUBLIC + ACC_STATIC +
                ACC_SYNTHETIC + ACC_BRIDGE, decryptMethodName,
                "(Ljava/lang/Object;I)Ljava/lang/String;", null, null);
        mv.visitCode();
        Label l0 = new Label();
        mv.visitLabel(l0);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitTypeInsn(CHECKCAST, "java/lang/String");
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "length", "()I",
                false);
        mv.visitIntInsn(NEWARRAY, T_CHAR);
        mv.visitVarInsn(ASTORE, 2);
        Label l1 = new Label();
        mv.visitLabel(l1);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitTypeInsn(CHECKCAST, "java/lang/String");
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "toCharArray",
                "()[C", false);
        mv.visitVarInsn(ASTORE, 3);
        Label l2 = new Label();
        mv.visitLabel(l2);
        mv.visitTypeInsn(NEW, "java/lang/Throwable");
        mv.visitInsn(DUP);
        mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Throwable", "<init>",
                "()V", false);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Throwable",
                "getStackTrace", "()[Ljava/lang/StackTraceElement;", false);
        mv.visitVarInsn(ASTORE, 4);
        Label l3 = new Label();
        mv.visitLabel(l3);
        mv.visitInsn(ICONST_0);
        mv.visitVarInsn(ISTORE, 5);
        Label l4 = new Label();
        mv.visitLabel(l4);
        Label l5 = new Label();
        mv.visitJumpInsn(GOTO, l5);
        Label l6 = new Label();
        mv.visitLabel(l6);
        mv.visitFrame(Opcodes.F_FULL, 6, new Object[]{"java/lang/Object",
                Opcodes.INTEGER, "[C", "[C", "[Ljava/lang/StackTraceElement;",
                Opcodes.INTEGER}, 0, new Object[]{});
        mv.visitVarInsn(ALOAD, 4);
        mv.visitInsn(ICONST_0);
        mv.visitInsn(AALOAD);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StackTraceElement",
                "getClassName", "()Ljava/lang/String;", false);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "hashCode",
                "()I", false);
        mv.visitInsn(I2C);
        mv.visitVarInsn(ISTORE, 6);
        Label l7 = new Label();
        mv.visitLabel(l7);
        mv.visitVarInsn(ALOAD, 4);
        mv.visitInsn(ICONST_0);
        mv.visitInsn(AALOAD);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StackTraceElement",
                "getMethodName", "()Ljava/lang/String;", false);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "hashCode",
                "()I", false);
        mv.visitInsn(I2C);
        mv.visitVarInsn(ISTORE, 7);
        Label l8 = new Label();
        mv.visitLabel(l8);
        mv.visitVarInsn(ALOAD, 2);
        mv.visitVarInsn(ILOAD, 5);
        mv.visitVarInsn(ALOAD, 3);
        mv.visitVarInsn(ILOAD, 5);
        mv.visitInsn(CALOAD);
        mv.visitVarInsn(ILOAD, 6);
        mv.visitInsn(IXOR);
        mv.visitVarInsn(ILOAD, 7);
        mv.visitInsn(IXOR);
        mv.visitVarInsn(ILOAD, 1);
        mv.visitInsn(IXOR);
        mv.visitInsn(I2C);
        mv.visitInsn(CASTORE);
        Label l9 = new Label();
        mv.visitLabel(l9);
        mv.visitIincInsn(5, 1);
        mv.visitLabel(l5);
        mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
        mv.visitVarInsn(ILOAD, 5);
        mv.visitVarInsn(ALOAD, 2);
        mv.visitInsn(ARRAYLENGTH);
        mv.visitJumpInsn(IF_ICMPLT, l6);
        Label l10 = new Label();
        mv.visitLabel(l10);
        mv.visitTypeInsn(NEW, "java/lang/String");
        mv.visitInsn(DUP);
        mv.visitVarInsn(ALOAD, 2);
        mv.visitMethodInsn(INVOKESPECIAL, "java/lang/String", "<init>",
                "([C)V", false);
        mv.visitInsn(ARETURN);
        Label l11 = new Label();
        mv.visitLabel(l11);
        mv.visitMaxs(4, 8);
        mv.visitEnd();

        return mv;
    }

    /**
     * Returns a {@link MethodNode} that returns a {@link String} needed to
     * decrypt strings encrypted by {@link NormalStringEncryption}.
     *
     * @param decryptionMethodName used to determine the name of the
     *                             generated {@link MethodNode}.
     * @return a {@link MethodNode} that returns a {@link String} needed to
     * decrypt strings encrypted by {@link NormalStringEncryption}.
     */
    public static MethodNode normalMethod(String decryptionMethodName) {
        MethodNode mv = new MethodNode(ACC_PUBLIC + ACC_STATIC +
                ACC_BRIDGE + ACC_SYNTHETIC, decryptionMethodName,
                "(Ljava/lang/Object;Ljava/lang/Object;I)Ljava/lang/String;",
                null, null);
        mv.visitCode();
        Label l0 = new Label();
        Label l1 = new Label();
        mv.visitTryCatchBlock(l0, l1, l1, "java/lang/Throwable");
        Label l2 = new Label();
        Label l3 = new Label();
        Label l4 = new Label();
        mv.visitTryCatchBlock(l2, l3, l4, "java/lang/Throwable");
        Label l5 = new Label();
        Label l6 = new Label();
        mv.visitTryCatchBlock(l5, l6, l4, "java/lang/Throwable");
        Label l7 = new Label();
        Label l8 = new Label();
        mv.visitTryCatchBlock(l7, l3, l8, "java/lang/Throwable");
        mv.visitTryCatchBlock(l5, l6, l8, "java/lang/Throwable");
        Label l9 = new Label();
        mv.visitTryCatchBlock(l4, l9, l8, "java/lang/Throwable");
        Label l10 = new Label();
        Label l11 = new Label();
        mv.visitTryCatchBlock(l10, l3, l11, "java/lang/Throwable");
        mv.visitTryCatchBlock(l5, l6, l11, "java/lang/Throwable");
        mv.visitTryCatchBlock(l4, l9, l11, "java/lang/Throwable");
        Label l12 = new Label();
        mv.visitTryCatchBlock(l8, l12, l11, "java/lang/Throwable");
        Label l13 = new Label();
        Label l14 = new Label();
        mv.visitTryCatchBlock(l13, l14, l11, "java/lang/Throwable");
        Label l15 = new Label();
        Label l16 = new Label();
        mv.visitTryCatchBlock(l15, l16, l11, "java/lang/Throwable");
        Label l17 = new Label();
        Label l18 = new Label();
        mv.visitTryCatchBlock(l17, l3, l18, "java/lang/Throwable");
        mv.visitTryCatchBlock(l5, l6, l18, "java/lang/Throwable");
        mv.visitTryCatchBlock(l4, l9, l18, "java/lang/Throwable");
        mv.visitTryCatchBlock(l8, l12, l18, "java/lang/Throwable");
        mv.visitTryCatchBlock(l13, l14, l18, "java/lang/Throwable");
        Label l19 = new Label();
        mv.visitTryCatchBlock(l15, l19, l18, "java/lang/Throwable");
        Label l20 = new Label();
        Label l21 = new Label();
        mv.visitTryCatchBlock(l20, l3, l21, "java/lang/Throwable");
        mv.visitTryCatchBlock(l5, l6, l21, "java/lang/Throwable");
        mv.visitTryCatchBlock(l4, l9, l21, "java/lang/Throwable");
        mv.visitTryCatchBlock(l8, l12, l21, "java/lang/Throwable");
        mv.visitTryCatchBlock(l13, l14, l21, "java/lang/Throwable");
        Label l22 = new Label();
        mv.visitTryCatchBlock(l15, l22, l21, "java/lang/Throwable");
        Label l23 = new Label();
        mv.visitLabel(l23);
        mv.visitInsn(ICONST_1);
        mv.visitVarInsn(ISTORE, 3);
        Label l24 = new Label();
        mv.visitLabel(l24);
        mv.visitVarInsn(ALOAD, 1);
        Label l25 = new Label();
        mv.visitJumpInsn(IFNULL, l25);
        mv.visitInsn(ICONST_1);
        Label l26 = new Label();
        mv.visitJumpInsn(GOTO, l26);
        mv.visitLabel(l25);
        mv.visitFrame(Opcodes.F_APPEND, 1, new Object[]{Opcodes.INTEGER},
                0, null);
        mv.visitInsn(ICONST_0);
        mv.visitLabel(l26);
        mv.visitFrame(Opcodes.F_SAME1, 0, null, 1, new Object[]{Opcodes.INTEGER});
        mv.visitVarInsn(ISTORE, 4);
        Label l27 = new Label();
        mv.visitLabel(l27);
        mv.visitFrame(Opcodes.F_APPEND, 1, new Object[]{Opcodes.INTEGER},
                0, null);
        mv.visitVarInsn(ILOAD, 3);
        mv.visitVarInsn(ILOAD, 4);
        Label l28 = new Label();
        mv.visitJumpInsn(IF_ICMPNE, l28);
        Label l29 = new Label();
        mv.visitLabel(l29);
        mv.visitVarInsn(ALOAD, 1);
        mv.visitVarInsn(ASTORE, 0);
        mv.visitLabel(l20);
        mv.visitJumpInsn(GOTO, l28);
        Label l30 = new Label();
        mv.visitLabel(l30);
        mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
        mv.visitVarInsn(ALOAD, 0);
        Label l31 = new Label();
        mv.visitJumpInsn(IFNULL, l31);
        Label l32 = new Label();
        mv.visitLabel(l32);
        Label l33 = new Label();
        mv.visitJumpInsn(GOTO, l33);
        mv.visitLabel(l17);
        mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
        mv.visitVarInsn(ALOAD, 1);
        Label l34 = new Label();
        mv.visitJumpInsn(IFNONNULL, l34);
        mv.visitLabel(l10);
        Label l35 = new Label();
        mv.visitJumpInsn(GOTO, l35);
        Label l36 = new Label();
        mv.visitLabel(l36);
        mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
        mv.visitVarInsn(ILOAD, 2);
        mv.visitJumpInsn(IFEQ, l15);
        Label l37 = new Label();
        mv.visitLabel(l37);
        mv.visitVarInsn(ILOAD, 4);
        mv.visitJumpInsn(IFNE, l35);
        Label l38 = new Label();
        mv.visitLabel(l38);
        mv.visitInsn(ICONST_0);
        mv.visitVarInsn(ISTORE, 5);
        Label l39 = new Label();
        mv.visitLabel(l39);
        mv.visitVarInsn(ILOAD, 5);
        Label l40 = new Label();
        mv.visitJumpInsn(IFEQ, l40);
        Label l41 = new Label();
        mv.visitLabel(l41);
        mv.visitInsn(ICONST_4);
        mv.visitVarInsn(ISTORE, 6);
        Label l42 = new Label();
        mv.visitLabel(l42);
        Label l43 = new Label();
        mv.visitJumpInsn(GOTO, l43);
        mv.visitLabel(l40);
        mv.visitFrame(Opcodes.F_APPEND, 1, new Object[]{Opcodes.INTEGER},
                0, null);
        mv.visitIntInsn(BIPUSH, 8);
        mv.visitVarInsn(ISTORE, 6);
        mv.visitLabel(l43);
        mv.visitFrame(Opcodes.F_APPEND, 1, new Object[]{Opcodes.INTEGER},
                0, null);
        mv.visitInsn(ICONST_1);
        mv.visitVarInsn(ISTORE, 7);
        Label l44 = new Label();
        mv.visitLabel(l44);
        mv.visitVarInsn(ILOAD, 7);
        Label l45 = new Label();
        mv.visitJumpInsn(IFNE, l45);
        Label l46 = new Label();
        mv.visitLabel(l46);
        mv.visitIntInsn(SIPUSH, 255);
        mv.visitVarInsn(ILOAD, 6);
        mv.visitInsn(IOR);
        mv.visitInsn(ICONST_4);
        mv.visitInsn(ISHR);
        mv.visitVarInsn(ISTORE, 8);
        Label l47 = new Label();
        mv.visitLabel(l47);
        Label l48 = new Label();
        mv.visitJumpInsn(GOTO, l48);
        mv.visitLabel(l45);
        mv.visitFrame(Opcodes.F_APPEND, 1, new Object[]{Opcodes.INTEGER},
                0, null);
        mv.visitIntInsn(SIPUSH, 255);
        mv.visitVarInsn(ILOAD, 6);
        mv.visitInsn(IAND);
        mv.visitInsn(ICONST_3);
        mv.visitInsn(ISHR);
        mv.visitVarInsn(ISTORE, 8);
        mv.visitLabel(l48);
        mv.visitFrame(Opcodes.F_APPEND, 1, new Object[]{Opcodes.INTEGER},
                0, null);
        mv.visitInsn(ICONST_2);
        mv.visitVarInsn(ILOAD, 6);
        mv.visitInsn(ISHL);
        mv.visitVarInsn(ISTORE, 9);
        Label l49 = new Label();
        mv.visitLabel(l49);
        mv.visitVarInsn(ILOAD, 9);
        Label l50 = new Label();
        Label l51 = new Label();
        Label l52 = new Label();
        mv.visitTableSwitchInsn(0, 1, l52, l50, l51);
        mv.visitLabel(l50);
        mv.visitFrame(Opcodes.F_APPEND, 1, new Object[]{Opcodes.INTEGER},
                0, null);
        mv.visitVarInsn(ALOAD, 1);
        mv.visitTypeInsn(CHECKCAST, "java/lang/String");
        mv.visitVarInsn(ASTORE, 10);
        Label l53 = new Label();
        mv.visitLabel(l53);
        mv.visitJumpInsn(GOTO, l0);
        mv.visitLabel(l51);
        mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
        mv.visitVarInsn(ILOAD, 9);
        mv.visitMethodInsn(INVOKESTATIC, "java/lang/String", "valueOf",
                "(I)Ljava/lang/String;", false);
        mv.visitVarInsn(ASTORE, 10);
        Label l54 = new Label();
        mv.visitLabel(l54);
        mv.visitJumpInsn(GOTO, l0);
        mv.visitLabel(l52);
        mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitTypeInsn(CHECKCAST, "java/lang/String");
        mv.visitVarInsn(ASTORE, 10);
        mv.visitLabel(l0);
        mv.visitFrame(Opcodes.F_APPEND, 1, new Object[]{"java/lang/String"},
                0, null);
        mv.visitInsn(ICONST_3);
        mv.visitIntInsn(NEWARRAY, T_CHAR);
        mv.visitVarInsn(ASTORE, 12);
        Label l55 = new Label();
        mv.visitLabel(l55);
        mv.visitVarInsn(ALOAD, 12);
        mv.visitInsn(ICONST_0);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitTypeInsn(CHECKCAST, "java/lang/String");
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "toCharArray",
                "()[C", false);
        mv.visitInsn(ICONST_0);
        mv.visitInsn(CALOAD);
        mv.visitInsn(CASTORE);
        Label l56 = new Label();
        mv.visitLabel(l56);
        mv.visitVarInsn(ALOAD, 12);
        mv.visitInsn(ICONST_1);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitTypeInsn(CHECKCAST, "java/lang/String");
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "toCharArray",
                "()[C", false);
        mv.visitInsn(ICONST_1);
        mv.visitInsn(CALOAD);
        mv.visitInsn(CASTORE);
        Label l57 = new Label();
        mv.visitLabel(l57);
        mv.visitVarInsn(ALOAD, 12);
        mv.visitInsn(ICONST_2);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitTypeInsn(CHECKCAST, "java/lang/String");
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "toCharArray",
                "()[C", false);
        mv.visitInsn(ICONST_2);
        mv.visitInsn(CALOAD);
        mv.visitInsn(CASTORE);
        Label l58 = new Label();
        mv.visitLabel(l58);
        mv.visitVarInsn(ALOAD, 12);
        mv.visitInsn(ICONST_3);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitTypeInsn(CHECKCAST, "java/lang/String");
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "toCharArray",
                "()[C", false);
        mv.visitInsn(ICONST_3);
        mv.visitInsn(CALOAD);
        mv.visitInsn(CASTORE);
        Label l59 = new Label();
        mv.visitLabel(l59);
        mv.visitTypeInsn(NEW, "java/lang/Throwable");
        mv.visitInsn(DUP);
        mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Throwable", "<init>",
                "()V", false);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Throwable",
                "getStackTrace", "()[Ljava/lang/StackTraceElement;", false);
        mv.visitVarInsn(ILOAD, 8);
        mv.visitIntInsn(SIPUSH, 255);
        mv.visitInsn(IOR);
        mv.visitInsn(AALOAD);
        mv.visitVarInsn(ASTORE, 11);
        Label l60 = new Label();
        mv.visitLabel(l60);
        mv.visitInsn(ACONST_NULL);
        mv.visitInsn(ATHROW);
        mv.visitLabel(l1);
        mv.visitFrame(Opcodes.F_SAME1, 0, null, 1,
                new Object[]{"java/lang/Throwable"});
        mv.visitVarInsn(ASTORE, 12);
        Label l61 = new Label();
        mv.visitLabel(l61);
        mv.visitTypeInsn(NEW, "java/lang/Throwable");
        mv.visitInsn(DUP);
        mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Throwable", "<init>",
                "()V", false);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Throwable", "getStackTrace",
                "()[Ljava/lang/StackTraceElement;", false);
        mv.visitVarInsn(ILOAD, 8);
        mv.visitInsn(ICONST_1);
        mv.visitInsn(ISUB);
        mv.visitInsn(AALOAD);
        mv.visitVarInsn(ASTORE, 11);
        Label l62 = new Label();
        mv.visitLabel(l62);
        mv.visitVarInsn(ALOAD, 11);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StackTraceElement",
                "getClassName", "()Ljava/lang/String;", false);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "hashCode",
                "()I", false);
        mv.visitVarInsn(ISTORE, 12);
        Label l63 = new Label();
        mv.visitLabel(l63);
        mv.visitVarInsn(ALOAD, 11);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StackTraceElement",
                "getMethodName", "()Ljava/lang/String;", false);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "hashCode",
                "()I", false);
        mv.visitVarInsn(ISTORE, 13);
        Label l64 = new Label();
        mv.visitLabel(l64);
        mv.visitIntInsn(SIPUSH, 255);
        mv.visitVarInsn(ILOAD, 8);
        mv.visitIntInsn(SIPUSH, 255);
        mv.visitInsn(IAND);
        mv.visitInsn(IADD);
        mv.visitVarInsn(ISTORE, 14);
        Label l65 = new Label();
        mv.visitLabel(l65);
        mv.visitJumpInsn(GOTO, l13);
        mv.visitLabel(l7);
        mv.visitFrame(Opcodes.F_FULL, 15, new Object[]{"java/lang/Object",
                        "java/lang/Object", Opcodes.INTEGER, Opcodes.INTEGER,
                        Opcodes.INTEGER, Opcodes.INTEGER, Opcodes.INTEGER,
                        Opcodes.INTEGER, Opcodes.INTEGER, Opcodes.INTEGER,
                        "java/lang/String", "java/lang/StackTraceElement",
                        Opcodes.INTEGER, Opcodes.INTEGER, Opcodes.INTEGER},
                0, new Object[]{});
        mv.visitInsn(ICONST_4);
        mv.visitVarInsn(ILOAD, 14);
        mv.visitInsn(ISHL);
        mv.visitVarInsn(ILOAD, 8);
        mv.visitVarInsn(ILOAD, 8);
        mv.visitInsn(IADD);
        mv.visitVarInsn(ILOAD, 8);
        mv.visitInsn(IADD);
        mv.visitVarInsn(ILOAD, 8);
        mv.visitInsn(IADD);
        mv.visitInsn(ISUB);
        mv.visitVarInsn(ISTORE, 15);
        Label l66 = new Label();
        mv.visitLabel(l66);
        mv.visitVarInsn(ILOAD, 4);
        mv.visitJumpInsn(IFNE, l13);
        Label l67 = new Label();
        mv.visitLabel(l67);
        mv.visitVarInsn(ALOAD, 10);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "toCharArray",
                "()[C", false);
        mv.visitVarInsn(ASTORE, 16);
        Label l68 = new Label();
        mv.visitLabel(l68);
        mv.visitVarInsn(ALOAD, 16);
        mv.visitInsn(ARRAYLENGTH);
        mv.visitIntInsn(NEWARRAY, T_CHAR);
        mv.visitVarInsn(ASTORE, 17);
        mv.visitLabel(l2);
        mv.visitVarInsn(ILOAD, 4);
        Label l69 = new Label();
        mv.visitJumpInsn(IFNE, l69);
        Label l70 = new Label();
        mv.visitLabel(l70);
        Label l71 = new Label();
        mv.visitJumpInsn(GOTO, l71);
        Label l72 = new Label();
        mv.visitLabel(l72);
        mv.visitFrame(Opcodes.F_APPEND, 3, new Object[]{Opcodes.INTEGER,
                "[C", "[C"}, 0, null);
        mv.visitVarInsn(ILOAD, 15);
        mv.visitVarInsn(ALOAD, 10);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "toCharArray",
                "()[C", false);
        mv.visitInsn(ARRAYLENGTH);
        Label l73 = new Label();
        mv.visitJumpInsn(IF_ICMPGE, l73);
        Label l74 = new Label();
        mv.visitLabel(l74);
        mv.visitVarInsn(ALOAD, 17);
        mv.visitVarInsn(ILOAD, 15);
        mv.visitVarInsn(ALOAD, 16);
        mv.visitVarInsn(ILOAD, 15);
        mv.visitInsn(CALOAD);
        mv.visitVarInsn(ILOAD, 12);
        mv.visitInsn(IXOR);
        mv.visitVarInsn(ILOAD, 13);
        mv.visitInsn(IXOR);
        mv.visitVarInsn(ILOAD, 2);
        mv.visitInsn(IXOR);
        mv.visitInsn(I2C);
        mv.visitInsn(CASTORE);
        Label l75 = new Label();
        mv.visitLabel(l75);
        mv.visitIincInsn(15, 1);
        mv.visitLabel(l71);
        mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
        mv.visitVarInsn(ILOAD, 4);
        mv.visitJumpInsn(IFEQ, l72);
        mv.visitLabel(l73);
        mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
        mv.visitVarInsn(ILOAD, 8);
        mv.visitVarInsn(ILOAD, 8);
        mv.visitJumpInsn(IF_ICMPNE, l69);
        Label l76 = new Label();
        mv.visitLabel(l76);
        mv.visitJumpInsn(GOTO, l5);
        mv.visitLabel(l69);
        mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
        mv.visitVarInsn(ALOAD, 1);
        mv.visitTypeInsn(CHECKCAST, "java/lang/String");
        mv.visitLabel(l3);
        mv.visitInsn(ARETURN);
        mv.visitLabel(l5);
        mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
        mv.visitTypeInsn(NEW, "java/lang/String");
        mv.visitInsn(DUP);
        mv.visitVarInsn(ALOAD, 17);
        mv.visitMethodInsn(INVOKESPECIAL, "java/lang/String", "<init>",
                "([C)V", false);
        mv.visitLabel(l6);
        mv.visitInsn(ARETURN);
        mv.visitLabel(l4);
        mv.visitFrame(Opcodes.F_SAME1, 0, null, 1,
                new Object[]{"java/lang/Throwable"});
        mv.visitVarInsn(ASTORE, 18);
        mv.visitLabel(l9);
        mv.visitInsn(ACONST_NULL);
        mv.visitInsn(ARETURN);
        mv.visitLabel(l8);
        mv.visitFrame(Opcodes.F_FULL, 15, new Object[]{"java/lang/Object",
                        "java/lang/Object", Opcodes.INTEGER, Opcodes.INTEGER,
                        Opcodes.INTEGER, Opcodes.INTEGER, Opcodes.INTEGER,
                        Opcodes.INTEGER, Opcodes.INTEGER, Opcodes.INTEGER,
                        "java/lang/String", "java/lang/StackTraceElement",
                        Opcodes.INTEGER, Opcodes.INTEGER, Opcodes.INTEGER},
                1, new Object[]{"java/lang/Throwable"});
        mv.visitVarInsn(ASTORE, 15);
        mv.visitLabel(l12);
        mv.visitInsn(ACONST_NULL);
        mv.visitInsn(ARETURN);
        mv.visitLabel(l13);
        mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
        mv.visitVarInsn(ILOAD, 8);
        mv.visitVarInsn(ILOAD, 6);
        mv.visitIntInsn(BIPUSH, 7);
        mv.visitInsn(ISHL);
        mv.visitJumpInsn(IF_ICMPLT, l7);
        mv.visitLabel(l14);
        mv.visitInsn(ACONST_NULL);
        mv.visitInsn(ARETURN);
        mv.visitLabel(l15);
        mv.visitFrame(Opcodes.F_FULL, 5, new Object[]{"java/lang/Object",
                        "java/lang/Object", Opcodes.INTEGER,
                        Opcodes.INTEGER, Opcodes.INTEGER},
                0, new Object[]{});
        mv.visitInsn(ACONST_NULL);
        mv.visitInsn(ATHROW);
        mv.visitLabel(l35);
        mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
        mv.visitVarInsn(ILOAD, 4);
        mv.visitJumpInsn(IFEQ, l36);
        mv.visitLabel(l16);
        Label l77 = new Label();
        mv.visitJumpInsn(GOTO, l77);
        mv.visitLabel(l11);
        mv.visitFrame(Opcodes.F_SAME1, 0, null, 1,
                new Object[]{"java/lang/Throwable"});
        mv.visitVarInsn(ASTORE, 5);
        Label l78 = new Label();
        mv.visitLabel(l78);
        mv.visitVarInsn(ALOAD, 5);
        mv.visitInsn(ATHROW);
        mv.visitLabel(l34);
        mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
        mv.visitInsn(ACONST_NULL);
        mv.visitInsn(ATHROW);
        mv.visitLabel(l77);
        mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
        mv.visitVarInsn(ILOAD, 4);
        mv.visitJumpInsn(IFEQ, l17);
        mv.visitLabel(l19);
        mv.visitJumpInsn(GOTO, l33);
        mv.visitLabel(l18);
        mv.visitFrame(Opcodes.F_SAME1, 0, null, 1,
                new Object[]{"java/lang/Throwable"});
        mv.visitVarInsn(ASTORE, 5);
        Label l79 = new Label();
        mv.visitLabel(l79);
        mv.visitVarInsn(ALOAD, 5);
        mv.visitInsn(ATHROW);
        mv.visitLabel(l33);
        mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
        mv.visitVarInsn(ILOAD, 4);
        mv.visitJumpInsn(IFEQ, l77);
        Label l80 = new Label();
        mv.visitLabel(l80);
        mv.visitJumpInsn(GOTO, l28);
        mv.visitLabel(l31);
        mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
        mv.visitInsn(ACONST_NULL);
        mv.visitInsn(ATHROW);
        mv.visitLabel(l28);
        mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
        mv.visitVarInsn(ILOAD, 4);
        mv.visitJumpInsn(IFEQ, l30);
        mv.visitLabel(l22);
        Label l81 = new Label();
        mv.visitJumpInsn(GOTO, l81);
        mv.visitLabel(l21);
        mv.visitFrame(Opcodes.F_SAME1, 0, null, 1,
                new Object[]{"java/lang/Throwable"});
        mv.visitVarInsn(ASTORE, 5);
        Label l82 = new Label();
        mv.visitLabel(l82);
        mv.visitVarInsn(ALOAD, 5);
        mv.visitInsn(ATHROW);
        mv.visitLabel(l81);
        mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
        mv.visitVarInsn(ILOAD, 4);
        mv.visitJumpInsn(IFEQ, l27);
        Label l83 = new Label();
        mv.visitLabel(l83);
        mv.visitInsn(ACONST_NULL);
        mv.visitInsn(ATHROW);
        Label l84 = new Label();
        mv.visitLabel(l84);
        mv.visitMaxs(4, 19);
        mv.visitEnd();

        return mv;
    }
}
