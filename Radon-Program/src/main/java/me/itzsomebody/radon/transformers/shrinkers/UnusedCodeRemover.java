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

package me.itzsomebody.radon.transformers.shrinkers;

import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import me.itzsomebody.radon.asm.UsedInstructionsFinder;
import me.itzsomebody.radon.utils.LoggerUtils;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;

/**
 * FIXME: COMPLETELY BROKEN
 */
public class UnusedCodeRemover extends Shrinker {
    @Override
    public void transform() {
        AtomicInteger counter = new AtomicInteger();

        getClassWrappers().parallelStream().filter(classWrapper -> !excluded(classWrapper)).forEach(classWrapper -> {
            ClassNode classNode = classWrapper.classNode;

            classNode.methods.parallelStream().filter(this::hasInstructions).forEach(methodNode -> {
                Set<AbstractInsnNode> usedInstructions = new UsedInstructionsFinder(methodNode.instructions).getUsedInstructions();
                methodNode.localVariables.forEach(localVariableNode -> {
                    usedInstructions.add(localVariableNode.start);
                    usedInstructions.add(localVariableNode.end);
                });
                methodNode.tryCatchBlocks.forEach(tryCatchBlockNode -> {
                    usedInstructions.add(tryCatchBlockNode.start);
                    usedInstructions.add(tryCatchBlockNode.end);
                    usedInstructions.add(tryCatchBlockNode.handler);
                });
                int originalSize = methodNode.instructions.size();

                for (AbstractInsnNode insn : methodNode.instructions.toArray()) {
                    if (!usedInstructions.contains(insn)) {
                        methodNode.instructions.remove(insn);
                    }
                }

                counter.getAndAdd(originalSize - methodNode.instructions.size());
            });
        });

        LoggerUtils.stdOut(String.format("Removed %d unused instructions.", counter.get()));
    }

    @Override
    public String getName() {
        return "Unused Code Remover";
    }
}
