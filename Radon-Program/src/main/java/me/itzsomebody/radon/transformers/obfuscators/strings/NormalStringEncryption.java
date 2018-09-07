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

public class NormalStringEncryption extends StringEncryption {
    public NormalStringEncryption(StringEncryptionSetup setup) {
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
                            int callerClassHC = classWrapper.classNode.name.replace("/", ".").hashCode();
                            int callerMethodHC = methodNode.name.hashCode();
                            int decryptorClassHC = memberNames.className.replace("/", ".").hashCode();
                            ldc.cst = encrypt(cst, decryptorClassHC, callerClassHC, callerMethodHC, extraKey);
                            methodNode.instructions.insert(insn, new MethodInsnNode(INVOKESTATIC, memberNames.className, memberNames.decryptMethodName, "(Ljava/lang/Object;I)Ljava/lang/String;", false));
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

    @Override
    public String getName() {
        return "Normal string encryption";
    }

    private static String encrypt(String msg, int decryptorClassHC, int callerClassNameHC, int callerMethodNameHC, int extraKey) {
        char[] chars = msg.toCharArray();
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < chars.length; i++) {
            switch (i % 8) {
                case 0: {
                    sb.append((char) (chars[i] ^ callerClassNameHC ^ extraKey));
                    break;
                }
                case 1: {
                    sb.append((char) (chars[i] ^ "<clinit>".hashCode() ^ callerMethodNameHC));
                    break;
                }
                case 2: {
                    sb.append((char) (chars[i] ^ decryptorClassHC ^ callerClassNameHC));
                    break;
                }
                case 3: {
                    sb.append((char) (chars[i] ^ extraKey ^ "<clinit>".hashCode()));
                    break;
                }
                case 4: {
                    sb.append((char) (chars[i] ^ callerMethodNameHC ^ decryptorClassHC));
                    break;
                }
                case 5: {
                    sb.append((char) (chars[i] ^ callerClassNameHC ^ "<clinit>".hashCode()));
                    break;
                }
                case 6: {
                    sb.append((char) (chars[i] ^ callerMethodNameHC ^ callerClassNameHC));
                    break;
                }
                case 7: {
                    sb.append((char) (chars[i] ^ extraKey ^ callerMethodNameHC));
                    break;
                }
            }
        }

        return sb.toString();
    }

    private ClassNode createDecryptor(MemberNames memberNames) {
        ClassNode cw = new ClassNode();
        FieldVisitor fv;
        MethodVisitor mv;

        cw.visit(V1_5, ACC_PUBLIC + ACC_SUPER, memberNames.className, null, "java/lang/Object", null);

        {
            fv = cw.visitField(ACC_PRIVATE + ACC_STATIC, memberNames.cacheFieldName, "Ljava/util/HashMap;", null, null);
            fv.visitEnd();
        }
        {
            fv = cw.visitField(ACC_PRIVATE + ACC_STATIC, memberNames.key1FieldName, "I", null, null);
            fv.visitEnd();
        }
        {
            fv = cw.visitField(ACC_PRIVATE + ACC_STATIC, memberNames.key2FieldName, "I", null, null);
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
            Label l1 = new Label();
            mv.visitLabel(l1);
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Thread", "currentThread", "()Ljava/lang/Thread;", false);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Thread", "getStackTrace", "()[Ljava/lang/StackTraceElement;", false);
            mv.visitInsn(ICONST_1);
            mv.visitInsn(AALOAD);
            mv.visitVarInsn(ASTORE, 0);
            Label l2 = new Label();
            mv.visitLabel(l2);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StackTraceElement", "getClassName", "()Ljava/lang/String;", false);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "hashCode", "()I", false);
            mv.visitFieldInsn(PUTSTATIC, memberNames.className, memberNames.key1FieldName, "I");
            Label l3 = new Label();
            mv.visitLabel(l3);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StackTraceElement", "getMethodName", "()Ljava/lang/String;", false);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "hashCode", "()I", false);
            mv.visitFieldInsn(PUTSTATIC, memberNames.className, memberNames.key2FieldName, "I");
            Label l4 = new Label();
            mv.visitLabel(l4);
            mv.visitInsn(RETURN);
            mv.visitMaxs(2, 1);
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
            mv = cw.visitMethod(ACC_PRIVATE + ACC_STATIC, memberNames.hashMethodName, "([C)I", null, null);
            mv.visitCode();
            Label l0 = new Label();
            mv.visitLabel(l0);
            mv.visitInsn(ICONST_1);
            mv.visitVarInsn(ISTORE, 1);
            Label l1 = new Label();
            mv.visitLabel(l1);
            mv.visitInsn(ICONST_0);
            mv.visitVarInsn(ISTORE, 2);
            Label l2 = new Label();
            mv.visitLabel(l2);
            Label l3 = new Label();
            mv.visitJumpInsn(GOTO, l3);
            Label l4 = new Label();
            mv.visitLabel(l4);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ILOAD, 2);
            mv.visitInsn(CALOAD);
            mv.visitVarInsn(ISTORE, 3);
            Label l5 = new Label();
            mv.visitLabel(l5);
            mv.visitVarInsn(ILOAD, 3);
            mv.visitIntInsn(SIPUSH, 255);
            mv.visitInsn(IAND);
            mv.visitVarInsn(ISTORE, 4);
            Label l6 = new Label();
            mv.visitLabel(l6);
            mv.visitVarInsn(ILOAD, 3);
            mv.visitIntInsn(SIPUSH, 255);
            mv.visitInsn(IOR);
            mv.visitVarInsn(ISTORE, 5);
            Label l7 = new Label();
            mv.visitLabel(l7);
            mv.visitVarInsn(ILOAD, 3);
            mv.visitIntInsn(SIPUSH, 255);
            mv.visitInsn(IXOR);
            mv.visitVarInsn(ISTORE, 6);
            Label l8 = new Label();
            mv.visitLabel(l8);
            mv.visitVarInsn(ILOAD, 4);
            mv.visitInsn(ICONST_4);
            mv.visitInsn(ISHL);
            mv.visitVarInsn(ILOAD, 5);
            mv.visitInsn(ICONST_4);
            mv.visitInsn(IUSHR);
            mv.visitInsn(IOR);
            mv.visitVarInsn(ISTORE, 7);
            Label l9 = new Label();
            mv.visitLabel(l9);
            mv.visitVarInsn(ILOAD, 6);
            mv.visitInsn(ICONST_3);
            mv.visitInsn(ISHL);
            mv.visitVarInsn(ILOAD, 7);
            mv.visitIntInsn(BIPUSH, 6);
            mv.visitInsn(IUSHR);
            mv.visitInsn(IOR);
            mv.visitVarInsn(ISTORE, 8);
            Label l10 = new Label();
            mv.visitLabel(l10);
            mv.visitVarInsn(ILOAD, 4);
            mv.visitVarInsn(ILOAD, 7);
            mv.visitInsn(ICONST_2);
            mv.visitInsn(ISHL);
            mv.visitVarInsn(ILOAD, 4);
            mv.visitInsn(ICONST_2);
            mv.visitInsn(IUSHR);
            mv.visitInsn(IOR);
            mv.visitInsn(IAND);
            mv.visitVarInsn(ISTORE, 4);
            Label l11 = new Label();
            mv.visitLabel(l11);
            mv.visitVarInsn(ILOAD, 6);
            mv.visitVarInsn(ILOAD, 4);
            mv.visitInsn(ICONST_4);
            mv.visitInsn(ISHR);
            mv.visitVarInsn(ILOAD, 5);
            mv.visitInsn(ICONST_2);
            mv.visitInsn(ISHL);
            mv.visitInsn(IOR);
            mv.visitInsn(IOR);
            mv.visitVarInsn(ISTORE, 6);
            Label l12 = new Label();
            mv.visitLabel(l12);
            mv.visitVarInsn(ILOAD, 5);
            mv.visitVarInsn(ILOAD, 8);
            mv.visitInsn(ICONST_4);
            mv.visitInsn(IUSHR);
            mv.visitVarInsn(ILOAD, 6);
            mv.visitIntInsn(BIPUSH, 6);
            mv.visitInsn(ISHL);
            mv.visitInsn(IOR);
            mv.visitInsn(IXOR);
            mv.visitVarInsn(ISTORE, 5);
            Label l13 = new Label();
            mv.visitLabel(l13);
            mv.visitVarInsn(ILOAD, 7);
            mv.visitVarInsn(ILOAD, 5);
            mv.visitInsn(IADD);
            mv.visitVarInsn(ISTORE, 7);
            Label l14 = new Label();
            mv.visitLabel(l14);
            mv.visitVarInsn(ILOAD, 4);
            mv.visitInsn(ICONST_5);
            mv.visitInsn(IUSHR);
            mv.visitVarInsn(ILOAD, 6);
            mv.visitInsn(ICONST_2);
            mv.visitInsn(ISHL);
            mv.visitInsn(IOR);
            mv.visitVarInsn(ISTORE, 8);
            Label l15 = new Label();
            mv.visitLabel(l15);
            mv.visitVarInsn(ILOAD, 1);
            mv.visitVarInsn(ILOAD, 4);
            mv.visitVarInsn(ILOAD, 5);
            mv.visitInsn(IXOR);
            mv.visitVarInsn(ILOAD, 6);
            mv.visitInsn(IXOR);
            mv.visitVarInsn(ILOAD, 7);
            mv.visitInsn(IXOR);
            mv.visitVarInsn(ILOAD, 8);
            mv.visitInsn(IXOR);
            mv.visitInsn(IXOR);
            mv.visitVarInsn(ISTORE, 1);
            Label l16 = new Label();
            mv.visitLabel(l16);
            mv.visitIincInsn(2, 1);
            mv.visitLabel(l3);
            mv.visitVarInsn(ILOAD, 2);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitInsn(ARRAYLENGTH);
            mv.visitJumpInsn(IF_ICMPLT, l4);
            Label l17 = new Label();
            mv.visitLabel(l17);
            mv.visitVarInsn(ILOAD, 1);
            mv.visitInsn(IRETURN);
            Label l18 = new Label();
            mv.visitLabel(l18);
            mv.visitMaxs(4, 9);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PRIVATE + ACC_STATIC, memberNames.returnCacheMethodName, "(I)Ljava/lang/String;", null, null);
            mv.visitCode();
            Label l0 = new Label();
            mv.visitLabel(l0);
            mv.visitFieldInsn(GETSTATIC, memberNames.className, memberNames.cacheFieldName, "Ljava/util/HashMap;");
            mv.visitVarInsn(ILOAD, 0);
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/util/HashMap", "get", "(Ljava/lang/Object;)Ljava/lang/Object;", false);
            mv.visitTypeInsn(CHECKCAST, "java/lang/String");
            mv.visitInsn(ARETURN);
            Label l1 = new Label();
            mv.visitLabel(l1);
            mv.visitMaxs(2, 1);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PRIVATE + ACC_STATIC, memberNames.cacheStringMethodName, "(Ljava/lang/String;I)V", null, null);
            mv.visitCode();
            Label l0 = new Label();
            mv.visitLabel(l0);
            mv.visitFieldInsn(GETSTATIC, memberNames.className, memberNames.cacheFieldName, "Ljava/util/HashMap;");
            mv.visitVarInsn(ILOAD, 1);
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
            mv.visitVarInsn(ALOAD, 0);
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
            mv = cw.visitMethod(ACC_PUBLIC + ACC_STATIC, memberNames.decryptMethodName, "(Ljava/lang/Object;I)Ljava/lang/String;", null, null);
            mv.visitCode();
            Label l0 = new Label();
            mv.visitLabel(l0);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitTypeInsn(CHECKCAST, "java/lang/String");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "toCharArray", "()[C", false);
            mv.visitVarInsn(ASTORE, 2);
            Label l1 = new Label();
            mv.visitLabel(l1);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitMethodInsn(INVOKESTATIC, memberNames.className, memberNames.hashMethodName, "([C)I", false);
            mv.visitVarInsn(ISTORE, 3);
            Label l2 = new Label();
            mv.visitLabel(l2);
            mv.visitVarInsn(ILOAD, 3);
            mv.visitMethodInsn(INVOKESTATIC, memberNames.className, memberNames.returnCacheMethodName, "(I)Ljava/lang/String;", false);
            mv.visitVarInsn(ASTORE, 4);
            Label l3 = new Label();
            mv.visitLabel(l3);
            mv.visitVarInsn(ALOAD, 4);
            Label l4 = new Label();
            mv.visitJumpInsn(IFNULL, l4);
            Label l5 = new Label();
            mv.visitLabel(l5);
            mv.visitVarInsn(ALOAD, 4);
            mv.visitInsn(ARETURN);
            mv.visitLabel(l4);
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Thread", "currentThread", "()Ljava/lang/Thread;", false);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Thread", "getStackTrace", "()[Ljava/lang/StackTraceElement;", false);
            mv.visitVarInsn(ASTORE, 5);
            Label l6 = new Label();
            mv.visitLabel(l6);
            mv.visitVarInsn(ALOAD, 5);
            mv.visitInsn(ICONST_2);
            mv.visitInsn(AALOAD);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StackTraceElement", "getClassName", "()Ljava/lang/String;", false);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "hashCode", "()I", false);
            mv.visitVarInsn(ISTORE, 6);
            Label l7 = new Label();
            mv.visitLabel(l7);
            mv.visitVarInsn(ALOAD, 5);
            mv.visitInsn(ICONST_2);
            mv.visitInsn(AALOAD);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StackTraceElement", "getMethodName", "()Ljava/lang/String;", false);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "hashCode", "()I", false);
            mv.visitVarInsn(ISTORE, 7);
            Label l8 = new Label();
            mv.visitLabel(l8);
            mv.visitTypeInsn(NEW, "java/lang/StringBuilder");
            mv.visitInsn(DUP);
            mv.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "()V", false);
            mv.visitVarInsn(ASTORE, 8);
            Label l9 = new Label();
            mv.visitLabel(l9);
            mv.visitInsn(ICONST_0);
            mv.visitVarInsn(ISTORE, 9);
            Label l10 = new Label();
            mv.visitLabel(l10);
            Label l11 = new Label();
            mv.visitJumpInsn(GOTO, l11);
            Label l12 = new Label();
            mv.visitLabel(l12);
            mv.visitVarInsn(ILOAD, 9);
            mv.visitIntInsn(BIPUSH, 8);
            mv.visitInsn(IREM);
            Label l13 = new Label();
            Label l14 = new Label();
            Label l15 = new Label();
            Label l16 = new Label();
            Label l17 = new Label();
            Label l18 = new Label();
            Label l19 = new Label();
            Label l20 = new Label();
            Label l21 = new Label();
            mv.visitTableSwitchInsn(0, 7, l21, l13, l14, l15, l16, l17, l18, l19, l20);
            mv.visitLabel(l13);
            mv.visitVarInsn(ALOAD, 8);
            mv.visitVarInsn(ILOAD, 1);
            mv.visitVarInsn(ILOAD, 6);
            mv.visitInsn(IXOR);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitVarInsn(ILOAD, 9);
            mv.visitInsn(CALOAD);
            mv.visitInsn(IXOR);
            mv.visitInsn(I2C);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(C)Ljava/lang/StringBuilder;", false);
            mv.visitInsn(POP);
            Label l22 = new Label();
            mv.visitLabel(l22);
            mv.visitJumpInsn(GOTO, l21);
            mv.visitLabel(l14);
            mv.visitVarInsn(ALOAD, 8);
            mv.visitVarInsn(ILOAD, 7);
            mv.visitFieldInsn(GETSTATIC, memberNames.className, memberNames.key2FieldName, "I");
            mv.visitInsn(IXOR);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitVarInsn(ILOAD, 9);
            mv.visitInsn(CALOAD);
            mv.visitInsn(IXOR);
            mv.visitInsn(I2C);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(C)Ljava/lang/StringBuilder;", false);
            mv.visitInsn(POP);
            Label l23 = new Label();
            mv.visitLabel(l23);
            mv.visitJumpInsn(GOTO, l21);
            mv.visitLabel(l15);
            mv.visitVarInsn(ALOAD, 8);
            mv.visitVarInsn(ILOAD, 6);
            mv.visitFieldInsn(GETSTATIC, memberNames.className, memberNames.key1FieldName, "I");
            mv.visitInsn(IXOR);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitVarInsn(ILOAD, 9);
            mv.visitInsn(CALOAD);
            mv.visitInsn(IXOR);
            mv.visitInsn(I2C);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(C)Ljava/lang/StringBuilder;", false);
            mv.visitInsn(POP);
            Label l24 = new Label();
            mv.visitLabel(l24);
            mv.visitJumpInsn(GOTO, l21);
            mv.visitLabel(l16);
            mv.visitVarInsn(ALOAD, 8);
            mv.visitFieldInsn(GETSTATIC, memberNames.className, memberNames.key2FieldName, "I");
            mv.visitVarInsn(ILOAD, 1);
            mv.visitInsn(IXOR);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitVarInsn(ILOAD, 9);
            mv.visitInsn(CALOAD);
            mv.visitInsn(IXOR);
            mv.visitInsn(I2C);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(C)Ljava/lang/StringBuilder;", false);
            mv.visitInsn(POP);
            Label l25 = new Label();
            mv.visitLabel(l25);
            mv.visitJumpInsn(GOTO, l21);
            mv.visitLabel(l17);
            mv.visitVarInsn(ALOAD, 8);
            mv.visitFieldInsn(GETSTATIC, memberNames.className, memberNames.key1FieldName, "I");
            mv.visitVarInsn(ILOAD, 7);
            mv.visitInsn(IXOR);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitVarInsn(ILOAD, 9);
            mv.visitInsn(CALOAD);
            mv.visitInsn(IXOR);
            mv.visitInsn(I2C);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(C)Ljava/lang/StringBuilder;", false);
            mv.visitInsn(POP);
            Label l26 = new Label();
            mv.visitLabel(l26);
            mv.visitJumpInsn(GOTO, l21);
            mv.visitLabel(l18);
            mv.visitVarInsn(ALOAD, 8);
            mv.visitFieldInsn(GETSTATIC, memberNames.className, memberNames.key2FieldName, "I");
            mv.visitVarInsn(ILOAD, 6);
            mv.visitInsn(IXOR);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitVarInsn(ILOAD, 9);
            mv.visitInsn(CALOAD);
            mv.visitInsn(IXOR);
            mv.visitInsn(I2C);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(C)Ljava/lang/StringBuilder;", false);
            mv.visitInsn(POP);
            Label l27 = new Label();
            mv.visitLabel(l27);
            mv.visitJumpInsn(GOTO, l21);
            mv.visitLabel(l19);
            mv.visitVarInsn(ALOAD, 8);
            mv.visitVarInsn(ILOAD, 6);
            mv.visitVarInsn(ILOAD, 7);
            mv.visitInsn(IXOR);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitVarInsn(ILOAD, 9);
            mv.visitInsn(CALOAD);
            mv.visitInsn(IXOR);
            mv.visitInsn(I2C);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(C)Ljava/lang/StringBuilder;", false);
            mv.visitInsn(POP);
            Label l28 = new Label();
            mv.visitLabel(l28);
            mv.visitJumpInsn(GOTO, l21);
            mv.visitLabel(l20);
            mv.visitVarInsn(ALOAD, 8);
            mv.visitVarInsn(ILOAD, 7);
            mv.visitVarInsn(ILOAD, 1);
            mv.visitInsn(IXOR);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitVarInsn(ILOAD, 9);
            mv.visitInsn(CALOAD);
            mv.visitInsn(IXOR);
            mv.visitInsn(I2C);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(C)Ljava/lang/StringBuilder;", false);
            mv.visitInsn(POP);
            mv.visitLabel(l21);
            mv.visitIincInsn(9, 1);
            mv.visitLabel(l11);
            mv.visitVarInsn(ILOAD, 9);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitInsn(ARRAYLENGTH);
            mv.visitJumpInsn(IF_ICMPLT, l12);
            Label l29 = new Label();
            mv.visitLabel(l29);
            mv.visitVarInsn(ALOAD, 8);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false);
            mv.visitVarInsn(ASTORE, 9);
            Label l30 = new Label();
            mv.visitLabel(l30);
            mv.visitVarInsn(ALOAD, 9);
            mv.visitVarInsn(ILOAD, 3);
            mv.visitMethodInsn(INVOKESTATIC, memberNames.className, memberNames.cacheStringMethodName, "(Ljava/lang/String;I)V", false);
            Label l31 = new Label();
            mv.visitLabel(l31);
            mv.visitVarInsn(ALOAD, 9);
            mv.visitInsn(ARETURN);
            Label l32 = new Label();
            mv.visitLabel(l32);
            mv.visitMaxs(4, 10);
            mv.visitEnd();
        }
        cw.visitEnd();

        return cw;
    }

    private class MemberNames {
        private String className;
        private String cacheFieldName;
        private String key1FieldName;
        private String key2FieldName;
        private String hashMethodName;
        private String returnCacheMethodName;
        private String cacheStringMethodName;
        private String decryptMethodName;

        private MemberNames() {
            this.className = StringUtils.randomClassName(getClasses().keySet());
            this.cacheFieldName = randomString(4);
            this.key1FieldName = randomString(4);
            this.key2FieldName = randomString(4);
            this.hashMethodName = randomString(4);
            this.returnCacheMethodName = randomString(4);
            this.cacheStringMethodName = randomString(4);
            this.decryptMethodName = randomString(4);
        }
    }
}
