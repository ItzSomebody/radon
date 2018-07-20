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

import me.itzsomebody.radon.methods.StringEncryptionGenerator;
import me.itzsomebody.radon.utils.BytecodeUtils;
import me.itzsomebody.radon.utils.NumberUtils;
import me.itzsomebody.radon.utils.StringUtils;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

/**
 * Transformer that encrypts strings using a stacktrace-backed method.
 *
 * @author ItzSomebody
 */
public class LightStringEncryption extends VeryLightStringEncryption {
    /**
     * Constructor used to create a {@link LightStringEncryption} object.
     *
     * @param spigotMode indication to not encrypt strings containing Spigot
     *                   placeholders(%%__USER__%%, %%__RESOURCE__%%
     *                   and %%__NONCE__%%).
     */
    public LightStringEncryption(boolean spigotMode) {
        super(spigotMode);
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
}
