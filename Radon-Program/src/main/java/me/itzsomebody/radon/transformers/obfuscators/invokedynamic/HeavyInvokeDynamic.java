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

import java.lang.reflect.Modifier;
import java.util.concurrent.atomic.AtomicInteger;
import me.itzsomebody.radon.asm.ClassWrapper;
import me.itzsomebody.radon.asm.FieldWrapper;
import me.itzsomebody.radon.exceptions.MissingClassException;
import me.itzsomebody.radon.utils.LoggerUtils;
import me.itzsomebody.radon.utils.StringUtils;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InvokeDynamicInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;

public class HeavyInvokeDynamic extends InvokeDynamic {
    @Override
    public void transform() {
        AtomicInteger counter = new AtomicInteger();
        MemberNames memberNames = new MemberNames();
        Handle bsmHandle = new Handle(H_INVOKESTATIC, memberNames.className, memberNames.bootstrapMethodName, "(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", false);
        this.getClassWrappers().parallelStream().filter(classWrapper -> !excluded(classWrapper) && classWrapper.classNode.version >= V1_7).forEach(classWrapper -> {
            ClassNode classNode = classWrapper.classNode;

            classWrapper.methods.parallelStream().filter(methodWrapper -> !excluded(methodWrapper) && hasInstructions(methodWrapper.methodNode)).forEach(methodWrapper -> {
                MethodNode methodNode = methodWrapper.methodNode;

                for (AbstractInsnNode insn : methodNode.instructions.toArray()) {
                    if (insn instanceof MethodInsnNode) {
                        MethodInsnNode methodInsnNode = (MethodInsnNode) insn;
                        if (!methodInsnNode.name.equals("<init>")) {
                            boolean isStatic = (methodInsnNode.getOpcode() == INVOKESTATIC);
                            String newSig = isStatic ? methodInsnNode.desc : methodInsnNode.desc.replace("(", "(Ljava/lang/Object;");
                            Type returnType = Type.getReturnType(methodInsnNode.desc);
                            Type[] args = Type.getArgumentTypes(newSig);
                            for (int i = 0; i < args.length; i++) {
                                Type arg = args[i];
                                if (arg.getSort() == Type.OBJECT) {
                                    args[i] = Type.getType("Ljava/lang/Object;");
                                }
                            }
                            newSig = Type.getMethodDescriptor(returnType, args);
                            StringBuilder sb = new StringBuilder();
                            sb.append(methodInsnNode.owner.replace("/", ".")).append("<>").append(methodInsnNode.name).append("<>");

                            switch (insn.getOpcode()) {
                                case INVOKEINTERFACE:
                                case INVOKEVIRTUAL: {
                                    sb.append("1<>").append(methodInsnNode.desc);
                                    break;
                                }
                                case INVOKESPECIAL: {
                                    sb.append("2<>").append(methodInsnNode.desc).append("<>").append(classNode.name.replace("/", "."));
                                    break;
                                }
                                case INVOKESTATIC: {
                                    sb.append("0<>").append(methodInsnNode.desc);
                                    break;
                                }
                            }

                            InvokeDynamicInsnNode indy = new InvokeDynamicInsnNode(
                                encrypt(sb.toString(), memberNames),
                                newSig,
                                bsmHandle
                            );

                            methodNode.instructions.set(insn, indy);
                            if (returnType.getSort() == Type.ARRAY) {
                                methodNode.instructions.insert(indy, new TypeInsnNode(CHECKCAST, returnType.getInternalName()));
                            }
                            counter.incrementAndGet();
                        }
                    } else if (insn instanceof FieldInsnNode) {
                        if (!methodNode.name.equals("<init>")) {
                            FieldInsnNode fieldInsnNode = (FieldInsnNode) insn;

                            ClassWrapper cw = getClassPath().get(fieldInsnNode.owner);
                            if (cw == null) {
                                throw new MissingClassException(fieldInsnNode.owner + " does not exist in classpath");
                            }
                            FieldWrapper fw = cw.fields.stream().filter(fieldWrapper -> fieldWrapper.fieldNode.name.equals(fieldInsnNode.name) && fieldWrapper.fieldNode.desc.equals(fieldInsnNode.desc)).findFirst().orElse(null);
                            if (fw != null && Modifier.isFinal(fw.fieldNode.access)) {
                                continue;
                            }

                            boolean isStatic = (fieldInsnNode.getOpcode() == GETSTATIC || fieldInsnNode.getOpcode() == PUTSTATIC);
                            boolean isSetter = (fieldInsnNode.getOpcode() == PUTFIELD || fieldInsnNode.getOpcode() == PUTSTATIC);
                            String newSig = (isSetter) ? "(" + fieldInsnNode.desc + ")V" : "()" + fieldInsnNode.desc;
                            if (!isStatic)
                                newSig = newSig.replace("(", "(Ljava/lang/Object;");

                            StringBuilder sb = new StringBuilder();
                            sb.append(fieldInsnNode.owner.replace("/", ".")).append("<>").append(fieldInsnNode.name).append("<>");

                            switch (insn.getOpcode()) {
                                case GETSTATIC: {
                                    sb.append("3");
                                    break;
                                }
                                case GETFIELD: {
                                    sb.append("4");
                                    break;
                                }
                                case PUTSTATIC: {
                                    sb.append("5");
                                    break;
                                }
                                case PUTFIELD: {
                                    sb.append("6");
                                    break;
                                }
                            }

                            InvokeDynamicInsnNode indy = new InvokeDynamicInsnNode(
                                encrypt(sb.toString(), memberNames),
                                newSig,
                                bsmHandle
                            );

                            methodNode.instructions.set(insn, indy);
                            counter.incrementAndGet();
                        }
                    }
                }
            });
        });

        ClassNode decryptor = createBootstrap(memberNames);
        this.getClasses().put(decryptor.name, new ClassWrapper(decryptor, false));
        LoggerUtils.stdOut(String.format("Hid %d field and/or method accesses with invokedynamics.", counter.get()));
    }

    @Override
    public String getName() {
        return "Heavy invokedynamic";
    }

    private static String encrypt(String msg, MemberNames memberNames) {
        char[] chars = msg.toCharArray();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < chars.length; i++) {
            switch (i % 4) {
                case 0: {
                    sb.append((char) (chars[i] ^ memberNames.className.replace("/", ".").hashCode()));
                    break;
                }
                case 1: {
                    sb.append((char) (chars[i] ^ memberNames.bootstrapMethodName.hashCode()));
                    break;
                }
                case 2: {
                    sb.append((char) (chars[i] ^ memberNames.className.replace("/", ".").hashCode()));
                    break;
                }
                case 3: {
                    sb.append((char) (chars[i] ^ memberNames.decryptorMethodName.hashCode()));
                    break;
                }
            }
        }

        return sb.toString();
    }

    private static ClassNode createBootstrap(MemberNames memberNames) {
        ClassNode cw = new ClassNode();
        MethodVisitor mv;

        cw.visit(V1_5, ACC_PUBLIC + ACC_SUPER, memberNames.className, null, "java/lang/Object", null);

        cw.visitInnerClass("java/lang/invoke/MethodHandles$Lookup", "java/lang/invoke/MethodHandles", "Lookup", ACC_PUBLIC + ACC_FINAL + ACC_STATIC);

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
            mv = cw.visitMethod(ACC_PRIVATE + ACC_STATIC, memberNames.decryptorMethodName, "(Ljava/lang/String;)Ljava/lang/String;", null, null);
            mv.visitCode();
            Label l0 = new Label();
            mv.visitLabel(l0);
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Thread", "currentThread", "()Ljava/lang/Thread;", false);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Thread", "getStackTrace", "()[Ljava/lang/StackTraceElement;", false);
            mv.visitVarInsn(ASTORE, 1);
            Label l1 = new Label();
            mv.visitLabel(l1);
            mv.visitTypeInsn(NEW, "java/lang/StringBuilder");
            mv.visitInsn(DUP);
            mv.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "()V", false);
            mv.visitVarInsn(ASTORE, 2);
            Label l2 = new Label();
            mv.visitLabel(l2);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "toCharArray", "()[C", false);
            mv.visitVarInsn(ASTORE, 3);
            Label l3 = new Label();
            mv.visitLabel(l3);
            mv.visitInsn(ICONST_0);
            mv.visitVarInsn(ISTORE, 4);
            Label l4 = new Label();
            mv.visitLabel(l4);
            Label l5 = new Label();
            mv.visitJumpInsn(GOTO, l5);
            Label l6 = new Label();
            mv.visitLabel(l6);
            mv.visitVarInsn(ILOAD, 4);
            mv.visitInsn(ICONST_4);
            mv.visitInsn(IREM);
            Label l7 = new Label();
            Label l8 = new Label();
            Label l9 = new Label();
            Label l10 = new Label();
            Label l11 = new Label();
            mv.visitTableSwitchInsn(0, 3, l11, l7, l8, l9, l10);
            mv.visitLabel(l7);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitVarInsn(ALOAD, 3);
            mv.visitVarInsn(ILOAD, 4);
            mv.visitInsn(CALOAD);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitInsn(ICONST_2);
            mv.visitInsn(AALOAD);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StackTraceElement", "getClassName", "()Ljava/lang/String;", false);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "hashCode", "()I", false);
            mv.visitInsn(IXOR);
            mv.visitInsn(I2C);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(C)Ljava/lang/StringBuilder;", false);
            mv.visitInsn(POP);
            Label l12 = new Label();
            mv.visitLabel(l12);
            mv.visitJumpInsn(GOTO, l11);
            mv.visitLabel(l8);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitVarInsn(ALOAD, 3);
            mv.visitVarInsn(ILOAD, 4);
            mv.visitInsn(CALOAD);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitInsn(ICONST_2);
            mv.visitInsn(AALOAD);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StackTraceElement", "getMethodName", "()Ljava/lang/String;", false);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "hashCode", "()I", false);
            mv.visitInsn(IXOR);
            mv.visitInsn(I2C);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(C)Ljava/lang/StringBuilder;", false);
            mv.visitInsn(POP);
            Label l13 = new Label();
            mv.visitLabel(l13);
            mv.visitJumpInsn(GOTO, l11);
            mv.visitLabel(l9);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitVarInsn(ALOAD, 3);
            mv.visitVarInsn(ILOAD, 4);
            mv.visitInsn(CALOAD);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitInsn(ICONST_1);
            mv.visitInsn(AALOAD);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StackTraceElement", "getClassName", "()Ljava/lang/String;", false);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "hashCode", "()I", false);
            mv.visitInsn(IXOR);
            mv.visitInsn(I2C);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(C)Ljava/lang/StringBuilder;", false);
            mv.visitInsn(POP);
            Label l14 = new Label();
            mv.visitLabel(l14);
            mv.visitJumpInsn(GOTO, l11);
            mv.visitLabel(l10);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitVarInsn(ALOAD, 3);
            mv.visitVarInsn(ILOAD, 4);
            mv.visitInsn(CALOAD);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitInsn(ICONST_1);
            mv.visitInsn(AALOAD);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StackTraceElement", "getMethodName", "()Ljava/lang/String;", false);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "hashCode", "()I", false);
            mv.visitInsn(IXOR);
            mv.visitInsn(I2C);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(C)Ljava/lang/StringBuilder;", false);
            mv.visitInsn(POP);
            mv.visitLabel(l11);
            mv.visitIincInsn(4, 1);
            mv.visitLabel(l5);
            mv.visitVarInsn(ILOAD, 4);
            mv.visitVarInsn(ALOAD, 3);
            mv.visitInsn(ARRAYLENGTH);
            mv.visitJumpInsn(IF_ICMPLT, l6);
            Label l15 = new Label();
            mv.visitLabel(l15);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false);
            mv.visitInsn(ARETURN);
            Label l16 = new Label();
            mv.visitLabel(l16);
            mv.visitMaxs(4, 5);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC + ACC_STATIC, memberNames.bootstrapMethodName, "(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", null, null);
            mv.visitCode();
            Label l0 = new Label();
            Label l1 = new Label();
            Label l2 = new Label();
            mv.visitTryCatchBlock(l0, l1, l2, "java/lang/Throwable");
            mv.visitLabel(l0);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitTypeInsn(CHECKCAST, "java/lang/String");
            mv.visitMethodInsn(INVOKESTATIC, memberNames.className, memberNames.decryptorMethodName, "(Ljava/lang/String;)Ljava/lang/String;", false);
            mv.visitLdcInsn("<>");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "split", "(Ljava/lang/String;)[Ljava/lang/String;", false);
            mv.visitVarInsn(ASTORE, 3);
            Label l3 = new Label();
            mv.visitLabel(l3);
            mv.visitVarInsn(ALOAD, 3);
            mv.visitInsn(ICONST_0);
            mv.visitInsn(AALOAD);
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Class", "forName", "(Ljava/lang/String;)Ljava/lang/Class;", false);
            mv.visitVarInsn(ASTORE, 4);
            Label l4 = new Label();
            mv.visitLabel(l4);
            mv.visitVarInsn(ALOAD, 3);
            mv.visitInsn(ICONST_1);
            mv.visitInsn(AALOAD);
            mv.visitVarInsn(ASTORE, 5);
            Label l5 = new Label();
            mv.visitLabel(l5);
            mv.visitVarInsn(ALOAD, 3);
            mv.visitInsn(ICONST_2);
            mv.visitInsn(AALOAD);
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "parseInt", "(Ljava/lang/String;)I", false);
            mv.visitVarInsn(ISTORE, 6);
            Label l6 = new Label();
            mv.visitLabel(l6);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitTypeInsn(CHECKCAST, "java/lang/invoke/MethodHandles$Lookup");
            mv.visitVarInsn(ASTORE, 7);
            Label l7 = new Label();
            mv.visitLabel(l7);
            mv.visitInsn(ACONST_NULL);
            mv.visitVarInsn(ASTORE, 8);
            Label l8 = new Label();
            mv.visitLabel(l8);
            mv.visitVarInsn(ILOAD, 6);
            Label l9 = new Label();
            Label l10 = new Label();
            Label l11 = new Label();
            Label l12 = new Label();
            Label l13 = new Label();
            Label l14 = new Label();
            Label l15 = new Label();
            Label l16 = new Label();
            mv.visitTableSwitchInsn(0, 6, l16, l9, l10, l11, l12, l13, l14, l15);
            mv.visitLabel(l9);
            mv.visitVarInsn(ALOAD, 7);
            mv.visitVarInsn(ALOAD, 4);
            mv.visitVarInsn(ALOAD, 5);
            mv.visitVarInsn(ALOAD, 3);
            mv.visitInsn(ICONST_3);
            mv.visitInsn(AALOAD);
            mv.visitLdcInsn(Type.getType("L" + memberNames.className + ";"));
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Class", "getClassLoader", "()Ljava/lang/ClassLoader;", false);
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/invoke/MethodType", "fromMethodDescriptorString", "(Ljava/lang/String;Ljava/lang/ClassLoader;)Ljava/lang/invoke/MethodType;", false);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/invoke/MethodHandles$Lookup", "findStatic", "(Ljava/lang/Class;Ljava/lang/String;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/MethodHandle;", false);
            mv.visitVarInsn(ASTORE, 8);
            Label l17 = new Label();
            mv.visitLabel(l17);
            mv.visitJumpInsn(GOTO, l16);
            mv.visitLabel(l10);
            mv.visitVarInsn(ALOAD, 7);
            mv.visitVarInsn(ALOAD, 4);
            mv.visitVarInsn(ALOAD, 5);
            mv.visitVarInsn(ALOAD, 3);
            mv.visitInsn(ICONST_3);
            mv.visitInsn(AALOAD);
            mv.visitLdcInsn(Type.getType("L" + memberNames.className + ";"));
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Class", "getClassLoader", "()Ljava/lang/ClassLoader;", false);
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/invoke/MethodType", "fromMethodDescriptorString", "(Ljava/lang/String;Ljava/lang/ClassLoader;)Ljava/lang/invoke/MethodType;", false);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/invoke/MethodHandles$Lookup", "findVirtual", "(Ljava/lang/Class;Ljava/lang/String;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/MethodHandle;", false);
            mv.visitVarInsn(ASTORE, 8);
            Label l18 = new Label();
            mv.visitLabel(l18);
            mv.visitJumpInsn(GOTO, l16);
            mv.visitLabel(l11);
            mv.visitVarInsn(ALOAD, 7);
            mv.visitVarInsn(ALOAD, 4);
            mv.visitVarInsn(ALOAD, 5);
            mv.visitVarInsn(ALOAD, 3);
            mv.visitInsn(ICONST_3);
            mv.visitInsn(AALOAD);
            mv.visitLdcInsn(Type.getType("L" + memberNames.className + ";"));
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Class", "getClassLoader", "()Ljava/lang/ClassLoader;", false);
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/invoke/MethodType", "fromMethodDescriptorString", "(Ljava/lang/String;Ljava/lang/ClassLoader;)Ljava/lang/invoke/MethodType;", false);
            mv.visitVarInsn(ALOAD, 3);
            mv.visitInsn(ICONST_4);
            mv.visitInsn(AALOAD);
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Class", "forName", "(Ljava/lang/String;)Ljava/lang/Class;", false);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/invoke/MethodHandles$Lookup", "findSpecial", "(Ljava/lang/Class;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/Class;)Ljava/lang/invoke/MethodHandle;", false);
            mv.visitVarInsn(ASTORE, 8);
            Label l19 = new Label();
            mv.visitLabel(l19);
            mv.visitJumpInsn(GOTO, l16);
            mv.visitLabel(l12);
            mv.visitVarInsn(ALOAD, 4);
            mv.visitVarInsn(ALOAD, 5);
            mv.visitMethodInsn(INVOKESTATIC, memberNames.className, memberNames.searchMethodName, "(Ljava/lang/Class;Ljava/lang/String;)Ljava/lang/reflect/Field;", false);
            mv.visitVarInsn(ASTORE, 9);
            Label l20 = new Label();
            mv.visitLabel(l20);
            mv.visitVarInsn(ALOAD, 9);
            mv.visitJumpInsn(IFNULL, l16);
            Label l21 = new Label();
            mv.visitLabel(l21);
            mv.visitVarInsn(ALOAD, 7);
            mv.visitVarInsn(ALOAD, 4);
            mv.visitVarInsn(ALOAD, 5);
            mv.visitVarInsn(ALOAD, 9);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/reflect/Field", "getType", "()Ljava/lang/Class;", false);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/invoke/MethodHandles$Lookup", "findStaticGetter", "(Ljava/lang/Class;Ljava/lang/String;Ljava/lang/Class;)Ljava/lang/invoke/MethodHandle;", false);
            mv.visitVarInsn(ASTORE, 8);
            Label l22 = new Label();
            mv.visitLabel(l22);
            mv.visitJumpInsn(GOTO, l16);
            mv.visitLabel(l13);
            mv.visitVarInsn(ALOAD, 4);
            mv.visitVarInsn(ALOAD, 5);
            mv.visitMethodInsn(INVOKESTATIC, memberNames.className, memberNames.searchMethodName, "(Ljava/lang/Class;Ljava/lang/String;)Ljava/lang/reflect/Field;", false);
            mv.visitVarInsn(ASTORE, 9);
            Label l23 = new Label();
            mv.visitLabel(l23);
            mv.visitVarInsn(ALOAD, 9);
            mv.visitJumpInsn(IFNULL, l16);
            Label l24 = new Label();
            mv.visitLabel(l24);
            mv.visitVarInsn(ALOAD, 7);
            mv.visitVarInsn(ALOAD, 4);
            mv.visitVarInsn(ALOAD, 5);
            mv.visitVarInsn(ALOAD, 9);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/reflect/Field", "getType", "()Ljava/lang/Class;", false);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/invoke/MethodHandles$Lookup", "findGetter", "(Ljava/lang/Class;Ljava/lang/String;Ljava/lang/Class;)Ljava/lang/invoke/MethodHandle;", false);
            mv.visitVarInsn(ASTORE, 8);
            Label l25 = new Label();
            mv.visitLabel(l25);
            mv.visitJumpInsn(GOTO, l16);
            mv.visitLabel(l14);
            mv.visitVarInsn(ALOAD, 4);
            mv.visitVarInsn(ALOAD, 5);
            mv.visitMethodInsn(INVOKESTATIC, memberNames.className, memberNames.searchMethodName, "(Ljava/lang/Class;Ljava/lang/String;)Ljava/lang/reflect/Field;", false);
            mv.visitVarInsn(ASTORE, 9);
            Label l26 = new Label();
            mv.visitLabel(l26);
            mv.visitVarInsn(ALOAD, 9);
            mv.visitJumpInsn(IFNULL, l16);
            Label l27 = new Label();
            mv.visitLabel(l27);
            mv.visitVarInsn(ALOAD, 7);
            mv.visitVarInsn(ALOAD, 4);
            mv.visitVarInsn(ALOAD, 5);
            mv.visitVarInsn(ALOAD, 9);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/reflect/Field", "getType", "()Ljava/lang/Class;", false);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/invoke/MethodHandles$Lookup", "findStaticSetter", "(Ljava/lang/Class;Ljava/lang/String;Ljava/lang/Class;)Ljava/lang/invoke/MethodHandle;", false);
            mv.visitVarInsn(ASTORE, 8);
            Label l28 = new Label();
            mv.visitLabel(l28);
            mv.visitJumpInsn(GOTO, l16);
            mv.visitLabel(l15);
            mv.visitVarInsn(ALOAD, 4);
            mv.visitVarInsn(ALOAD, 5);
            mv.visitMethodInsn(INVOKESTATIC, memberNames.className, memberNames.searchMethodName, "(Ljava/lang/Class;Ljava/lang/String;)Ljava/lang/reflect/Field;", false);
            mv.visitVarInsn(ASTORE, 9);
            Label l29 = new Label();
            mv.visitLabel(l29);
            mv.visitVarInsn(ALOAD, 9);
            mv.visitJumpInsn(IFNULL, l16);
            Label l30 = new Label();
            mv.visitLabel(l30);
            mv.visitVarInsn(ALOAD, 7);
            mv.visitVarInsn(ALOAD, 4);
            mv.visitVarInsn(ALOAD, 5);
            mv.visitVarInsn(ALOAD, 9);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/reflect/Field", "getType", "()Ljava/lang/Class;", false);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/invoke/MethodHandles$Lookup", "findSetter", "(Ljava/lang/Class;Ljava/lang/String;Ljava/lang/Class;)Ljava/lang/invoke/MethodHandle;", false);
            mv.visitVarInsn(ASTORE, 8);
            mv.visitLabel(l16);
            mv.visitTypeInsn(NEW, "java/lang/invoke/ConstantCallSite");
            mv.visitInsn(DUP);
            mv.visitVarInsn(ALOAD, 8);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitTypeInsn(CHECKCAST, "java/lang/invoke/MethodType");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/invoke/MethodHandle", "asType", "(Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/MethodHandle;", false);
            mv.visitMethodInsn(INVOKESPECIAL, "java/lang/invoke/ConstantCallSite", "<init>", "(Ljava/lang/invoke/MethodHandle;)V", false);
            mv.visitLabel(l1);
            mv.visitInsn(ARETURN);
            mv.visitLabel(l2);
            mv.visitVarInsn(ASTORE, 3);
            Label l31 = new Label();
            mv.visitLabel(l31);
            mv.visitVarInsn(ALOAD, 3);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Throwable", "printStackTrace", "()V", false);
            Label l32 = new Label();
            mv.visitLabel(l32);
            mv.visitInsn(ACONST_NULL);
            mv.visitInsn(ARETURN);
            Label l33 = new Label();
            mv.visitLabel(l33);
            mv.visitMaxs(6, 10);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PRIVATE + ACC_STATIC, memberNames.searchMethodName, "(Ljava/lang/Class;Ljava/lang/String;)Ljava/lang/reflect/Field;", "(Ljava/lang/Class<*>;Ljava/lang/String;)Ljava/lang/reflect/Field;", null);
            mv.visitCode();
            Label l0 = new Label();
            Label l1 = new Label();
            Label l2 = new Label();
            mv.visitTryCatchBlock(l0, l1, l2, "java/lang/NoSuchFieldException");
            Label l3 = new Label();
            Label l4 = new Label();
            Label l5 = new Label();
            mv.visitTryCatchBlock(l3, l4, l5, "java/lang/NoSuchFieldException");
            mv.visitLabel(l0);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Class", "getDeclaredField", "(Ljava/lang/String;)Ljava/lang/reflect/Field;", false);
            mv.visitVarInsn(ASTORE, 2);
            Label l6 = new Label();
            mv.visitLabel(l6);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitInsn(ICONST_1);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/reflect/Field", "setAccessible", "(Z)V", false);
            Label l7 = new Label();
            mv.visitLabel(l7);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitLabel(l1);
            mv.visitInsn(ARETURN);
            mv.visitLabel(l2);
            mv.visitVarInsn(ASTORE, 2);
            mv.visitLabel(l3);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Class", "getSuperclass", "()Ljava/lang/Class;", false);
            mv.visitVarInsn(ASTORE, 3);
            Label l8 = new Label();
            mv.visitLabel(l8);
            mv.visitVarInsn(ALOAD, 3);
            Label l9 = new Label();
            mv.visitJumpInsn(IFNONNULL, l9);
            Label l10 = new Label();
            mv.visitLabel(l10);
            mv.visitTypeInsn(NEW, "java/lang/NoSuchFieldException");
            mv.visitInsn(DUP);
            mv.visitMethodInsn(INVOKESPECIAL, "java/lang/NoSuchFieldException", "<init>", "()V", false);
            mv.visitInsn(ATHROW);
            mv.visitLabel(l9);
            mv.visitVarInsn(ALOAD, 3);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitMethodInsn(INVOKESTATIC, memberNames.className, memberNames.searchMethodName, "(Ljava/lang/Class;Ljava/lang/String;)Ljava/lang/reflect/Field;", false);
            mv.visitInsn(DUP);
            mv.visitVarInsn(ASTORE, 4);
            Label l11 = new Label();
            mv.visitLabel(l11);
            Label l12 = new Label();
            mv.visitJumpInsn(IFNULL, l12);
            Label l13 = new Label();
            mv.visitLabel(l13);
            mv.visitVarInsn(ALOAD, 4);
            mv.visitLabel(l4);
            mv.visitInsn(ARETURN);
            mv.visitLabel(l5);
            mv.visitVarInsn(ASTORE, 3);
            Label l14 = new Label();
            mv.visitLabel(l14);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Class", "getInterfaces", "()[Ljava/lang/Class;", false);
            mv.visitVarInsn(ASTORE, 4);
            Label l15 = new Label();
            mv.visitLabel(l15);
            mv.visitVarInsn(ALOAD, 4);
            Label l16 = new Label();
            mv.visitJumpInsn(IFNONNULL, l16);
            Label l17 = new Label();
            mv.visitLabel(l17);
            mv.visitTypeInsn(NEW, "java/lang/RuntimeException");
            mv.visitInsn(DUP);
            mv.visitMethodInsn(INVOKESPECIAL, "java/lang/RuntimeException", "<init>", "()V", false);
            mv.visitInsn(ATHROW);
            mv.visitLabel(l16);
            mv.visitInsn(ICONST_0);
            mv.visitVarInsn(ISTORE, 5);
            Label l18 = new Label();
            mv.visitLabel(l18);
            Label l19 = new Label();
            mv.visitJumpInsn(GOTO, l19);
            Label l20 = new Label();
            mv.visitLabel(l20);
            mv.visitVarInsn(ALOAD, 4);
            mv.visitVarInsn(ILOAD, 5);
            mv.visitInsn(AALOAD);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitMethodInsn(INVOKESTATIC, memberNames.className, memberNames.searchMethodName, "(Ljava/lang/Class;Ljava/lang/String;)Ljava/lang/reflect/Field;", false);
            mv.visitInsn(DUP);
            mv.visitVarInsn(ASTORE, 6);
            Label l21 = new Label();
            mv.visitLabel(l21);
            Label l22 = new Label();
            mv.visitJumpInsn(IFNULL, l22);
            Label l23 = new Label();
            mv.visitLabel(l23);
            mv.visitVarInsn(ALOAD, 6);
            mv.visitInsn(ARETURN);
            mv.visitLabel(l22);
            mv.visitIincInsn(5, 1);
            mv.visitLabel(l19);
            mv.visitVarInsn(ILOAD, 5);
            mv.visitVarInsn(ALOAD, 4);
            mv.visitInsn(ARRAYLENGTH);
            mv.visitJumpInsn(IF_ICMPLT, l20);
            mv.visitLabel(l12);
            mv.visitInsn(ACONST_NULL);
            mv.visitInsn(ARETURN);
            Label l24 = new Label();
            mv.visitLabel(l24);
            mv.visitMaxs(2, 7);
            mv.visitEnd();
        }
        cw.visitEnd();

        return cw;
    }

    private class MemberNames {
        private String className;
        private String decryptorMethodName;
        private String bootstrapMethodName;
        private String searchMethodName;

        private MemberNames() {
            this.className = StringUtils.randomClassName(getClasses().keySet());
            this.decryptorMethodName = randomString(4);
            this.bootstrapMethodName = randomString(4);
            this.searchMethodName = randomString(4);
        }
    }
}
