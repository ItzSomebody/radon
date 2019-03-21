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

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import me.itzsomebody.radon.Logger;
import me.itzsomebody.radon.asm.ClassWrapper;
import me.itzsomebody.radon.utils.RandomUtils;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
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
    private static final int PRED_ACCESS = ACC_PUBLIC | ACC_STATIC;

    @Override
    public void transform() {
        AtomicInteger counter = new AtomicInteger();

        int fieldInjections = getClassWrappers().size() / 5;
        if (fieldInjections == 0)
            fieldInjections = 1;

        FieldNode[] predicates = new FieldNode[fieldInjections];
        for (int i = 0; i < fieldInjections; i++)
            predicates[i] = new FieldNode(PRED_ACCESS, randomString(), "Z", null, null);

        ClassNode[] predicateClasses = new ClassNode[fieldInjections];
        ArrayList<ClassWrapper> wrappers = new ArrayList<>(getClassWrappers());
        for (int i = 0; i < fieldInjections; i++) {
            predicateClasses[i] = wrappers.get(RandomUtils.getRandomInt(wrappers.size())).classNode;

            if (predicateClasses[i].fields == null)
                predicateClasses[i].fields = new ArrayList<>(1);

            predicateClasses[i].fields.add(predicates[i]);
        }

        getClassWrappers().stream().filter(classWrapper -> !excluded(classWrapper)).forEach(classWrapper ->
                classWrapper.methods.stream().filter(methodWrapper -> !excluded(methodWrapper)
                        && hasInstructions(methodWrapper.methodNode)).forEach(methodWrapper -> {
                    MethodNode methodNode = methodWrapper.methodNode;

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
                        }
                    }

                    int index = RandomUtils.getRandomInt(predicateClasses.length);

                    methodNode.instructions.insertBefore(methodNode.instructions.getFirst(),
                            new VarInsnNode(ISTORE, varIndex));
                    methodNode.instructions.insertBefore(methodNode.instructions.getFirst(),
                            new FieldInsnNode(GETSTATIC, predicateClasses[index].name, predicates[index].name, "Z"));
                }));

        Logger.stdOut("Swapped " + counter.get() + " GOTO instructions");
    }
}
