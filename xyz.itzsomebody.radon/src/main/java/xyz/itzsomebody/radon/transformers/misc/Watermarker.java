/*
 * Radon - An open-source Java obfuscator
 * Copyright (C) 2021 ItzSomebody
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

package xyz.itzsomebody.radon.transformers.misc;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.VarInsnNode;
import xyz.itzsomebody.commons.MaxLocalsUpdater;
import xyz.itzsomebody.commons.matcher.InstructionPattern;
import xyz.itzsomebody.commons.matcher.rules.IntConstRule;
import xyz.itzsomebody.commons.matcher.rules.OpcodeRule;
import xyz.itzsomebody.radon.config.Configuration;
import xyz.itzsomebody.radon.exceptions.FatalRadonException;
import xyz.itzsomebody.radon.exclusions.Exclusion;
import xyz.itzsomebody.radon.transformers.Transformer;
import xyz.itzsomebody.radon.transformers.Transformers;
import xyz.itzsomebody.radon.utils.RandomUtils;
import xyz.itzsomebody.radon.utils.asm.ASMUtils;
import xyz.itzsomebody.radon.utils.asm.ClassWrapper;
import xyz.itzsomebody.radon.utils.asm.MethodWrapper;
import xyz.itzsomebody.radon.utils.logging.RadonLogger;

import java.util.*;
import java.util.zip.ZipFile;

public class Watermarker extends Transformer {
    private char[] message;
    private char[] key;
    private int copies;

    @Override
    public void transform() {
        ArrayList<ClassWrapper> wrappersList = new ArrayList<>(classes());

        for (int i = 0; i < copies; i++) {
            Deque<Character> cipheredCharacters = cipheredWatermark();
            while (!cipheredCharacters.isEmpty()) {
                MethodWrapper wrapper = getInjectableMethod(wrappersList);
                MaxLocalsUpdater.update(wrapper.getMethodNode());

                wrapper.getMethodNode().instructions.insert(createInstructions(cipheredCharacters, wrapper.getMethodNode().maxLocals));
                wrapper.getMethodNode().maxLocals += 4;
            }
        }

        RadonLogger.info("Successfully injected watermark");
    }

    private MethodWrapper getInjectableMethod(ArrayList<ClassWrapper> wrappersList) {
        var missCount = 0;
        for (; missCount < 20; missCount++) {
            var wrapper = wrappersList.get(RandomUtils.randomInt(wrappersList.size()));

            if (!wrapper.getMethods().isEmpty()) {
                var method = wrapper.getMethods().get(RandomUtils.randomInt(wrapper.getMethods().size()));

                if (method.hasInstructions()) {
                    return method;
                }
            }
        }
        throw new FatalRadonException("Could not find a suitable method to inject watermark into after " + missCount + " tries");
    }

    private InsnList createInstructions(Deque<Character> ciphered, int offset) {
        var charXorKey = RandomUtils.randomInt(Character.MAX_VALUE);
        var xorChar = ciphered.pop() ^ charXorKey;
        var indexXorKey = RandomUtils.randomInt(Short.MIN_VALUE, Short.MAX_VALUE);
        var xorIndex = ciphered.size() ^ indexXorKey;

        InsnList instructions = new InsnList();
        instructions.add(ASMUtils.getNumberInsn(charXorKey));
        instructions.add(ASMUtils.getNumberInsn(xorChar));
        instructions.add(ASMUtils.getNumberInsn(indexXorKey));
        instructions.add(ASMUtils.getNumberInsn(xorIndex));

        instructions.add(new VarInsnNode(ISTORE, offset + 1));
        instructions.add(new VarInsnNode(ISTORE, offset + 2));
        instructions.add(new VarInsnNode(ISTORE, offset + 3));
        instructions.add(new VarInsnNode(ISTORE, offset + 4));

        return instructions;
    }

    private Deque<Character> cipheredWatermark() {
        var ciphered = new ArrayDeque<Character>();
        for (int i = 0; i < message.length; i++) {
            ciphered.push((char) (message[i] ^ key[i % key.length]));
        }
        return ciphered;
    }

    @Override
    public Exclusion.ExclusionType getExclusionType() {
        return Exclusion.ExclusionType.WATERMARK;
    }

    @Override
    public void loadSetup(Configuration config) {
        message = config.getOrDefault(getLocalConfigPath() + ".message", "tucks is love, tucks is life").toCharArray();
        key = config.getOrDefault(getLocalConfigPath() + ".key", "ginkoid").toCharArray();
        copies = config.getOrDefault(getLocalConfigPath() + ".copies", 2) + 1;
    }

    @Override
    public String getConfigName() {
        return Transformers.WATERMARK.getConfigName();
    }

    public static class Extractor {
        private static final InstructionPattern PATTERN = new InstructionPattern(
                new IntConstRule(),
                new IntConstRule(),
                new IntConstRule(),
                new IntConstRule(),
                new OpcodeRule(ISTORE),
                new OpcodeRule(ISTORE),
                new OpcodeRule(ISTORE),
                new OpcodeRule(ISTORE)
        );
        private final ZipFile file;
        private final String key;

        public Extractor(ZipFile file, String key) {
            this.file = file;
            this.key = key;
        }

        private boolean enoughInfo(Map<Integer, Character> charMap) {
            if (charMap.size() < 1) {
                return false;
            }

            for (var i = 0; i < charMap.size(); i++) {
                if (!charMap.containsKey(i)) {
                    return false;
                }
            }

            return true;
        }

        private String constructString(Map<Integer, Character> embedMap) {
            var sb = new StringBuilder();
            for (var i = 0; i < embedMap.size(); i++) {
                sb.append((char) embedMap.get(i));
            }
            return sb.toString();
        }

        private String decrypt(String enc, String key) {
            var messageChars = enc.toCharArray();
            var keyChars = key.toCharArray();
            var sb = new StringBuilder();

            for (var i = 0; i < messageChars.length; i++) {
                sb.append((char) (messageChars[i] ^ keyChars[i % keyChars.length]));
            }

            return sb.toString();
        }

        private void attemptExtract(ClassNode classNode, Map<Integer, Character> map) {
            classNode.methods.forEach(methodNode -> {
                if (methodNode.instructions.size() > 0) {
                    var matcher = PATTERN.matcher(methodNode.instructions.getFirst());

                    if (matcher.find()) {
                        matcher.getAllCaptured().forEach(captured -> {
                            char character = (char) (
                                    ASMUtils.getIntegerFromInsn(captured.get(0)) ^ ASMUtils.getIntegerFromInsn(captured.get(1))
                            );
                            int index = (
                                    ASMUtils.getIntegerFromInsn(captured.get(2)) ^ ASMUtils.getIntegerFromInsn(captured.get(3))
                            );
                            map.put(index, character);
                        });
                    }
                }
            });
        }

        public String extractId() {
            Map<Integer, Character> map = new HashMap<>();
            var entries = file.entries();
            while (entries.hasMoreElements()) {
                var entry = entries.nextElement();
                if (!entry.isDirectory() && entry.getName().endsWith(".class")) {
                    try {
                        var node = new ClassNode();
                        var reader = new ClassReader(file.getInputStream(entry));
                        reader.accept(node, ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
                        attemptExtract(node, map);
                    } catch (Throwable ignored) {
                    }
                }
            }
            if (enoughInfo(map)) {
                return "Found watermark: " + decrypt(constructString(map), key);
            }
            return "Unable to extract watermarked id (does it exist?)";
        }
    }
}
