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

package me.itzsomebody.radon.transformers.obfuscators.numbers;

import java.util.concurrent.atomic.AtomicInteger;
import me.itzsomebody.radon.Logger;
import me.itzsomebody.radon.utils.BytecodeUtils;
import me.itzsomebody.radon.utils.RandomUtils;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodNode;

/**
 * Splits integer and long constants into random bitwise operations.
 *
 * @author ItzSomebody
 */
public class BitwiseObfuscator extends NumberObfuscation {
    @Override
    public void transform() {
        AtomicInteger counter = new AtomicInteger();

        getClassWrappers().stream().filter(classWrapper -> !excluded(classWrapper)).forEach(classWrapper ->
                classWrapper.methods.stream().filter(methodWrapper -> !excluded(methodWrapper)
                        && hasInstructions(methodWrapper)).forEach(methodWrapper -> {
                    MethodNode methodNode = methodWrapper.methodNode;
                    int leeway = getSizeLeeway(methodNode);

                    for (AbstractInsnNode insn : methodNode.instructions.toArray()) {
                        if (leeway < 10000)
                            break;

                        if (BytecodeUtils.isIntInsn(insn) && master.isIntegerTamperingEnabled()) {
                            InsnList insns = obfuscateNumber(BytecodeUtils.getIntegerFromInsn(insn));

                            methodNode.instructions.insert(insn, insns);
                            methodNode.instructions.remove(insn);

                            counter.incrementAndGet();
                        } else if (BytecodeUtils.isLongInsn(insn) && master.isLongTamperingEnabled()) {
                            InsnList insns = obfuscateNumber(BytecodeUtils.getLongFromInsn(insn));

                            methodNode.instructions.insert(insn, insns);
                            methodNode.instructions.remove(insn);

                            counter.incrementAndGet();
                        }
                    }
                }));

        Logger.stdOut("Split " + counter.get() + " number constants into bitwise instructions");
    }

    private InsnList obfuscateNumber(int originalNum) {
        int current = randomInt(originalNum);

        InsnList insns = new InsnList();
        insns.add(BytecodeUtils.getNumberInsn(current));

        for (int i = 0; i < RandomUtils.getRandomInt(2, 6); i++) {
            int operand;

            switch (RandomUtils.getRandomInt(6)) {
                case 0:
                    operand = randomInt(current);

                    insns.add(BytecodeUtils.getNumberInsn(operand));
                    insns.add(new InsnNode(IAND));

                    current &= operand;
                    break;
                case 1:
                    operand = randomInt(current);

                    insns.add(BytecodeUtils.getNumberInsn(operand));
                    insns.add(new InsnNode(IOR));

                    current |= operand;
                    break;
                case 2:
                    operand = randomInt(current);

                    insns.add(BytecodeUtils.getNumberInsn(operand));
                    insns.add(new InsnNode(IXOR));

                    current ^= operand;
                    break;
                case 3:
                    operand = RandomUtils.getRandomInt(1, 5);

                    insns.add(BytecodeUtils.getNumberInsn(operand));
                    insns.add(new InsnNode(ISHL));

                    current <<= operand;
                    break;
                case 4:
                    operand = RandomUtils.getRandomInt(1, 5);

                    insns.add(BytecodeUtils.getNumberInsn(operand));
                    insns.add(new InsnNode(ISHR));

                    current >>= operand;
                    break;
                case 5:
                default:
                    operand = RandomUtils.getRandomInt(1, 5);

                    insns.add(BytecodeUtils.getNumberInsn(operand));
                    insns.add(new InsnNode(IUSHR));

                    current >>>= operand;
                    break;
            }
        }

        int correctionOperand = originalNum ^ current;
        insns.add(BytecodeUtils.getNumberInsn(correctionOperand));
        insns.add(new InsnNode(IXOR));

        return insns;
    }

    private InsnList obfuscateNumber(long originalNum) {
        long current = randomLong(originalNum);

        InsnList insns = new InsnList();
        insns.add(BytecodeUtils.getNumberInsn(current));

        for (int i = 0; i < RandomUtils.getRandomInt(2, 6); i++) {
            long operand;

            switch (RandomUtils.getRandomInt(6)) {
                case 0:
                    operand = randomLong(current);

                    insns.add(BytecodeUtils.getNumberInsn(operand));
                    insns.add(new InsnNode(LAND));

                    current &= operand;
                    break;
                case 1:
                    operand = randomLong(current);

                    insns.add(BytecodeUtils.getNumberInsn(operand));
                    insns.add(new InsnNode(LOR));

                    current |= operand;
                    break;
                case 2:
                    operand = randomLong(current);

                    insns.add(BytecodeUtils.getNumberInsn(operand));
                    insns.add(new InsnNode(LXOR));

                    current ^= operand;
                    break;
                case 3:
                    operand = RandomUtils.getRandomInt(1, 32);

                    insns.add(BytecodeUtils.getNumberInsn((int) operand));
                    insns.add(new InsnNode(LSHL));

                    current <<= operand;
                    break;
                case 4:
                    operand = RandomUtils.getRandomInt(1, 32);

                    insns.add(BytecodeUtils.getNumberInsn((int) operand));
                    insns.add(new InsnNode(LSHR));

                    current >>= operand;
                    break;
                case 5:
                default:
                    operand = RandomUtils.getRandomInt(1, 32);

                    insns.add(BytecodeUtils.getNumberInsn((int) operand));
                    insns.add(new InsnNode(LUSHR));

                    current >>>= operand;
                    break;
            }
        }

        long correctionOperand = originalNum ^ current;
        insns.add(BytecodeUtils.getNumberInsn(correctionOperand));
        insns.add(new InsnNode(LXOR));

        return insns;
    }
}
