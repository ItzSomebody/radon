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
import me.itzsomebody.radon.utils.LoggerUtils;
import me.itzsomebody.radon.utils.RandomUtils;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodNode;

public class LightNumberObfuscation extends NumberObfuscation {
    @Override
    public void transform() {
        AtomicInteger counter = new AtomicInteger();
        this.getClassWrappers().parallelStream().filter(classWrapper -> !excluded(classWrapper)).forEach(classWrapper ->
            classWrapper.methods.parallelStream().filter(methodWrapper -> !excluded(methodWrapper) && hasInstructions(methodWrapper.methodNode)).forEach(methodWrapper -> {
                MethodNode methodNode = methodWrapper.methodNode;
                int leeway = getSizeLeeway(methodNode);

                for (AbstractInsnNode insn : methodNode.instructions.toArray()) {
                    if (leeway < 10000)
                        break;
                    if (BytecodeUtils.isIntInsn(insn)) {
                        int originalNum = BytecodeUtils.getIntNumber(insn);
                        int value1 = RandomUtils.getRandomInt();
                        int value2 = originalNum ^ value1;

                        InsnList insnList = new InsnList();
                        insnList.add(BytecodeUtils.getNumberInsn(value1));
                        insnList.add(BytecodeUtils.getNumberInsn(RandomUtils.getRandomInt()));
                        insnList.add(new InsnNode(SWAP));
                        insnList.add(new InsnNode(DUP_X1));
                        insnList.add(new InsnNode(POP2));
                        insnList.add(BytecodeUtils.getNumberInsn(value2));
                        insnList.add(new InsnNode(IXOR));

                        methodNode.instructions.insertBefore(insn, insnList);
                        methodNode.instructions.remove(insn);
                        leeway -= 10;
                        counter.incrementAndGet();
                    } else if (BytecodeUtils.isLongInsn(insn)) {
                        long originalNum = BytecodeUtils.getLongNumber(insn);
                        long value1 = RandomUtils.getRandomLong();
                        long value2 = originalNum ^ value1;

                        InsnList insnList = new InsnList();
                        insnList.add(BytecodeUtils.getNumberInsn(RandomUtils.getRandomLong()));
                        insnList.add(BytecodeUtils.getNumberInsn(value1));
                        insnList.add(new InsnNode(DUP2_X2));
                        insnList.add(new InsnNode(POP2));
                        insnList.add(new InsnNode(POP2));
                        insnList.add(BytecodeUtils.getNumberInsn(value2));
                        insnList.add(new InsnNode(LXOR));

                        methodNode.instructions.insertBefore(insn, insnList);
                        methodNode.instructions.remove(insn);
                        leeway -= 15;
                        counter.incrementAndGet();
                    }
                }
            })
        );
        LoggerUtils.stdOut(String.format("Split %d numbers into bitwise xor instructions.", counter.get()));
    }

    @Override
    public String getName() {
        return "Light number obfuscation";
    }
}
