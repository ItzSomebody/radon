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

import me.itzsomebody.radon.generate.StringEncryptionGenerator;
import me.itzsomebody.radon.transformers.AbstractTransformer;
import me.itzsomebody.radon.utils.BytecodeUtils;
import me.itzsomebody.radon.utils.LoggerUtils;
import me.itzsomebody.radon.utils.NumberUtils;
import me.itzsomebody.radon.utils.StringUtils;
import org.objectweb.asm.tree.*;

/**
 * Transformer that encrypts strings using an extremely simple XOR algorithm.
 *
 * @author ItzSomebody
 */
public class SuperLightStringEncryption extends AbstractTransformer {
    /**
     * Length of names to generate.
     */
    protected final int len = 64;
    /**
     * Indication to not encrypt strings containing Spigot placeholders
     * (%%__USER__%%, %%__RESOURCE__%% and %%__NONCE__%%).
     */
    protected final boolean spigotMode;
    /**
     * Constructor used to create a {@link LightStringEncryption} object.
     *
     * @param spigotMode indication to not encrypt strings containing Spigot
     *                   placeholders (%%__USER__%%, %%__RESOURCE__%%
     *                   and %%__NONCE__%%).
     */
    public SuperLightStringEncryption(boolean spigotMode) {
        this.spigotMode = spigotMode;
    }

    /**
     * Applies obfuscation.
     */
    public void obfuscate() {
        AtomicInteger counter = new AtomicInteger();
        long current = System.currentTimeMillis();
        this.logStrings.add(LoggerUtils.stdOut("------------------------------------------------"));
        this.logStrings.add(LoggerUtils.stdOut("Started string encryption transformer"));
        String[] decryptorPath = new String[]{StringUtils.randomClass(classNames()), StringUtils.randomString(this.dictionary, len)};
        this.classNodes().stream().filter(classNode -> !this.exempted(classNode.name, "StringEncryptionGenerator")).forEach(classNode -> {
            classNode.methods.stream().filter(methodNode ->
                    !this.exempted(classNode.name + '.' + methodNode.name + methodNode.desc, "StringEncryptionGenerator")
                            && hasInstructions(methodNode)).forEach(methodNode -> {
                for (AbstractInsnNode insn : methodNode.instructions.toArray()) {
                    if (methodSize(methodNode) > 60000) break;
                    if (insn instanceof LdcInsnNode) {
                        LdcInsnNode ldc = (LdcInsnNode) insn;
                        if (ldc.cst instanceof String) {
                            String cst = (String) ldc.cst;
                            if (spigotCheck(cst))
                                continue;
                            encrypt(methodNode,decryptorPath, ldc, cst);
                            counter.incrementAndGet();
                        }
                    }
                }
            });
        });
        // Add decrypt method
        ClassNode decryptor = getClassMap().get(decryptorPath[0]);
        addDecryptor(decryptor, decryptorPath[1]);
        // Do logging
        logStrings.add(LoggerUtils.stdOut("Encrypted " + counter + " strings."));
        logStrings.add(LoggerUtils.stdOut("Finished. [" + tookThisLong(current) + "ms]"));
    }

    protected boolean spigotCheck(String cst) {
        return spigotMode &&
                (cst).contains("%%__USER__%%")
                || (cst).contains("%%__RESOURCE__%%")
                || (cst).contains("%%__NONCE__%%");
    }

    protected void addDecryptor(ClassNode decryptor, String methodName) {
        decryptor.methods.add(StringEncryptionGenerator.superLightMethod(methodName));
        decryptor.access = BytecodeUtils.makePublic(decryptor.access);
    }

    protected void encrypt(MethodNode methodNode, String[] decryptorPath,  LdcInsnNode ldc, String cst) {
        int key = NumberUtils.getRandomInt();
        ldc.cst = StringUtils.superLightEncrypt(cst, key);
        methodNode.instructions.insert(ldc,
                new MethodInsnNode(
                        INVOKESTATIC,
                        decryptorPath[0],
                        decryptorPath[1],
                        "(Ljava/lang/String;I)Ljava/lang/String;",
                        false));
        methodNode.instructions.insert(ldc, BytecodeUtils.createNumberInsn(key));
    }


}
