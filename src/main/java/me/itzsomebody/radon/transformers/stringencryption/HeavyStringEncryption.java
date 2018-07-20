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

package me.itzsomebody.radon.transformers.stringencryption;

import java.util.concurrent.atomic.AtomicInteger;

import me.itzsomebody.radon.generate.StringDecryptorGenerator;
import me.itzsomebody.radon.generate.StringEncryptionGenerator;
import me.itzsomebody.radon.utils.BytecodeUtils;
import me.itzsomebody.radon.utils.LoggerUtils;
import me.itzsomebody.radon.utils.NumberUtils;
import me.itzsomebody.radon.utils.StringUtils;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

public class HeavyStringEncryption extends SuperLightStringEncryption {
    /**
     * Constructor used to create a {@link HeavyStringEncryption} object.
     *
     * @param spigotMode indication to not encrypt strings containing Spigot
     *                   placeholders (%%__USER__%%, %%__RESOURCE__%% and
     *                   %%__NONCE__%%).
     */
    public HeavyStringEncryption(boolean spigotMode) {
        super(spigotMode);
    }

    /**
     * Applies obfuscation.
     */
    @Override
    public void obfuscate() {
        AtomicInteger counter = new AtomicInteger();
        long current = System.currentTimeMillis();
        MemberNames memberNames = new MemberNames(this);
        this.logStrings.add(LoggerUtils.stdOut("------------------------------------------------"));
        this.logStrings.add(LoggerUtils.stdOut("Started heavy string encryption transformer"));
        this.classNodes().parallelStream().filter(classNode -> !this.exempted(classNode.name, "StringEncryption")).forEach(classNode ->
                classNode.methods.parallelStream().filter(methodNode ->
                        !this.exempted(classNode.name + '.' + methodNode.name + methodNode.desc, "StringEncryption")
                                && hasInstructions(methodNode)).forEach(methodNode -> {
                    for (AbstractInsnNode insn : methodNode.instructions.toArray()) {
                        if (methodSize(methodNode) > 60000) break;
                        if (insn instanceof LdcInsnNode) {
                            LdcInsnNode ldc = (LdcInsnNode) insn;
                            if (ldc.cst instanceof String) {
                                String cst = (String) ldc.cst;
                                if (spigotCheck(cst))
                                    continue;
                                int extraKey = NumberUtils.getRandomInt();
                                int callerClassHC = classNode.name.replace("/", ".").hashCode();
                                int callerMethodHC = methodNode.name.hashCode();
                                int decryptorClassHC = memberNames.className.replace("/", ".").hashCode();
                                int decryptorMethodHC = memberNames.decryptorMethodName.hashCode();
                                ldc.cst = encrypt(cst, callerClassHC, callerMethodHC, decryptorClassHC, decryptorMethodHC, extraKey);
                                methodNode.instructions.insert(insn, new MethodInsnNode(INVOKESTATIC, memberNames.className, memberNames.decryptorMethodName, "(Ljava/lang/Object;I)Ljava/lang/String;", false));
                                methodNode.instructions.insert(insn, new InsnNode(POP));
                                methodNode.instructions.insert(insn, new InsnNode(DUP_X1));
                                methodNode.instructions.insertBefore(insn, BytecodeUtils.createNumberInsn(extraKey));
                                counter.incrementAndGet();
                            }
                        }
                    }
                })
        );
        // Add decrypt method
        ClassNode decryptor = StringDecryptorGenerator.heavyStringDecryptor(memberNames);
        getClassMap().put(decryptor.name, decryptor);
        // Do logging
        logStrings.add(LoggerUtils.stdOut("Encrypted " + counter + " strings."));
        logStrings.add(LoggerUtils.stdOut("Finished. [" + tookThisLong(current) + "ms]"));
    }

    @Override
    protected void addDecryptor(ClassNode decryptor, String methodName) {
        decryptor.methods.add(StringEncryptionGenerator.lightMethod(methodName));
        decryptor.access = BytecodeUtils.makePublic(decryptor.access);
    }

    @Override
    protected void encrypt(MethodNode methodNode, String[] decryptorPath, LdcInsnNode ldc, String cst) {
        int key = NumberUtils.getRandomInt();
        ldc.cst = StringUtils.lightEncrypt(cst,
                decryptorPath[0].replace("/", "."),
                decryptorPath[1], key);
        methodNode.instructions.insert(ldc,
                new MethodInsnNode(Opcodes.INVOKESTATIC,
                        decryptorPath[0], decryptorPath[1],
                        "(Ljava/lang/Object;I)" +
                                "Ljava/lang/String;",
                        false));
        methodNode.instructions.insert(ldc, BytecodeUtils.createNumberInsn(key));
    }

    private static String encrypt(String msg, int callerClassHC, int callerMethodHC, int decryptorClassHC, int decryptorMethodHC, int extraKey) {
        StringBuilder sb = new StringBuilder();
        char[] chars = msg.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            switch (i % 4) {
                case 0: {
                    sb.append((char) (extraKey ^ callerClassHC ^ chars[i]));
                    break;
                }
                case 1: {
                    sb.append((char) (extraKey ^ callerMethodHC ^ chars[i]));
                    break;
                }
                case 2: {
                    sb.append((char) (extraKey ^ decryptorClassHC ^ chars[i]));
                    break;
                }
                case 3: {
                    sb.append((char) (extraKey ^ decryptorMethodHC ^ chars[i]));
                    break;
                }
            }
        }

        return sb.toString();
    }

    /**
     * Names of decryptor class and its members.
     */
    public class MemberNames {
        public String className;
        public String infoFieldName;
        public String cacheFieldName;
        public String populateMethodName;
        public String createInfoMethodName;
        public String setCacheMethodName;
        public String getCacheMethodName;
        public String cacheContainsMethodName;
        public String decryptorMethodName;

        MemberNames(HeavyStringEncryption instance) {
            this.className = StringUtils.randomClassName(instance.classNames(), instance.dictionary, len);
            this.infoFieldName = StringUtils.randomString(instance.dictionary, len);
            this.cacheFieldName = StringUtils.randomString(instance.dictionary, len);
            this.populateMethodName = StringUtils.randomString(instance.dictionary, len);
            this.createInfoMethodName = StringUtils.randomString(instance.dictionary, len);
            this.setCacheMethodName = StringUtils.randomString(instance.dictionary, len);
            this.getCacheMethodName = StringUtils.randomString(instance.dictionary, len);
            this.cacheContainsMethodName = StringUtils.randomString(instance.dictionary, len);
            this.decryptorMethodName = StringUtils.randomString(instance.dictionary, len);
        }
    }
}
