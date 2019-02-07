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

package me.itzsomebody.radon.transformers.miscellaneous.watermarker;

import java.util.ArrayList;
import java.util.Stack;
import me.itzsomebody.radon.asm.ClassWrapper;
import me.itzsomebody.radon.exclusions.ExclusionType;
import me.itzsomebody.radon.transformers.Transformer;
import me.itzsomebody.radon.utils.BytecodeUtils;
import me.itzsomebody.radon.utils.LoggerUtils;
import me.itzsomebody.radon.utils.RandomUtils;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

/**
 * Embeds a watermark into random classes.
 *
 * @author ItzSomebody.
 */
public class Watermarker extends Transformer {
    private WatermarkerSetup setup;

    public Watermarker(WatermarkerSetup setup) {
        this.setup = setup;
    }

    @Override
    public void transform() {
        ArrayList<ClassWrapper> classWrappers = new ArrayList<>(this.getClassWrappers());

        for (int i = 0; i < 3; i++) { // Two extra injections helps with reliability of watermark to be extracted
            Stack<Character> watermark = cipheredWatermark();
            while (!watermark.isEmpty()) {
                ClassWrapper classWrapper;
                int counter = 0;

                do {
                    classWrapper = classWrappers.get(RandomUtils.getRandomInt(0, classWrappers.size()));
                    counter++;

                    if (counter > 20)
                        throw new RuntimeException("Radon couldn't find any methods to embed a watermark in after " + counter + " tries.");
                } while (classWrapper.classNode.methods.size() != 0);

                MethodNode methodNode = classWrapper.classNode.methods.get(RandomUtils.getRandomInt(0,
                        classWrapper.classNode.methods.size()));
                if (hasInstructions(methodNode)) {
                    methodNode.instructions.insertBefore(methodNode.instructions.getFirst(),
                            createInstructions(watermark, methodNode));
                }
            }
        }

        LoggerUtils.stdOut("Successfully embedded watermark.");
    }

    private static InsnList createInstructions(Stack<Character> watermark, MethodNode methodNode) {
        int offset = methodNode.maxLocals;
        int xorKey = RandomUtils.getRandomInt();
        int watermarkChar = watermark.pop() ^ xorKey;
        int indexXorKey = RandomUtils.getRandomInt();
        int watermarkIndex = watermark.size() ^ indexXorKey;

        InsnList instructions = new InsnList();
        instructions.add(BytecodeUtils.getNumberInsn(xorKey));
        instructions.add(BytecodeUtils.getNumberInsn(watermarkChar));
        instructions.add(BytecodeUtils.getNumberInsn(indexXorKey));
        instructions.add(BytecodeUtils.getNumberInsn(watermarkIndex));

        // Local variable x where x is the max locals allowed in method can be the top of a long or double so we add 1
        instructions.add(new VarInsnNode(ISTORE, offset + 1));
        instructions.add(new VarInsnNode(ISTORE, offset + 2));
        instructions.add(new VarInsnNode(ISTORE, offset + 3));
        instructions.add(new VarInsnNode(ISTORE, offset + 4));

        return instructions;
    }

    // Really weak cipher, lul.
    private Stack<Character> cipheredWatermark() {
        char[] messageChars = setup.getMessage().toCharArray();
        char[] keyChars = setup.getKey().toCharArray();
        Stack<Character> returnThis = new Stack<>();

        for (int i = 0; i < messageChars.length; i++) {
            returnThis.push((char) (messageChars[i] ^ keyChars[i % keyChars.length]));
        }

        return returnThis;
    }

    @Override
    protected ExclusionType getExclusionType() {
        return null;
    }

    @Override
    public String getName() {
        return "Watermarker";
    }

    public WatermarkerSetup getSetup() {
        return setup;
    }
}
