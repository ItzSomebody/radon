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

package me.itzsomebody.radon.transformers.obfuscators.strings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;
import me.itzsomebody.radon.Main;
import me.itzsomebody.radon.asm.ClassWrapper;
import me.itzsomebody.radon.config.ConfigurationSetting;
import me.itzsomebody.radon.exceptions.InvalidConfigurationValueException;
import me.itzsomebody.radon.exclusions.ExclusionType;
import me.itzsomebody.radon.transformers.Transformer;
import me.itzsomebody.radon.utils.ASMUtils;
import me.itzsomebody.radon.utils.RandomUtils;
import me.itzsomebody.radon.utils.StringUtils;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;

import static me.itzsomebody.radon.utils.ConfigUtils.getValueOrDefault;

/**
 * Abstract class for string encryption transformers.
 *
 * @author ItzSomebody
 */
public class StringEncryption extends Transformer {
    private static final Map<String, StringEncryptionSetting> KEY_MAP = new HashMap<>();
    private List<String> exemptedStrings;
    private boolean contextCheckingEnabled;
    private boolean stringPoolingEnabled;

    static {
        Stream.of(StringEncryptionSetting.values()).forEach(setting -> KEY_MAP.put(setting.getName(), setting));
    }

    @Override
    public void transform() {
        if (isStringPoolingEnabled()) {
            StringPooler pooler = new StringPooler(this);
            pooler.init(radon);
            pooler.transform();
        }

        MemberNames memberNames = new MemberNames();
        AtomicInteger counter = new AtomicInteger();

        getClassWrappers().stream().filter(classWrapper -> !excluded(classWrapper)).forEach(classWrapper ->
                classWrapper.getMethods().stream().filter(methodWrapper -> !excluded(methodWrapper)).forEach(methodWrapper -> {
                    // TODO: leeway safeguard

                    Stream.of(methodWrapper.getMethodNode().instructions.toArray()).filter(insn -> insn instanceof LdcInsnNode
                            && ((LdcInsnNode) insn).cst instanceof String).forEach(insn -> {
                        if (excludedString((String) ((LdcInsnNode) insn).cst))
                            return;

                        int callerClassHC = classWrapper.getName().replace("/", ".").hashCode();
                        int callerMethodHC = methodWrapper.getMethodNode().name.replace("/", ".").hashCode();
                        int decryptorClassHC = memberNames.className.replace("/", ".").hashCode();
                        int decryptorMethodHC = memberNames.decryptMethodName.replace("/", ".").hashCode();

                        int randomKey = RandomUtils.getRandomInt();
                        int key1 = ((isContextCheckingEnabled()) ? callerClassHC + decryptorClassHC + callerMethodHC : 0) ^ randomKey;
                        int key2 = ((isContextCheckingEnabled()) ? callerMethodHC + decryptorMethodHC + callerClassHC : 0) ^ randomKey;
                        int key3 = ((isContextCheckingEnabled()) ? decryptorClassHC + callerClassHC + callerMethodHC : 0) ^ randomKey;
                        int key4 = ((isContextCheckingEnabled()) ? decryptorMethodHC + callerClassHC + decryptorClassHC : 0) ^ randomKey;

                        LdcInsnNode ldc = (LdcInsnNode) insn;
                        ldc.cst = encrypt((String) ldc.cst, key1, key2, key3, key4);

                        methodWrapper.getMethodNode().instructions.insert(ldc, new MethodInsnNode(
                                INVOKESTATIC,
                                memberNames.className,
                                memberNames.decryptMethodName,
                                "(Ljava/lang/Object;I)Ljava/lang/String;",
                                false
                        ));
                        methodWrapper.getMethodNode().instructions.insert(ldc, ASMUtils.getNumberInsn(randomKey));

                        counter.incrementAndGet();
                    });
                }));

        ClassNode decryptor = createDecryptor(memberNames);
        getClasses().put(decryptor.name, new ClassWrapper(decryptor, false));

        Main.info("Encrypted " + counter.get() + " strings");
    }

    @Override
    public String getName() {
        return "String Encryption";
    }

    @Override
    public ExclusionType getExclusionType() {
        return ExclusionType.STRING_ENCRYPTION;
    }

    @Override
    public Object getConfiguration() {
        Map<String, Object> config = new LinkedHashMap<>();

        config.put(StringEncryptionSetting.EXEMPTED_STRINGS.getName(), getExemptedStrings());
        config.put(StringEncryptionSetting.POOL_STRINGS.getName(), isStringPoolingEnabled());
        config.put(StringEncryptionSetting.CHECK_CONTEXT.getName(), isContextCheckingEnabled());

        return config;
    }

    @Override
    public void setConfiguration(Map<String, Object> config) {
        setExemptedStrings(getValueOrDefault(StringEncryptionSetting.EXEMPTED_STRINGS.getName(), config, new ArrayList<>()));
        setStringPoolingEnabled(getValueOrDefault(StringEncryptionSetting.POOL_STRINGS.getName(), config, false));
        setContextCheckingEnabled(getValueOrDefault(StringEncryptionSetting.CHECK_CONTEXT.getName(), config, false));
    }

    @Override
    public void verifyConfiguration(Map<String, Object> config) {
        config.forEach((k, v) -> {
            StringEncryptionSetting setting = KEY_MAP.get(k);

            if (setting == null)
                throw new InvalidConfigurationValueException(ConfigurationSetting.STRING_ENCRYPTION.getName() + '.' + k
                        + " is an invalid configuration key");
            if (!setting.getExpectedType().isInstance(v))
                throw new InvalidConfigurationValueException(ConfigurationSetting.STRING_ENCRYPTION.getName() + '.' + k,
                        setting.getExpectedType(), v.getClass());
        });
    }

    protected boolean excludedString(String str) {
        return getExemptedStrings().stream().anyMatch(str::contains);
    }

    private List<String> getExemptedStrings() {
        return exemptedStrings;
    }

    private void setExemptedStrings(List<String> exemptedStrings) {
        this.exemptedStrings = exemptedStrings;
    }

    private boolean isContextCheckingEnabled() {
        return contextCheckingEnabled;
    }

    private void setContextCheckingEnabled(boolean contextCheckingEnabled) {
        this.contextCheckingEnabled = contextCheckingEnabled;
    }

    private boolean isStringPoolingEnabled() {
        return stringPoolingEnabled;
    }

    private void setStringPoolingEnabled(boolean stringPoolingEnabled) {
        this.stringPoolingEnabled = stringPoolingEnabled;
    }

    private static String encrypt(String s, int key1, int key2, int key3, int key4) {
        StringBuilder sb = new StringBuilder();
        char[] chars = s.toCharArray();

        for (int i = 0; i < chars.length; i++) {
            switch (i % 4) {
                case 0:
                    sb.append((char) (chars[i] ^ key1));
                    break;
                case 1:
                    sb.append((char) (chars[i] ^ key2));
                    break;
                case 2:
                    sb.append((char) (chars[i] ^ key3));
                    break;
                default:
                    sb.append((char) (chars[i] ^ key4));
                    break;
            }
        }

        return sb.toString();
    }

    @SuppressWarnings("Duplicates")
    private ClassNode createDecryptor(MemberNames memberNames) {
        ClassNode cw = new ClassNode();
        FieldVisitor fv;
        MethodVisitor mv;

        cw.visit(V1_5, ACC_PUBLIC + ACC_SUPER, memberNames.className, null, "java/lang/Object", null);

        {
            fv = cw.visitField(ACC_PRIVATE + ACC_FINAL + ACC_STATIC, memberNames.cacheFieldName, "Ljava/util/Map;", null, null);
            fv.visitEnd();
        }
        {
            fv = cw.visitField(ACC_PRIVATE + ACC_FINAL + ACC_STATIC, memberNames.bigBoizFieldName, "[J", null, null);
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
            mv = cw.visitMethod(ACC_PUBLIC + ACC_STATIC, memberNames.decryptMethodName, "(Ljava/lang/Object;I)Ljava/lang/String;", null, null);
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
            Label l8 = new Label();
            mv.visitTryCatchBlock(l6, l7, l8, "java/lang/Throwable");
            Label l9 = new Label();
            mv.visitTryCatchBlock(l9, l4, l8, "java/lang/Throwable");
            Label l10 = new Label();
            mv.visitTryCatchBlock(l5, l10, l8, "java/lang/Throwable");
            Label l11 = new Label();
            mv.visitLabel(l11);
            mv.visitLdcInsn("\u9081\u76e1\uaffe\u6721\u45f9\ud627\u0f38\u2c54\u49c6\u5700");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "hashCode", "()I", false);
            mv.visitVarInsn(ISTORE, 2);
            Label l12 = new Label();
            mv.visitLabel(l12);
            mv.visitLdcInsn("\u6dcf\ucd2e\u739c\u6cec\u5344\u34aa\u873a\u6248\u66fd?");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "hashCode", "()I", false);
            mv.visitVarInsn(ISTORE, 3);
            Label l13 = new Label();
            mv.visitLabel(l13);
            mv.visitLdcInsn("\ue465\u1c76\u4ea0\u4eb5\u675e\uac6b\u976b\u5d9b\u851f\u6619");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "hashCode", "()I", false);
            mv.visitVarInsn(ISTORE, 4);
            Label l14 = new Label();
            mv.visitLabel(l14);
            mv.visitLdcInsn("\u4e92\u8f0f\uab1e\ud035\u80a1\u77ef\u7501\u0773\u3acf\ub9f2");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "hashCode", "()I", false);
            mv.visitVarInsn(ISTORE, 5);
            Label l15 = new Label();
            mv.visitLabel(l15);
            mv.visitLdcInsn("\uf0f4\u838d\u947d\u854d\u0d3e?\u98ee\ub733\uf42f\u3315");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "hashCode", "()I", false);
            mv.visitVarInsn(ISTORE, 6);
            Label l16 = new Label();
            mv.visitLabel(l16);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitTypeInsn(CHECKCAST, "java/lang/String");
            mv.visitVarInsn(ASTORE, 7);
            Label l17 = new Label();
            mv.visitLabel(l17);
            mv.visitTypeInsn(NEW, "java/util/concurrent/atomic/AtomicInteger");
            mv.visitInsn(DUP);
            mv.visitVarInsn(ILOAD, 2);
            mv.visitMethodInsn(INVOKESPECIAL, "java/util/concurrent/atomic/AtomicInteger", "<init>", "(I)V", false);
            mv.visitVarInsn(ASTORE, 8);
            Label l18 = new Label();
            mv.visitLabel(l18);
            mv.visitVarInsn(ALOAD, 8);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/util/concurrent/atomic/AtomicInteger", "incrementAndGet", "()I", false);
            mv.visitInsn(POP);
            Label l19 = new Label();
            mv.visitLabel(l19);
            mv.visitFieldInsn(GETSTATIC, memberNames.className, memberNames.cacheFieldName, "Ljava/util/Map;");
            Label l20 = new Label();
            mv.visitJumpInsn(IFNONNULL, l20);
            mv.visitVarInsn(ILOAD, 2);
            mv.visitInsn(I2L);
            mv.visitLdcInsn(2L);
            mv.visitInsn(LDIV);
            mv.visitFieldInsn(GETSTATIC, memberNames.className, memberNames.bigBoizFieldName, "[J");
            mv.visitInsn(ICONST_0);
            mv.visitInsn(LALOAD);
            mv.visitInsn(LCMP);
            mv.visitJumpInsn(IFGT, l20);
            mv.visitVarInsn(ILOAD, 3);
            mv.visitInsn(I2L);
            mv.visitLdcInsn(2L);
            mv.visitInsn(LDIV);
            mv.visitFieldInsn(GETSTATIC, memberNames.className, memberNames.bigBoizFieldName, "[J");
            mv.visitInsn(ICONST_1);
            mv.visitInsn(LALOAD);
            mv.visitInsn(LCMP);
            mv.visitJumpInsn(IFGT, l20);
            mv.visitVarInsn(ILOAD, 4);
            mv.visitInsn(I2L);
            mv.visitLdcInsn(3L);
            mv.visitInsn(LDIV);
            mv.visitFieldInsn(GETSTATIC, memberNames.className, memberNames.bigBoizFieldName, "[J");
            mv.visitInsn(ICONST_2);
            mv.visitInsn(LALOAD);
            mv.visitInsn(LCMP);
            mv.visitJumpInsn(IFGT, l20);
            mv.visitVarInsn(ILOAD, 5);
            mv.visitInsn(I2L);
            mv.visitLdcInsn(6L);
            mv.visitInsn(LDIV);
            mv.visitFieldInsn(GETSTATIC, memberNames.className, memberNames.bigBoizFieldName, "[J");
            mv.visitInsn(ICONST_3);
            mv.visitInsn(LALOAD);
            mv.visitInsn(LCMP);
            mv.visitJumpInsn(IFGT, l20);
            mv.visitVarInsn(ILOAD, 6);
            mv.visitInsn(ICONST_3);
            mv.visitInsn(IREM);
            mv.visitInsn(I2L);
            mv.visitFieldInsn(GETSTATIC, memberNames.className, memberNames.bigBoizFieldName, "[J");
            mv.visitInsn(ICONST_4);
            mv.visitInsn(LALOAD);
            mv.visitInsn(LCMP);
            mv.visitJumpInsn(IFGT, l20);
            Label l21 = new Label();
            mv.visitLabel(l21);
            mv.visitTypeInsn(NEW, "java/lang/StringBuilder");
            mv.visitInsn(DUP);
            mv.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "()V", false);
            mv.visitVarInsn(ASTORE, 10);
            Label l22 = new Label();
            mv.visitLabel(l22);
            mv.visitVarInsn(ALOAD, 7);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "toCharArray", "()[C", false);
            mv.visitVarInsn(ASTORE, 11);
            mv.visitLabel(l0);
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Thread", "currentThread", "()Ljava/lang/Thread;", false);
            mv.visitVarInsn(ASTORE, 12);
            Label l23 = new Label();
            mv.visitLabel(l23);
            mv.visitVarInsn(ALOAD, 12);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Thread", "getStackTrace", "()[Ljava/lang/StackTraceElement;", false);
            mv.visitVarInsn(ASTORE, 13);
            Label l24 = new Label();
            mv.visitLabel(l24);
            mv.visitTypeInsn(NEW, "java/lang/StringBuilder");
            mv.visitInsn(DUP);
            mv.visitVarInsn(ALOAD, 13);
            mv.visitInsn(ICONST_2);
            mv.visitInsn(AALOAD);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StackTraceElement", "getClassName", "()Ljava/lang/String;", false);
            mv.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "(Ljava/lang/String;)V", false);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "reverse", "()Ljava/lang/StringBuilder;", false);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Object", "hashCode", "()I", false);
            mv.visitTypeInsn(NEW, "java/lang/StringBuilder");
            mv.visitInsn(DUP);
            mv.visitVarInsn(ALOAD, 13);
            mv.visitInsn(ICONST_2);
            mv.visitInsn(AALOAD);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StackTraceElement", "getMethodName", "()Ljava/lang/String;", false);
            mv.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "(Ljava/lang/String;)V", false);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "reverse", "()Ljava/lang/StringBuilder;", false);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Object", "hashCode", "()I", false);
            mv.visitInsn(IADD);
            mv.visitVarInsn(ISTORE, 14);
            Label l25 = new Label();
            mv.visitLabel(l25);
            mv.visitInsn(ICONST_0);
            mv.visitVarInsn(ISTORE, 15);
            Label l26 = new Label();
            mv.visitLabel(l26);
            mv.visitVarInsn(ILOAD, 15);
            mv.visitVarInsn(ALOAD, 11);
            mv.visitInsn(ARRAYLENGTH);
            Label l27 = new Label();
            mv.visitJumpInsn(IF_ICMPGE, l27);
            Label l28 = new Label();
            mv.visitLabel(l28);
            mv.visitVarInsn(ALOAD, 11);
            mv.visitVarInsn(ILOAD, 15);
            mv.visitInsn(CALOAD);
            mv.visitVarInsn(ISTORE, 16);
            Label l29 = new Label();
            mv.visitLabel(l29);
            mv.visitVarInsn(ILOAD, 15);
            mv.visitInsn(ICONST_4);
            mv.visitInsn(IREM);
            Label l30 = new Label();
            Label l31 = new Label();
            Label l32 = new Label();
            Label l33 = new Label();
            Label l34 = new Label();
            mv.visitTableSwitchInsn(0, 3, l34, l30, l31, l32, l33);
            mv.visitLabel(l30);
            mv.visitVarInsn(ILOAD, 16);
            mv.visitVarInsn(ILOAD, 16);
            mv.visitIntInsn(BIPUSH, 16);
            mv.visitInsn(ISHL);
            mv.visitVarInsn(ILOAD, 16);
            mv.visitIntInsn(BIPUSH, 16);
            mv.visitInsn(IUSHR);
            mv.visitInsn(IOR);
            mv.visitInsn(IXOR);
            mv.visitVarInsn(ISTORE, 17);
            Label l35 = new Label();
            mv.visitLabel(l35);
            mv.visitVarInsn(ILOAD, 16);
            mv.visitInsn(ICONST_4);
            mv.visitInsn(ISHR);
            mv.visitVarInsn(ILOAD, 17);
            mv.visitLdcInsn(65535);
            mv.visitInsn(IAND);
            mv.visitVarInsn(ILOAD, 16);
            mv.visitInsn(IXOR);
            mv.visitInsn(ICONST_M1);
            mv.visitInsn(IXOR);
            mv.visitInsn(IOR);
            mv.visitVarInsn(ISTORE, 18);
            Label l36 = new Label();
            mv.visitLabel(l36);
            mv.visitVarInsn(ILOAD, 16);
            mv.visitVarInsn(ILOAD, 14);
            mv.visitInsn(IAND);
            mv.visitVarInsn(ISTORE, 19);
            Label l37 = new Label();
            mv.visitLabel(l37);
            mv.visitVarInsn(ALOAD, 10);
            mv.visitVarInsn(ILOAD, 17);
            mv.visitIntInsn(BIPUSH, 16);
            mv.visitInsn(ISHR);
            mv.visitVarInsn(ILOAD, 18);
            mv.visitInsn(IOR);
            mv.visitVarInsn(ILOAD, 19);
            mv.visitInsn(IXOR);
            mv.visitLdcInsn(65535);
            mv.visitInsn(IAND);
            mv.visitInsn(I2C);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(C)Ljava/lang/StringBuilder;", false);
            mv.visitInsn(POP);
            Label l38 = new Label();
            mv.visitLabel(l38);
            mv.visitJumpInsn(GOTO, l34);
            mv.visitLabel(l31);
            mv.visitVarInsn(ILOAD, 16);
            mv.visitVarInsn(ILOAD, 16);
            mv.visitLdcInsn(65535);
            mv.visitInsn(IAND);
            mv.visitInsn(ICONST_M1);
            mv.visitInsn(IXOR);
            mv.visitInsn(IXOR);
            mv.visitVarInsn(ISTORE, 17);
            Label l39 = new Label();
            mv.visitLabel(l39);
            mv.visitVarInsn(ILOAD, 16);
            mv.visitInsn(ICONST_4);
            mv.visitInsn(ISHL);
            mv.visitVarInsn(ILOAD, 17);
            mv.visitLdcInsn(65535);
            mv.visitVarInsn(ILOAD, 16);
            mv.visitInsn(IXOR);
            mv.visitInsn(IOR);
            mv.visitInsn(ICONST_M1);
            mv.visitInsn(IXOR);
            mv.visitInsn(IOR);
            mv.visitVarInsn(ISTORE, 18);
            Label l40 = new Label();
            mv.visitLabel(l40);
            mv.visitVarInsn(ILOAD, 16);
            mv.visitVarInsn(ILOAD, 14);
            mv.visitInsn(IOR);
            mv.visitVarInsn(ISTORE, 19);
            Label l41 = new Label();
            mv.visitLabel(l41);
            mv.visitVarInsn(ALOAD, 10);
            mv.visitVarInsn(ILOAD, 17);
            mv.visitIntInsn(BIPUSH, 16);
            mv.visitInsn(ISHR);
            mv.visitVarInsn(ILOAD, 18);
            mv.visitInsn(IOR);
            mv.visitVarInsn(ILOAD, 19);
            mv.visitInsn(IXOR);
            mv.visitLdcInsn(65535);
            mv.visitInsn(IAND);
            mv.visitInsn(I2C);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(C)Ljava/lang/StringBuilder;", false);
            mv.visitInsn(POP);
            Label l42 = new Label();
            mv.visitLabel(l42);
            mv.visitJumpInsn(GOTO, l34);
            mv.visitLabel(l32);
            mv.visitVarInsn(ILOAD, 16);
            mv.visitVarInsn(ILOAD, 16);
            mv.visitIntInsn(BIPUSH, 16);
            mv.visitInsn(ISHL);
            mv.visitVarInsn(ILOAD, 16);
            mv.visitIntInsn(BIPUSH, 16);
            mv.visitInsn(IUSHR);
            mv.visitInsn(IOR);
            mv.visitInsn(ICONST_M1);
            mv.visitInsn(IXOR);
            mv.visitInsn(IXOR);
            mv.visitVarInsn(ISTORE, 17);
            Label l43 = new Label();
            mv.visitLabel(l43);
            mv.visitVarInsn(ILOAD, 16);
            mv.visitInsn(ICONST_4);
            mv.visitInsn(ISHR);
            mv.visitVarInsn(ILOAD, 17);
            mv.visitLdcInsn(65535);
            mv.visitInsn(IADD);
            mv.visitVarInsn(ILOAD, 16);
            mv.visitInsn(IXOR);
            mv.visitInsn(ICONST_M1);
            mv.visitInsn(IXOR);
            mv.visitInsn(IOR);
            mv.visitVarInsn(ISTORE, 18);
            Label l44 = new Label();
            mv.visitLabel(l44);
            mv.visitVarInsn(ILOAD, 16);
            mv.visitVarInsn(ILOAD, 14);
            mv.visitInsn(IXOR);
            mv.visitVarInsn(ISTORE, 19);
            Label l45 = new Label();
            mv.visitLabel(l45);
            mv.visitVarInsn(ALOAD, 10);
            mv.visitVarInsn(ILOAD, 17);
            mv.visitIntInsn(BIPUSH, 16);
            mv.visitInsn(ISHR);
            mv.visitVarInsn(ILOAD, 18);
            mv.visitInsn(IOR);
            mv.visitVarInsn(ILOAD, 19);
            mv.visitInsn(IXOR);
            mv.visitLdcInsn(65535);
            mv.visitInsn(IAND);
            mv.visitInsn(I2C);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(C)Ljava/lang/StringBuilder;", false);
            mv.visitInsn(POP);
            Label l46 = new Label();
            mv.visitLabel(l46);
            mv.visitJumpInsn(GOTO, l34);
            mv.visitLabel(l33);
            mv.visitVarInsn(ILOAD, 16);
            mv.visitVarInsn(ILOAD, 16);
            mv.visitLdcInsn(65535);
            mv.visitInsn(IAND);
            mv.visitInsn(IXOR);
            mv.visitVarInsn(ISTORE, 17);
            Label l47 = new Label();
            mv.visitLabel(l47);
            mv.visitVarInsn(ILOAD, 16);
            mv.visitInsn(ICONST_4);
            mv.visitInsn(ISHL);
            mv.visitVarInsn(ILOAD, 17);
            mv.visitLdcInsn(65535);
            mv.visitInsn(IREM);
            mv.visitVarInsn(ILOAD, 16);
            mv.visitInsn(IXOR);
            mv.visitInsn(ICONST_M1);
            mv.visitInsn(IXOR);
            mv.visitInsn(IOR);
            mv.visitVarInsn(ISTORE, 18);
            Label l48 = new Label();
            mv.visitLabel(l48);
            mv.visitVarInsn(ILOAD, 16);
            mv.visitVarInsn(ILOAD, 14);
            mv.visitInsn(ICONST_M1);
            mv.visitInsn(IXOR);
            mv.visitInsn(IAND);
            mv.visitVarInsn(ISTORE, 19);
            Label l49 = new Label();
            mv.visitLabel(l49);
            mv.visitVarInsn(ALOAD, 10);
            mv.visitVarInsn(ILOAD, 17);
            mv.visitIntInsn(BIPUSH, 16);
            mv.visitInsn(ISHR);
            mv.visitVarInsn(ILOAD, 18);
            mv.visitInsn(IOR);
            mv.visitVarInsn(ILOAD, 19);
            mv.visitInsn(IXOR);
            mv.visitLdcInsn(65535);
            mv.visitInsn(IAND);
            mv.visitInsn(I2C);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(C)Ljava/lang/StringBuilder;", false);
            mv.visitInsn(POP);
            mv.visitLabel(l34);
            mv.visitIincInsn(15, 1);
            mv.visitJumpInsn(GOTO, l26);
            mv.visitLabel(l27);
            mv.visitVarInsn(ALOAD, 10);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false);
            mv.visitLabel(l1);
            mv.visitInsn(ARETURN);
            mv.visitLabel(l2);
            mv.visitVarInsn(ASTORE, 12);
            Label l50 = new Label();
            mv.visitLabel(l50);
            mv.visitVarInsn(ALOAD, 7);
            mv.visitInsn(ARETURN);
            mv.visitLabel(l20);
            mv.visitFieldInsn(GETSTATIC, memberNames.className, memberNames.cacheFieldName, "Ljava/util/Map;");
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKEINTERFACE, "java/util/Map", "get", "(Ljava/lang/Object;)Ljava/lang/Object;", true);
            mv.visitTypeInsn(CHECKCAST, "java/lang/String");
            mv.visitInsn(DUP);
            mv.visitVarInsn(ASTORE, 9);
            Label l51 = new Label();
            mv.visitLabel(l51);
            Label l52 = new Label();
            mv.visitJumpInsn(IFNULL, l52);
            Label l53 = new Label();
            mv.visitLabel(l53);
            mv.visitVarInsn(ALOAD, 9);
            mv.visitInsn(ARETURN);
            mv.visitLabel(l52);
            mv.visitLdcInsn("\u6b40\u0304\u6293\u06b0\u6835\u1870\u7e9f\u811b\u7d58\ub1db");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "hashCode", "()I", false);
            mv.visitVarInsn(ISTORE, 10);
            Label l54 = new Label();
            mv.visitLabel(l54);
            mv.visitLdcInsn("\u0db1\ue04a\ua586\u7651\u8ae3\u6b16\u936d\ub649\u04e8\u38fa");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "hashCode", "()I", false);
            mv.visitVarInsn(ISTORE, 11);
            Label l55 = new Label();
            mv.visitLabel(l55);
            mv.visitVarInsn(ALOAD, 7);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "toCharArray", "()[C", false);
            mv.visitVarInsn(ASTORE, 12);
            Label l56 = new Label();
            mv.visitLabel(l56);
            mv.visitLdcInsn("\ua91e\u4d22\ua711\u961f\uf7da\u72f4\u302e\u4562\u6adb\ub288");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "hashCode", "()I", false);
            mv.visitVarInsn(ISTORE, 13);
            Label l57 = new Label();
            mv.visitLabel(l57);
            mv.visitLdcInsn("\ube16\u9e52\u35f2\u6697\u0898\ue5e6\u914e\u2e51\uc9e8\uf3d2");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "hashCode", "()I", false);
            mv.visitVarInsn(ISTORE, 14);
            Label l58 = new Label();
            mv.visitLabel(l58);
            mv.visitVarInsn(ALOAD, 12);
            mv.visitInsn(ARRAYLENGTH);
            mv.visitIntInsn(NEWARRAY, T_CHAR);
            mv.visitVarInsn(ASTORE, 15);
            mv.visitLabel(l6);

            if (isContextCheckingEnabled()) {
                mv.visitMethodInsn(INVOKESTATIC, "java/lang/Thread", "currentThread", "()Ljava/lang/Thread;", false);
                mv.visitVarInsn(ASTORE, 16);
                Label l59 = new Label();
                mv.visitLabel(l59);
                mv.visitVarInsn(ALOAD, 16);
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Thread", "getStackTrace", "()[Ljava/lang/StackTraceElement;", false);
                mv.visitVarInsn(ASTORE, 17);
                Label l60 = new Label();
                mv.visitLabel(l60);
                mv.visitVarInsn(ALOAD, 17);
                mv.visitInsn(ICONST_2);
                mv.visitInsn(AALOAD);
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StackTraceElement", "getClassName", "()Ljava/lang/String;", false);
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "hashCode", "()I", false);
                mv.visitVarInsn(ALOAD, 17);
                mv.visitInsn(ICONST_1);
                mv.visitInsn(AALOAD);
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StackTraceElement", "getClassName", "()Ljava/lang/String;", false);
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "hashCode", "()I", false);
                mv.visitInsn(IADD);
                mv.visitVarInsn(ALOAD, 17);
                mv.visitInsn(ICONST_2);
                mv.visitInsn(AALOAD);
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StackTraceElement", "getMethodName", "()Ljava/lang/String;", false);
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "hashCode", "()I", false);
                mv.visitInsn(IADD);
            } else
                mv.visitInsn(ICONST_0);

            mv.visitVarInsn(ILOAD, 1);
            mv.visitInsn(IXOR);
            mv.visitVarInsn(ISTORE, 18);

            if (isContextCheckingEnabled()) {
                Label l61 = new Label();
                mv.visitLabel(l61);
                mv.visitVarInsn(ALOAD, 17);
                mv.visitInsn(ICONST_2);
                mv.visitInsn(AALOAD);
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StackTraceElement", "getMethodName", "()Ljava/lang/String;", false);
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "hashCode", "()I", false);
                mv.visitVarInsn(ALOAD, 17);
                mv.visitInsn(ICONST_1);
                mv.visitInsn(AALOAD);
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StackTraceElement", "getMethodName", "()Ljava/lang/String;", false);
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "hashCode", "()I", false);
                mv.visitInsn(IADD);
                mv.visitVarInsn(ALOAD, 17);
                mv.visitInsn(ICONST_2);
                mv.visitInsn(AALOAD);
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StackTraceElement", "getClassName", "()Ljava/lang/String;", false);
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "hashCode", "()I", false);
                mv.visitInsn(IADD);
            } else
                mv.visitInsn(ICONST_0);

            mv.visitVarInsn(ILOAD, 1);
            mv.visitInsn(IXOR);
            mv.visitVarInsn(ISTORE, 19);
            Label l62 = new Label();
            mv.visitLabel(l62);

            if (isContextCheckingEnabled()) {
                mv.visitVarInsn(ALOAD, 17);
                mv.visitInsn(ICONST_1);
                mv.visitInsn(AALOAD);
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StackTraceElement", "getClassName", "()Ljava/lang/String;", false);
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "hashCode", "()I", false);
                mv.visitVarInsn(ALOAD, 17);
                mv.visitInsn(ICONST_2);
                mv.visitInsn(AALOAD);
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StackTraceElement", "getClassName", "()Ljava/lang/String;", false);
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "hashCode", "()I", false);
                mv.visitInsn(IADD);
                mv.visitVarInsn(ALOAD, 17);
                mv.visitInsn(ICONST_2);
                mv.visitInsn(AALOAD);
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StackTraceElement", "getMethodName", "()Ljava/lang/String;", false);
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "hashCode", "()I", false);
                mv.visitInsn(IADD);
            } else
                mv.visitInsn(ICONST_0);

            mv.visitVarInsn(ILOAD, 1);
            mv.visitInsn(IXOR);
            mv.visitVarInsn(ISTORE, 20);
            Label l63 = new Label();
            mv.visitLabel(l63);

            if (isContextCheckingEnabled()) {

                mv.visitVarInsn(ALOAD, 17);
                mv.visitInsn(ICONST_1);
                mv.visitInsn(AALOAD);
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StackTraceElement", "getMethodName", "()Ljava/lang/String;", false);
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "hashCode", "()I", false);
                mv.visitVarInsn(ALOAD, 17);
                mv.visitInsn(ICONST_2);
                mv.visitInsn(AALOAD);
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StackTraceElement", "getClassName", "()Ljava/lang/String;", false);
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "hashCode", "()I", false);
                mv.visitInsn(IADD);
                mv.visitVarInsn(ALOAD, 17);
                mv.visitInsn(ICONST_1);
                mv.visitInsn(AALOAD);
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StackTraceElement", "getClassName", "()Ljava/lang/String;", false);
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "hashCode", "()I", false);
                mv.visitInsn(IADD);
            } else
                mv.visitInsn(ICONST_0);

            mv.visitVarInsn(ILOAD, 1);
            mv.visitInsn(IXOR);
            mv.visitVarInsn(ISTORE, 21);
            Label l64 = new Label();
            mv.visitLabel(l64);
            mv.visitVarInsn(ILOAD, 10);
            mv.visitInsn(I2L);
            mv.visitLdcInsn(2L);
            mv.visitInsn(LDIV);
            mv.visitFieldInsn(GETSTATIC, memberNames.className, memberNames.bigBoizFieldName, "[J");
            mv.visitInsn(ICONST_5);
            mv.visitInsn(LALOAD);
            mv.visitInsn(LCMP);
            mv.visitJumpInsn(IFGT, l9);
            mv.visitVarInsn(ILOAD, 11);
            mv.visitInsn(I2L);
            mv.visitLdcInsn(2L);
            mv.visitInsn(LDIV);
            mv.visitFieldInsn(GETSTATIC, memberNames.className, memberNames.bigBoizFieldName, "[J");
            mv.visitIntInsn(BIPUSH, 6);
            mv.visitInsn(LALOAD);
            mv.visitInsn(LCMP);
            mv.visitJumpInsn(IFGT, l9);
            mv.visitVarInsn(ILOAD, 13);
            mv.visitInsn(I2L);
            mv.visitLdcInsn(2L);
            mv.visitInsn(LDIV);
            mv.visitFieldInsn(GETSTATIC, memberNames.className, memberNames.bigBoizFieldName, "[J");
            mv.visitIntInsn(BIPUSH, 7);
            mv.visitInsn(LALOAD);
            mv.visitInsn(LCMP);
            mv.visitJumpInsn(IFGT, l9);
            mv.visitVarInsn(ILOAD, 14);
            mv.visitInsn(I2L);
            mv.visitLdcInsn(2L);
            mv.visitInsn(LDIV);
            mv.visitFieldInsn(GETSTATIC, memberNames.className, memberNames.bigBoizFieldName, "[J");
            mv.visitIntInsn(BIPUSH, 7);
            mv.visitInsn(LALOAD);
            mv.visitInsn(LCMP);
            mv.visitJumpInsn(IFGT, l9);
            Label l65 = new Label();
            mv.visitLabel(l65);
            mv.visitInsn(ICONST_0);
            mv.visitVarInsn(ISTORE, 22);
            Label l66 = new Label();
            mv.visitLabel(l66);
            mv.visitVarInsn(ILOAD, 22);
            mv.visitVarInsn(ALOAD, 12);
            mv.visitInsn(ARRAYLENGTH);
            Label l67 = new Label();
            mv.visitJumpInsn(IF_ICMPGE, l67);
            Label l68 = new Label();
            mv.visitLabel(l68);
            mv.visitVarInsn(ILOAD, 22);
            mv.visitInsn(ICONST_4);
            mv.visitInsn(IREM);
            Label l69 = new Label();
            Label l70 = new Label();
            Label l71 = new Label();
            Label l72 = new Label();
            Label l73 = new Label();
            mv.visitTableSwitchInsn(0, 3, l73, l69, l70, l71, l72);
            mv.visitLabel(l69);
            mv.visitVarInsn(ALOAD, 15);
            mv.visitVarInsn(ILOAD, 22);
            mv.visitVarInsn(ALOAD, 12);
            mv.visitVarInsn(ILOAD, 22);
            mv.visitInsn(CALOAD);
            mv.visitVarInsn(ILOAD, 18);
            mv.visitInsn(IXOR);
            mv.visitLdcInsn(65535);
            mv.visitInsn(IAND);
            mv.visitInsn(I2C);
            mv.visitInsn(CASTORE);
            Label l74 = new Label();
            mv.visitLabel(l74);
            mv.visitJumpInsn(GOTO, l73);
            mv.visitLabel(l70);
            mv.visitVarInsn(ALOAD, 15);
            mv.visitVarInsn(ILOAD, 22);
            mv.visitVarInsn(ALOAD, 12);
            mv.visitVarInsn(ILOAD, 22);
            mv.visitInsn(CALOAD);
            mv.visitVarInsn(ILOAD, 19);
            mv.visitInsn(IXOR);
            mv.visitLdcInsn(65535);
            mv.visitInsn(IAND);
            mv.visitInsn(I2C);
            mv.visitInsn(CASTORE);
            Label l75 = new Label();
            mv.visitLabel(l75);
            mv.visitJumpInsn(GOTO, l73);
            mv.visitLabel(l71);
            mv.visitVarInsn(ALOAD, 15);
            mv.visitVarInsn(ILOAD, 22);
            mv.visitVarInsn(ALOAD, 12);
            mv.visitVarInsn(ILOAD, 22);
            mv.visitInsn(CALOAD);
            mv.visitVarInsn(ILOAD, 20);
            mv.visitInsn(IXOR);
            mv.visitLdcInsn(65535);
            mv.visitInsn(IAND);
            mv.visitInsn(I2C);
            mv.visitInsn(CASTORE);
            Label l76 = new Label();
            mv.visitLabel(l76);
            mv.visitJumpInsn(GOTO, l73);
            mv.visitLabel(l72);
            mv.visitVarInsn(ALOAD, 15);
            mv.visitVarInsn(ILOAD, 22);
            mv.visitVarInsn(ALOAD, 12);
            mv.visitVarInsn(ILOAD, 22);
            mv.visitInsn(CALOAD);
            mv.visitVarInsn(ILOAD, 21);
            mv.visitInsn(IXOR);
            mv.visitLdcInsn(65535);
            mv.visitInsn(IAND);
            mv.visitInsn(I2C);
            mv.visitInsn(CASTORE);
            mv.visitLabel(l73);
            mv.visitIincInsn(22, 1);
            mv.visitJumpInsn(GOTO, l66);
            mv.visitLabel(l67);
            mv.visitTypeInsn(NEW, "java/lang/String");
            mv.visitInsn(DUP);
            mv.visitVarInsn(ALOAD, 15);
            mv.visitMethodInsn(INVOKESPECIAL, "java/lang/String", "<init>", "([C)V", false);
            mv.visitVarInsn(ASTORE, 22);
            Label l77 = new Label();
            mv.visitLabel(l77);
            mv.visitFieldInsn(GETSTATIC, memberNames.className, memberNames.cacheFieldName, "Ljava/util/Map;");
            mv.visitVarInsn(ALOAD, 7);
            mv.visitVarInsn(ALOAD, 22);
            mv.visitMethodInsn(INVOKEINTERFACE, "java/util/Map", "put", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", true);
            mv.visitInsn(POP);
            Label l78 = new Label();
            mv.visitLabel(l78);
            mv.visitVarInsn(ALOAD, 22);
            mv.visitLabel(l7);
            mv.visitInsn(ARETURN);
            mv.visitLabel(l9);
            mv.visitTypeInsn(NEW, "java/lang/StringBuilder");
            mv.visitInsn(DUP);
            mv.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "()V", false);
            mv.visitVarInsn(ASTORE, 22);
            mv.visitLabel(l3);
            mv.visitInsn(ICONST_0);
            mv.visitVarInsn(ISTORE, 23);
            Label l79 = new Label();
            mv.visitLabel(l79);
            mv.visitVarInsn(ILOAD, 23);
            mv.visitVarInsn(ALOAD, 12);
            mv.visitInsn(ARRAYLENGTH);
            Label l80 = new Label();
            mv.visitJumpInsn(IF_ICMPGE, l80);
            Label l81 = new Label();
            mv.visitLabel(l81);
            //mv.visitVarInsn(ALOAD, 16);
            mv.visitInsn(ACONST_NULL);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Thread", "getId", "()J", false);
            mv.visitInsn(L2I);
            mv.visitVarInsn(ISTORE, 24);
            Label l82 = new Label();
            mv.visitLabel(l82);
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Runtime", "getRuntime", "()Ljava/lang/Runtime;", false);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Runtime", "availableProcessors", "()I", false);
            mv.visitVarInsn(ISTORE, 25);
            Label l83 = new Label();
            mv.visitLabel(l83);
            mv.visitVarInsn(ALOAD, 8);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/util/concurrent/atomic/AtomicInteger", "get", "()I", false);
            mv.visitVarInsn(ILOAD, 1);
            mv.visitInsn(IXOR);
            mv.visitVarInsn(ISTORE, 26);
            Label l84 = new Label();
            mv.visitLabel(l84);
            mv.visitVarInsn(ILOAD, 23);
            mv.visitInsn(ICONST_4);
            mv.visitInsn(IREM);
            Label l85 = new Label();
            Label l86 = new Label();
            Label l87 = new Label();
            Label l88 = new Label();
            Label l89 = new Label();
            mv.visitTableSwitchInsn(0, 3, l89, l85, l86, l87, l88);
            mv.visitLabel(l85);
            mv.visitVarInsn(ALOAD, 22);
            mv.visitVarInsn(ILOAD, 24);
            mv.visitIntInsn(BIPUSH, 16);
            mv.visitVarInsn(ILOAD, 25);
            mv.visitInsn(IREM);
            mv.visitInsn(ISHR);
            mv.visitVarInsn(ILOAD, 26);
            mv.visitInsn(IAND);
            mv.visitVarInsn(ALOAD, 12);
            mv.visitVarInsn(ILOAD, 23);
            mv.visitInsn(CALOAD);
            mv.visitInsn(IAND);
            mv.visitInsn(I2C);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(C)Ljava/lang/StringBuilder;", false);
            mv.visitInsn(POP);
            Label l90 = new Label();
            mv.visitLabel(l90);
            mv.visitJumpInsn(GOTO, l89);
            mv.visitLabel(l86);
            mv.visitVarInsn(ALOAD, 22);
            mv.visitVarInsn(ILOAD, 24);
            mv.visitIntInsn(BIPUSH, 16);
            mv.visitVarInsn(ILOAD, 25);
            mv.visitInsn(IMUL);
            mv.visitInsn(ISHR);
            mv.visitVarInsn(ILOAD, 26);
            mv.visitInsn(IXOR);
            mv.visitVarInsn(ALOAD, 12);
            mv.visitVarInsn(ILOAD, 23);
            mv.visitInsn(CALOAD);
            mv.visitInsn(IOR);
            mv.visitInsn(I2C);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(C)Ljava/lang/StringBuilder;", false);
            mv.visitInsn(POP);
            Label l91 = new Label();
            mv.visitLabel(l91);
            mv.visitJumpInsn(GOTO, l89);
            mv.visitLabel(l87);
            mv.visitVarInsn(ALOAD, 22);
            mv.visitVarInsn(ILOAD, 24);
            mv.visitIntInsn(BIPUSH, 16);
            mv.visitVarInsn(ILOAD, 25);
            mv.visitInsn(IDIV);
            mv.visitInsn(ISHR);
            mv.visitVarInsn(ILOAD, 26);
            mv.visitInsn(IOR);
            mv.visitVarInsn(ALOAD, 12);
            mv.visitVarInsn(ILOAD, 23);
            mv.visitInsn(CALOAD);
            mv.visitInsn(IXOR);
            mv.visitInsn(I2C);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(C)Ljava/lang/StringBuilder;", false);
            mv.visitInsn(POP);
            Label l92 = new Label();
            mv.visitLabel(l92);
            mv.visitJumpInsn(GOTO, l89);
            mv.visitLabel(l88);
            mv.visitVarInsn(ALOAD, 22);
            mv.visitVarInsn(ILOAD, 24);
            mv.visitIntInsn(BIPUSH, 16);
            mv.visitVarInsn(ILOAD, 25);
            mv.visitInsn(IADD);
            mv.visitInsn(ISHR);
            mv.visitVarInsn(ILOAD, 26);
            mv.visitInsn(ICONST_M1);
            mv.visitInsn(IXOR);
            mv.visitInsn(IAND);
            mv.visitVarInsn(ALOAD, 12);
            mv.visitVarInsn(ILOAD, 23);
            mv.visitInsn(CALOAD);
            mv.visitInsn(ICONST_M1);
            mv.visitInsn(IXOR);
            mv.visitInsn(IXOR);
            mv.visitInsn(I2C);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(C)Ljava/lang/StringBuilder;", false);
            mv.visitInsn(POP);
            mv.visitLabel(l89);
            mv.visitIincInsn(23, 1);
            mv.visitJumpInsn(GOTO, l79);
            mv.visitLabel(l80);
            mv.visitVarInsn(ALOAD, 22);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false);
            mv.visitVarInsn(ASTORE, 23);
            Label l93 = new Label();
            mv.visitLabel(l93);
            mv.visitFieldInsn(GETSTATIC, memberNames.className, memberNames.cacheFieldName, "Ljava/util/Map;");
            mv.visitVarInsn(ALOAD, 7);
            mv.visitVarInsn(ALOAD, 23);
            mv.visitMethodInsn(INVOKEINTERFACE, "java/util/Map", "put", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", true);
            mv.visitInsn(POP);
            Label l94 = new Label();
            mv.visitLabel(l94);
            mv.visitVarInsn(ALOAD, 23);
            mv.visitLabel(l4);
            mv.visitInsn(ARETURN);
            mv.visitLabel(l5);
            mv.visitVarInsn(ASTORE, 23);
            Label l95 = new Label();
            mv.visitLabel(l95);
            mv.visitVarInsn(ALOAD, 7);
            mv.visitLabel(l10);
            mv.visitInsn(ARETURN);
            mv.visitLabel(l8);
            mv.visitVarInsn(ASTORE, 16);
            Label l96 = new Label();
            mv.visitLabel(l96);
            mv.visitVarInsn(ALOAD, 7);
            mv.visitInsn(ARETURN);
            Label l97 = new Label();
            mv.visitLabel(l97);
            mv.visitMaxs(5, 27);
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
            mv.visitFieldInsn(PUTSTATIC, memberNames.className, memberNames.cacheFieldName, "Ljava/util/Map;");
            Label l1 = new Label();
            mv.visitLabel(l1);
            mv.visitIntInsn(BIPUSH, 13);
            mv.visitIntInsn(NEWARRAY, T_LONG);
            mv.visitFieldInsn(PUTSTATIC, memberNames.className, memberNames.bigBoizFieldName, "[J");
            Label l2 = new Label();
            mv.visitLabel(l2);
            mv.visitFieldInsn(GETSTATIC, memberNames.className, memberNames.bigBoizFieldName, "[J");
            mv.visitInsn(ICONST_0);
            mv.visitLdcInsn(8829304729L);
            mv.visitInsn(LASTORE);
            Label l3 = new Label();
            mv.visitLabel(l3);
            mv.visitFieldInsn(GETSTATIC, memberNames.className, memberNames.bigBoizFieldName, "[J");
            mv.visitInsn(ICONST_1);
            mv.visitLdcInsn(4848002993994L);
            mv.visitInsn(LASTORE);
            Label l4 = new Label();
            mv.visitLabel(l4);
            mv.visitFieldInsn(GETSTATIC, memberNames.className, memberNames.bigBoizFieldName, "[J");
            mv.visitInsn(ICONST_2);
            mv.visitLdcInsn(8844039203925L);
            mv.visitInsn(LASTORE);
            Label l5 = new Label();
            mv.visitLabel(l5);
            mv.visitFieldInsn(GETSTATIC, memberNames.className, memberNames.bigBoizFieldName, "[J");
            mv.visitInsn(ICONST_3);
            mv.visitLdcInsn(77493848003273L);
            mv.visitInsn(LASTORE);
            Label l6 = new Label();
            mv.visitLabel(l6);
            mv.visitFieldInsn(GETSTATIC, memberNames.className, memberNames.bigBoizFieldName, "[J");
            mv.visitInsn(ICONST_4);
            mv.visitLdcInsn(1777293846418288384L);
            mv.visitInsn(LASTORE);
            Label l7 = new Label();
            mv.visitLabel(l7);
            mv.visitFieldInsn(GETSTATIC, memberNames.className, memberNames.bigBoizFieldName, "[J");
            mv.visitInsn(ICONST_5);
            mv.visitLdcInsn(48830029394L);
            mv.visitInsn(LASTORE);
            Label l8 = new Label();
            mv.visitLabel(l8);
            mv.visitFieldInsn(GETSTATIC, memberNames.className, memberNames.bigBoizFieldName, "[J");
            mv.visitIntInsn(BIPUSH, 6);
            mv.visitLdcInsn(19949830293L);
            mv.visitInsn(LASTORE);
            Label l9 = new Label();
            mv.visitLabel(l9);
            mv.visitFieldInsn(GETSTATIC, memberNames.className, memberNames.bigBoizFieldName, "[J");
            mv.visitIntInsn(BIPUSH, 7);
            mv.visitLdcInsn(848039293975993L);
            mv.visitInsn(LASTORE);
            Label l10 = new Label();
            mv.visitLabel(l10);
            mv.visitFieldInsn(GETSTATIC, memberNames.className, memberNames.bigBoizFieldName, "[J");
            mv.visitIntInsn(BIPUSH, 8);
            mv.visitLdcInsn(18717729394885L);
            mv.visitInsn(LASTORE);
            Label l11 = new Label();
            mv.visitLabel(l11);
            mv.visitFieldInsn(GETSTATIC, memberNames.className, memberNames.bigBoizFieldName, "[J");
            mv.visitIntInsn(BIPUSH, 9);
            mv.visitLdcInsn(28838847379432L);
            mv.visitInsn(LASTORE);
            Label l12 = new Label();
            mv.visitLabel(l12);
            mv.visitFieldInsn(GETSTATIC, memberNames.className, memberNames.bigBoizFieldName, "[J");
            mv.visitIntInsn(BIPUSH, 10);
            mv.visitLdcInsn(9991828838749L);
            mv.visitInsn(LASTORE);
            Label l13 = new Label();
            mv.visitLabel(l13);
            mv.visitFieldInsn(GETSTATIC, memberNames.className, memberNames.bigBoizFieldName, "[J");
            mv.visitIntInsn(BIPUSH, 11);
            mv.visitLdcInsn(47774434991928L);
            mv.visitInsn(LASTORE);
            Label l14 = new Label();
            mv.visitLabel(l14);
            mv.visitFieldInsn(GETSTATIC, memberNames.className, memberNames.bigBoizFieldName, "[J");
            mv.visitIntInsn(BIPUSH, 12);
            mv.visitLdcInsn(1111144744434L);
            mv.visitInsn(LASTORE);
            Label l15 = new Label();
            mv.visitLabel(l15);
            mv.visitInsn(RETURN);
            mv.visitMaxs(4, 0);
            mv.visitEnd();
        }
        cw.visitEnd();

        return cw;
    }

    private class MemberNames {
        private String className = StringUtils.randomClassName(getClasses().keySet());
        private String cacheFieldName = uniqueRandomString();
        private String bigBoizFieldName = uniqueRandomString();
        private String decryptMethodName = uniqueRandomString();
    }
}
