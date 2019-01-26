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

import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import me.itzsomebody.radon.asm.StackEmulator;
import me.itzsomebody.radon.utils.LoggerUtils;
import me.itzsomebody.radon.utils.RandomUtils;
import me.itzsomebody.radon.utils.StringUtils;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LineNumberNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

/**
 * Same as{@link NormalFlowObfuscation}, but also inserts a jump which never is made before all conditionals.
 *
 * @author ItzSomebody
 */
public class HeavyFlowObfuscation extends NormalFlowObfuscation {
    @Override
    public void transform() {
        AtomicInteger counter = new AtomicInteger();

        this.getClassWrappers().parallelStream().filter(classWrapper ->
                !excluded(classWrapper)).forEach(classWrapper -> {
            ClassNode classNode = classWrapper.classNode;
            FieldNode field = new FieldNode(ACC_PUBLIC + ACC_STATIC + ACC_FINAL,
                    StringUtils.randomSpacesString(RandomUtils.getRandomInt(10)), "Z", null, null);

            classNode.fields.add(field);
            classWrapper.methods.parallelStream().filter(methodWrapper -> !excluded(methodWrapper)
                    && hasInstructions(methodWrapper.methodNode)).forEach(methodWrapper -> {
                MethodNode methodNode = methodWrapper.methodNode;
                int leeway = getSizeLeeway(methodNode);
                int varIndex = methodNode.maxLocals;
                methodNode.maxLocals++;
                AbstractInsnNode[] untouchedList = methodNode.instructions.toArray();
                LabelNode labelNode = exitLabel(methodNode);
                boolean calledSuper = false;
                Set<AbstractInsnNode> emptyAt = new StackEmulator(methodNode,
                        methodNode.instructions.getLast()).getEmptyAt();
                for (AbstractInsnNode insn : untouchedList) {
                    if (leeway < 10000) {
                        break;
                    }
                    if ("<init>".equals(methodNode.name)) {
                        calledSuper = (insn instanceof MethodInsnNode && insn.getOpcode() == INVOKESPECIAL
                                && insn.getPrevious() instanceof VarInsnNode && ((VarInsnNode) insn.getPrevious()).var == 0);
                    }
                    if (insn != methodNode.instructions.getFirst() && !(insn instanceof LineNumberNode)) {
                        if ("<init>".equals(methodNode.name) && !calledSuper)
                            continue;
                        if (emptyAt.contains(insn)) { // We need to make sure stack is empty before making jumps
                            methodNode.instructions.insertBefore(insn, new VarInsnNode(ILOAD, varIndex));
                            methodNode.instructions.insertBefore(insn, new JumpInsnNode(IFNE, labelNode));
                            leeway -= 5;
                            counter.incrementAndGet();
                        }
                    }
                    if (insn.getOpcode() == GOTO) {
                        methodNode.instructions.insertBefore(insn, new VarInsnNode(ILOAD, varIndex));
                        methodNode.instructions.insert(insn, new InsnNode(ATHROW));
                        methodNode.instructions.insert(insn, new InsnNode(ACONST_NULL));
                        methodNode.instructions.set(insn, new JumpInsnNode(IFEQ, ((JumpInsnNode) insn).label));
                        leeway -= 7;
                        counter.incrementAndGet();
                    } else if (insn.getOpcode() >= IFEQ && insn.getOpcode() <= IF_ICMPLE) {
                        methodNode.instructions.insert(insn, new JumpInsnNode(IFNE, ((JumpInsnNode) insn).label));
                        methodNode.instructions.insert(insn, new VarInsnNode(ILOAD, varIndex));
                        leeway -= 7;
                        counter.incrementAndGet();
                    }
                }
                methodNode.instructions.insertBefore(methodNode.instructions.getFirst(), new VarInsnNode(ISTORE,
                        varIndex));
                methodNode.instructions.insertBefore(methodNode.instructions.getFirst(), new FieldInsnNode(GETSTATIC,
                        classNode.name, field.name, "Z"));
            });
        });

        LoggerUtils.stdOut(String.format("Added %d fake jump sequences", counter.get()));
    }

    @Override
    public String getName() {
        return "Heavy flow obfuscation";
    }
}
