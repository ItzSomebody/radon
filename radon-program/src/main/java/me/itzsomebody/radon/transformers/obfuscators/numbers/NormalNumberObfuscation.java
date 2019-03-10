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

package me.itzsomebody.radon.transformers.obfuscators.numbers;

import java.util.concurrent.atomic.AtomicInteger;
import me.itzsomebody.radon.utils.BytecodeUtils;
import me.itzsomebody.radon.Logger;
import me.itzsomebody.radon.utils.RandomUtils;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodNode;

/**
 * Obfuscates integer and long constants using arithmetic.
 *
 * @author ItzSomebody
 */
public class NormalNumberObfuscation extends NumberObfuscation {
    @Override
    public void transform() {
        AtomicInteger counter = new AtomicInteger();
        this.getClassWrappers().stream().filter(classWrapper ->
                !excluded(classWrapper)).forEach(classWrapper ->
                classWrapper.methods.stream().filter(methodWrapper -> !excluded(methodWrapper)
                        && hasInstructions(methodWrapper.methodNode)).forEach(methodWrapper -> {
                    MethodNode methodNode = methodWrapper.methodNode;
                    int leeway = getSizeLeeway(methodNode);

                    for (AbstractInsnNode insn : methodNode.instructions.toArray()) {
                        if (leeway < 10000)
                            break;
                        if (BytecodeUtils.isIntInsn(insn)) {
                            int originalNum = BytecodeUtils.getIntegerFromInsn(insn);
                            switch (RandomUtils.getRandomInt(3)) {
                                case 0: {
                                    int value1 = RandomUtils.getRandomInt(255) + 20;
                                    int value2 = RandomUtils.getRandomInt(value1) + value1;
                                    int value3 = originalNum - value1 + value2; // You kids say algebra is useless???
                                    InsnList insnList = new InsnList();
                                    insnList.add(BytecodeUtils.getNumberInsn(value1));
                                    insnList.add(BytecodeUtils.getNumberInsn(value2));
                                    insnList.add(new InsnNode(ISUB));
                                    insnList.add(BytecodeUtils.getNumberInsn(value3));
                                    insnList.add(new InsnNode(IADD));
                                    methodNode.instructions.insertBefore(insn, insnList);
                                    methodNode.instructions.remove(insn);
                                    leeway -= 8;
                                    counter.incrementAndGet();
                                    break;
                                }
                                case 1: {
                                    int value1 = RandomUtils.getRandomInt(255) + 20;
                                    int value2 = RandomUtils.getRandomInt(value1) + value1;
                                    int value3 = RandomUtils.getRandomInt(value2 + 1);
                                    int value4 = originalNum - value1 + value2 - value3;
                                    InsnList insnList = new InsnList();
                                    insnList.add(BytecodeUtils.getNumberInsn(value1));
                                    insnList.add(BytecodeUtils.getNumberInsn(value2));
                                    insnList.add(new InsnNode(ISUB));
                                    insnList.add(BytecodeUtils.getNumberInsn(value3));
                                    insnList.add(new InsnNode(IADD));
                                    insnList.add(BytecodeUtils.getNumberInsn(value4));
                                    insnList.add(new InsnNode(IADD));
                                    methodNode.instructions.insertBefore(insn, insnList);
                                    methodNode.instructions.remove(insn);
                                    leeway -= 10;
                                    counter.incrementAndGet();
                                    break;
                                }
                                case 2: {
                                    int value1 = RandomUtils.getRandomInt(255) + 20;
                                    int value2 = RandomUtils.getRandomInt(value1) + value1;
                                    int value3 = RandomUtils.getRandomInt(value2 + 1);
                                    int value4 = RandomUtils.getRandomInt(value3 + 1);
                                    int value5 = originalNum - value1 + value2 - value3 + value4;
                                    InsnList insnList = new InsnList();
                                    insnList.add(BytecodeUtils.getNumberInsn(value1));
                                    insnList.add(BytecodeUtils.getNumberInsn(value2));
                                    insnList.add(new InsnNode(ISUB));
                                    insnList.add(BytecodeUtils.getNumberInsn(value3));
                                    insnList.add(new InsnNode(IADD));
                                    insnList.add(BytecodeUtils.getNumberInsn(value4));
                                    insnList.add(new InsnNode(ISUB));
                                    insnList.add(BytecodeUtils.getNumberInsn(value5));
                                    insnList.add(new InsnNode(IADD));
                                    methodNode.instructions.insertBefore(insn, insnList);
                                    methodNode.instructions.remove(insn);
                                    leeway -= 12;
                                    counter.incrementAndGet();
                                    break;
                                }
                            }
                        } else if (BytecodeUtils.isLongInsn(insn)) {
                            long originalNum = BytecodeUtils.getLongFromInsn(insn);
                            switch (RandomUtils.getRandomInt(3)) {
                                case 0: {
                                    long value1 = RandomUtils.getRandomLong(255) + 20;
                                    long value2 = RandomUtils.getRandomLong(value1) + value1;
                                    long value3 = originalNum - value1 + value2;
                                    InsnList insnList = new InsnList();
                                    insnList.add(BytecodeUtils.getNumberInsn(value1));
                                    insnList.add(BytecodeUtils.getNumberInsn(value2));
                                    insnList.add(new InsnNode(LSUB));
                                    insnList.add(BytecodeUtils.getNumberInsn(value3));
                                    insnList.add(new InsnNode(LADD));
                                    methodNode.instructions.insertBefore(insn, insnList);
                                    methodNode.instructions.remove(insn);
                                    leeway -= 15;
                                    counter.incrementAndGet();
                                    break;
                                }
                                case 1: {
                                    long value1 = RandomUtils.getRandomInt(255) + 20;
                                    long value2 = RandomUtils.getRandomInt((int) value1) + value1;
                                    long value3 = RandomUtils.getRandomInt((int) (value2 + 1));
                                    long value4 = originalNum - value1 + value2 - value3;
                                    InsnList insnList = new InsnList();
                                    insnList.add(BytecodeUtils.getNumberInsn(value1));
                                    insnList.add(BytecodeUtils.getNumberInsn(value2));
                                    insnList.add(new InsnNode(LSUB));
                                    insnList.add(BytecodeUtils.getNumberInsn(value3));
                                    insnList.add(new InsnNode(LADD));
                                    insnList.add(BytecodeUtils.getNumberInsn(value4));
                                    insnList.add(new InsnNode(LADD));
                                    methodNode.instructions.insertBefore(insn, insnList);
                                    methodNode.instructions.remove(insn);
                                    leeway -= 17;
                                    counter.incrementAndGet();
                                    break;
                                }
                                case 2: {
                                    long value1 = RandomUtils.getRandomInt(255) + 20;
                                    long value2 = RandomUtils.getRandomInt((int) value1) + value1;
                                    long value3 = RandomUtils.getRandomInt((int) (value2 + 1));
                                    long value4 = RandomUtils.getRandomInt((int) (value3 + 1));
                                    long value5 = originalNum - value1 + value2 - value3 + value4;
                                    InsnList insnList = new InsnList();
                                    insnList.add(BytecodeUtils.getNumberInsn(value1));
                                    insnList.add(BytecodeUtils.getNumberInsn(value2));
                                    insnList.add(new InsnNode(LSUB));
                                    insnList.add(BytecodeUtils.getNumberInsn(value3));
                                    insnList.add(new InsnNode(LADD));
                                    insnList.add(BytecodeUtils.getNumberInsn(value4));
                                    insnList.add(new InsnNode(LSUB));
                                    insnList.add(BytecodeUtils.getNumberInsn(value5));
                                    insnList.add(new InsnNode(LADD));
                                    methodNode.instructions.insertBefore(insn, insnList);
                                    methodNode.instructions.remove(insn);
                                    leeway -= 20;
                                    counter.incrementAndGet();
                                    break;
                                }
                            }
                        }
                    }
                })
        );
        Logger.stdOut(String.format("Split %d numbers into math instructions.", counter.get()));
    }

    @Override
    public String getName() {
        return "Normal number obfuscation";
    }
}
