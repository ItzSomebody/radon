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

package me.itzsomebody.radon.transformers.miscellaneous.watermarker;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Stream;
import me.itzsomebody.radon.Logger;
import me.itzsomebody.radon.asm.ClassWrapper;
import me.itzsomebody.radon.config.ConfigurationSetting;
import me.itzsomebody.radon.exceptions.InvalidConfigurationValueException;
import me.itzsomebody.radon.exclusions.ExclusionType;
import me.itzsomebody.radon.transformers.Transformer;
import me.itzsomebody.radon.utils.ASMUtils;
import me.itzsomebody.radon.utils.RandomUtils;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import static me.itzsomebody.radon.utils.ConfigUtils.getValueOrDefault;

/**
 * Embeds a watermark into random classes.
 *
 * @author ItzSomebody.
 */
public class Watermarker extends Transformer {
    private final Map<String, WatermarkerSetting> KEY_MAP = new HashMap<>();
    private String message;
    private String key;

    public Watermarker() {
        Stream.of(WatermarkerSetting.values()).forEach(setting -> KEY_MAP.put(setting.getName(), setting));
    }

    @Override
    public void transform() {
        ArrayList<ClassWrapper> classWrappers = new ArrayList<>(this.getClassWrappers());

        for (int i = 0; i < 3; i++) { // Two extra injections helps with reliability of watermark to be extracted
            Deque<Character> watermark = cipheredWatermark();
            while (!watermark.isEmpty()) {
                ClassWrapper classWrapper;
                int counter = 0;

                do {
                    classWrapper = classWrappers.get(RandomUtils.getRandomInt(0, classWrappers.size()));
                    counter++;

                    if (counter > 20)
                        throw new IllegalStateException("Radon couldn't find any methods to embed a watermark in after " + counter + " tries.");
                } while (classWrapper.classNode.methods.size() != 0);

                MethodNode methodNode = classWrapper.classNode.methods.get(RandomUtils.getRandomInt(0,
                        classWrapper.classNode.methods.size()));
                if (hasInstructions(methodNode)) {
                    methodNode.instructions.insertBefore(methodNode.instructions.getFirst(),
                            createInstructions(watermark, methodNode));
                }
            }
        }

        Logger.stdOut("Successfully embedded watermark.");
    }

    private static InsnList createInstructions(Deque<Character> watermark, MethodNode methodNode) {
        int offset = methodNode.maxLocals;
        int xorKey = RandomUtils.getRandomInt();
        int watermarkChar = watermark.pop() ^ xorKey;
        int indexXorKey = RandomUtils.getRandomInt();
        int watermarkIndex = watermark.size() ^ indexXorKey;

        InsnList instructions = new InsnList();
        instructions.add(ASMUtils.getNumberInsn(xorKey));
        instructions.add(ASMUtils.getNumberInsn(watermarkChar));
        instructions.add(ASMUtils.getNumberInsn(indexXorKey));
        instructions.add(ASMUtils.getNumberInsn(watermarkIndex));

        // Local variable x where x is the max locals allowed in method can be the top of a long or double so we add 1
        instructions.add(new VarInsnNode(ISTORE, offset + 1));
        instructions.add(new VarInsnNode(ISTORE, offset + 2));
        instructions.add(new VarInsnNode(ISTORE, offset + 3));
        instructions.add(new VarInsnNode(ISTORE, offset + 4));

        return instructions;
    }

    // Really weak cipher, lul.
    private Deque<Character> cipheredWatermark() {
        char[] messageChars = getMessage().toCharArray();
        char[] keyChars = getKey().toCharArray();
        Deque<Character> returnThis = new ArrayDeque<>();

        for (int i = 0; i < messageChars.length; i++) {
            returnThis.push((char) (messageChars[i] ^ keyChars[i % keyChars.length]));
        }

        return returnThis;
    }

    @Override
    public ExclusionType getExclusionType() {
        return null;
    }

    @Override
    public Object getConfiguration() {
        Map<String, Object> config = new LinkedHashMap<>();

        setMessage(getValueOrDefault(WatermarkerSetting.MESSAGE.getName(), config, null));
        setKey(getValueOrDefault(WatermarkerSetting.KEY.getName(), config, null));

        return config;
    }

    @Override
    public void setConfiguration(Map<String, Object> config) {
        config.put(WatermarkerSetting.MESSAGE.getName(), getMessage());
        config.put(WatermarkerSetting.KEY.getName(), getKey());
    }

    @Override
    public void verifyConfiguration(Map<String, Object> config) {
        config.forEach((k, v) -> {
            WatermarkerSetting setting = KEY_MAP.get(k);

            if (setting == null)
                throw new InvalidConfigurationValueException(ConfigurationSetting.WATERMARK.getName() + '.' + k
                        + " is an invalid configuration key");
            if (!setting.getExpectedType().isInstance(v))
                throw new InvalidConfigurationValueException(ConfigurationSetting.WATERMARK.getName() + '.' + k,
                        setting.getExpectedType(), v.getClass());

        });
    }

    @Override
    public String getName() {
        return "Watermarker";
    }

    private String getMessage() {
        return message;
    }

    private void setMessage(String message) {
        this.message = message;
    }

    private String getKey() {
        return key;
    }

    private void setKey(String key) {
        this.key = key;
    }
}
