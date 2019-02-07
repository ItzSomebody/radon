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

package me.itzsomebody.radon.transformers.obfuscators.invokedynamic;

import java.util.concurrent.atomic.AtomicInteger;
import me.itzsomebody.radon.asm.ClassWrapper;
import me.itzsomebody.radon.utils.LoggerUtils;
import me.itzsomebody.radon.utils.StringUtils;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InvokeDynamicInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;

/**
 * Replaces invokestatic, invokevirtual and invokeinterface with invokedynamic instructions.
 *
 * @author ItzSomebody
 */
public class LightInvokeDynamic extends InvokeDynamic {
    @Override
    public void transform() {
        AtomicInteger counter = new AtomicInteger();
        String className = StringUtils.randomClassName(getClasses().keySet());
        String bsmName = randomString(4);
        Handle bsmHandle = new Handle(Opcodes.H_INVOKESTATIC, className, bsmName,
                "(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;" +
                        "Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", false);
        this.getClassWrappers().stream().filter(classWrapper ->
                !"java/lang/Enum".equals(classWrapper.classNode.superName) && !excluded(classWrapper)
                        && classWrapper.classNode.version >= V1_7).forEach(classWrapper ->
                classWrapper.methods.stream().filter(methodWrapper -> !excluded(methodWrapper)
                        && hasInstructions(methodWrapper.methodNode)).forEach(methodWrapper -> {
                    MethodNode methodNode = methodWrapper.methodNode;

                    for (AbstractInsnNode insn : methodNode.instructions.toArray()) {
                        if (insn instanceof MethodInsnNode && insn.getOpcode() != INVOKESPECIAL) {
                            MethodInsnNode methodInsnNode = (MethodInsnNode) insn;
                            boolean isStatic = (methodInsnNode.getOpcode() == Opcodes.INVOKESTATIC);

                            String newSig = isStatic ? methodInsnNode.desc : methodInsnNode.desc.replace("(",
                                    "(Ljava/lang/Object;");
                            Type returnType = Type.getReturnType(methodInsnNode.desc);
                            int opcode = (isStatic) ? 0 : 1;

                            InvokeDynamicInsnNode indy = new InvokeDynamicInsnNode(StringUtils.randomSpacesString(4),
                                    newSig,
                                    bsmHandle,
                                    opcode,
                                    encrypt(methodInsnNode.owner.replace("/", "."), 1029),
                                    encrypt(methodInsnNode.name, 2038),
                                    encrypt(methodInsnNode.desc, 1928));
                            methodNode.instructions.set(insn, indy);
                            if (returnType.getSort() == Type.ARRAY) {
                                methodNode.instructions.insert(indy, new TypeInsnNode(CHECKCAST,
                                        returnType.getInternalName()));
                            }
                            counter.incrementAndGet();
                        }
                    }
                })
        );

        ClassNode bsmHost = new ClassNode();
        bsmHost.name = className;
        bsmHost.version = V1_7;
        bsmHost.methods.add(createBootstrap(bsmName, bsmHost.name));
        bsmHost.superName = "java/lang/Object";
        bsmHost.access = ACC_PUBLIC | ACC_SUPER;
        getClasses().put(className, new ClassWrapper(bsmHost, false));
        LoggerUtils.stdOut(String.format("Replaced %d method invocations with invokedynamics.", counter.get()));
    }

    private static String encrypt(String msg, int key) {
        char[] encClassNameChars = msg.toCharArray();
        char[] classNameChars = new char[encClassNameChars.length];
        for (int i = 0; i < encClassNameChars.length; i++) {
            classNameChars[i] = (char) (encClassNameChars[i] ^ key);
        }

        return new String(classNameChars);
    }

    @Override
    public String getName() {
        return "Light invokedynamic";
    }

    private static MethodNode createBootstrap(String bsmName, String className) {
        MethodNode mv = new MethodNode(ACC_PUBLIC + ACC_STATIC + ACC_SYNTHETIC + ACC_BRIDGE, bsmName,
                "(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;" +
                        "Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", null, null);
        mv.visitCode();
        Label l0 = new Label();
        Label l1 = new Label();
        Label l2 = new Label();
        mv.visitTryCatchBlock(l0, l1, l2, "java/lang/Exception");
        mv.visitLabel(l0);
        mv.visitVarInsn(ALOAD, 4);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Object", "toString", "()Ljava/lang/String;", false);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "toCharArray", "()[C", false);
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
        mv.visitFrame(F_APPEND, 3, new Object[]{"[C", "[C", INTEGER}, 0, null);
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
        mv.visitFrame(F_SAME, 0, null, 0, null);
        mv.visitVarInsn(ILOAD, 9);
        mv.visitVarInsn(ALOAD, 7);
        mv.visitInsn(ARRAYLENGTH);
        mv.visitJumpInsn(IF_ICMPLT, l7);
        Label l9 = new Label();
        mv.visitLabel(l9);
        mv.visitVarInsn(ALOAD, 5);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Object", "toString", "()Ljava/lang/String;", false);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "toCharArray", "()[C", false);
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
        mv.visitFrame(F_FULL, 12, new Object[]{"java/lang/Object", "java/lang/Object", "java/lang/Object",
                "java/lang/Object", "java/lang/Object", "java/lang/Object", "java/lang/Object", "[C", "[C", "[C", "[C",
                INTEGER}, 0, new Object[]{});
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
        mv.visitFrame(F_SAME, 0, null, 0, null);
        mv.visitVarInsn(ILOAD, 11);
        mv.visitVarInsn(ALOAD, 9);
        mv.visitInsn(ARRAYLENGTH);
        mv.visitJumpInsn(IF_ICMPLT, l14);
        Label l16 = new Label();
        mv.visitLabel(l16);
        mv.visitVarInsn(ALOAD, 6);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Object", "toString", "()Ljava/lang/String;", false);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "toCharArray", "()[C", false);
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
        mv.visitFrame(F_FULL, 14, new Object[]{"java/lang/Object", "java/lang/Object", "java/lang/Object",
                "java/lang/Object", "java/lang/Object", "java/lang/Object", "java/lang/Object", "[C", "[C", "[C", "[C",
                "[C", "[C", INTEGER}, 0, new Object[]{});
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
        mv.visitFrame(F_SAME, 0, null, 0, null);
        mv.visitVarInsn(ILOAD, 13);
        mv.visitVarInsn(ALOAD, 11);
        mv.visitInsn(ARRAYLENGTH);
        mv.visitJumpInsn(IF_ICMPLT, l21);
        Label l23 = new Label();
        mv.visitLabel(l23);
        mv.visitVarInsn(ALOAD, 3);
        mv.visitTypeInsn(CHECKCAST, "java/lang/Integer");
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Integer", "intValue", "()I", false);
        mv.visitVarInsn(ISTORE, 14);
        Label l24 = new Label();
        mv.visitLabel(l24);
        mv.visitVarInsn(ILOAD, 14);
        Label l25 = new Label();
        Label l26 = new Label();
        Label l27 = new Label();
        mv.visitTableSwitchInsn(0, 1, l27, l25, l26);
        mv.visitLabel(l25);
        mv.visitFrame(F_FULL, 15, new Object[]{"java/lang/Object", "java/lang/Object", "java/lang/Object",
                "java/lang/Object", "java/lang/Object", "java/lang/Object", "java/lang/Object", "[C", "[C", "[C", "[C",
                "[C", "[C", TOP, INTEGER}, 0, new Object[]{});
        mv.visitVarInsn(ALOAD, 0);
        mv.visitTypeInsn(CHECKCAST, "java/lang/invoke/MethodHandles$Lookup");
        mv.visitTypeInsn(NEW, "java/lang/String");
        mv.visitInsn(DUP);
        mv.visitVarInsn(ALOAD, 8);
        mv.visitMethodInsn(INVOKESPECIAL, "java/lang/String", "<init>", "([C)V", false);
        mv.visitMethodInsn(INVOKESTATIC, "java/lang/Class", "forName", "(Ljava/lang/String;)Ljava/lang/Class;", false);
        mv.visitTypeInsn(NEW, "java/lang/String");
        mv.visitInsn(DUP);
        mv.visitVarInsn(ALOAD, 10);
        mv.visitMethodInsn(INVOKESPECIAL, "java/lang/String", "<init>", "([C)V", false);
        mv.visitTypeInsn(NEW, "java/lang/String");
        mv.visitInsn(DUP);
        mv.visitVarInsn(ALOAD, 12);
        mv.visitMethodInsn(INVOKESPECIAL, "java/lang/String", "<init>", "([C)V", false);
        mv.visitLdcInsn(Type.getType("L" + className + ";"));
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Class", "getClassLoader", "()Ljava/lang/ClassLoader;", false);
        mv.visitMethodInsn(INVOKESTATIC, "java/lang/invoke/MethodType", "fromMethodDescriptorString",
                "(Ljava/lang/String;Ljava/lang/ClassLoader;)Ljava/lang/invoke/MethodType;", false);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/invoke/MethodHandles$Lookup", "findStatic",
                "(Ljava/lang/Class;Ljava/lang/String;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/MethodHandle;",
                false);
        mv.visitVarInsn(ASTORE, 13);
        Label l28 = new Label();
        mv.visitLabel(l28);
        Label l29 = new Label();
        mv.visitJumpInsn(GOTO, l29);
        mv.visitLabel(l26);
        mv.visitFrame(F_SAME, 0, null, 0, null);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitTypeInsn(CHECKCAST, "java/lang/invoke/MethodHandles$Lookup");
        mv.visitTypeInsn(NEW, "java/lang/String");
        mv.visitInsn(DUP);
        mv.visitVarInsn(ALOAD, 8);
        mv.visitMethodInsn(INVOKESPECIAL, "java/lang/String", "<init>", "([C)V", false);
        mv.visitMethodInsn(INVOKESTATIC, "java/lang/Class", "forName", "(Ljava/lang/String;)Ljava/lang/Class;", false);
        mv.visitTypeInsn(NEW, "java/lang/String");
        mv.visitInsn(DUP);
        mv.visitVarInsn(ALOAD, 10);
        mv.visitMethodInsn(INVOKESPECIAL, "java/lang/String", "<init>", "([C)V", false);
        mv.visitTypeInsn(NEW, "java/lang/String");
        mv.visitInsn(DUP);
        mv.visitVarInsn(ALOAD, 12);
        mv.visitMethodInsn(INVOKESPECIAL, "java/lang/String", "<init>", "([C)V", false);
        mv.visitLdcInsn(Type.getType("L" + className + ";"));
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Class", "getClassLoader", "()Ljava/lang/ClassLoader;", false);
        mv.visitMethodInsn(INVOKESTATIC, "java/lang/invoke/MethodType", "fromMethodDescriptorString",
                "(Ljava/lang/String;Ljava/lang/ClassLoader;)Ljava/lang/invoke/MethodType;", false);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/invoke/MethodHandles$Lookup", "findVirtual",
                "(Ljava/lang/Class;Ljava/lang/String;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/MethodHandle;",
                false);
        mv.visitVarInsn(ASTORE, 13);
        Label l30 = new Label();
        mv.visitLabel(l30);
        mv.visitJumpInsn(GOTO, l29);
        mv.visitLabel(l27);
        mv.visitFrame(F_SAME, 0, null, 0, null);
        mv.visitTypeInsn(NEW, "java/lang/BootstrapMethodError");
        mv.visitInsn(DUP);
        mv.visitMethodInsn(INVOKESPECIAL, "java/lang/BootstrapMethodError", "<init>", "()V", false);
        mv.visitInsn(ATHROW);
        mv.visitLabel(l29);
        mv.visitFrame(F_FULL, 15, new Object[]{"java/lang/Object", "java/lang/Object", "java/lang/Object",
                "java/lang/Object", "java/lang/Object", "java/lang/Object", "java/lang/Object", "[C", "[C", "[C", "[C",
                "[C", "[C", "java/lang/invoke/MethodHandle", INTEGER}, 0, new Object[]{});
        mv.visitVarInsn(ALOAD, 13);
        mv.visitVarInsn(ALOAD, 2);
        mv.visitTypeInsn(CHECKCAST, "java/lang/invoke/MethodType");
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/invoke/MethodHandle", "asType",
                "(Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/MethodHandle;", false);
        mv.visitVarInsn(ASTORE, 13);
        Label l31 = new Label();
        mv.visitLabel(l31);
        mv.visitTypeInsn(NEW, "java/lang/invoke/ConstantCallSite");
        mv.visitInsn(DUP);
        mv.visitVarInsn(ALOAD, 13);
        mv.visitMethodInsn(INVOKESPECIAL, "java/lang/invoke/ConstantCallSite", "<init>",
                "(Ljava/lang/invoke/MethodHandle;)V", false);
        mv.visitLabel(l1);
        mv.visitInsn(ARETURN);
        mv.visitLabel(l2);
        mv.visitFrame(F_FULL, 7, new Object[]{"java/lang/Object", "java/lang/Object", "java/lang/Object",
                        "java/lang/Object", "java/lang/Object", "java/lang/Object", "java/lang/Object"},
                1, new Object[]{"java/lang/Exception"});
        mv.visitVarInsn(ASTORE, 7);
        Label l32 = new Label();
        mv.visitLabel(l32);
        mv.visitTypeInsn(NEW, "java/lang/BootstrapMethodError");
        mv.visitInsn(DUP);
        mv.visitMethodInsn(INVOKESPECIAL, "java/lang/BootstrapMethodError", "<init>", "()V", false);
        mv.visitInsn(ATHROW);
        Label l33 = new Label();
        mv.visitLabel(l33);
        mv.visitMaxs(6, 15);
        mv.visitEnd();

        return mv;
    }
}
