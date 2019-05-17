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
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InvokeDynamicInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;

/**
 * Hides method invocations and field accesses by swapping them out with an invokedynamic instruction.
 *
 * @author ItzSomebody
 */
public class InvokedynamicTransformer extends ReferenceObfuscation {
    @Override
    public void transform() {
        MemberNames memberNames = new MemberNames();
        AtomicInteger counter = new AtomicInteger();

        Handle bootstrapHandle = new Handle(H_INVOKESTATIC, memberNames.className, memberNames.bootstrapMethodName,
                "(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", false);

        getClassWrappers().stream().filter(cw -> !excluded(cw) && !"java/lang/Enum".equals(cw.getSuperName())
                && cw.allowsIndy()).forEach(classWrapper ->
                classWrapper.getMethods().stream().filter(mw -> !excluded(mw) && hasInstructions(mw)).forEach(mw -> {
                    InsnList insns = mw.getInstructions();

                    Stream.of(insns.toArray()).forEach(insn -> {
                        if (insn instanceof MethodInsnNode) {
                            MethodInsnNode m = (MethodInsnNode) insn;

                            if (m.name.startsWith("<"))
                                return;

                            String newDesc = m.desc.replace(")", "Ljava/lang/String;J)");
                            if (m.getOpcode() != INVOKESTATIC)
                                newDesc = newDesc.replace("(", "(Ljava/lang/Object;");

                            newDesc = ASMUtils.getGenericMethodDesc(newDesc);
                            String name = (insn.getOpcode() == INVOKESTATIC) ? "a" : "b";

                            InvokeDynamicInsnNode indy = new InvokeDynamicInsnNode(
                                    name,
                                    newDesc,
                                    bootstrapHandle
                            );

                            insns.insertBefore(m, new LdcInsnNode(m.owner.replace("/", ".")));
                            insns.insertBefore(m, ASMUtils.getNumberInsn(
                                    (((long) hash(m.desc) & 0xffffffffL) | (((long) m.name.hashCode()) << 32))
                            ));
                            insns.set(m, indy);

                            counter.incrementAndGet();
                        } else if (insn instanceof FieldInsnNode && !"<init>".equals(mw.getName())) {
                            FieldInsnNode f = (FieldInsnNode) insn;

                            boolean isStatic = (f.getOpcode() == GETSTATIC || f.getOpcode() == PUTSTATIC);
                            boolean isSetter = (f.getOpcode() == PUTFIELD || f.getOpcode() == PUTSTATIC);

                            String newDesc = (isSetter) ? "(" + f.desc + "Ljava/lang/String;J)V" : "(Ljava/lang/String;J)" + f.desc;
                            if (!isStatic)
                                newDesc = newDesc.replace("(", "(Ljava/lang/Object;");

                            String name;

                            switch (insn.getOpcode()) {
                                case GETSTATIC:
                                    name = "d";
                                    break;
                                case GETFIELD:
                                    name = "e";
                                    break;
                                case PUTSTATIC:
                                    name = "f";
                                    break;
                                default:
                                    name = "g";
                                    break;
                            }

                            InvokeDynamicInsnNode indy = new InvokeDynamicInsnNode(
                                    name,
                                    newDesc,
                                    bootstrapHandle
                            );

                            insns.insertBefore(f, new LdcInsnNode(f.owner.replace("/", ".")));
                            insns.insertBefore(f, ASMUtils.getNumberInsn(
                                    (((long) hashType(f.desc) & 0xffffffffL) | (((long) f.name.hashCode()) << 32))
                            ));
                            insns.set(f, indy);

                            counter.incrementAndGet();
                        }
                    });
                }));

        ClassNode decryptor = createBootstrapClass(memberNames);
        getClasses().put(decryptor.name, new ClassWrapper(decryptor, false));

        Main.info("Hid API " + counter.get() + " references using invokedynamic");
    }

    private int hashType(String sType) {
        Type type = Type.getType(sType);

        if (type.getSort() == Type.ARRAY)
            return type.getInternalName().replace('/', '.').hashCode();
        else
            return type.getClassName().hashCode();
    }

    private int hash(String methodDescriptor) {
        int hash = 0;

        Type[] types = Type.getArgumentTypes(methodDescriptor);

        for (Type type : types) {
            if (type.getSort() == Type.ARRAY)
                hash ^= type.getInternalName().replace('/', '.').hashCode();
            else
                hash ^= type.getClassName().hashCode();
        }

        Type returnType = Type.getReturnType(methodDescriptor);
        if (returnType.getSort() == Type.ARRAY)
            hash ^= returnType.getInternalName().replace('/', '.').hashCode();
        else
            hash ^= returnType.getClassName().hashCode();

        return hash;
    }

    @SuppressWarnings("Duplicates")
    private ClassNode createBootstrapClass(MemberNames memberNames) {
        ClassNode cw = new ClassNode();
        MethodVisitor mv;
        FieldVisitor fv;

        cw.visit(V1_7, ACC_PUBLIC + ACC_SUPER, memberNames.className, null, "java/lang/Object", null);

        {
            fv = cw.visitField(ACC_PRIVATE + ACC_FINAL + ACC_STATIC, memberNames.methodCacheFieldName, "Ljava/util/Map;", null, null);
            fv.visitEnd();
        }
        {
            fv = cw.visitField(ACC_PRIVATE + ACC_FINAL + ACC_STATIC, memberNames.fieldCacheFieldName, "Ljava/util/Map;", null, null);
            fv.visitEnd();
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
            mv = cw.visitMethod(ACC_PRIVATE + ACC_STATIC, memberNames.hashMethodName, "(Ljava/lang/reflect/Method;)I", null, null);
            mv.visitCode();
            Label l0 = new Label();
            mv.visitLabel(l0);
            mv.visitInsn(ICONST_0);
            mv.visitVarInsn(ISTORE, 1);
            Label l1 = new Label();
            mv.visitLabel(l1);
            mv.visitInsn(ICONST_0);
            mv.visitVarInsn(ISTORE, 2);
            Label l2 = new Label();
            mv.visitLabel(l2);
            mv.visitVarInsn(ILOAD, 2);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/reflect/Method", "getParameterCount", "()I", false);
            Label l3 = new Label();
            mv.visitJumpInsn(IF_ICMPGE, l3);
            Label l4 = new Label();
            mv.visitLabel(l4);
            mv.visitVarInsn(ILOAD, 1);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/reflect/Method", "getParameterTypes", "()[Ljava/lang/Class;", false);
            mv.visitVarInsn(ILOAD, 2);
            mv.visitInsn(AALOAD);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Class", "getName", "()Ljava/lang/String;", false);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "hashCode", "()I", false);
            mv.visitInsn(IXOR);
            mv.visitVarInsn(ISTORE, 1);
            Label l5 = new Label();
            mv.visitLabel(l5);
            mv.visitIincInsn(2, 1);
            mv.visitJumpInsn(GOTO, l2);
            mv.visitLabel(l3);
            mv.visitVarInsn(ILOAD, 1);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/reflect/Method", "getReturnType", "()Ljava/lang/Class;", false);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Class", "getName", "()Ljava/lang/String;", false);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "hashCode", "()I", false);
            mv.visitInsn(IXOR);
            mv.visitVarInsn(ISTORE, 1);
            Label l6 = new Label();
            mv.visitLabel(l6);
            mv.visitVarInsn(ILOAD, 1);
            mv.visitInsn(IRETURN);
            Label l7 = new Label();
            mv.visitLabel(l7);
            mv.visitMaxs(3, 3);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PRIVATE + ACC_STATIC, memberNames.findMethodMethodName, "(Ljava/lang/Class;II)Ljava/lang/reflect/Method;", null, null);
            mv.visitCode();
            Label l0 = new Label();
            mv.visitLabel(l0);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Class", "getDeclaredMethods", "()[Ljava/lang/reflect/Method;", false);
            mv.visitVarInsn(ASTORE, 3);
            Label l1 = new Label();
            mv.visitLabel(l1);
            mv.visitInsn(ICONST_0);
            mv.visitVarInsn(ISTORE, 5);
            Label l2 = new Label();
            mv.visitLabel(l2);
            mv.visitVarInsn(ILOAD, 5);
            mv.visitVarInsn(ALOAD, 3);
            mv.visitInsn(ARRAYLENGTH);
            Label l3 = new Label();
            mv.visitJumpInsn(IF_ICMPGE, l3);
            Label l4 = new Label();
            mv.visitLabel(l4);
            mv.visitVarInsn(ALOAD, 3);
            mv.visitVarInsn(ILOAD, 5);
            mv.visitInsn(AALOAD);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/reflect/Method", "getName", "()Ljava/lang/String;", false);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "hashCode", "()I", false);
            mv.visitVarInsn(ILOAD, 1);
            Label l5 = new Label();
            mv.visitJumpInsn(IF_ICMPNE, l5);
            mv.visitVarInsn(ALOAD, 3);
            mv.visitVarInsn(ILOAD, 5);
            mv.visitInsn(AALOAD);
            mv.visitMethodInsn(INVOKESTATIC, memberNames.className, memberNames.hashMethodName, "(Ljava/lang/reflect/Method;)I", false);
            mv.visitVarInsn(ILOAD, 2);
            mv.visitJumpInsn(IF_ICMPNE, l5);
            Label l6 = new Label();
            mv.visitLabel(l6);
            mv.visitVarInsn(ALOAD, 3);
            mv.visitVarInsn(ILOAD, 5);
            mv.visitInsn(AALOAD);
            mv.visitVarInsn(ASTORE, 4);
            Label l7 = new Label();
            mv.visitLabel(l7);
            mv.visitVarInsn(ALOAD, 4);
            mv.visitInsn(ICONST_1);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/reflect/Method", "setAccessible", "(Z)V", false);
            Label l8 = new Label();
            mv.visitLabel(l8);
            mv.visitVarInsn(ALOAD, 4);
            mv.visitInsn(ARETURN);
            mv.visitLabel(l5);
            mv.visitIincInsn(5, 1);
            mv.visitJumpInsn(GOTO, l2);
            mv.visitLabel(l3);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Class", "getInterfaces", "()[Ljava/lang/Class;", false);
            Label l9 = new Label();
            mv.visitJumpInsn(IFNULL, l9);
            Label l10 = new Label();
            mv.visitLabel(l10);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Class", "getInterfaces", "()[Ljava/lang/Class;", false);
            mv.visitVarInsn(ASTORE, 5);
            mv.visitVarInsn(ALOAD, 5);
            mv.visitInsn(ARRAYLENGTH);
            mv.visitVarInsn(ISTORE, 6);
            mv.visitInsn(ICONST_0);
            mv.visitVarInsn(ISTORE, 7);
            Label l11 = new Label();
            mv.visitLabel(l11);
            mv.visitVarInsn(ILOAD, 7);
            mv.visitVarInsn(ILOAD, 6);
            mv.visitJumpInsn(IF_ICMPGE, l9);
            mv.visitVarInsn(ALOAD, 5);
            mv.visitVarInsn(ILOAD, 7);
            mv.visitInsn(AALOAD);
            mv.visitVarInsn(ASTORE, 8);
            Label l12 = new Label();
            mv.visitLabel(l12);
            mv.visitVarInsn(ALOAD, 8);
            mv.visitVarInsn(ILOAD, 1);
            mv.visitVarInsn(ILOAD, 2);
            mv.visitMethodInsn(INVOKESTATIC, memberNames.className, memberNames.findMethodMethodName, "(Ljava/lang/Class;II)Ljava/lang/reflect/Method;", false);
            mv.visitVarInsn(ASTORE, 4);
            Label l13 = new Label();
            mv.visitLabel(l13);
            mv.visitVarInsn(ALOAD, 4);
            Label l14 = new Label();
            mv.visitJumpInsn(IFNULL, l14);
            Label l15 = new Label();
            mv.visitLabel(l15);
            mv.visitVarInsn(ALOAD, 4);
            mv.visitInsn(ICONST_1);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/reflect/Method", "setAccessible", "(Z)V", false);
            Label l16 = new Label();
            mv.visitLabel(l16);
            mv.visitVarInsn(ALOAD, 4);
            mv.visitInsn(ARETURN);
            mv.visitLabel(l14);
            mv.visitIincInsn(7, 1);
            mv.visitJumpInsn(GOTO, l11);
            mv.visitLabel(l9);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Class", "getSuperclass", "()Ljava/lang/Class;", false);
            Label l17 = new Label();
            mv.visitJumpInsn(IFNULL, l17);
            Label l18 = new Label();
            mv.visitLabel(l18);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Class", "getSuperclass", "()Ljava/lang/Class;", false);
            mv.visitVarInsn(ASTORE, 0);
            Label l19 = new Label();
            mv.visitLabel(l19);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ILOAD, 1);
            mv.visitVarInsn(ILOAD, 2);
            mv.visitMethodInsn(INVOKESTATIC, memberNames.className, memberNames.findMethodMethodName, "(Ljava/lang/Class;II)Ljava/lang/reflect/Method;", false);
            mv.visitVarInsn(ASTORE, 4);
            Label l20 = new Label();
            mv.visitLabel(l20);
            mv.visitVarInsn(ALOAD, 4);
            mv.visitJumpInsn(IFNULL, l9);
            Label l21 = new Label();
            mv.visitLabel(l21);
            mv.visitVarInsn(ALOAD, 4);
            mv.visitInsn(ICONST_1);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/reflect/Method", "setAccessible", "(Z)V", false);
            Label l22 = new Label();
            mv.visitLabel(l22);
            mv.visitVarInsn(ALOAD, 4);
            mv.visitInsn(ARETURN);
            mv.visitLabel(l17);
            mv.visitInsn(ACONST_NULL);
            mv.visitInsn(ARETURN);
            Label l23 = new Label();
            mv.visitLabel(l23);
            mv.visitMaxs(3, 9);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PRIVATE + ACC_STATIC, memberNames.findFieldMethodName, "(Ljava/lang/Class;II)Ljava/lang/reflect/Field;", null, null);
            mv.visitCode();
            Label l0 = new Label();
            mv.visitLabel(l0);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Class", "getDeclaredFields", "()[Ljava/lang/reflect/Field;", false);
            mv.visitVarInsn(ASTORE, 3);
            Label l1 = new Label();
            mv.visitLabel(l1);
            mv.visitInsn(ICONST_0);
            mv.visitVarInsn(ISTORE, 5);
            Label l2 = new Label();
            mv.visitLabel(l2);
            mv.visitVarInsn(ILOAD, 5);
            mv.visitVarInsn(ALOAD, 3);
            mv.visitInsn(ARRAYLENGTH);
            Label l3 = new Label();
            mv.visitJumpInsn(IF_ICMPGE, l3);
            Label l4 = new Label();
            mv.visitLabel(l4);
            mv.visitVarInsn(ALOAD, 3);
            mv.visitVarInsn(ILOAD, 5);
            mv.visitInsn(AALOAD);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/reflect/Field", "getName", "()Ljava/lang/String;", false);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "hashCode", "()I", false);
            mv.visitVarInsn(ILOAD, 1);
            Label l5 = new Label();
            mv.visitJumpInsn(IF_ICMPNE, l5);
            mv.visitVarInsn(ALOAD, 3);
            mv.visitVarInsn(ILOAD, 5);
            mv.visitInsn(AALOAD);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/reflect/Field", "getType", "()Ljava/lang/Class;", false);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Class", "getName", "()Ljava/lang/String;", false);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "hashCode", "()I", false);
            mv.visitVarInsn(ILOAD, 2);
            mv.visitJumpInsn(IF_ICMPNE, l5);
            Label l6 = new Label();
            mv.visitLabel(l6);
            mv.visitVarInsn(ALOAD, 3);
            mv.visitVarInsn(ILOAD, 5);
            mv.visitInsn(AALOAD);
            mv.visitVarInsn(ASTORE, 4);
            Label l7 = new Label();
            mv.visitLabel(l7);
            mv.visitVarInsn(ALOAD, 4);
            mv.visitInsn(ICONST_1);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/reflect/Field", "setAccessible", "(Z)V", false);
            Label l8 = new Label();
            mv.visitLabel(l8);
            mv.visitVarInsn(ALOAD, 4);
            mv.visitInsn(ARETURN);
            mv.visitLabel(l5);
            mv.visitIincInsn(5, 1);
            mv.visitJumpInsn(GOTO, l2);
            mv.visitLabel(l3);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Class", "getInterfaces", "()[Ljava/lang/Class;", false);
            Label l9 = new Label();
            mv.visitJumpInsn(IFNULL, l9);
            Label l10 = new Label();
            mv.visitLabel(l10);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Class", "getInterfaces", "()[Ljava/lang/Class;", false);
            mv.visitVarInsn(ASTORE, 5);
            mv.visitVarInsn(ALOAD, 5);
            mv.visitInsn(ARRAYLENGTH);
            mv.visitVarInsn(ISTORE, 6);
            mv.visitInsn(ICONST_0);
            mv.visitVarInsn(ISTORE, 7);
            Label l11 = new Label();
            mv.visitLabel(l11);
            mv.visitVarInsn(ILOAD, 7);
            mv.visitVarInsn(ILOAD, 6);
            mv.visitJumpInsn(IF_ICMPGE, l9);
            mv.visitVarInsn(ALOAD, 5);
            mv.visitVarInsn(ILOAD, 7);
            mv.visitInsn(AALOAD);
            mv.visitVarInsn(ASTORE, 8);
            Label l12 = new Label();
            mv.visitLabel(l12);
            mv.visitVarInsn(ALOAD, 8);
            mv.visitVarInsn(ILOAD, 1);
            mv.visitVarInsn(ILOAD, 2);
            mv.visitMethodInsn(INVOKESTATIC, memberNames.className, memberNames.findFieldMethodName, "(Ljava/lang/Class;II)Ljava/lang/reflect/Field;", false);
            mv.visitVarInsn(ASTORE, 4);
            Label l13 = new Label();
            mv.visitLabel(l13);
            mv.visitVarInsn(ALOAD, 4);
            Label l14 = new Label();
            mv.visitJumpInsn(IFNULL, l14);
            Label l15 = new Label();
            mv.visitLabel(l15);
            mv.visitVarInsn(ALOAD, 4);
            mv.visitInsn(ICONST_1);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/reflect/Field", "setAccessible", "(Z)V", false);
            Label l16 = new Label();
            mv.visitLabel(l16);
            mv.visitVarInsn(ALOAD, 4);
            mv.visitInsn(ARETURN);
            mv.visitLabel(l14);
            mv.visitIincInsn(7, 1);
            mv.visitJumpInsn(GOTO, l11);
            mv.visitLabel(l9);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Class", "getSuperclass", "()Ljava/lang/Class;", false);
            Label l17 = new Label();
            mv.visitJumpInsn(IFNULL, l17);
            Label l18 = new Label();
            mv.visitLabel(l18);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Class", "getSuperclass", "()Ljava/lang/Class;", false);
            mv.visitVarInsn(ASTORE, 0);
            Label l19 = new Label();
            mv.visitLabel(l19);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ILOAD, 1);
            mv.visitVarInsn(ILOAD, 2);
            mv.visitMethodInsn(INVOKESTATIC, memberNames.className, memberNames.findFieldMethodName, "(Ljava/lang/Class;II)Ljava/lang/reflect/Field;", false);
            mv.visitVarInsn(ASTORE, 4);
            Label l20 = new Label();
            mv.visitLabel(l20);
            mv.visitVarInsn(ALOAD, 4);
            mv.visitJumpInsn(IFNULL, l9);
            Label l21 = new Label();
            mv.visitLabel(l21);
            mv.visitVarInsn(ALOAD, 4);
            mv.visitInsn(ICONST_1);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/reflect/Field", "setAccessible", "(Z)V", false);
            Label l22 = new Label();
            mv.visitLabel(l22);
            mv.visitVarInsn(ALOAD, 4);
            mv.visitInsn(ARETURN);
            mv.visitLabel(l17);
            mv.visitInsn(ACONST_NULL);
            mv.visitInsn(ARETURN);
            Label l23 = new Label();
            mv.visitLabel(l23);
            mv.visitMaxs(3, 9);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PRIVATE + ACC_STATIC, memberNames.resolveMethodHandleMethodName, "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MutableCallSite;[Ljava/lang/Object;)Ljava/lang/Object;", null, new String[]{"java/lang/Throwable"});
            mv.visitCode();
            Label l0 = new Label();
            mv.visitLabel(l0);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitInsn(ICONST_0);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "charAt", "(I)C", false);
            mv.visitVarInsn(ISTORE, 5);
            Label l1 = new Label();
            mv.visitLabel(l1);
            mv.visitVarInsn(ALOAD, 4);
            mv.visitVarInsn(ALOAD, 4);
            mv.visitInsn(ARRAYLENGTH);
            mv.visitInsn(ICONST_1);
            mv.visitInsn(ISUB);
            mv.visitInsn(AALOAD);
            mv.visitTypeInsn(CHECKCAST, "java/lang/Long");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Long", "longValue", "()J", false);
            mv.visitVarInsn(LSTORE, 6);
            Label l2 = new Label();
            mv.visitLabel(l2);
            mv.visitVarInsn(LLOAD, 6);
            mv.visitIntInsn(BIPUSH, 32);
            mv.visitInsn(LSHR);
            mv.visitInsn(L2I);
            mv.visitVarInsn(ISTORE, 8);
            Label l3 = new Label();
            mv.visitLabel(l3);
            mv.visitVarInsn(LLOAD, 6);
            mv.visitInsn(L2I);
            mv.visitVarInsn(ISTORE, 9);
            Label l4 = new Label();
            mv.visitLabel(l4);
            mv.visitVarInsn(ALOAD, 4);
            mv.visitVarInsn(ALOAD, 4);
            mv.visitInsn(ARRAYLENGTH);
            mv.visitInsn(ICONST_2);
            mv.visitInsn(ISUB);
            mv.visitInsn(AALOAD);
            mv.visitTypeInsn(CHECKCAST, "java/lang/String");
            mv.visitVarInsn(ASTORE, 10);
            Label l5 = new Label();
            mv.visitLabel(l5);
            mv.visitVarInsn(ALOAD, 10);
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Class", "forName", "(Ljava/lang/String;)Ljava/lang/Class;", false);
            mv.visitVarInsn(ASTORE, 11);
            Label l6 = new Label();
            mv.visitLabel(l6);
            mv.visitVarInsn(ILOAD, 5);
            mv.visitIntInsn(BIPUSH, 97);
            Label l7 = new Label();
            mv.visitJumpInsn(IF_ICMPLT, l7);
            mv.visitVarInsn(ILOAD, 5);
            mv.visitIntInsn(BIPUSH, 99);
            mv.visitJumpInsn(IF_ICMPGT, l7);
            Label l8 = new Label();
            mv.visitLabel(l8);
            mv.visitVarInsn(ALOAD, 11);
            mv.visitVarInsn(ILOAD, 8);
            mv.visitVarInsn(ILOAD, 9);
            mv.visitMethodInsn(INVOKESTATIC, memberNames.className, memberNames.findMethodMethodName, "(Ljava/lang/Class;II)Ljava/lang/reflect/Method;", false);
            mv.visitVarInsn(ASTORE, 13);
            Label l9 = new Label();
            mv.visitLabel(l9);
            mv.visitVarInsn(ALOAD, 13);
            Label l10 = new Label();
            mv.visitJumpInsn(IFNONNULL, l10);
            Label l11 = new Label();
            mv.visitLabel(l11);
            mv.visitTypeInsn(NEW, "java/lang/NoSuchMethodException");
            mv.visitInsn(DUP);
            mv.visitTypeInsn(NEW, "java/lang/StringBuilder");
            mv.visitInsn(DUP);
            mv.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "()V", false);
            mv.visitVarInsn(ALOAD, 10);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
            mv.visitIntInsn(BIPUSH, 32);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(C)Ljava/lang/StringBuilder;", false);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
            mv.visitIntInsn(BIPUSH, 32);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(C)Ljava/lang/StringBuilder;", false);
            mv.visitVarInsn(ILOAD, 9);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(I)Ljava/lang/StringBuilder;", false);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false);
            mv.visitMethodInsn(INVOKESPECIAL, "java/lang/NoSuchMethodException", "<init>", "(Ljava/lang/String;)V", false);
            mv.visitInsn(ATHROW);
            mv.visitLabel(l10);
            mv.visitVarInsn(ALOAD, 13);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/reflect/Method", "getReturnType", "()Ljava/lang/Class;", false);
            mv.visitVarInsn(ALOAD, 13);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/reflect/Method", "getParameterTypes", "()[Ljava/lang/Class;", false);
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/invoke/MethodType", "methodType", "(Ljava/lang/Class;[Ljava/lang/Class;)Ljava/lang/invoke/MethodType;", false);
            mv.visitVarInsn(ASTORE, 14);
            Label l12 = new Label();
            mv.visitLabel(l12);
            mv.visitVarInsn(ILOAD, 5);
            mv.visitIntInsn(BIPUSH, 97);
            Label l13 = new Label();
            mv.visitJumpInsn(IF_ICMPNE, l13);
            Label l14 = new Label();
            mv.visitLabel(l14);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ALOAD, 13);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/invoke/MethodHandles$Lookup", "unreflect", "(Ljava/lang/reflect/Method;)Ljava/lang/invoke/MethodHandle;", false);
            mv.visitVarInsn(ALOAD, 14);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/invoke/MethodHandle", "asType", "(Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/MethodHandle;", false);
            mv.visitVarInsn(ASTORE, 12);
            Label l15 = new Label();
            mv.visitLabel(l15);
            Label l16 = new Label();
            mv.visitJumpInsn(GOTO, l16);
            mv.visitLabel(l13);
            mv.visitVarInsn(ILOAD, 5);
            mv.visitIntInsn(BIPUSH, 98);
            Label l17 = new Label();
            mv.visitJumpInsn(IF_ICMPNE, l17);
            Label l18 = new Label();
            mv.visitLabel(l18);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ALOAD, 13);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/invoke/MethodHandles$Lookup", "unreflect", "(Ljava/lang/reflect/Method;)Ljava/lang/invoke/MethodHandle;", false);
            mv.visitVarInsn(ALOAD, 14);
            mv.visitInsn(ICONST_0);
            mv.visitInsn(ICONST_1);
            mv.visitTypeInsn(ANEWARRAY, "java/lang/Class");
            mv.visitInsn(DUP);
            mv.visitInsn(ICONST_0);
            mv.visitLdcInsn(Type.getType("Ljava/lang/Object;"));
            mv.visitInsn(AASTORE);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/invoke/MethodType", "insertParameterTypes", "(I[Ljava/lang/Class;)Ljava/lang/invoke/MethodType;", false);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/invoke/MethodHandle", "asType", "(Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/MethodHandle;", false);
            mv.visitVarInsn(ASTORE, 12);
            Label l19 = new Label();
            mv.visitLabel(l19);
            mv.visitJumpInsn(GOTO, l16);
            mv.visitLabel(l17);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ALOAD, 13);
            mv.visitLdcInsn(Type.getType("L" + memberNames.className + ";"));
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/invoke/MethodHandles$Lookup", "unreflectSpecial", "(Ljava/lang/reflect/Method;Ljava/lang/Class;)Ljava/lang/invoke/MethodHandle;", false);
            mv.visitVarInsn(ALOAD, 14);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/invoke/MethodHandle", "asType", "(Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/MethodHandle;", false);
            mv.visitVarInsn(ASTORE, 12);
            mv.visitLabel(l16);
            Label l20 = new Label();
            mv.visitJumpInsn(GOTO, l20);
            mv.visitLabel(l7);
            mv.visitVarInsn(ALOAD, 11);
            mv.visitVarInsn(ILOAD, 8);
            mv.visitVarInsn(ILOAD, 9);
            mv.visitMethodInsn(INVOKESTATIC, memberNames.className, memberNames.findFieldMethodName, "(Ljava/lang/Class;II)Ljava/lang/reflect/Field;", false);
            mv.visitVarInsn(ASTORE, 13);
            Label l21 = new Label();
            mv.visitLabel(l21);
            mv.visitVarInsn(ALOAD, 13);
            Label l22 = new Label();
            mv.visitJumpInsn(IFNONNULL, l22);
            Label l23 = new Label();
            mv.visitLabel(l23);
            mv.visitTypeInsn(NEW, "java/lang/NoSuchFieldException");
            mv.visitInsn(DUP);
            mv.visitTypeInsn(NEW, "java/lang/StringBuilder");
            mv.visitInsn(DUP);
            mv.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "()V", false);
            mv.visitVarInsn(ALOAD, 10);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
            mv.visitIntInsn(BIPUSH, 32);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(C)Ljava/lang/StringBuilder;", false);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
            mv.visitIntInsn(BIPUSH, 32);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(C)Ljava/lang/StringBuilder;", false);
            mv.visitVarInsn(ILOAD, 9);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(I)Ljava/lang/StringBuilder;", false);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false);
            mv.visitMethodInsn(INVOKESPECIAL, "java/lang/NoSuchFieldException", "<init>", "(Ljava/lang/String;)V", false);
            mv.visitInsn(ATHROW);
            mv.visitLabel(l22);
            mv.visitVarInsn(ILOAD, 5);
            mv.visitIntInsn(BIPUSH, 100);
            Label l24 = new Label();
            mv.visitJumpInsn(IF_ICMPNE, l24);
            Label l25 = new Label();
            mv.visitLabel(l25);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ALOAD, 13);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/invoke/MethodHandles$Lookup", "unreflectGetter", "(Ljava/lang/reflect/Field;)Ljava/lang/invoke/MethodHandle;", false);
            mv.visitVarInsn(ALOAD, 13);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/reflect/Field", "getType", "()Ljava/lang/Class;", false);
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/invoke/MethodType", "methodType", "(Ljava/lang/Class;)Ljava/lang/invoke/MethodType;", false);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/invoke/MethodHandle", "asType", "(Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/MethodHandle;", false);
            mv.visitVarInsn(ASTORE, 12);
            Label l26 = new Label();
            mv.visitLabel(l26);
            mv.visitJumpInsn(GOTO, l20);
            mv.visitLabel(l24);
            mv.visitVarInsn(ILOAD, 5);
            mv.visitIntInsn(BIPUSH, 101);
            Label l27 = new Label();
            mv.visitJumpInsn(IF_ICMPNE, l27);
            Label l28 = new Label();
            mv.visitLabel(l28);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ALOAD, 13);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/invoke/MethodHandles$Lookup", "unreflectGetter", "(Ljava/lang/reflect/Field;)Ljava/lang/invoke/MethodHandle;", false);
            mv.visitVarInsn(ALOAD, 13);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/reflect/Field", "getType", "()Ljava/lang/Class;", false);
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/invoke/MethodType", "methodType", "(Ljava/lang/Class;)Ljava/lang/invoke/MethodType;", false);
            mv.visitInsn(ICONST_0);
            mv.visitInsn(ICONST_1);
            mv.visitTypeInsn(ANEWARRAY, "java/lang/Class");
            mv.visitInsn(DUP);
            mv.visitInsn(ICONST_0);
            mv.visitLdcInsn(Type.getType("Ljava/lang/Object;"));
            mv.visitInsn(AASTORE);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/invoke/MethodType", "insertParameterTypes", "(I[Ljava/lang/Class;)Ljava/lang/invoke/MethodType;", false);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/invoke/MethodHandle", "asType", "(Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/MethodHandle;", false);
            mv.visitVarInsn(ASTORE, 12);
            Label l29 = new Label();
            mv.visitLabel(l29);
            mv.visitJumpInsn(GOTO, l20);
            mv.visitLabel(l27);
            mv.visitVarInsn(ILOAD, 5);
            mv.visitIntInsn(BIPUSH, 102);
            Label l30 = new Label();
            mv.visitJumpInsn(IF_ICMPNE, l30);
            Label l31 = new Label();
            mv.visitLabel(l31);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ALOAD, 13);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/invoke/MethodHandles$Lookup", "unreflectSetter", "(Ljava/lang/reflect/Field;)Ljava/lang/invoke/MethodHandle;", false);
            mv.visitFieldInsn(GETSTATIC, "java/lang/Void", "TYPE", "Ljava/lang/Class;");
            mv.visitVarInsn(ALOAD, 13);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/reflect/Field", "getType", "()Ljava/lang/Class;", false);
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/invoke/MethodType", "methodType", "(Ljava/lang/Class;Ljava/lang/Class;)Ljava/lang/invoke/MethodType;", false);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/invoke/MethodHandle", "asType", "(Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/MethodHandle;", false);
            mv.visitVarInsn(ASTORE, 12);
            Label l32 = new Label();
            mv.visitLabel(l32);
            mv.visitJumpInsn(GOTO, l20);
            mv.visitLabel(l30);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ALOAD, 13);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/invoke/MethodHandles$Lookup", "unreflectSetter", "(Ljava/lang/reflect/Field;)Ljava/lang/invoke/MethodHandle;", false);
            mv.visitFieldInsn(GETSTATIC, "java/lang/Void", "TYPE", "Ljava/lang/Class;");
            mv.visitVarInsn(ALOAD, 13);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/reflect/Field", "getType", "()Ljava/lang/Class;", false);
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/invoke/MethodType", "methodType", "(Ljava/lang/Class;Ljava/lang/Class;)Ljava/lang/invoke/MethodType;", false);
            mv.visitInsn(ICONST_0);
            mv.visitInsn(ICONST_1);
            mv.visitTypeInsn(ANEWARRAY, "java/lang/Class");
            mv.visitInsn(DUP);
            mv.visitInsn(ICONST_0);
            mv.visitLdcInsn(Type.getType("Ljava/lang/Object;"));
            mv.visitInsn(AASTORE);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/invoke/MethodType", "insertParameterTypes", "(I[Ljava/lang/Class;)Ljava/lang/invoke/MethodType;", false);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/invoke/MethodHandle", "asType", "(Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/MethodHandle;", false);
            mv.visitVarInsn(ASTORE, 12);
            mv.visitLabel(l20);
            mv.visitVarInsn(ALOAD, 12);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/invoke/MethodType", "parameterCount", "()I", false);
            mv.visitInsn(ICONST_2);
            mv.visitInsn(ISUB);
            mv.visitInsn(ICONST_2);
            mv.visitTypeInsn(ANEWARRAY, "java/lang/Class");
            mv.visitInsn(DUP);
            mv.visitInsn(ICONST_0);
            mv.visitLdcInsn(Type.getType("Ljava/lang/String;"));
            mv.visitInsn(AASTORE);
            mv.visitInsn(DUP);
            mv.visitInsn(ICONST_1);
            mv.visitFieldInsn(GETSTATIC, "java/lang/Long", "TYPE", "Ljava/lang/Class;");
            mv.visitInsn(AASTORE);
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/invoke/MethodHandles", "dropArguments", "(Ljava/lang/invoke/MethodHandle;I[Ljava/lang/Class;)Ljava/lang/invoke/MethodHandle;", false);
            mv.visitLdcInsn(Type.getType("[Ljava/lang/Object;"));
            mv.visitVarInsn(ALOAD, 4);
            mv.visitInsn(ARRAYLENGTH);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/invoke/MethodHandle", "asSpreader", "(Ljava/lang/Class;I)Ljava/lang/invoke/MethodHandle;", false);
            mv.visitVarInsn(ALOAD, 4);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/invoke/MethodHandle", "invoke", "([Ljava/lang/Object;)Ljava/lang/Object;", false);
            mv.visitInsn(ARETURN);
            Label l33 = new Label();
            mv.visitLabel(l33);
            mv.visitMaxs(7, 15);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC + ACC_STATIC, memberNames.bootstrapMethodName, "(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", null, null);
            mv.visitCode();
            Label l0 = new Label();
            Label l1 = new Label();
            Label l2 = new Label();
            mv.visitTryCatchBlock(l0, l1, l2, "java/lang/Exception");
            Label l3 = new Label();
            mv.visitLabel(l3);
            mv.visitTypeInsn(NEW, "java/lang/invoke/MutableCallSite");
            mv.visitInsn(DUP);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitTypeInsn(CHECKCAST, "java/lang/invoke/MethodType");
            mv.visitMethodInsn(INVOKESPECIAL, "java/lang/invoke/MutableCallSite", "<init>", "(Ljava/lang/invoke/MethodType;)V", false);
            mv.visitVarInsn(ASTORE, 3);
            mv.visitLabel(l0);
            mv.visitVarInsn(ALOAD, 3);
            Label l4 = new Label();
            mv.visitLabel(l4);
            mv.visitLdcInsn(new Handle(H_INVOKESTATIC, memberNames.className, memberNames.resolveMethodHandleMethodName, "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MutableCallSite;[Ljava/lang/Object;)Ljava/lang/Object;", false));
            mv.visitLdcInsn(Type.getType("[Ljava/lang/Object;"));
            mv.visitVarInsn(ALOAD, 2);
            mv.visitTypeInsn(CHECKCAST, "java/lang/invoke/MethodType");
            Label l7 = new Label();
            mv.visitLabel(l7);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/invoke/MethodType", "parameterCount", "()I", false);
            Label l8 = new Label();
            mv.visitLabel(l8);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/invoke/MethodHandle", "asCollector", "(Ljava/lang/Class;I)Ljava/lang/invoke/MethodHandle;", false);
            mv.visitInsn(ICONST_0);
            mv.visitInsn(ICONST_4);
            mv.visitTypeInsn(ANEWARRAY, "java/lang/Object");
            mv.visitInsn(DUP);
            mv.visitInsn(ICONST_0);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitInsn(AASTORE);
            mv.visitInsn(DUP);
            mv.visitInsn(ICONST_1);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitInsn(AASTORE);
            mv.visitInsn(DUP);
            mv.visitInsn(ICONST_2);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitInsn(AASTORE);
            mv.visitInsn(DUP);
            mv.visitInsn(ICONST_3);
            mv.visitVarInsn(ALOAD, 3);
            mv.visitInsn(AASTORE);
            Label l9 = new Label();
            mv.visitLabel(l9);
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/invoke/MethodHandles", "insertArguments", "(Ljava/lang/invoke/MethodHandle;I[Ljava/lang/Object;)Ljava/lang/invoke/MethodHandle;", false);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitTypeInsn(CHECKCAST, "java/lang/invoke/MethodType");
            Label l10 = new Label();
            mv.visitLabel(l10);
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/invoke/MethodHandles", "explicitCastArguments", "(Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/MethodHandle;", false);
            Label l11 = new Label();
            mv.visitLabel(l11);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/invoke/MutableCallSite", "setTarget", "(Ljava/lang/invoke/MethodHandle;)V", false);
            Label l12 = new Label();
            mv.visitLabel(l12);
            mv.visitTypeInsn(NEW, "java/lang/invoke/ConstantCallSite");
            mv.visitInsn(DUP);
            mv.visitVarInsn(ALOAD, 3);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/invoke/CallSite", "getTarget", "()Ljava/lang/invoke/MethodHandle;", false);
            mv.visitLabel(l1);
            mv.visitMethodInsn(INVOKESPECIAL, "java/lang/invoke/ConstantCallSite", "<init>", "(Ljava/lang/invoke/MethodHandle;)V", false);
            mv.visitInsn(ARETURN);
            mv.visitLabel(l2);
            mv.visitVarInsn(ASTORE, 4);
            Label l13 = new Label();
            mv.visitLabel(l13);
            mv.visitVarInsn(ALOAD, 4);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Exception", "printStackTrace", "()V", false);
            Label l14 = new Label();
            mv.visitLabel(l14);
            mv.visitInsn(ACONST_NULL);
            mv.visitInsn(ARETURN);
            Label l15 = new Label();
            mv.visitLabel(l15);
            mv.visitMaxs(7, 5);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_STATIC, "<clinit>", "()V", null, null);
            mv.visitCode();
            Label l0 = new Label();
            mv.visitLabel(l0);
            mv.visitTypeInsn(NEW, "java/util/HashMap");
            mv.visitInsn(DUP);
            mv.visitMethodInsn(INVOKESPECIAL, "java/util/HashMap", "<init>", "()V", false);
            mv.visitFieldInsn(PUTSTATIC, memberNames.className, memberNames.methodCacheFieldName, "Ljava/util/Map;");
            Label l1 = new Label();
            mv.visitLabel(l1);
            mv.visitTypeInsn(NEW, "java/util/HashMap");
            mv.visitInsn(DUP);
            mv.visitMethodInsn(INVOKESPECIAL, "java/util/HashMap", "<init>", "()V", false);
            mv.visitFieldInsn(PUTSTATIC, memberNames.className, memberNames.fieldCacheFieldName, "Ljava/util/Map;");
            mv.visitInsn(RETURN);
            mv.visitMaxs(2, 0);
            mv.visitEnd();
        }
        cw.visitEnd();

        return cw;
    }

    private class MemberNames {
        private String className = StringUtils.randomClassName(getClasses().keySet());
        private String methodCacheFieldName = uniqueRandomString();
        private String fieldCacheFieldName = uniqueRandomString();
        private String hashMethodName = uniqueRandomString();
        private String findMethodMethodName = uniqueRandomString();
        private String findFieldMethodName = uniqueRandomString();
        private String resolveMethodHandleMethodName = uniqueRandomString();
        private String bootstrapMethodName = uniqueRandomString();
    }
}
