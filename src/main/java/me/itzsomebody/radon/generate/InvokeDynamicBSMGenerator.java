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

package me.itzsomebody.radon.generate;

import java.lang.invoke.ConstantCallSite;

import me.itzsomebody.radon.transformers.invokedynamic.HeavyInvokeDynamic;
import me.itzsomebody.radon.transformers.invokedynamic.LightInvokeDynamic;
import me.itzsomebody.radon.transformers.invokedynamic.NormalInvokeDynamic;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.MethodNode;

/**
 * That returns methods needed to produce a {@link ConstantCallSite} for
 * the appropriate InvokeDynamic transformer.
 *
 * @author ItzSomebody
 * @author ASMifier by OW2
 */
public class InvokeDynamicBSMGenerator implements Opcodes {
    /**
     * Returns a {@link MethodNode} that returns a {@link ConstantCallSite}
     * statically linked to a method for {@link LightInvokeDynamic}.
     *
     * @param bsmName used to determine the name of the generated
     *                {@link MethodNode}.
     * @return a {@link MethodNode} that returns a {@link ConstantCallSite}
     * statically linked to a method for {@link LightInvokeDynamic}.
     */
    public static MethodNode lightBSM(String bsmName, String className) {
        MethodNode mv = new MethodNode(Opcodes.ACC_PUBLIC + Opcodes.ACC_STATIC
                + Opcodes.ACC_SYNTHETIC + Opcodes.ACC_BRIDGE,
                bsmName,
                "(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;" +
                        "Ljava/lang/Object;Ljava/lang/Object;" +
                        "Ljava/lang/Object;Ljava/lang/Object;)" +
                        "Ljava/lang/Object;",
                null,
                null);
        mv.visitCode();
        Label l0 = new Label();
        Label l1 = new Label();
        Label l2 = new Label();
        mv.visitTryCatchBlock(l0, l1, l2, "java/lang/Exception");
        mv.visitLabel(l0);
        mv.visitVarInsn(ALOAD, 4);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Object", "toString",
                "()Ljava/lang/String;", false);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "toCharArray",
                "()[C", false);
        mv.visitVarInsn(ASTORE, 7);
        Label l3 = new Label();
        mv.visitLabel(l3);
        mv.visitVarInsn(ALOAD, 7);
        mv.visitInsn(ARRAYLENGTH);
        mv.visitIntInsn(NEWARRAY, T_CHAR);
        mv.visitVarInsn(ASTORE, 8);
        Label l4 = new Label();
        mv.visitLabel(l4);
        mv.visitInsn(ICONST_0);
        mv.visitVarInsn(ISTORE, 9);
        Label l5 = new Label();
        mv.visitLabel(l5);
        Label l6 = new Label();
        mv.visitJumpInsn(GOTO, l6);
        Label l7 = new Label();
        mv.visitLabel(l7);
        mv.visitFrame(Opcodes.F_APPEND, 3, new Object[]{"[C", "[C",
                Opcodes.INTEGER}, 0, null);
        mv.visitVarInsn(ALOAD, 8);
        mv.visitVarInsn(ILOAD, 9);
        mv.visitVarInsn(ALOAD, 7);
        mv.visitVarInsn(ILOAD, 9);
        mv.visitInsn(CALOAD);
        mv.visitIntInsn(SIPUSH, 1029);
        mv.visitInsn(IXOR);
        mv.visitInsn(I2C);
        mv.visitInsn(CASTORE);
        Label l8 = new Label();
        mv.visitLabel(l8);
        mv.visitIincInsn(9, 1);
        mv.visitLabel(l6);
        mv.visitFrame(Opcodes.F_SAME, 0, null, 0,
                null);
        mv.visitVarInsn(ILOAD, 9);
        mv.visitVarInsn(ALOAD, 7);
        mv.visitInsn(ARRAYLENGTH);
        mv.visitJumpInsn(IF_ICMPLT, l7);
        Label l9 = new Label();
        mv.visitLabel(l9);
        mv.visitVarInsn(ALOAD, 5);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Object", "toString",
                "()Ljava/lang/String;", false);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "toCharArray",
                "()[C", false);
        mv.visitVarInsn(ASTORE, 9);
        Label l10 = new Label();
        mv.visitLabel(l10);
        mv.visitVarInsn(ALOAD, 9);
        mv.visitInsn(ARRAYLENGTH);
        mv.visitIntInsn(NEWARRAY, T_CHAR);
        mv.visitVarInsn(ASTORE, 10);
        Label l11 = new Label();
        mv.visitLabel(l11);
        mv.visitInsn(ICONST_0);
        mv.visitVarInsn(ISTORE, 11);
        Label l12 = new Label();
        mv.visitLabel(l12);
        Label l13 = new Label();
        mv.visitJumpInsn(GOTO, l13);
        Label l14 = new Label();
        mv.visitLabel(l14);
        mv.visitFrame(Opcodes.F_FULL, 12, new Object[]{"java/lang/Object",
                "java/lang/Object", "java/lang/Object", "java/lang/Object",
                "java/lang/Object", "java/lang/Object", "java/lang/Object",
                "[C", "[C", "[C", "[C", Opcodes.INTEGER}, 0, new Object[]{});
        mv.visitVarInsn(ALOAD, 10);
        mv.visitVarInsn(ILOAD, 11);
        mv.visitVarInsn(ALOAD, 9);
        mv.visitVarInsn(ILOAD, 11);
        mv.visitInsn(CALOAD);
        mv.visitIntInsn(SIPUSH, 2038);
        mv.visitInsn(IXOR);
        mv.visitInsn(I2C);
        mv.visitInsn(CASTORE);
        Label l15 = new Label();
        mv.visitLabel(l15);
        mv.visitIincInsn(11, 1);
        mv.visitLabel(l13);
        mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
        mv.visitVarInsn(ILOAD, 11);
        mv.visitVarInsn(ALOAD, 9);
        mv.visitInsn(ARRAYLENGTH);
        mv.visitJumpInsn(IF_ICMPLT, l14);
        Label l16 = new Label();
        mv.visitLabel(l16);
        mv.visitVarInsn(ALOAD, 6);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Object", "toString",
                "()Ljava/lang/String;", false);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "toCharArray",
                "()[C", false);
        mv.visitVarInsn(ASTORE, 11);
        Label l17 = new Label();
        mv.visitLabel(l17);
        mv.visitVarInsn(ALOAD, 11);
        mv.visitInsn(ARRAYLENGTH);
        mv.visitIntInsn(NEWARRAY, T_CHAR);
        mv.visitVarInsn(ASTORE, 12);
        Label l18 = new Label();
        mv.visitLabel(l18);
        mv.visitInsn(ICONST_0);
        mv.visitVarInsn(ISTORE, 13);
        Label l19 = new Label();
        mv.visitLabel(l19);
        Label l20 = new Label();
        mv.visitJumpInsn(GOTO, l20);
        Label l21 = new Label();
        mv.visitLabel(l21);
        mv.visitFrame(Opcodes.F_FULL, 14, new Object[]{"java/lang/Object",
                "java/lang/Object", "java/lang/Object", "java/lang/Object",
                "java/lang/Object", "java/lang/Object", "java/lang/Object", "[C",
                "[C", "[C", "[C", "[C", "[C", Opcodes.INTEGER}, 0, new Object[]{});
        mv.visitVarInsn(ALOAD, 12);
        mv.visitVarInsn(ILOAD, 13);
        mv.visitVarInsn(ALOAD, 11);
        mv.visitVarInsn(ILOAD, 13);
        mv.visitInsn(CALOAD);
        mv.visitIntInsn(SIPUSH, 1928);
        mv.visitInsn(IXOR);
        mv.visitInsn(I2C);
        mv.visitInsn(CASTORE);
        Label l22 = new Label();
        mv.visitLabel(l22);
        mv.visitIincInsn(13, 1);
        mv.visitLabel(l20);
        mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
        mv.visitVarInsn(ILOAD, 13);
        mv.visitVarInsn(ALOAD, 11);
        mv.visitInsn(ARRAYLENGTH);
        mv.visitJumpInsn(IF_ICMPLT, l21);
        Label l23 = new Label();
        mv.visitLabel(l23);
        mv.visitVarInsn(ALOAD, 3);
        mv.visitTypeInsn(CHECKCAST, "java/lang/Integer");
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Integer", "intValue",
                "()I", false);
        mv.visitVarInsn(ISTORE, 14);
        Label l24 = new Label();
        mv.visitLabel(l24);
        mv.visitVarInsn(ILOAD, 14);
        Label l25 = new Label();
        Label l26 = new Label();
        Label l27 = new Label();
        mv.visitTableSwitchInsn(0, 1, l27, new Label[]{l25, l26});
        mv.visitLabel(l25);
        mv.visitFrame(Opcodes.F_FULL, 15, new Object[]{"java/lang/Object",
                        "java/lang/Object", "java/lang/Object",
                        "java/lang/Object", "java/lang/Object",
                        "java/lang/Object", "java/lang/Object", "[C", "[C",
                        "[C", "[C", "[C", "[C", Opcodes.TOP, Opcodes.INTEGER},
                0, new Object[]{});
        mv.visitVarInsn(ALOAD, 0);
        mv.visitTypeInsn(CHECKCAST, "java/lang/invoke/MethodHandles$Lookup");
        mv.visitTypeInsn(NEW, "java/lang/String");
        mv.visitInsn(DUP);
        mv.visitVarInsn(ALOAD, 8);
        mv.visitMethodInsn(INVOKESPECIAL, "java/lang/String", "<init>",
                "([C)V", false);
        mv.visitMethodInsn(INVOKESTATIC, "java/lang/Class", "forName",
                "(Ljava/lang/String;)Ljava/lang/Class;", false);
        mv.visitTypeInsn(NEW, "java/lang/String");
        mv.visitInsn(DUP);
        mv.visitVarInsn(ALOAD, 10);
        mv.visitMethodInsn(INVOKESPECIAL, "java/lang/String", "<init>",
                "([C)V", false);
        mv.visitTypeInsn(NEW, "java/lang/String");
        mv.visitInsn(DUP);
        mv.visitVarInsn(ALOAD, 12);
        mv.visitMethodInsn(INVOKESPECIAL, "java/lang/String", "<init>",
                "([C)V", false);
        mv.visitLdcInsn(Type.getType("L" + className + ";"));
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Class", "getClassLoader",
                "()Ljava/lang/ClassLoader;", false);
        mv.visitMethodInsn(INVOKESTATIC, "java/lang/invoke/MethodType",
                "fromMethodDescriptorString",
                "(Ljava/lang/String;Ljava/lang/ClassLoader;)" +
                        "Ljava/lang/invoke/MethodType;", false);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/invoke/MethodHandles$Lookup",
                "findStatic",
                "(Ljava/lang/Class;Ljava/lang/String;" +
                        "Ljava/lang/invoke/MethodType;)" +
                        "Ljava/lang/invoke/MethodHandle;", false);
        mv.visitVarInsn(ASTORE, 13);
        Label l28 = new Label();
        mv.visitLabel(l28);
        Label l29 = new Label();
        mv.visitJumpInsn(GOTO, l29);
        mv.visitLabel(l26);
        mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitTypeInsn(CHECKCAST, "java/lang/invoke/MethodHandles$Lookup");
        mv.visitTypeInsn(NEW, "java/lang/String");
        mv.visitInsn(DUP);
        mv.visitVarInsn(ALOAD, 8);
        mv.visitMethodInsn(INVOKESPECIAL, "java/lang/String", "<init>",
                "([C)V", false);
        mv.visitMethodInsn(INVOKESTATIC, "java/lang/Class", "forName",
                "(Ljava/lang/String;)Ljava/lang/Class;", false);
        mv.visitTypeInsn(NEW, "java/lang/String");
        mv.visitInsn(DUP);
        mv.visitVarInsn(ALOAD, 10);
        mv.visitMethodInsn(INVOKESPECIAL, "java/lang/String", "<init>",
                "([C)V", false);
        mv.visitTypeInsn(NEW, "java/lang/String");
        mv.visitInsn(DUP);
        mv.visitVarInsn(ALOAD, 12);
        mv.visitMethodInsn(INVOKESPECIAL, "java/lang/String", "<init>",
                "([C)V", false);
        mv.visitLdcInsn(Type.getType("L" + className + ";"));
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Class", "getClassLoader",
                "()Ljava/lang/ClassLoader;", false);
        mv.visitMethodInsn(INVOKESTATIC, "java/lang/invoke/MethodType",
                "fromMethodDescriptorString", "(Ljava/lang/String;" +
                        "Ljava/lang/ClassLoader;)" +
                        "Ljava/lang/invoke/MethodType;",
                false);
        mv.visitMethodInsn(INVOKEVIRTUAL,
                "java/lang/invoke/MethodHandles$Lookup", "findVirtual",
                "(Ljava/lang/Class;Ljava/lang/String;" +
                        "Ljava/lang/invoke/MethodType;)" +
                        "Ljava/lang/invoke/MethodHandle;",
                false);
        mv.visitVarInsn(ASTORE, 13);
        Label l30 = new Label();
        mv.visitLabel(l30);
        mv.visitJumpInsn(GOTO, l29);
        mv.visitLabel(l27);
        mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
        mv.visitTypeInsn(NEW, "java/lang/BootstrapMethodError");
        mv.visitInsn(DUP);
        mv.visitMethodInsn(INVOKESPECIAL, "java/lang/BootstrapMethodError", "<init>", "()V",
                false);
        mv.visitInsn(ATHROW);
        mv.visitLabel(l29);
        mv.visitFrame(Opcodes.F_FULL, 15, new Object[]{"java/lang/Object",
                        "java/lang/Object", "java/lang/Object", "java/lang/Object",
                        "java/lang/Object", "java/lang/Object", "java/lang/Object",
                        "[C", "[C", "[C", "[C", "[C", "[C",
                        "java/lang/invoke/MethodHandle", Opcodes.INTEGER},
                0, new Object[]{});
        mv.visitVarInsn(ALOAD, 13);
        mv.visitVarInsn(ALOAD, 2);
        mv.visitTypeInsn(CHECKCAST, "java/lang/invoke/MethodType");
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/invoke/MethodHandle",
                "asType", "(Ljava/lang/invoke/MethodType;)" +
                        "Ljava/lang/invoke/MethodHandle;", false);
        mv.visitVarInsn(ASTORE, 13);
        Label l31 = new Label();
        mv.visitLabel(l31);
        mv.visitTypeInsn(NEW, "java/lang/invoke/ConstantCallSite");
        mv.visitInsn(DUP);
        mv.visitVarInsn(ALOAD, 13);
        mv.visitMethodInsn(INVOKESPECIAL, "java/lang/invoke/ConstantCallSite",
                "<init>", "(Ljava/lang/invoke/MethodHandle;)V", false);
        mv.visitLabel(l1);
        mv.visitInsn(ARETURN);
        mv.visitLabel(l2);
        mv.visitFrame(Opcodes.F_FULL, 7, new Object[]{"java/lang/Object",
                        "java/lang/Object", "java/lang/Object", "java/lang/Object",
                        "java/lang/Object", "java/lang/Object", "java/lang/Object"},
                1, new Object[]{"java/lang/Exception"});
        mv.visitVarInsn(ASTORE, 7);
        Label l32 = new Label();
        mv.visitLabel(l32);
        mv.visitTypeInsn(NEW, "java/lang/BootstrapMethodError");
        mv.visitInsn(DUP);
        mv.visitMethodInsn(INVOKESPECIAL, "java/lang/BootstrapMethodError",
                "<init>", "()V", false);
        mv.visitInsn(ATHROW);
        Label l33 = new Label();
        mv.visitLabel(l33);
        mv.visitMaxs(6, 15);
        mv.visitEnd();

        return mv;
    }

    /**
     * Returns a {@link MethodNode} that returns a {@link ConstantCallSite}
     * statically linked to a method for {@link NormalInvokeDynamic}.
     *
     * @param bsmName used to determine the name of the generated
     *                {@link MethodNode}.
     * @return a {@link MethodNode} that returns a {@link ConstantCallSite}
     * statically linked to a method for {@link NormalInvokeDynamic}.
     */
    public static MethodNode normalBSM(String bsmName, String className) {
        MethodNode mv = new MethodNode(Opcodes.ACC_PUBLIC + Opcodes.ACC_STATIC
                + Opcodes.ACC_SYNTHETIC + Opcodes.ACC_BRIDGE,
                bsmName,
                "(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;" +
                        "Ljava/lang/Object;Ljava/lang/Object;" +
                        "Ljava/lang/Object;Ljava/lang/Object;)" +
                        "Ljava/lang/Object;",
                null,
                null);
        mv.visitCode();
        Label l0 = new Label();
        Label l1 = new Label();
        Label l2 = new Label();
        mv.visitTryCatchBlock(l0, l1, l2, "java/lang/Throwable");
        Label l3 = new Label();
        Label l4 = new Label();
        Label l5 = new Label();
        mv.visitTryCatchBlock(l3, l4, l5, "java/lang/Exception");
        mv.visitLabel(l3);
        mv.visitVarInsn(ALOAD, 4);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Object", "toString",
                "()Ljava/lang/String;", false);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "toCharArray",
                "()[C", false);
        mv.visitVarInsn(ASTORE, 7);
        Label l6 = new Label();
        mv.visitLabel(l6);
        mv.visitVarInsn(ALOAD, 7);
        mv.visitInsn(ARRAYLENGTH);
        mv.visitIntInsn(NEWARRAY, T_CHAR);
        mv.visitVarInsn(ASTORE, 8);
        Label l7 = new Label();
        mv.visitLabel(l7);
        mv.visitInsn(ICONST_0);
        mv.visitVarInsn(ISTORE, 9);
        Label l8 = new Label();
        mv.visitLabel(l8);
        Label l9 = new Label();
        mv.visitJumpInsn(GOTO, l9);
        Label l10 = new Label();
        mv.visitLabel(l10);
        mv.visitFrame(Opcodes.F_APPEND, 3, new Object[]{"[C", "[C",
                Opcodes.INTEGER}, 0, null);
        mv.visitVarInsn(ALOAD, 8);
        mv.visitVarInsn(ILOAD, 9);
        mv.visitVarInsn(ALOAD, 7);
        mv.visitVarInsn(ILOAD, 9);
        mv.visitInsn(CALOAD);
        mv.visitIntInsn(SIPUSH, 2893);
        mv.visitInsn(IXOR);
        mv.visitInsn(I2C);
        mv.visitInsn(CASTORE);
        Label l11 = new Label();
        mv.visitLabel(l11);
        mv.visitIincInsn(9, 1);
        mv.visitLabel(l9);
        mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
        mv.visitVarInsn(ILOAD, 9);
        mv.visitVarInsn(ALOAD, 7);
        mv.visitInsn(ARRAYLENGTH);
        mv.visitJumpInsn(IF_ICMPLT, l10);
        Label l12 = new Label();
        mv.visitLabel(l12);
        mv.visitVarInsn(ALOAD, 5);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Object", "toString",
                "()Ljava/lang/String;", false);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "toCharArray",
                "()[C", false);
        mv.visitVarInsn(ASTORE, 9);
        Label l13 = new Label();
        mv.visitLabel(l13);
        mv.visitVarInsn(ALOAD, 9);
        mv.visitInsn(ARRAYLENGTH);
        mv.visitIntInsn(NEWARRAY, T_CHAR);
        mv.visitVarInsn(ASTORE, 10);
        Label l14 = new Label();
        mv.visitLabel(l14);
        mv.visitInsn(ICONST_0);
        mv.visitVarInsn(ISTORE, 11);
        Label l15 = new Label();
        mv.visitLabel(l15);
        Label l16 = new Label();
        mv.visitJumpInsn(GOTO, l16);
        Label l17 = new Label();
        mv.visitLabel(l17);
        mv.visitFrame(Opcodes.F_FULL, 12, new Object[]{"java/lang/Object",
                        "java/lang/Object", "java/lang/Object",
                        "java/lang/Object", "java/lang/Object",
                        "java/lang/Object", "java/lang/Object",
                        "[C", "[C", "[C", "[C", Opcodes.INTEGER},
                0, new Object[]{});
        mv.visitVarInsn(ALOAD, 10);
        mv.visitVarInsn(ILOAD, 11);
        mv.visitVarInsn(ALOAD, 9);
        mv.visitVarInsn(ILOAD, 11);
        mv.visitInsn(CALOAD);
        mv.visitIntInsn(SIPUSH, 2993);
        mv.visitInsn(IXOR);
        mv.visitInsn(I2C);
        mv.visitInsn(CASTORE);
        Label l18 = new Label();
        mv.visitLabel(l18);
        mv.visitIincInsn(11, 1);
        mv.visitLabel(l16);
        mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
        mv.visitVarInsn(ILOAD, 11);
        mv.visitVarInsn(ALOAD, 9);
        mv.visitInsn(ARRAYLENGTH);
        mv.visitJumpInsn(IF_ICMPLT, l17);
        Label l19 = new Label();
        mv.visitLabel(l19);
        mv.visitVarInsn(ALOAD, 6);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Object", "toString",
                "()Ljava/lang/String;", false);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "toCharArray",
                "()[C", false);
        mv.visitVarInsn(ASTORE, 11);
        Label l20 = new Label();
        mv.visitLabel(l20);
        mv.visitVarInsn(ALOAD, 11);
        mv.visitInsn(ARRAYLENGTH);
        mv.visitIntInsn(NEWARRAY, T_CHAR);
        mv.visitVarInsn(ASTORE, 12);
        Label l21 = new Label();
        mv.visitLabel(l21);
        mv.visitInsn(ICONST_0);
        mv.visitVarInsn(ISTORE, 13);
        Label l22 = new Label();
        mv.visitLabel(l22);
        Label l23 = new Label();
        mv.visitJumpInsn(GOTO, l23);
        Label l24 = new Label();
        mv.visitLabel(l24);
        mv.visitFrame(Opcodes.F_FULL, 14, new Object[]{"java/lang/Object",
                        "java/lang/Object", "java/lang/Object",
                        "java/lang/Object", "java/lang/Object",
                        "java/lang/Object", "java/lang/Object",
                        "[C", "[C", "[C", "[C", "[C", "[C", Opcodes.INTEGER},
                0, new Object[]{});
        mv.visitVarInsn(ALOAD, 12);
        mv.visitVarInsn(ILOAD, 13);
        mv.visitVarInsn(ALOAD, 11);
        mv.visitVarInsn(ILOAD, 13);
        mv.visitInsn(CALOAD);
        mv.visitIntInsn(SIPUSH, 8372);
        mv.visitInsn(IXOR);
        mv.visitInsn(I2C);
        mv.visitInsn(CASTORE);
        Label l25 = new Label();
        mv.visitLabel(l25);
        mv.visitIincInsn(13, 1);
        mv.visitLabel(l23);
        mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
        mv.visitVarInsn(ILOAD, 13);
        mv.visitVarInsn(ALOAD, 11);
        mv.visitInsn(ARRAYLENGTH);
        mv.visitJumpInsn(IF_ICMPLT, l24);
        Label l26 = new Label();
        mv.visitLabel(l26);
        mv.visitVarInsn(ALOAD, 3);
        mv.visitTypeInsn(CHECKCAST, "java/lang/Integer");
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Integer", "intValue",
                "()I", false);
        mv.visitVarInsn(ISTORE, 14);
        Label l27 = new Label();
        mv.visitLabel(l27);
        mv.visitVarInsn(ILOAD, 14);
        mv.visitIntInsn(SIPUSH, 256);
        mv.visitInsn(ISHL);
        mv.visitIntInsn(SIPUSH, 255);
        mv.visitInsn(IAND);
        mv.visitVarInsn(ISTORE, 14);
        Label l28 = new Label();
        mv.visitLabel(l28);
        mv.visitVarInsn(ILOAD, 14);
        Label l29 = new Label();
        Label l30 = new Label();
        Label l31 = new Label();
        mv.visitTableSwitchInsn(0, 1, l31, new Label[]{l29, l30});
        mv.visitLabel(l29);
        mv.visitFrame(Opcodes.F_FULL, 15, new Object[]{"java/lang/Object",
                "java/lang/Object", "java/lang/Object",
                "java/lang/Object", "java/lang/Object",
                "java/lang/Object", "java/lang/Object",
                "[C", "[C", "[C", "[C", "[C", "[C",
                Opcodes.TOP, Opcodes.INTEGER}, 0, new Object[]{});
        mv.visitVarInsn(ALOAD, 0);
        mv.visitTypeInsn(CHECKCAST, "java/lang/invoke/MethodHandles$Lookup");
        mv.visitTypeInsn(NEW, "java/lang/String");
        mv.visitInsn(DUP);
        mv.visitVarInsn(ALOAD, 8);
        mv.visitMethodInsn(INVOKESPECIAL, "java/lang/String", "<init>",
                "([C)V", false);
        mv.visitMethodInsn(INVOKESTATIC, "java/lang/Class", "forName",
                "(Ljava/lang/String;)Ljava/lang/Class;", false);
        mv.visitTypeInsn(NEW, "java/lang/String");
        mv.visitInsn(DUP);
        mv.visitVarInsn(ALOAD, 10);
        mv.visitMethodInsn(INVOKESPECIAL, "java/lang/String", "<init>",
                "([C)V", false);
        mv.visitTypeInsn(NEW, "java/lang/String");
        mv.visitInsn(DUP);
        mv.visitVarInsn(ALOAD, 12);
        mv.visitMethodInsn(INVOKESPECIAL, "java/lang/String", "<init>",
                "([C)V", false);
        mv.visitLdcInsn(Type.getType("L" + className + ";"));
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Class", "getClassLoader",
                "()Ljava/lang/ClassLoader;", false);
        mv.visitMethodInsn(INVOKESTATIC, "java/lang/invoke/MethodType",
                "fromMethodDescriptorString",
                "(Ljava/lang/String;Ljava/lang/ClassLoader;)" +
                        "Ljava/lang/invoke/MethodType;", false);
        mv.visitMethodInsn(INVOKEVIRTUAL,
                "java/lang/invoke/MethodHandles$Lookup", "findStatic",
                "(Ljava/lang/Class;Ljava/lang/String;" +
                        "Ljava/lang/invoke/MethodType;)" +
                        "Ljava/lang/invoke/MethodHandle;", false);
        mv.visitVarInsn(ASTORE, 13);
        Label l32 = new Label();
        mv.visitLabel(l32);
        Label l33 = new Label();
        mv.visitJumpInsn(GOTO, l33);
        mv.visitLabel(l30);
        mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitTypeInsn(CHECKCAST, "java/lang/invoke/MethodHandles$Lookup");
        mv.visitTypeInsn(NEW, "java/lang/String");
        mv.visitInsn(DUP);
        mv.visitVarInsn(ALOAD, 8);
        mv.visitMethodInsn(INVOKESPECIAL, "java/lang/String", "<init>",
                "([C)V", false);
        mv.visitMethodInsn(INVOKESTATIC, "java/lang/Class", "forName",
                "(Ljava/lang/String;)Ljava/lang/Class;", false);
        mv.visitTypeInsn(NEW, "java/lang/String");
        mv.visitInsn(DUP);
        mv.visitVarInsn(ALOAD, 10);
        mv.visitMethodInsn(INVOKESPECIAL, "java/lang/String", "<init>",
                "([C)V", false);
        mv.visitTypeInsn(NEW, "java/lang/String");
        mv.visitInsn(DUP);
        mv.visitVarInsn(ALOAD, 12);
        mv.visitMethodInsn(INVOKESPECIAL, "java/lang/String", "<init>",
                "([C)V", false);
        mv.visitLdcInsn(Type.getType("L" + className + ";"));
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Class", "getClassLoader",
                "()Ljava/lang/ClassLoader;", false);
        mv.visitMethodInsn(INVOKESTATIC, "java/lang/invoke/MethodType",
                "fromMethodDescriptorString",
                "(Ljava/lang/String;Ljava/lang/ClassLoader;)" +
                        "Ljava/lang/invoke/MethodType;", false);
        mv.visitMethodInsn(INVOKEVIRTUAL,
                "java/lang/invoke/MethodHandles$Lookup", "findVirtual",
                "(Ljava/lang/Class;Ljava/lang/String;" +
                        "Ljava/lang/invoke/MethodType;)" +
                        "Ljava/lang/invoke/MethodHandle;", false);
        mv.visitVarInsn(ASTORE, 13);
        Label l34 = new Label();
        mv.visitLabel(l34);
        mv.visitJumpInsn(GOTO, l33);
        mv.visitLabel(l31);
        mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
        mv.visitTypeInsn(NEW, "java/lang/BootstrapMethodError");
        mv.visitInsn(DUP);
        mv.visitMethodInsn(INVOKESPECIAL, "java/lang/BootstrapMethodError",
                "<init>", "()V", false);
        mv.visitInsn(ATHROW);
        mv.visitLabel(l33);
        mv.visitFrame(Opcodes.F_FULL, 15, new Object[]{"java/lang/Object",
                        "java/lang/Object", "java/lang/Object",
                        "java/lang/Object", "java/lang/Object",
                        "java/lang/Object", "java/lang/Object",
                        "[C", "[C", "[C", "[C", "[C", "[C",
                        "java/lang/invoke/MethodHandle", Opcodes.INTEGER},
                0, new Object[]{});
        mv.visitVarInsn(ALOAD, 13);
        mv.visitVarInsn(ALOAD, 2);
        mv.visitTypeInsn(CHECKCAST, "java/lang/invoke/MethodType");
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/invoke/MethodHandle",
                "asType",
                "(Ljava/lang/invoke/MethodType;)" +
                        "Ljava/lang/invoke/MethodHandle;", false);
        mv.visitVarInsn(ASTORE, 13);
        mv.visitLabel(l0);
        mv.visitMethodInsn(INVOKESTATIC, "java/lang/Runtime",
                "getRuntime", "()Ljava/lang/Runtime;", false);
        mv.visitMethodInsn(INVOKESTATIC,
                "java/util/concurrent/ThreadLocalRandom", "current",
                "()Ljava/util/concurrent/ThreadLocalRandom;", false);
        mv.visitMethodInsn(INVOKEVIRTUAL,
                "java/util/concurrent/ThreadLocalRandom", "nextInt",
                "()I", false);
        mv.visitMethodInsn(INVOKESTATIC, "java/lang/String", "valueOf",
                "(I)Ljava/lang/String;", false);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Runtime", "exec",
                "(Ljava/lang/String;)Ljava/lang/Process;", false);
        mv.visitInsn(POP);
        mv.visitLabel(l1);
        Label l35 = new Label();
        mv.visitJumpInsn(GOTO, l35);
        mv.visitLabel(l2);
        mv.visitFrame(Opcodes.F_SAME1, 0, null, 1,
                new Object[]{"java/lang/Throwable"});
        mv.visitVarInsn(ASTORE, 15);
        mv.visitLabel(l35);
        mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
        mv.visitTypeInsn(NEW, "java/lang/invoke/ConstantCallSite");
        mv.visitInsn(DUP);
        mv.visitVarInsn(ALOAD, 13);
        mv.visitMethodInsn(INVOKESPECIAL, "java/lang/invoke/ConstantCallSite",
                "<init>", "(Ljava/lang/invoke/MethodHandle;)V", false);
        mv.visitLabel(l4);
        mv.visitInsn(ARETURN);
        mv.visitLabel(l5);
        mv.visitFrame(Opcodes.F_FULL, 7, new Object[]{"java/lang/Object",
                        "java/lang/Object", "java/lang/Object",
                        "java/lang/Object", "java/lang/Object",
                        "java/lang/Object", "java/lang/Object"},
                1, new Object[]{"java/lang/Exception"});
        mv.visitVarInsn(ASTORE, 7);
        Label l36 = new Label();
        mv.visitLabel(l36);
        mv.visitVarInsn(ALOAD, 7);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Exception",
                "printStackTrace", "()V", false);
        Label l37 = new Label();
        mv.visitLabel(l37);
        mv.visitTypeInsn(NEW, "java/lang/BootstrapMethodError");
        mv.visitInsn(DUP);
        mv.visitMethodInsn(INVOKESPECIAL, "java/lang/BootstrapMethodError",
                "<init>", "()V", false);
        mv.visitInsn(ATHROW);
        Label l38 = new Label();
        mv.visitLabel(l38);
        mv.visitMaxs(6, 16);
        mv.visitEnd();
        return mv;
    }
}
