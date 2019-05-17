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

package me.itzsomebody.radon.transformers.obfuscators.flow;

import java.util.concurrent.atomic.AtomicInteger;
import me.itzsomebody.radon.Main;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

/**
 * Replaces GOTO instructions with an expression which is always true. This does nothing more than adding
 * a one more edge to a control flow graph for every GOTO instruction present.
 *
 * @author ItzSomebody
 */
public class GotoReplacer extends FlowObfuscation {
    private static final int PRED_ACCESS = ACC_PUBLIC | ACC_STATIC | ACC_FINAL;

    @Override
    public void transform() {
        AtomicInteger counter = new AtomicInteger();

        getClassWrappers().stream().filter(classWrapper -> !excluded(classWrapper)).forEach(classWrapper -> {
            FieldNode predicate = new FieldNode(PRED_ACCESS, uniqueRandomString(), "Z", null, null);

            classWrapper.getMethods().stream().filter(methodWrapper -> !excluded(methodWrapper)
                    && hasInstructions(methodWrapper.getMethodNode())).forEach(methodWrapper -> {
                MethodNode methodNode = methodWrapper.getMethodNode();

                int leeway = getSizeLeeway(methodNode);
                int varIndex = methodNode.maxLocals;
                methodNode.maxLocals++; // Prevents breaking of other transformers which rely on this field.

                for (AbstractInsnNode insn : methodNode.instructions.toArray()) {
                    if (leeway < 10000)
                        break;

                    if (insn.getOpcode() == GOTO) {
                        methodNode.instructions.insertBefore(insn, new VarInsnNode(ILOAD, varIndex));
                        methodNode.instructions.insertBefore(insn, new JumpInsnNode(IFEQ, ((JumpInsnNode) insn).label));
                        methodNode.instructions.insert(insn, new InsnNode(ATHROW));
                        methodNode.instructions.insert(insn, new InsnNode(ACONST_NULL));
                        methodNode.instructions.remove(insn);

                        leeway -= 10;

                        counter.incrementAndGet();
                    }
                }

                methodNode.instructions.insertBefore(methodNode.instructions.getFirst(),
                        new VarInsnNode(ISTORE, varIndex));
                methodNode.instructions.insertBefore(methodNode.instructions.getFirst(),
                        new FieldInsnNode(GETSTATIC, classWrapper.getName(), predicate.name, "Z"));
            });

            classWrapper.getClassNode().fields.add(predicate);
        });

        Main.info("Swapped " + counter.get() + " GOTO instructions");
    }
}
