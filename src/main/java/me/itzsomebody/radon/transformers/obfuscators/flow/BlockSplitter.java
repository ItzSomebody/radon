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
import me.itzsomebody.radon.Main;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodNode;

/**
 * This splits a method's block of code into two blocks: P1 and P2 and then inserting P2 behind P1.
 * <p>
 * P1->P2 becomes GOTO_P1->P2->P1->GOTO_P2
 * <p>
 *
 * @author ItzSomebody
 */
public class BlockSplitter extends FlowObfuscation {
    @Override
    public void transform() {
        AtomicInteger counter = new AtomicInteger();

        getClassWrappers().stream().filter(cw -> !excluded(cw)).forEach(cw ->
                cw.getMethods().stream().filter(mw -> !excluded(mw)).forEach(mw -> {
                    MethodNode methodNode = mw.getMethodNode();

                    if (methodNode.instructions.size() > 10) {
                        LabelNode p1 = new LabelNode();
                        LabelNode p2 = new LabelNode();

                        AbstractInsnNode p2Start = methodNode.instructions.get((methodNode.instructions.size() - 1) / 2);
                        AbstractInsnNode p2End = methodNode.instructions.getLast();

                        AbstractInsnNode p1Start = methodNode.instructions.getFirst();

                        // We can't have trap ranges mutilated by block splitting
                        if (methodNode.tryCatchBlocks.stream().anyMatch(tcbn ->
                                methodNode.instructions.indexOf(tcbn.end) >= methodNode.instructions.indexOf(p2Start)
                                        && methodNode.instructions.indexOf(tcbn.start) <= methodNode.instructions.indexOf(p2Start)))
                            return;

                        ArrayList<AbstractInsnNode> insnNodes = new ArrayList<>();
                        AbstractInsnNode currentInsn = p1Start;

                        InsnList p1Block = new InsnList();

                        while (currentInsn != p2Start) {
                            insnNodes.add(currentInsn);

                            currentInsn = currentInsn.getNext();
                        }

                        insnNodes.forEach(insn -> {
                            methodNode.instructions.remove(insn);
                            p1Block.add(insn);
                        });

                        p1Block.insert(p1);
                        p1Block.add(new JumpInsnNode(GOTO, p2));

                        methodNode.instructions.insert(p2End, p1Block);
                        methodNode.instructions.insertBefore(p2Start, new JumpInsnNode(GOTO, p1));
                        methodNode.instructions.insertBefore(p2Start, p2);

                        counter.incrementAndGet();

                        // We might have messed up variable ranges when rearranging the block order.
                        if (methodNode.localVariables != null)
                            new ArrayList<>(methodNode.localVariables).stream().filter(lvn ->
                                    methodNode.instructions.indexOf(lvn.end) < methodNode.instructions.indexOf(lvn.start)
                            ).forEach(methodNode.localVariables::remove);
                    }
                }));

        Main.info("Rearranged " + counter.get() + " blocks");
    }
}
