package me.itzsomebody.radon.methods;

import me.itzsomebody.radon.transformers.stringencryption.HeavyStringEncryption;
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
        mv.visitTableSwitchInsn(0, 1, l52, new Label[]{l50, l51});
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

    /**
     * Returns a {@link MethodNode} that returns a {@link String} needed to
     * decrypt strings encrypted by {@link HeavyStringEncryption}.
     *
     * @param decryptionMethodName used to determine the name of the
     *                             generated {@link MethodNode}.
     * @return a {@link MethodNode} that returns a {@link String} needed to
     * decrypt strings encrypted by {@link HeavyStringEncryption}.
     */
    public static MethodNode heavyMethod(String decryptionMethodName) {
        MethodNode mv = new MethodNode(ACC_PUBLIC + ACC_STATIC +
                ACC_SYNTHETIC + ACC_BRIDGE, decryptionMethodName,
                "(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)" +
                        "Ljava/lang/String;", null, null);
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
        Label l7 = new Label();
        mv.visitTryCatchBlock(l5, l6, l7, "java/lang/Throwable");
        Label l8 = new Label();
        Label l9 = new Label();
        mv.visitTryCatchBlock(l8, l6, l9, "java/lang/Throwable");
        Label l10 = new Label();
        mv.visitTryCatchBlock(l7, l10, l9, "java/lang/Throwable");
        Label l11 = new Label();
        Label l12 = new Label();
        mv.visitTryCatchBlock(l11, l12, l9, "java/lang/Throwable");
        Label l13 = new Label();
        Label l14 = new Label();
        mv.visitTryCatchBlock(l13, l6, l14, "java/lang/Throwable");
        mv.visitTryCatchBlock(l7, l10, l14, "java/lang/Throwable");
        mv.visitTryCatchBlock(l11, l12, l14, "java/lang/Throwable");
        Label l15 = new Label();
        mv.visitTryCatchBlock(l9, l15, l14, "java/lang/Throwable");
        Label l16 = new Label();
        Label l17 = new Label();
        mv.visitTryCatchBlock(l16, l6, l17, "java/lang/Throwable");
        mv.visitTryCatchBlock(l7, l10, l17, "java/lang/Throwable");
        mv.visitTryCatchBlock(l11, l12, l17, "java/lang/Throwable");
        mv.visitTryCatchBlock(l9, l15, l17, "java/lang/Throwable");
        Label l18 = new Label();
        mv.visitTryCatchBlock(l14, l18, l17, "java/lang/Throwable");
        Label l19 = new Label();
        Label l20 = new Label();
        mv.visitTryCatchBlock(l19, l20, l17, "java/lang/Throwable");
        Label l21 = new Label();
        mv.visitLabel(l21);
        mv.visitInsn(ICONST_1);
        mv.visitVarInsn(ISTORE, 3);
        Label l22 = new Label();
        mv.visitLabel(l22);
        mv.visitVarInsn(ALOAD, 1);
        Label l23 = new Label();
        mv.visitJumpInsn(IFNULL, l23);
        mv.visitInsn(ICONST_1);
        Label l24 = new Label();
        mv.visitJumpInsn(GOTO, l24);
        mv.visitLabel(l23);
        mv.visitFrame(Opcodes.F_APPEND, 1, new Object[]{Opcodes.INTEGER},
                0, null);
        mv.visitInsn(ICONST_0);
        mv.visitLabel(l24);
        mv.visitFrame(Opcodes.F_SAME1, 0, null, 1,
                new Object[]{Opcodes.INTEGER});
        mv.visitVarInsn(ISTORE, 4);
        Label l25 = new Label();
        mv.visitLabel(l25);
        mv.visitFrame(Opcodes.F_APPEND, 1, new Object[]{Opcodes.INTEGER},
                0, null);
        mv.visitVarInsn(ILOAD, 3);
        mv.visitVarInsn(ILOAD, 4);
        Label l26 = new Label();
        mv.visitJumpInsn(IF_ICMPEQ, l26);
        Label l27 = new Label();
        mv.visitLabel(l27);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitVarInsn(ASTORE, 1);
        mv.visitLabel(l16);
        mv.visitJumpInsn(GOTO, l26);
        Label l28 = new Label();
        mv.visitLabel(l28);
        mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
        mv.visitVarInsn(ALOAD, 0);
        Label l29 = new Label();
        mv.visitJumpInsn(IFNULL, l29);
        Label l30 = new Label();
        mv.visitLabel(l30);
        Label l31 = new Label();
        mv.visitJumpInsn(GOTO, l31);
        Label l32 = new Label();
        mv.visitLabel(l32);
        mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
        mv.visitInsn(ICONST_0);
        mv.visitVarInsn(ISTORE, 5);
        Label l33 = new Label();
        mv.visitLabel(l33);
        mv.visitVarInsn(ILOAD, 5);
        Label l34 = new Label();
        mv.visitJumpInsn(IFEQ, l34);
        Label l35 = new Label();
        mv.visitLabel(l35);
        mv.visitInsn(ICONST_4);
        mv.visitVarInsn(ISTORE, 6);
        Label l36 = new Label();
        mv.visitLabel(l36);
        Label l37 = new Label();
        mv.visitJumpInsn(GOTO, l37);
        mv.visitLabel(l34);
        mv.visitFrame(Opcodes.F_APPEND, 1, new Object[]{Opcodes.INTEGER},
                0, null);
        mv.visitIntInsn(BIPUSH, 8);
        mv.visitVarInsn(ISTORE, 6);
        mv.visitLabel(l37);
        mv.visitFrame(Opcodes.F_APPEND, 1, new Object[]{Opcodes.INTEGER},
                0, null);
        mv.visitInsn(ICONST_1);
        mv.visitVarInsn(ISTORE, 7);
        Label l38 = new Label();
        mv.visitLabel(l38);
        mv.visitVarInsn(ILOAD, 7);
        Label l39 = new Label();
        mv.visitJumpInsn(IFNE, l39);
        Label l40 = new Label();
        mv.visitLabel(l40);
        mv.visitIntInsn(SIPUSH, 255);
        mv.visitVarInsn(ILOAD, 6);
        mv.visitInsn(IOR);
        mv.visitInsn(ICONST_4);
        mv.visitInsn(ISHR);
        mv.visitVarInsn(ISTORE, 8);
        Label l41 = new Label();
        mv.visitLabel(l41);
        Label l42 = new Label();
        mv.visitJumpInsn(GOTO, l42);
        mv.visitLabel(l39);
        mv.visitFrame(Opcodes.F_APPEND, 1, new Object[]{Opcodes.INTEGER},
                0, null);
        mv.visitIntInsn(SIPUSH, 255);
        mv.visitVarInsn(ILOAD, 6);
        mv.visitInsn(IAND);
        mv.visitInsn(ICONST_3);
        mv.visitInsn(ISHR);
        mv.visitVarInsn(ISTORE, 8);
        mv.visitLabel(l42);
        mv.visitFrame(Opcodes.F_APPEND, 1, new Object[]{Opcodes.INTEGER},
                0, null);
        mv.visitInsn(ICONST_2);
        mv.visitVarInsn(ILOAD, 6);
        mv.visitInsn(ISHL);
        mv.visitVarInsn(ISTORE, 9);
        Label l43 = new Label();
        mv.visitLabel(l43);
        mv.visitVarInsn(ILOAD, 9);
        Label l44 = new Label();
        Label l45 = new Label();
        Label l46 = new Label();
        mv.visitTableSwitchInsn(0, 1, l46, new Label[]{l44, l45});
        mv.visitLabel(l44);
        mv.visitFrame(Opcodes.F_APPEND, 1, new Object[]{Opcodes.INTEGER},
                0, null);
        mv.visitVarInsn(ALOAD, 1);
        mv.visitTypeInsn(CHECKCAST, "java/lang/String");
        mv.visitVarInsn(ASTORE, 10);
        Label l47 = new Label();
        mv.visitLabel(l47);
        mv.visitJumpInsn(GOTO, l0);
        mv.visitLabel(l45);
        mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
        mv.visitVarInsn(ILOAD, 9);
        mv.visitMethodInsn(INVOKESTATIC, "java/lang/String", "valueOf",
                "(I)Ljava/lang/String;", false);
        mv.visitVarInsn(ASTORE, 10);
        Label l48 = new Label();
        mv.visitLabel(l48);
        mv.visitJumpInsn(GOTO, l0);
        mv.visitLabel(l46);
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
        Label l49 = new Label();
        mv.visitLabel(l49);
        mv.visitVarInsn(ALOAD, 12);
        mv.visitInsn(ICONST_0);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitTypeInsn(CHECKCAST, "java/lang/String");
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "toCharArray",
                "()[C", false);
        mv.visitInsn(ICONST_0);
        mv.visitInsn(CALOAD);
        mv.visitInsn(CASTORE);
        Label l50 = new Label();
        mv.visitLabel(l50);
        mv.visitVarInsn(ALOAD, 12);
        mv.visitInsn(ICONST_1);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitTypeInsn(CHECKCAST, "java/lang/String");
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "toCharArray",
                "()[C", false);
        mv.visitInsn(ICONST_1);
        mv.visitInsn(CALOAD);
        mv.visitInsn(CASTORE);
        Label l51 = new Label();
        mv.visitLabel(l51);
        mv.visitVarInsn(ALOAD, 12);
        mv.visitInsn(ICONST_2);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitTypeInsn(CHECKCAST, "java/lang/String");
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "toCharArray",
                "()[C", false);
        mv.visitInsn(ICONST_2);
        mv.visitInsn(CALOAD);
        mv.visitInsn(CASTORE);
        Label l52 = new Label();
        mv.visitLabel(l52);
        mv.visitVarInsn(ALOAD, 12);
        mv.visitInsn(ICONST_3);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitTypeInsn(CHECKCAST, "java/lang/String");
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "toCharArray",
                "()[C", false);
        mv.visitInsn(ICONST_3);
        mv.visitInsn(CALOAD);
        mv.visitInsn(CASTORE);
        Label l53 = new Label();
        mv.visitLabel(l53);
        mv.visitMethodInsn(INVOKESTATIC, "java/lang/Thread", "currentThread",
                "()Ljava/lang/Thread;", false);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Thread", "getStackTrace",
                "()[Ljava/lang/StackTraceElement;", false);
        mv.visitVarInsn(ILOAD, 8);
        mv.visitIntInsn(SIPUSH, 255);
        mv.visitInsn(IOR);
        mv.visitInsn(AALOAD);
        mv.visitVarInsn(ASTORE, 11);
        Label l54 = new Label();
        mv.visitLabel(l54);
        mv.visitInsn(ACONST_NULL);
        mv.visitInsn(ATHROW);
        mv.visitLabel(l1);
        mv.visitFrame(Opcodes.F_SAME1, 0, null, 1,
                new Object[]{"java/lang/Throwable"});
        mv.visitVarInsn(ASTORE, 12);
        Label l55 = new Label();
        mv.visitLabel(l55);
        mv.visitMethodInsn(INVOKESTATIC, "java/lang/Thread", "currentThread",
                "()Ljava/lang/Thread;", false);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Thread", "getStackTrace",
                "()[Ljava/lang/StackTraceElement;", false);
        mv.visitVarInsn(ILOAD, 8);
        mv.visitInsn(AALOAD);
        mv.visitVarInsn(ASTORE, 11);
        Label l56 = new Label();
        mv.visitLabel(l56);
        mv.visitVarInsn(ALOAD, 11);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StackTraceElement",
                "getClassName", "()Ljava/lang/String;", false);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "hashCode",
                "()I", false);
        mv.visitVarInsn(ISTORE, 12);
        Label l57 = new Label();
        mv.visitLabel(l57);
        mv.visitVarInsn(ALOAD, 11);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StackTraceElement",
                "getMethodName", "()Ljava/lang/String;", false);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "hashCode",
                "()I", false);
        mv.visitVarInsn(ISTORE, 13);
        Label l58 = new Label();
        mv.visitLabel(l58);
        mv.visitIntInsn(SIPUSH, 255);
        mv.visitVarInsn(ILOAD, 8);
        mv.visitIntInsn(SIPUSH, 255);
        mv.visitInsn(IAND);
        mv.visitInsn(IADD);
        mv.visitVarInsn(ISTORE, 14);
        Label l59 = new Label();
        mv.visitLabel(l59);
        mv.visitJumpInsn(GOTO, l19);
        mv.visitLabel(l13);
        mv.visitFrame(Opcodes.F_FULL, 15, new Object[]{"java/lang/Object",
                "java/lang/Object", "java/lang/Object",
                Opcodes.INTEGER, Opcodes.INTEGER,
                Opcodes.INTEGER, Opcodes.INTEGER,
                Opcodes.INTEGER, Opcodes.INTEGER,
                Opcodes.INTEGER, "java/lang/String",
                "java/lang/StackTraceElement", Opcodes.INTEGER,
                Opcodes.INTEGER, Opcodes.INTEGER}, 0, new Object[]{});
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
        Label l60 = new Label();
        mv.visitLabel(l60);
        mv.visitVarInsn(ILOAD, 5);
        mv.visitJumpInsn(IFNE, l19);
        Label l61 = new Label();
        mv.visitLabel(l61);
        mv.visitVarInsn(ALOAD, 10);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "toCharArray",
                "()[C", false);
        mv.visitVarInsn(ASTORE, 16);
        Label l62 = new Label();
        mv.visitLabel(l62);
        mv.visitVarInsn(ALOAD, 16);
        mv.visitInsn(ARRAYLENGTH);
        mv.visitIntInsn(NEWARRAY, T_CHAR);
        mv.visitVarInsn(ASTORE, 17);
        mv.visitLabel(l8);
        mv.visitVarInsn(ILOAD, 5);
        Label l63 = new Label();
        mv.visitJumpInsn(IFNE, l63);
        Label l64 = new Label();
        mv.visitLabel(l64);
        mv.visitInsn(ACONST_NULL);
        mv.visitVarInsn(ASTORE, 18);
        mv.visitLabel(l2);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitTypeInsn(CHECKCAST, "java/lang/String");
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "toCharArray",
                "()[C", false);
        mv.visitVarInsn(ASTORE, 19);
        Label l65 = new Label();
        mv.visitLabel(l65);
        mv.visitVarInsn(ALOAD, 19);
        mv.visitInsn(ARRAYLENGTH);
        mv.visitIntInsn(NEWARRAY, T_CHAR);
        mv.visitVarInsn(ASTORE, 20);
        Label l66 = new Label();
        mv.visitLabel(l66);
        mv.visitInsn(ICONST_0);
        mv.visitVarInsn(ISTORE, 21);
        Label l67 = new Label();
        mv.visitLabel(l67);
        Label l68 = new Label();
        mv.visitJumpInsn(GOTO, l68);
        Label l69 = new Label();
        mv.visitLabel(l69);
        mv.visitFrame(Opcodes.F_FULL, 22, new Object[]{"java/lang/Object",
                "java/lang/Object", "java/lang/Object",
                Opcodes.INTEGER, Opcodes.INTEGER,
                Opcodes.INTEGER, Opcodes.INTEGER,
                Opcodes.INTEGER, Opcodes.INTEGER,
                Opcodes.INTEGER, "java/lang/String",
                "java/lang/StackTraceElement", Opcodes.INTEGER,
                Opcodes.INTEGER, Opcodes.INTEGER, Opcodes.INTEGER,
                "[C", "[C", "java/lang/String", "[C", "[C",
                Opcodes.INTEGER}, 0, new Object[]{});
        mv.visitVarInsn(ALOAD, 20);
        mv.visitVarInsn(ILOAD, 21);
        mv.visitVarInsn(ALOAD, 11);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StackTraceElement",
                "getMethodName", "()Ljava/lang/String;", false);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "hashCode",
                "()I", false);
        mv.visitVarInsn(ALOAD, 11);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StackTraceElement",
                "getClassName", "()Ljava/lang/String;", false);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "hashCode",
                "()I", false);
        mv.visitInsn(IXOR);
        mv.visitVarInsn(ALOAD, 19);
        mv.visitVarInsn(ILOAD, 21);
        mv.visitInsn(CALOAD);
        mv.visitInsn(IXOR);
        mv.visitInsn(I2C);
        mv.visitInsn(CASTORE);
        Label l70 = new Label();
        mv.visitLabel(l70);
        mv.visitIincInsn(21, 1);
        mv.visitLabel(l68);
        mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
        mv.visitVarInsn(ILOAD, 21);
        mv.visitVarInsn(ALOAD, 19);
        mv.visitInsn(ARRAYLENGTH);
        mv.visitJumpInsn(IF_ICMPLT, l69);
        Label l71 = new Label();
        mv.visitLabel(l71);
        mv.visitTypeInsn(NEW, "java/lang/String");
        mv.visitInsn(DUP);
        mv.visitVarInsn(ALOAD, 20);
        mv.visitMethodInsn(INVOKESPECIAL, "java/lang/String", "<init>",
                "([C)V", false);
        mv.visitVarInsn(ASTORE, 18);
        Label l72 = new Label();
        mv.visitLabel(l72);
        mv.visitVarInsn(ILOAD, 15);
        mv.visitIntInsn(SIPUSH, 255);
        mv.visitJumpInsn(IF_ICMPLE, l5);
        mv.visitLabel(l3);
        mv.visitJumpInsn(GOTO, l63);
        mv.visitLabel(l4);
        mv.visitFrame(Opcodes.F_FULL, 19, new Object[]{"java/lang/Object",
                        "java/lang/Object", "java/lang/Object",
                        Opcodes.INTEGER, Opcodes.INTEGER,
                        Opcodes.INTEGER, Opcodes.INTEGER,
                        Opcodes.INTEGER, Opcodes.INTEGER,
                        Opcodes.INTEGER, "java/lang/String",
                        "java/lang/StackTraceElement", Opcodes.INTEGER,
                        Opcodes.INTEGER, Opcodes.INTEGER, Opcodes.INTEGER,
                        "[C", "[C", "java/lang/String"},
                1, new Object[]{"java/lang/Throwable"});
        mv.visitVarInsn(ASTORE, 19);
        Label l73 = new Label();
        mv.visitLabel(l73);
        mv.visitInsn(ACONST_NULL);
        mv.visitInsn(ATHROW);
        mv.visitLabel(l5);
        mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
        mv.visitVarInsn(ALOAD, 2);
        mv.visitTypeInsn(CHECKCAST, "java/lang/String");
        mv.visitTypeInsn(NEW, "java/lang/String");
        mv.visitInsn(DUP);
        mv.visitInsn(ICONST_5);
        mv.visitIntInsn(NEWARRAY, T_BYTE);
        mv.visitInsn(DUP);
        mv.visitInsn(ICONST_0);
        mv.visitIntInsn(BIPUSH, 85);
        mv.visitInsn(BASTORE);
        mv.visitInsn(DUP);
        mv.visitInsn(ICONST_1);
        mv.visitIntInsn(BIPUSH, 84);
        mv.visitInsn(BASTORE);
        mv.visitInsn(DUP);
        mv.visitInsn(ICONST_2);
        mv.visitIntInsn(BIPUSH, 70);
        mv.visitInsn(BASTORE);
        mv.visitInsn(DUP);
        mv.visitInsn(ICONST_3);
        mv.visitIntInsn(BIPUSH, 45);
        mv.visitInsn(BASTORE);
        mv.visitInsn(DUP);
        mv.visitInsn(ICONST_4);
        mv.visitIntInsn(BIPUSH, 56);
        mv.visitInsn(BASTORE);
        mv.visitMethodInsn(INVOKESPECIAL, "java/lang/String", "<init>",
                "([B)V", false);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "getBytes",
                "(Ljava/lang/String;)[B", false);
        mv.visitVarInsn(ASTORE, 20);
        Label l74 = new Label();
        mv.visitLabel(l74);
        mv.visitTypeInsn(NEW, "java/lang/String");
        mv.visitInsn(DUP);
        mv.visitInsn(ICONST_5);
        mv.visitIntInsn(NEWARRAY, T_BYTE);
        mv.visitInsn(DUP);
        mv.visitInsn(ICONST_0);
        mv.visitIntInsn(BIPUSH, 83);
        mv.visitInsn(BASTORE);
        mv.visitInsn(DUP);
        mv.visitInsn(ICONST_1);
        mv.visitIntInsn(BIPUSH, 72);
        mv.visitInsn(BASTORE);
        mv.visitInsn(DUP);
        mv.visitInsn(ICONST_2);
        mv.visitIntInsn(BIPUSH, 65);
        mv.visitInsn(BASTORE);
        mv.visitInsn(DUP);
        mv.visitInsn(ICONST_3);
        mv.visitIntInsn(BIPUSH, 45);
        mv.visitInsn(BASTORE);
        mv.visitInsn(DUP);
        mv.visitInsn(ICONST_4);
        mv.visitIntInsn(BIPUSH, 49);
        mv.visitInsn(BASTORE);
        mv.visitMethodInsn(INVOKESPECIAL, "java/lang/String", "<init>",
                "([B)V", false);
        mv.visitMethodInsn(INVOKESTATIC, "java/security/MessageDigest",
                "getInstance", "(Ljava/lang/String;)" +
                        "Ljava/security/MessageDigest;", false);
        mv.visitVarInsn(ASTORE, 21);
        Label l75 = new Label();
        mv.visitLabel(l75);
        mv.visitVarInsn(ALOAD, 21);
        mv.visitVarInsn(ALOAD, 20);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/security/MessageDigest",
                "digest", "([B)[B", false);
        mv.visitVarInsn(ASTORE, 20);
        Label l76 = new Label();
        mv.visitLabel(l76);
        mv.visitVarInsn(ALOAD, 20);
        mv.visitIntInsn(BIPUSH, 16);
        mv.visitMethodInsn(INVOKESTATIC, "java/util/Arrays", "copyOf",
                "([BI)[B", false);
        mv.visitVarInsn(ASTORE, 20);
        Label l77 = new Label();
        mv.visitLabel(l77);
        mv.visitTypeInsn(NEW, "javax/crypto/spec/SecretKeySpec");
        mv.visitInsn(DUP);
        mv.visitVarInsn(ALOAD, 20);
        mv.visitTypeInsn(NEW, "java/lang/String");
        mv.visitInsn(DUP);
        mv.visitInsn(ICONST_3);
        mv.visitIntInsn(NEWARRAY, T_BYTE);
        mv.visitInsn(DUP);
        mv.visitInsn(ICONST_0);
        mv.visitIntInsn(BIPUSH, 65);
        mv.visitInsn(BASTORE);
        mv.visitInsn(DUP);
        mv.visitInsn(ICONST_1);
        mv.visitIntInsn(BIPUSH, 69);
        mv.visitInsn(BASTORE);
        mv.visitInsn(DUP);
        mv.visitInsn(ICONST_2);
        mv.visitIntInsn(BIPUSH, 83);
        mv.visitInsn(BASTORE);
        mv.visitMethodInsn(INVOKESPECIAL, "java/lang/String", "<init>",
                "([B)V", false);
        mv.visitMethodInsn(INVOKESPECIAL, "javax/crypto/spec/SecretKeySpec",
                "<init>", "([BLjava/lang/String;)V", false);
        mv.visitVarInsn(ASTORE, 19);
        Label l78 = new Label();
        mv.visitLabel(l78);
        mv.visitTypeInsn(NEW, "java/lang/String");
        mv.visitInsn(DUP);
        mv.visitIntInsn(BIPUSH, 20);
        mv.visitIntInsn(NEWARRAY, T_BYTE);
        mv.visitInsn(DUP);
        mv.visitInsn(ICONST_0);
        mv.visitIntInsn(BIPUSH, 65);
        mv.visitInsn(BASTORE);
        mv.visitInsn(DUP);
        mv.visitInsn(ICONST_1);
        mv.visitIntInsn(BIPUSH, 69);
        mv.visitInsn(BASTORE);
        mv.visitInsn(DUP);
        mv.visitInsn(ICONST_2);
        mv.visitIntInsn(BIPUSH, 83);
        mv.visitInsn(BASTORE);
        mv.visitInsn(DUP);
        mv.visitInsn(ICONST_3);
        mv.visitIntInsn(BIPUSH, 47);
        mv.visitInsn(BASTORE);
        mv.visitInsn(DUP);
        mv.visitInsn(ICONST_4);
        mv.visitIntInsn(BIPUSH, 69);
        mv.visitInsn(BASTORE);
        mv.visitInsn(DUP);
        mv.visitInsn(ICONST_5);
        mv.visitIntInsn(BIPUSH, 67);
        mv.visitInsn(BASTORE);
        mv.visitInsn(DUP);
        mv.visitIntInsn(BIPUSH, 6);
        mv.visitIntInsn(BIPUSH, 66);
        mv.visitInsn(BASTORE);
        mv.visitInsn(DUP);
        mv.visitIntInsn(BIPUSH, 7);
        mv.visitIntInsn(BIPUSH, 47);
        mv.visitInsn(BASTORE);
        mv.visitInsn(DUP);
        mv.visitIntInsn(BIPUSH, 8);
        mv.visitIntInsn(BIPUSH, 80);
        mv.visitInsn(BASTORE);
        mv.visitInsn(DUP);
        mv.visitIntInsn(BIPUSH, 9);
        mv.visitIntInsn(BIPUSH, 75);
        mv.visitInsn(BASTORE);
        mv.visitInsn(DUP);
        mv.visitIntInsn(BIPUSH, 10);
        mv.visitIntInsn(BIPUSH, 67);
        mv.visitInsn(BASTORE);
        mv.visitInsn(DUP);
        mv.visitIntInsn(BIPUSH, 11);
        mv.visitIntInsn(BIPUSH, 83);
        mv.visitInsn(BASTORE);
        mv.visitInsn(DUP);
        mv.visitIntInsn(BIPUSH, 12);
        mv.visitIntInsn(BIPUSH, 53);
        mv.visitInsn(BASTORE);
        mv.visitInsn(DUP);
        mv.visitIntInsn(BIPUSH, 13);
        mv.visitIntInsn(BIPUSH, 80);
        mv.visitInsn(BASTORE);
        mv.visitInsn(DUP);
        mv.visitIntInsn(BIPUSH, 14);
        mv.visitIntInsn(BIPUSH, 65);
        mv.visitInsn(BASTORE);
        mv.visitInsn(DUP);
        mv.visitIntInsn(BIPUSH, 15);
        mv.visitIntInsn(BIPUSH, 68);
        mv.visitInsn(BASTORE);
        mv.visitInsn(DUP);
        mv.visitIntInsn(BIPUSH, 16);
        mv.visitIntInsn(BIPUSH, 68);
        mv.visitInsn(BASTORE);
        mv.visitInsn(DUP);
        mv.visitIntInsn(BIPUSH, 17);
        mv.visitIntInsn(BIPUSH, 73);
        mv.visitInsn(BASTORE);
        mv.visitInsn(DUP);
        mv.visitIntInsn(BIPUSH, 18);
        mv.visitIntInsn(BIPUSH, 78);
        mv.visitInsn(BASTORE);
        mv.visitInsn(DUP);
        mv.visitIntInsn(BIPUSH, 19);
        mv.visitIntInsn(BIPUSH, 71);
        mv.visitInsn(BASTORE);
        mv.visitMethodInsn(INVOKESPECIAL, "java/lang/String", "<init>",
                "([B)V", false);
        mv.visitMethodInsn(INVOKESTATIC, "javax/crypto/Cipher", "getInstance",
                "(Ljava/lang/String;)Ljavax/crypto/Cipher;", false);
        mv.visitVarInsn(ASTORE, 22);
        Label l79 = new Label();
        mv.visitLabel(l79);
        mv.visitVarInsn(ALOAD, 22);
        mv.visitInsn(ICONST_2);
        mv.visitVarInsn(ALOAD, 19);
        mv.visitMethodInsn(INVOKEVIRTUAL, "javax/crypto/Cipher", "init",
                "(ILjava/security/Key;)V", false);
        Label l80 = new Label();
        mv.visitLabel(l80);
        mv.visitTypeInsn(NEW, "java/lang/String");
        mv.visitInsn(DUP);
        mv.visitVarInsn(ALOAD, 22);
        mv.visitMethodInsn(INVOKESTATIC, "java/util/Base64", "getDecoder",
                "()Ljava/util/Base64$Decoder;", false);
        mv.visitVarInsn(ALOAD, 18);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/util/Base64$Decoder", "decode",
                "(Ljava/lang/String;)[B", false);
        mv.visitMethodInsn(INVOKEVIRTUAL, "javax/crypto/Cipher", "doFinal",
                "([B)[B", false);
        mv.visitMethodInsn(INVOKESPECIAL, "java/lang/String", "<init>",
                "([B)V", false);
        mv.visitLabel(l6);
        mv.visitInsn(ARETURN);
        mv.visitLabel(l7);
        mv.visitFrame(Opcodes.F_SAME1, 0, null, 1,
                new Object[]{"java/lang/Throwable"});
        mv.visitVarInsn(ASTORE, 19);
        Label l81 = new Label();
        mv.visitLabel(l81);
        mv.visitVarInsn(ILOAD, 8);
        mv.visitInsn(ICONST_1);
        mv.visitJumpInsn(IF_ICMPNE, l63);
        Label l82 = new Label();
        mv.visitLabel(l82);
        mv.visitJumpInsn(GOTO, l11);
        mv.visitLabel(l63);
        mv.visitFrame(Opcodes.F_CHOP, 1, null, 0, null);
        mv.visitVarInsn(ALOAD, 1);
        mv.visitTypeInsn(CHECKCAST, "java/lang/String");
        mv.visitLabel(l10);
        mv.visitInsn(ARETURN);
        mv.visitLabel(l11);
        mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
        mv.visitTypeInsn(NEW, "java/lang/String");
        mv.visitInsn(DUP);
        mv.visitVarInsn(ALOAD, 17);
        mv.visitMethodInsn(INVOKESPECIAL, "java/lang/String", "<init>",
                "([C)V", false);
        mv.visitLabel(l12);
        mv.visitInsn(ARETURN);
        mv.visitLabel(l9);
        mv.visitFrame(Opcodes.F_SAME1, 0, null, 1,
                new Object[]{"java/lang/Throwable"});
        mv.visitVarInsn(ASTORE, 18);
        mv.visitLabel(l15);
        mv.visitInsn(ACONST_NULL);
        mv.visitInsn(ARETURN);
        mv.visitLabel(l14);
        mv.visitFrame(Opcodes.F_FULL, 15, new Object[]{"java/lang/Object",
                        "java/lang/Object", "java/lang/Object",
                        Opcodes.INTEGER, Opcodes.INTEGER,
                        Opcodes.INTEGER, Opcodes.INTEGER,
                        Opcodes.INTEGER, Opcodes.INTEGER,
                        Opcodes.INTEGER, "java/lang/String",
                        "java/lang/StackTraceElement", Opcodes.INTEGER,
                        Opcodes.INTEGER, Opcodes.INTEGER},
                1, new Object[]{"java/lang/Throwable"});
        mv.visitVarInsn(ASTORE, 15);
        mv.visitLabel(l18);
        mv.visitInsn(ACONST_NULL);
        mv.visitInsn(ARETURN);
        mv.visitLabel(l19);
        mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
        mv.visitVarInsn(ILOAD, 8);
        mv.visitVarInsn(ILOAD, 6);
        mv.visitIntInsn(BIPUSH, 7);
        mv.visitInsn(ISHL);
        mv.visitJumpInsn(IF_ICMPLT, l13);
        mv.visitLabel(l31);
        mv.visitFrame(Opcodes.F_FULL, 5, new Object[]{"java/lang/Object",
                "java/lang/Object", "java/lang/Object",
                Opcodes.INTEGER, Opcodes.INTEGER}, 0, new Object[]{});
        mv.visitVarInsn(ALOAD, 2);
        mv.visitJumpInsn(IFNONNULL, l32);
        Label l83 = new Label();
        mv.visitLabel(l83);
        mv.visitJumpInsn(GOTO, l26);
        mv.visitLabel(l29);
        mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
        mv.visitInsn(ACONST_NULL);
        mv.visitInsn(ATHROW);
        mv.visitLabel(l26);
        mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
        mv.visitVarInsn(ILOAD, 4);
        mv.visitJumpInsn(IFEQ, l28);
        mv.visitLabel(l20);
        Label l84 = new Label();
        mv.visitJumpInsn(GOTO, l84);
        mv.visitLabel(l17);
        mv.visitFrame(Opcodes.F_SAME1, 0, null, 1,
                new Object[]{"java/lang/Throwable"});
        mv.visitVarInsn(ASTORE, 5);
        Label l85 = new Label();
        mv.visitLabel(l85);
        mv.visitInsn(ACONST_NULL);
        mv.visitInsn(ATHROW);
        mv.visitLabel(l84);
        mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
        mv.visitVarInsn(ILOAD, 4);
        mv.visitJumpInsn(IFEQ, l25);
        Label l86 = new Label();
        mv.visitLabel(l86);
        mv.visitInsn(ACONST_NULL);
        mv.visitInsn(ATHROW);
        Label l87 = new Label();
        mv.visitLabel(l87);
        mv.visitMaxs(9, 23);
        mv.visitEnd();

        return mv;
    }
}
