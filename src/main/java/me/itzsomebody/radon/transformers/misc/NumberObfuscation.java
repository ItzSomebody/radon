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

package me.itzsomebody.radon.transformers.misc;

import java.util.concurrent.atomic.AtomicInteger;
import me.itzsomebody.radon.transformers.AbstractTransformer;
import me.itzsomebody.radon.utils.BytecodeUtils;
import me.itzsomebody.radon.utils.LoggerUtils;
import me.itzsomebody.radon.utils.NumberUtils;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;

/**
 * Transformer that splits up integers into simple arithmetic evaluations.
 *
 * @author ItzSomebody
 * @author VincBreaker (Sorry Vinc, I just had to steal the idea of Smoke's number obfuscation lol)
 */
public class NumberObfuscation extends AbstractTransformer {
    /**
     * Applies obfuscation.}.
     */
    public void obfuscate() {
        AtomicInteger counter = new AtomicInteger();
        long current = System.currentTimeMillis();
        this.logStrings.add(LoggerUtils.stdOut("------------------------------------------------"));
        this.logStrings.add(LoggerUtils.stdOut("Started number obfuscation transformer"));
        this.classNodes().stream().filter(classNode -> !this.exempted(classNode.name, "Numbers")).forEach(classNode -> {
            classNode.methods.stream().filter(methodNode ->
                    !this.exempted(classNode.name + '.' + methodNode.name + methodNode.desc, "Numbers"))
                    .filter(methodNode -> !BytecodeUtils.isAbstractMethod(methodNode.access)).forEach(methodNode -> {
                for (AbstractInsnNode insn : methodNode.instructions.toArray()) {
                    if (methodSize(methodNode) > 60000) break;
                    if (BytecodeUtils.isIntInsn(insn)) {
                        int originalNum = BytecodeUtils.getIntNumber(insn);
                        int value1 = NumberUtils.getRandomInt();
                        int value2 = originalNum ^ value1;

                        InsnList insnList = new InsnList();
                        insnList.add(BytecodeUtils.getNumberInsn(value1));
                        insnList.add(BytecodeUtils.getNumberInsn(value2));
                        insnList.add(new InsnNode(IXOR));

                        methodNode.instructions.insertBefore(insn, insnList);
                        methodNode.instructions.remove(insn);
                        counter.incrementAndGet();
                    } else if (BytecodeUtils.isLongInsn(insn)) {
                        long originalNum = BytecodeUtils.getLongNumber(insn);
                        long value1 = NumberUtils.getRandomLong();
                        long value2 = originalNum ^ value1;

                        InsnList insnList = new InsnList();
                        insnList.add(BytecodeUtils.getNumberInsn(value1));
                        insnList.add(BytecodeUtils.getNumberInsn(value2));
                        insnList.add(new InsnNode(LXOR));

                        methodNode.instructions.insertBefore(insn, insnList);
                        methodNode.instructions.remove(insn);
                        counter.incrementAndGet();
                    }
                }
            });
        });
        this.logStrings.add(LoggerUtils.stdOut("Split " + counter + " numbers into bitwise xor instructions."));
        this.logStrings.add(LoggerUtils.stdOut("Finished. [" + tookThisLong(current) + "ms]"));
    }
}
