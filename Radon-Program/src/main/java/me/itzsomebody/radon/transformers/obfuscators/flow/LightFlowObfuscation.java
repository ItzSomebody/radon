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

package me.itzsomebody.radon.transformers.obfuscators.flow;

import java.util.concurrent.atomic.AtomicInteger;
import me.itzsomebody.radon.utils.LoggerUtils;
import me.itzsomebody.radon.utils.RandomUtils;
import me.itzsomebody.radon.utils.StringUtils;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.MethodNode;

/**
 * This replaces all gotos with the equivalent: if (false) throw null;.
 *
 * @author ItzSomebody
 */
public class LightFlowObfuscation extends FlowObfuscation {
    @Override
    public void transform() {
        AtomicInteger counter = new AtomicInteger();

        this.getClassWrappers().parallelStream().filter(classWrapper ->
                !excluded(classWrapper)).forEach(classWrapper -> {
            ClassNode classNode = classWrapper.classNode;
            String fieldName = StringUtils.randomSpacesString(RandomUtils.getRandomInt(10));

            classWrapper.methods.parallelStream().filter(methodWrapper -> !excluded(methodWrapper)
                    && hasInstructions(methodWrapper.methodNode)).forEach(methodWrapper -> {
                MethodNode methodNode = methodWrapper.methodNode;
                int leeway = getSizeLeeway(methodNode);

                for (AbstractInsnNode insn : methodNode.instructions.toArray()) {
                    if (leeway < 10000) {
                        break;
                    }

                    if (insn.getOpcode() == GOTO) {
                        methodNode.instructions.insertBefore(insn, new FieldInsnNode(GETSTATIC, classNode.name,
                                fieldName, "Z"));
                        methodNode.instructions.insert(insn, new InsnNode(ATHROW));
                        methodNode.instructions.insert(insn, new InsnNode(ACONST_NULL));
                        methodNode.instructions.set(insn, new JumpInsnNode(IFEQ, ((JumpInsnNode) insn).label));
                        leeway -= 7;
                        counter.incrementAndGet();
                    }
                }
            });

            classNode.fields.add(new FieldNode(ACC_PUBLIC + ACC_STATIC + ACC_FINAL, fieldName, "Z", null, null));
        });

        LoggerUtils.stdOut(String.format("Added %d fake throw-null sequences", counter.get()));
    }

    @Override
    public String getName() {
        return "Light flow obfuscation";
    }
}
