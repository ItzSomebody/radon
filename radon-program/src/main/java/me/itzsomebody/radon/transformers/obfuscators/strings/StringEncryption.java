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

import java.util.List;

import me.itzsomebody.radon.exceptions.IllegalConfigurationValueException;
import me.itzsomebody.radon.exclusions.ExclusionType;
import me.itzsomebody.radon.transformers.Transformer;
import me.itzsomebody.radon.utils.StringUtils;
import org.objectweb.asm.tree.*;

/**
 * Abstract class for string encryption transformers.
 *
 * @author ItzSomebody
 */
public abstract class StringEncryption extends Transformer {
    protected StringEncryptionSetup setup;

    public StringEncryption(StringEncryptionSetup setup) {
        this.setup = setup;
    }

    public static StringEncryption getTransformerFromString(String s, StringEncryptionSetup setup) {
        switch (s.toLowerCase()) {
            case "light":
                return new LightStringEncryption(setup);
            case "normal":
                return new NormalStringEncryption(setup);
            case "heavy":
                return new HeavyStringEncryption(setup);
            default:
                throw new IllegalConfigurationValueException("Did not expect " + s + " as a string obfuscation mode");
        }
    }

    protected boolean excludedString(String str) {
        for (String s : this.setup.getExemptedStrings())
            if (str.contains(s))
                return true;

        return false;
    }

    @Override
    protected ExclusionType getExclusionType() {
        return ExclusionType.STRING_ENCRYPTION;
    }

    public List<String> getExcludedStrings() {
        return this.setup.getExemptedStrings();
    }

    protected InsnList getSafeStringInsnList(String string) {
        InsnList insnList = new InsnList();
        if (StringUtils.getUtf8StringSize(string) < StringUtils.MAX_SAFE_BYTE_COUNT) {
            insnList.add(new LdcInsnNode(string));
            return insnList;
        }

        insnList.add(new TypeInsnNode(NEW, "java/lang/StringBuilder"));
        insnList.add(new InsnNode(DUP));
        insnList.add(new MethodInsnNode(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "()V", false));

        String[] chunks = StringUtils.splitUtf8ToChunks(string, StringUtils.MAX_SAFE_BYTE_COUNT);
        for (String chunk : chunks) {
            insnList.add(new LdcInsnNode(chunk));
            insnList.add(new MethodInsnNode(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false));
        }
        insnList.add(new MethodInsnNode(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false));

        return insnList;
    }
}
