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
import me.itzsomebody.radon.Main;
import me.itzsomebody.radon.utils.ASMUtils;
import me.itzsomebody.radon.utils.RandomUtils;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;

/**
 * Splits number constants into arithmetic operations.
 *
 * @author ItzSomebody
 */
public class ArithmeticObfuscator extends NumberObfuscation {
    @Override
    public void transform() {
        AtomicInteger counter = new AtomicInteger();

        getClassWrappers().stream().filter(classWrapper -> !excluded(classWrapper)).forEach(classWrapper ->
                classWrapper.getMethods().stream().filter(methodWrapper -> !excluded(methodWrapper)).forEach(methodWrapper -> {
                    int leeway = getSizeLeeway(methodWrapper);
                    InsnList methodInstructions = methodWrapper.getInstructions();

                    for (AbstractInsnNode insn : methodInstructions.toArray()) {
                        if (leeway < 10000)
                            break;

                        if (ASMUtils.isIntInsn(insn) && master.isIntegerTamperingEnabled()) {
                            InsnList insns = obfuscateNumber(ASMUtils.getIntegerFromInsn(insn));

                            methodInstructions.insert(insn, insns);
                            methodInstructions.remove(insn);

                            counter.incrementAndGet();
                        } else if (ASMUtils.isLongInsn(insn) && master.isLongTamperingEnabled()) {
                            InsnList insns = obfuscateNumber(ASMUtils.getLongFromInsn(insn));

                            methodInstructions.insert(insn, insns);
                            methodInstructions.remove(insn);

                            counter.incrementAndGet();
                        } else if (ASMUtils.isFloatInsn(insn) && master.isFloatTamperingEnabled()) {
                            InsnList insns = obfuscateNumber(ASMUtils.getFloatFromInsn(insn));

                            methodInstructions.insert(insn, insns);
                            methodInstructions.remove(insn);

                            counter.incrementAndGet();
                        } else if (ASMUtils.isDoubleInsn(insn) && master.isDoubleTamperingEnabled()) {
                            InsnList insns = obfuscateNumber(ASMUtils.getDoubleFromInsn(insn));

                            methodInstructions.insert(insn, insns);
                            methodInstructions.remove(insn);

                            counter.incrementAndGet();
                        }
                    }
                }));

        Main.info("Split " + counter.get() + " number constants into arithmetic instructions");
    }

    private InsnList obfuscateNumber(int originalNum) {
        int current = randomInt(originalNum);

        InsnList insns = new InsnList();
        insns.add(ASMUtils.getNumberInsn(current));

        for (int i = 0; i < RandomUtils.getRandomInt(2, 6); i++) {
            int operand;

            switch (RandomUtils.getRandomInt(6)) {
                case 0:
                    operand = randomInt(current);

                    insns.add(ASMUtils.getNumberInsn(operand));
                    insns.add(new InsnNode(IADD));

                    current += operand;
                    break;
                case 1:
                    operand = randomInt(current);

                    insns.add(ASMUtils.getNumberInsn(operand));
                    insns.add(new InsnNode(ISUB));

                    current -= operand;
                    break;
                case 2:
                    operand = RandomUtils.getRandomInt(1, 255);

                    insns.add(ASMUtils.getNumberInsn(operand));
                    insns.add(new InsnNode(IMUL));

                    current *= operand;
                    break;
                case 3:
                    operand = RandomUtils.getRandomInt(1, 255);

                    insns.add(ASMUtils.getNumberInsn(operand));
                    insns.add(new InsnNode(IDIV));

                    current /= operand;
                    break;
                case 4:
                default:
                    operand = RandomUtils.getRandomInt(1, 255);

                    insns.add(ASMUtils.getNumberInsn(operand));
                    insns.add(new InsnNode(IREM));

                    current %= operand;
                    break;
            }
        }

        int correctionOperand = originalNum - current;
        insns.add(ASMUtils.getNumberInsn(correctionOperand));
        insns.add(new InsnNode(IADD));

        return insns;
    }

    private InsnList obfuscateNumber(long originalNum) {
        long current = randomLong(originalNum);

        InsnList insns = new InsnList();
        insns.add(ASMUtils.getNumberInsn(current));

        for (int i = 0; i < RandomUtils.getRandomInt(2, 6); i++) {
            long operand;

            switch (RandomUtils.getRandomInt(6)) {
                case 0:
                    operand = randomLong(current);

                    insns.add(ASMUtils.getNumberInsn(operand));
                    insns.add(new InsnNode(LADD));

                    current += operand;
                    break;
                case 1:
                    operand = randomLong(current);

                    insns.add(ASMUtils.getNumberInsn(operand));
                    insns.add(new InsnNode(LSUB));

                    current -= operand;
                    break;
                case 2:
                    operand = RandomUtils.getRandomInt(1, 65535);

                    insns.add(ASMUtils.getNumberInsn(operand));
                    insns.add(new InsnNode(LMUL));

                    current *= operand;
                    break;
                case 3:
                    operand = RandomUtils.getRandomInt(1, 65535);

                    insns.add(ASMUtils.getNumberInsn(operand));
                    insns.add(new InsnNode(LDIV));

                    current /= operand;
                    break;
                case 4:
                default:
                    operand = RandomUtils.getRandomInt(1, 255);

                    insns.add(ASMUtils.getNumberInsn(operand));
                    insns.add(new InsnNode(LREM));

                    current %= operand;
                    break;
            }
        }

        long correctionOperand = originalNum - current;
        insns.add(ASMUtils.getNumberInsn(correctionOperand));
        insns.add(new InsnNode(LADD));

        return insns;
    }

    private InsnList obfuscateNumber(float originalNum) {
        float current = randomFloat(originalNum);

        InsnList insns = new InsnList();
        insns.add(ASMUtils.getNumberInsn(current));

        for (int i = 0; i < RandomUtils.getRandomInt(2, 6); i++) {
            float operand;

            switch (RandomUtils.getRandomInt(6)) {
                case 0:
                    operand = randomFloat(current);

                    insns.add(ASMUtils.getNumberInsn(operand));
                    insns.add(new InsnNode(FADD));

                    current += operand;
                    break;
                case 1:
                    operand = randomFloat(current);

                    insns.add(ASMUtils.getNumberInsn(operand));
                    insns.add(new InsnNode(FSUB));

                    current -= operand;
                    break;
                case 2:
                    operand = RandomUtils.getRandomInt(1, 65535);

                    insns.add(ASMUtils.getNumberInsn(operand));
                    insns.add(new InsnNode(FMUL));

                    current *= operand;
                    break;
                case 3:
                    operand = RandomUtils.getRandomInt(1, 65535);

                    insns.add(ASMUtils.getNumberInsn(operand));
                    insns.add(new InsnNode(FDIV));

                    current /= operand;
                    break;
                case 4:
                default:
                    operand = RandomUtils.getRandomInt(1, 255);

                    insns.add(ASMUtils.getNumberInsn(operand));
                    insns.add(new InsnNode(FREM));

                    current %= operand;
                    break;
            }
        }

        float correctionOperand = originalNum - current;
        insns.add(ASMUtils.getNumberInsn(correctionOperand));
        insns.add(new InsnNode(FADD));

        return insns;
    }

    private InsnList obfuscateNumber(double originalNum) {
        double current = randomDouble(originalNum);

        InsnList insns = new InsnList();
        insns.add(ASMUtils.getNumberInsn(current));

        for (int i = 0; i < RandomUtils.getRandomInt(2, 6); i++) {
            double operand;

            switch (RandomUtils.getRandomInt(6)) {
                case 0:
                    operand = randomDouble(current);

                    insns.add(ASMUtils.getNumberInsn(operand));
                    insns.add(new InsnNode(DADD));

                    current += operand;
                    break;
                case 1:
                    operand = randomDouble(current);

                    insns.add(ASMUtils.getNumberInsn(operand));
                    insns.add(new InsnNode(DSUB));

                    current -= operand;
                    break;
                case 2:
                    operand = RandomUtils.getRandomInt(1, 65535);

                    insns.add(ASMUtils.getNumberInsn(operand));
                    insns.add(new InsnNode(DMUL));

                    current *= operand;
                    break;
                case 3:
                    operand = RandomUtils.getRandomInt(1, 65535);

                    insns.add(ASMUtils.getNumberInsn(operand));
                    insns.add(new InsnNode(DDIV));

                    current /= operand;
                    break;
                case 4:
                default:
                    operand = RandomUtils.getRandomInt(1, 255);

                    insns.add(ASMUtils.getNumberInsn(operand));
                    insns.add(new InsnNode(DREM));

                    current %= operand;
                    break;
            }
        }

        double correctionOperand = originalNum - current;
        insns.add(ASMUtils.getNumberInsn(correctionOperand));
        insns.add(new InsnNode(DADD));

        return insns;
    }
}
