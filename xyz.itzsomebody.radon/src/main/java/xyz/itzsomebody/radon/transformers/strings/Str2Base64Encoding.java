/*
 * Radon - An open-source Java obfuscator
 * Copyright (C) 2020 ItzSomebody
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

package xyz.itzsomebody.radon.transformers.strings;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import xyz.itzsomebody.commons.InsnListModifier;
import xyz.itzsomebody.radon.config.ConfigurationParser;
import xyz.itzsomebody.radon.transformers.Transformers;
import xyz.itzsomebody.radon.utils.asm.ClassWrapper;
import xyz.itzsomebody.radon.utils.logging.RadonLogger;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Transformer which encodes Utf8 constants with Base64. This is here just so I can quickly test stuff.
 * Please don't use this and not expect your strings to get decoded, lol.
 *
 * @author itzsomebody
 */
public class Str2Base64Encoding extends StringTransformer {
    @JsonProperty("decode_from_random_class")
    private boolean decodeFromRandomClass;

    @Override
    public void transform() {
        ClassWrapper toInject;

        if (decodeFromRandomClass) {
            toInject = randomClass();
        } else {
            ClassNode cn = new ClassNode();
            cn.version = V1_5;
            cn.access = ACC_PUBLIC;
            cn.name = fakeSubClass();
            cn.superName = "java/lang/Object";

            toInject = new ClassWrapper(cn, false);
            addClass(toInject);
        }

        var decryptor = decryptorNode(toInject.generateNextAllowedMethodName(dictionary.copy(), "(Ljava/lang/String;)Ljava/lang/String;"));
        toInject.addMethod(decryptor);

        AtomicInteger count = new AtomicInteger();

        classStream().filter(this::notExcluded).forEach(classWrapper -> {
            classWrapper.methodStream().filter(mw -> notExcluded(mw) && mw.hasInstructions()).forEach(methodWrapper -> {
                if (methodWrapper.getLeewaySize() > allowedLeeway) {
                    var methodNode = methodWrapper.getMethodNode();
                    var modifier = new InsnListModifier();
                    methodNode.instructions.forEach(current -> {
                        if (current instanceof LdcInsnNode && ((LdcInsnNode) current).cst instanceof String) {
                            ((LdcInsnNode) current).cst = encodeString((String) ((LdcInsnNode) current).cst);
                            modifier.insert(current, new MethodInsnNode(
                                    INVOKESTATIC,
                                    toInject.getName(),
                                    decryptor.name,
                                    decryptor.desc,
                                    toInject.isInterface()
                            ));
                            count.incrementAndGet();
                        }
                    });
                    modifier.apply(methodNode.instructions);
                }
            });
        });

        RadonLogger.info("Base64 encoded " + count.get() + " strings");
    }

    @Override
    public String getConfigName() {
        return Transformers.STRING_TO_BASE64_ENCODING.getConfigName();
    }

    public static String encodeString(String s) {
        // It's important to treat everything as UTF-8
        return new String(Base64.getEncoder().encode(s.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8);
    }

    /**
     * Template for ASMifier.
     */
    public static String decodeString(String s) {
        return new String(Base64.getDecoder().decode(s.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8);
    }

    private MethodNode decryptorNode(String methodName) {
        var mw = new MethodNode(ACC_PUBLIC | ACC_STATIC | ACC_SYNTHETIC | ACC_BRIDGE, methodName, "(Ljava/lang/String;)Ljava/lang/String;", null, null);
        mw.visitCode();
        mw.visitTypeInsn(NEW, "java/lang/String");
        mw.visitInsn(DUP);
        mw.visitMethodInsn(INVOKESTATIC, "java/util/Base64", "getDecoder", "()Ljava/util/Base64$Decoder;", false);
        mw.visitVarInsn(ALOAD, 0);
        mw.visitFieldInsn(GETSTATIC, "java/nio/charset/StandardCharsets", "UTF_8", "Ljava/nio/charset/Charset;");
        mw.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "getBytes", "(Ljava/nio/charset/Charset;)[B", false);
        mw.visitMethodInsn(INVOKEVIRTUAL, "java/util/Base64$Decoder", "decode", "([B)[B", false);
        mw.visitFieldInsn(GETSTATIC, "java/nio/charset/StandardCharsets", "UTF_8", "Ljava/nio/charset/Charset;");
        mw.visitMethodInsn(INVOKESPECIAL, "java/lang/String", "<init>", "([BLjava/nio/charset/Charset;)V", false);
        mw.visitInsn(ARETURN);
        mw.visitMaxs(5, 2);
        mw.visitEnd();
        return mw;
    }

    enum Base64EncoderKey {
        DECODE_FROM_RANDOM_CLASS;

        public String getKey() {
            return name().toLowerCase();
        }
    }
}
