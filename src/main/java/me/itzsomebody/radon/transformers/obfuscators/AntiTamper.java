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

package me.itzsomebody.radon.transformers.obfuscators;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import me.itzsomebody.radon.Main;
import me.itzsomebody.radon.asm.ClassWrapper;
import me.itzsomebody.radon.asm.MethodWrapper;
import me.itzsomebody.radon.config.ConfigurationSetting;
import me.itzsomebody.radon.exceptions.InvalidConfigurationValueException;
import me.itzsomebody.radon.exclusions.ExclusionType;
import me.itzsomebody.radon.transformers.Transformer;
import me.itzsomebody.radon.utils.RandomUtils;
import me.itzsomebody.radon.utils.StringUtils;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;

/**
 * This applies passive integrity checking to the application with a special
 * type of string encryption.
 * FIXME: kinda broken
 *
 * @author ItzSomebody
 */
public class AntiTamper extends Transformer {
    @Override
    public void transform() {
        MemberNames memberNames = new MemberNames();
        AtomicInteger counter = new AtomicInteger();

        getClassWrappers().stream().filter(classWrapper -> !excluded(classWrapper)).forEach(cw -> {
            Set<MethodWrapper> toProcess = new HashSet<>();

            cw.getMethods().stream().filter(mw -> !excluded(mw)).forEach(mw ->
                    Stream.of(mw.getInstructions().toArray()).filter(insn -> insn instanceof LdcInsnNode
                            && ((LdcInsnNode) insn).cst instanceof String).forEach(insn -> {
                        toProcess.add(mw);

                        mw.getMethodNode().instructions.insert(insn, new MethodInsnNode(
                                INVOKESTATIC,
                                memberNames.className,
                                memberNames.decryptMethodName,
                                "(Ljava/lang/String;)Ljava/lang/String;",
                                false
                        ));

                        counter.incrementAndGet();
                    }));

            if (counter.get() > 0) {
                IntStream.range(0, 120).forEach(i -> cw.addStringConst(StringUtils.randomAlphaString(RandomUtils.getRandomInt(0, 32))));

                int cpSize = new ClassReader(cw.toByteArray()).getItemCount();

                toProcess.forEach(methodWrapper -> Stream.of(methodWrapper.getMethodNode().instructions.toArray()).filter(insn -> insn instanceof LdcInsnNode
                        && ((LdcInsnNode) insn).cst instanceof String).forEach(insn -> {
                    LdcInsnNode ldc = (LdcInsnNode) insn;
                    String s = (String) ldc.cst;
                    ldc.cst = encrypt(s, memberNames, cw.getName().replace('/', '.'), methodWrapper.getMethodNode().name, cpSize);
                }));
            }
        });

        ClassNode decryptor = createDecryptor(memberNames);
        getClasses().put(decryptor.name, new ClassWrapper(decryptor, false));

        Main.info("Encrypted " + counter.get() + " strings with anti-tamper algorithm");
    }

    @Override
    public String getName() {
        return "Anti-Tamper";
    }

    @Override
    public ExclusionType getExclusionType() {
        return ExclusionType.ANTI_TAMPER;
    }

    @Override
    public Object getConfiguration() {
        return true;
    }

    @Override
    public void setConfiguration(Map<String, Object> config) {
        // Not needed
    }

    @Override
    public void verifyConfiguration(Map<String, Object> config) {
        throw new InvalidConfigurationValueException(ConfigurationSetting.ANTI_TAMPER + " expects a boolean");
    }

    private static String encrypt(String s, MemberNames memberNames, String className, String methodName, int cpSize) {
        char[] chars = s.toCharArray();
        char[] encrypted = new char[chars.length];

        int keyOne = memberNames.className.replace('/', '.').hashCode() ^ memberNames.decryptMethodName.hashCode() ^ className.hashCode() ^ methodName.hashCode() ^ cpSize;
        int keyTwo = className.hashCode() + methodName.hashCode() ^ memberNames.className.replace('/', '.').hashCode() ^ memberNames.decryptMethodName.hashCode() ^ cpSize;
        int keyThree = className.hashCode() - methodName.hashCode() ^ memberNames.className.replace('/', '.').hashCode() ^ memberNames.decryptMethodName.hashCode() ^ cpSize;
        int keyFour = className.hashCode() & methodName.hashCode() ^ memberNames.className.replace('/', '.').hashCode() ^ memberNames.decryptMethodName.hashCode() ^ cpSize;

        for (int i = 0; i < chars.length; i++) {
            switch (i % 4) {
                case 0:
                    encrypted[i] = (char) (chars[i] ^ keyOne);
                    break;
                case 1:
                    encrypted[i] = (char) (chars[i] ^ keyTwo);
                    break;
                case 2:
                    encrypted[i] = (char) (chars[i] ^ keyThree);
                    break;
                default:
                    encrypted[i] = (char) (chars[i] ^ keyFour);
                    break;
            }
        }

        return new String(encrypted);
    }

    @SuppressWarnings("Duplicates")
    private static ClassNode createDecryptor(MemberNames memberNames) {
        ClassNode cw = new ClassNode();
        MethodVisitor mv;

        cw.visit(V1_5, ACC_PUBLIC + ACC_SUPER, memberNames.className, null, "java/lang/Object", null);

        {
            mv = cw.visitMethod(ACC_PUBLIC + ACC_STATIC, memberNames.decryptMethodName, "(Ljava/lang/String;)Ljava/lang/String;", null, null);
            mv.visitCode();
            Label l0 = new Label();
            mv.visitLabel(l0);
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Thread", "currentThread", "()Ljava/lang/Thread;", false);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Thread", "getStackTrace", "()[Ljava/lang/StackTraceElement;", false);
            mv.visitVarInsn(ASTORE, 1);
            Label l1 = new Label();
            mv.visitLabel(l1);
            mv.visitLdcInsn(Type.getType("L" + memberNames.className + ";"));
            mv.visitTypeInsn(NEW, "java/lang/StringBuilder");
            mv.visitInsn(DUP);
            mv.visitLdcInsn("/");
            mv.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "(Ljava/lang/String;)V", false);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitInsn(ICONST_2);
            mv.visitInsn(AALOAD);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StackTraceElement", "getClassName", "()Ljava/lang/String;", false);
            mv.visitIntInsn(BIPUSH, 46);
            mv.visitIntInsn(BIPUSH, 47);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "replace", "(CC)Ljava/lang/String;", false);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
            mv.visitLdcInsn(".class");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Class", "getResourceAsStream", "(Ljava/lang/String;)Ljava/io/InputStream;", false);
            mv.visitVarInsn(ASTORE, 2);
            Label l2 = new Label();
            mv.visitLabel(l2);
            mv.visitTypeInsn(NEW, "java/io/ByteArrayOutputStream");
            mv.visitInsn(DUP);
            mv.visitMethodInsn(INVOKESPECIAL, "java/io/ByteArrayOutputStream", "<init>", "()V", false);
            mv.visitVarInsn(ASTORE, 3);
            Label l3 = new Label();
            mv.visitLabel(l3);
            mv.visitIntInsn(SIPUSH, 1024);
            mv.visitIntInsn(NEWARRAY, T_BYTE);
            mv.visitVarInsn(ASTORE, 4);
            Label l4 = new Label();
            mv.visitLabel(l4);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/InputStream", "available", "()I", false);
            Label l5 = new Label();
            mv.visitJumpInsn(IFLE, l5);
            Label l6 = new Label();
            mv.visitLabel(l6);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitVarInsn(ALOAD, 4);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/InputStream", "read", "([B)I", false);
            mv.visitVarInsn(ISTORE, 5);
            Label l7 = new Label();
            mv.visitLabel(l7);
            mv.visitVarInsn(ALOAD, 3);
            mv.visitVarInsn(ALOAD, 4);
            mv.visitInsn(ICONST_0);
            mv.visitVarInsn(ILOAD, 5);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/ByteArrayOutputStream", "write", "([BII)V", false);
            Label l8 = new Label();
            mv.visitLabel(l8);
            mv.visitJumpInsn(GOTO, l4);
            mv.visitLabel(l5);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/InputStream", "close", "()V", false);
            Label l9 = new Label();
            mv.visitLabel(l9);
            mv.visitVarInsn(ALOAD, 3);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/ByteArrayOutputStream", "close", "()V", false);
            Label l10 = new Label();
            mv.visitLabel(l10);
            mv.visitVarInsn(ALOAD, 3);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/ByteArrayOutputStream", "toByteArray", "()[B", false);
            mv.visitVarInsn(ASTORE, 5);
            Label l11 = new Label();
            mv.visitLabel(l11);
            mv.visitVarInsn(ALOAD, 5);
            mv.visitIntInsn(BIPUSH, 8);
            mv.visitInsn(BALOAD);
            mv.visitIntInsn(SIPUSH, 255);
            mv.visitInsn(IAND);
            mv.visitIntInsn(BIPUSH, 8);
            mv.visitInsn(ISHL);
            mv.visitVarInsn(ALOAD, 5);
            mv.visitIntInsn(BIPUSH, 9);
            mv.visitInsn(BALOAD);
            mv.visitIntInsn(SIPUSH, 255);
            mv.visitInsn(IAND);
            mv.visitInsn(IOR);
            mv.visitVarInsn(ISTORE, 6);
            Label l12 = new Label();
            mv.visitLabel(l12);
            Label l13 = new Label();
            mv.visitLabel(l13);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitInsn(ICONST_1);
            mv.visitInsn(AALOAD);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StackTraceElement", "getClassName", "()Ljava/lang/String;", false);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "hashCode", "()I", false);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitInsn(ICONST_1);
            mv.visitInsn(AALOAD);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StackTraceElement", "getMethodName", "()Ljava/lang/String;", false);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "hashCode", "()I", false);
            mv.visitInsn(IXOR);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitInsn(ICONST_2);
            mv.visitInsn(AALOAD);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StackTraceElement", "getClassName", "()Ljava/lang/String;", false);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "hashCode", "()I", false);
            mv.visitInsn(IXOR);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitInsn(ICONST_2);
            mv.visitInsn(AALOAD);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StackTraceElement", "getMethodName", "()Ljava/lang/String;", false);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "hashCode", "()I", false);
            mv.visitInsn(IXOR);
            mv.visitVarInsn(ILOAD, 6);
            mv.visitInsn(IXOR);
            mv.visitVarInsn(ISTORE, 7);
            Label l14 = new Label();
            mv.visitLabel(l14);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitInsn(ICONST_2);
            mv.visitInsn(AALOAD);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StackTraceElement", "getClassName", "()Ljava/lang/String;", false);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "hashCode", "()I", false);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitInsn(ICONST_2);
            mv.visitInsn(AALOAD);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StackTraceElement", "getMethodName", "()Ljava/lang/String;", false);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "hashCode", "()I", false);
            mv.visitInsn(IADD);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitInsn(ICONST_1);
            mv.visitInsn(AALOAD);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StackTraceElement", "getClassName", "()Ljava/lang/String;", false);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "hashCode", "()I", false);
            mv.visitInsn(IXOR);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitInsn(ICONST_1);
            mv.visitInsn(AALOAD);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StackTraceElement", "getMethodName", "()Ljava/lang/String;", false);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "hashCode", "()I", false);
            mv.visitInsn(IXOR);
            mv.visitVarInsn(ILOAD, 6);
            mv.visitInsn(IXOR);
            mv.visitVarInsn(ISTORE, 8);
            Label l15 = new Label();
            mv.visitLabel(l15);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitInsn(ICONST_2);
            mv.visitInsn(AALOAD);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StackTraceElement", "getClassName", "()Ljava/lang/String;", false);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "hashCode", "()I", false);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitInsn(ICONST_2);
            mv.visitInsn(AALOAD);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StackTraceElement", "getMethodName", "()Ljava/lang/String;", false);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "hashCode", "()I", false);
            mv.visitInsn(ISUB);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitInsn(ICONST_1);
            mv.visitInsn(AALOAD);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StackTraceElement", "getClassName", "()Ljava/lang/String;", false);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "hashCode", "()I", false);
            mv.visitInsn(IXOR);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitInsn(ICONST_1);
            mv.visitInsn(AALOAD);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StackTraceElement", "getMethodName", "()Ljava/lang/String;", false);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "hashCode", "()I", false);
            mv.visitInsn(IXOR);
            mv.visitVarInsn(ILOAD, 6);
            mv.visitInsn(IXOR);
            mv.visitVarInsn(ISTORE, 9);
            Label l16 = new Label();
            mv.visitLabel(l16);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitInsn(ICONST_2);
            mv.visitInsn(AALOAD);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StackTraceElement", "getClassName", "()Ljava/lang/String;", false);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "hashCode", "()I", false);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitInsn(ICONST_2);
            mv.visitInsn(AALOAD);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StackTraceElement", "getMethodName", "()Ljava/lang/String;", false);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "hashCode", "()I", false);
            mv.visitInsn(IAND);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitInsn(ICONST_1);
            mv.visitInsn(AALOAD);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StackTraceElement", "getClassName", "()Ljava/lang/String;", false);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "hashCode", "()I", false);
            mv.visitInsn(IXOR);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitInsn(ICONST_1);
            mv.visitInsn(AALOAD);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StackTraceElement", "getMethodName", "()Ljava/lang/String;", false);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "hashCode", "()I", false);
            mv.visitInsn(IXOR);
            mv.visitVarInsn(ILOAD, 6);
            mv.visitInsn(IXOR);
            mv.visitVarInsn(ISTORE, 10);
            Label l17 = new Label();
            mv.visitLabel(l17);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "toCharArray", "()[C", false);
            mv.visitVarInsn(ASTORE, 11);
            Label l18 = new Label();
            mv.visitLabel(l18);
            mv.visitVarInsn(ALOAD, 11);
            mv.visitInsn(ARRAYLENGTH);
            mv.visitIntInsn(NEWARRAY, T_CHAR);
            mv.visitVarInsn(ASTORE, 12);
            Label l19 = new Label();
            mv.visitLabel(l19);
            mv.visitInsn(ICONST_0);
            mv.visitVarInsn(ISTORE, 13);
            Label l20 = new Label();
            mv.visitLabel(l20);
            mv.visitVarInsn(ILOAD, 13);
            mv.visitVarInsn(ALOAD, 11);
            mv.visitInsn(ARRAYLENGTH);
            Label l21 = new Label();
            mv.visitJumpInsn(IF_ICMPGE, l21);
            Label l22 = new Label();
            mv.visitLabel(l22);
            mv.visitVarInsn(ILOAD, 13);
            mv.visitInsn(ICONST_4);
            mv.visitInsn(IREM);
            Label l23 = new Label();
            Label l24 = new Label();
            Label l25 = new Label();
            Label l26 = new Label();
            Label l27 = new Label();
            mv.visitTableSwitchInsn(0, 3, l27, l23, l24, l25, l26);
            mv.visitLabel(l23);
            mv.visitVarInsn(ALOAD, 12);
            mv.visitVarInsn(ILOAD, 13);
            mv.visitVarInsn(ALOAD, 11);
            mv.visitVarInsn(ILOAD, 13);
            mv.visitInsn(CALOAD);
            mv.visitVarInsn(ILOAD, 7);
            mv.visitInsn(IXOR);
            mv.visitInsn(I2C);
            mv.visitInsn(CASTORE);
            Label l28 = new Label();
            mv.visitLabel(l28);
            mv.visitJumpInsn(GOTO, l27);
            mv.visitLabel(l24);
            mv.visitVarInsn(ALOAD, 12);
            mv.visitVarInsn(ILOAD, 13);
            mv.visitVarInsn(ALOAD, 11);
            mv.visitVarInsn(ILOAD, 13);
            mv.visitInsn(CALOAD);
            mv.visitVarInsn(ILOAD, 8);
            mv.visitInsn(IXOR);
            mv.visitInsn(I2C);
            mv.visitInsn(CASTORE);
            Label l29 = new Label();
            mv.visitLabel(l29);
            mv.visitJumpInsn(GOTO, l27);
            mv.visitLabel(l25);
            mv.visitVarInsn(ALOAD, 12);
            mv.visitVarInsn(ILOAD, 13);
            mv.visitVarInsn(ALOAD, 11);
            mv.visitVarInsn(ILOAD, 13);
            mv.visitInsn(CALOAD);
            mv.visitVarInsn(ILOAD, 9);
            mv.visitInsn(IXOR);
            mv.visitInsn(I2C);
            mv.visitInsn(CASTORE);
            Label l30 = new Label();
            mv.visitLabel(l30);
            mv.visitJumpInsn(GOTO, l27);
            mv.visitLabel(l26);
            mv.visitVarInsn(ALOAD, 12);
            mv.visitVarInsn(ILOAD, 13);
            mv.visitVarInsn(ALOAD, 11);
            mv.visitVarInsn(ILOAD, 13);
            mv.visitInsn(CALOAD);
            mv.visitVarInsn(ILOAD, 10);
            mv.visitInsn(IXOR);
            mv.visitInsn(I2C);
            mv.visitInsn(CASTORE);
            mv.visitLabel(l27);
            mv.visitIincInsn(13, 1);
            mv.visitJumpInsn(GOTO, l20);
            mv.visitLabel(l21);
            mv.visitTypeInsn(NEW, "java/lang/String");
            mv.visitInsn(DUP);
            mv.visitVarInsn(ALOAD, 12);
            mv.visitMethodInsn(INVOKESPECIAL, "java/lang/String", "<init>", "([C)V", false);
            mv.visitInsn(ARETURN);
            Label l31 = new Label();
            mv.visitLabel(l31);
            mv.visitMaxs(5, 14);
            mv.visitEnd();
        }
        cw.visitEnd();

        return cw;
    }

    private class MemberNames {
        private String className = StringUtils.randomClassName(getClasses().keySet());
        private String decryptMethodName = uniqueRandomString();
    }
}
