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
import me.itzsomebody.radon.utils.RandomUtils;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TryCatchBlockNode;

/**
 * Replaces IFNONNULL and IFNULL with a semantically equivalent try-catch block. This relies on the fact that
 * {@link NullPointerException} is thrown when a method is invoked upon null.
 * FIXME: broken
 *
 * @author ItzSomebody
 */
public class NullCheckMutilator extends FlowObfuscation {
    @Override
    public void transform() {
        AtomicInteger counter = new AtomicInteger();

        getClassWrappers().stream().filter(classWrapper -> !excluded(classWrapper)).forEach(classWrapper ->
                classWrapper.getMethods().stream().filter(methodWrapper -> !excluded(methodWrapper)
                        && hasInstructions(methodWrapper.getMethodNode())).forEach(methodWrapper -> {
                    MethodNode methodNode = methodWrapper.getMethodNode();

                    int leeway = getSizeLeeway(methodNode);

                    for (AbstractInsnNode insn : methodNode.instructions.toArray()) {
                        if (leeway < 10000)
                            break;

                        if (insn.getOpcode() == IFNULL || insn.getOpcode() == IFNONNULL) {
                            JumpInsnNode jump = (JumpInsnNode) insn;

                            LabelNode trapStart = new LabelNode();
                            LabelNode trapEnd = new LabelNode();
                            LabelNode catchStart = new LabelNode();
                            LabelNode catchEnd = new LabelNode();

                            InsnList insns = new InsnList();
                            insns.add(trapStart);
                            switch (RandomUtils.getRandomInt(4)) {
                                case 0:
                                    insns.add(new MethodInsnNode(INVOKEVIRTUAL, "java/lang/Object", "getClass", "()Ljava/lang/Class;", false));
                                    break;
                                case 1:
                                    insns.add(new MethodInsnNode(INVOKEVIRTUAL, "java/lang/Object", "hashCode", "()I", false));
                                    break;
                                case 2:
                                    insns.add(new InsnNode(ACONST_NULL));
                                    insns.add(new MethodInsnNode(INVOKEVIRTUAL, "java/lang/Object", "equals", "(Ljava/lang/Object;)Z", false));
                                    break;
                                case 3:
                                default:
                                    insns.add(new MethodInsnNode(INVOKEVIRTUAL, "java/lang/Object", "toString", "()Ljava/lang/String;", false));
                                    break;
                            }

                            insns.add(new InsnNode(POP));
                            insns.add(trapEnd);

                            if (insn.getOpcode() == IFNONNULL) {
                                insns.add(new JumpInsnNode(GOTO, jump.label));
                                insns.add(catchStart);
                                insns.add(new InsnNode(POP));
                                insns.add(catchEnd);
                            } else {
                                insns.add(new JumpInsnNode(GOTO, catchEnd));
                                insns.add(catchStart);
                                insns.add(new InsnNode(POP));
                                insns.add(new JumpInsnNode(GOTO, jump.label));
                                insns.add(catchEnd);
                            }

                            methodNode.instructions.insert(insn, insns);
                            methodNode.instructions.remove(insn);
                            methodNode.tryCatchBlocks.add(0, new TryCatchBlockNode(trapStart, trapEnd, catchStart, "java/lang/NullPointerException"));

                            counter.incrementAndGet();
                        }
                    }
                }));

        Main.info("Mutilated " + counter.get() + " null checks");
    }
}
