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

import java.util.HashSet;
import java.util.concurrent.atomic.AtomicInteger;
import me.itzsomebody.radon.Main;
import me.itzsomebody.radon.utils.ASMUtils;
import me.itzsomebody.radon.utils.RandomUtils;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

/**
 * Combines the handler region of a try block into its trap region. The idea of this is from
 * https://github.com/Janmm14/decompiler-vulnerabilities-and-bugs/blob/master/DVB/DVB-0004.md.
 * <p>
 * To achieve this, we first change the handler start to the trap start then we insert a condition at the start of the
 * trap which indicates if execution should move into the trap or catch region.
 * <p>
 * FIXME: really broken
 *
 * @author ItzSombody
 */
public class TryCatchCombiner extends FlowObfuscation {
    @Override
    public void transform() {
        AtomicInteger counter = new AtomicInteger();

        getClassWrappers().stream().filter(classWrapper -> !excluded(classWrapper)).forEach(classWrapper ->
                classWrapper.getMethods().stream().filter(methodWrapper -> !excluded(methodWrapper)).forEach(methodWrapper -> {
                    MethodNode methodNode = methodWrapper.getMethodNode();

                    HashSet<LabelNode> starts = new HashSet<>();
                    if (methodNode.tryCatchBlocks.stream().anyMatch(tcbn -> !starts.add(tcbn.start)))
                        return;

                    int index = methodNode.maxLocals++;

                    methodNode.tryCatchBlocks.stream().filter(tcbn -> tcbn.start != tcbn.handler).forEach(tcbn -> {
                        LabelNode handler = tcbn.handler;
                        LabelNode init = new LabelNode();
                        LabelNode back = new LabelNode();

                        tcbn.handler = tcbn.start;

                        InsnList preTrap = new InsnList();
                        preTrap.add(new InsnNode(ICONST_0));
                        preTrap.add(new VarInsnNode(ISTORE, index));
                        preTrap.add(new JumpInsnNode(GOTO, init));
                        preTrap.add(back);

                        InsnList initSub = new InsnList();
                        initSub.add(init);
                        initSub.add(new InsnNode(ACONST_NULL));
                        initSub.add(new JumpInsnNode(GOTO, back));

                        InsnList startCondition = new InsnList();
                        startCondition.add(new VarInsnNode(ILOAD, index));
                        startCondition.add(new JumpInsnNode(IFNE, handler));
                        startCondition.add(new InsnNode(POP));
                        startCondition.add(ASMUtils.getNumberInsn(RandomUtils.getRandomInt(1, 20)));
                        startCondition.add(new VarInsnNode(ISTORE, index));

                        InsnList resetCondition = new InsnList();
                        resetCondition.add(new InsnNode(ICONST_0));
                        resetCondition.add(new VarInsnNode(ISTORE, index));

                        methodNode.instructions.insert(methodNode.instructions.getLast(), initSub);
                        methodNode.instructions.insert(tcbn.start, startCondition);
                        methodNode.instructions.insertBefore(tcbn.start, preTrap);
                        methodNode.instructions.insertBefore(tcbn.end, resetCondition);

                        counter.incrementAndGet();
                    });
                }));

        Main.info("Combined " + counter.incrementAndGet() + " try blocks with their catches.");
    }
}
