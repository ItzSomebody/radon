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
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import me.itzsomebody.radon.Logger;
import me.itzsomebody.radon.asm.ClassWrapper;
import me.itzsomebody.radon.asm.StackHeightZeroFinder;
import me.itzsomebody.radon.exceptions.RadonException;
import me.itzsomebody.radon.exceptions.StackEmulationException;
import me.itzsomebody.radon.utils.BytecodeUtils;
import me.itzsomebody.radon.utils.RandomUtils;
import org.objectweb.asm.Type;
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
 * Inserts opaque predicates which always evaluate to false but are meant to insert significantly more
 * edges to a control flow graph. To determine where we should insert the conditions, we use an analyzer
 * to determine where the stack is empty. This leads to less complication when applying obfuscation.
 *
 * @author ItzSomebody
 */
public class BogusJumpInserter extends FlowObfuscation {
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

                    AbstractInsnNode[] untouchedList = methodNode.instructions.toArray();
                    LabelNode labelNode = exitLabel(methodNode);
                    boolean calledSuper = false;

                    StackHeightZeroFinder stackHeightZeroFinder = new StackHeightZeroFinder(methodNode, methodNode.instructions.getLast());
                    try {
                        stackHeightZeroFinder.execute(false);
                    } catch (StackEmulationException e) {
                        e.printStackTrace();
                        throw new RadonException(String.format("Error happened while trying to emulate the stack of %s.%s%s",
                                classWrapper.classNode.name, methodNode.name, methodNode.desc));
                    }

                    Set<AbstractInsnNode> emptyAt = stackHeightZeroFinder.getEmptyAt();
                    for (AbstractInsnNode insn : untouchedList) {
                        if (leeway < 10000)
                            break;

                        // Bad way of detecting if this class was instantiated
                        if ("<init>".equals(methodNode.name))
                            calledSuper = (insn instanceof MethodInsnNode && insn.getOpcode() == INVOKESPECIAL
                                    && insn.getPrevious() instanceof VarInsnNode && ((VarInsnNode) insn.getPrevious()).var == 0);
                        if (insn != methodNode.instructions.getFirst() && !(insn instanceof LineNumberNode)) {
                            if ("<init>".equals(methodNode.name) && !calledSuper)
                                continue;
                            if (emptyAt.contains(insn)) { // We need to make sure stack is empty before making jumps
                                methodNode.instructions.insertBefore(insn, new VarInsnNode(ILOAD, varIndex));
                                methodNode.instructions.insertBefore(insn, new JumpInsnNode(IFNE, labelNode));
                                leeway -= 4;
                                counter.incrementAndGet();
                            }
                        }
                    }

                    int index = RandomUtils.getRandomInt(predicateClasses.length);

                    methodNode.instructions.insertBefore(methodNode.instructions.getFirst(),
                            new VarInsnNode(ISTORE, varIndex));
                    methodNode.instructions.insertBefore(methodNode.instructions.getFirst(),
                            new FieldInsnNode(GETSTATIC, predicateClasses[index].name, predicates[index].name, "Z"));
                }));

        Logger.stdOut("Inserted " + counter.get() + " bogus jumps");
    }

    /**
     * Generates a generic "escape" pattern to avoid inserting multiple copies of the same bytecode instructions.
     *
     * @param methodNode the {@link MethodNode} we are inserting into.
     * @return a {@link LabelNode} which "escapes" all other flow.
     */
    private static LabelNode exitLabel(MethodNode methodNode) {
        LabelNode lb = new LabelNode();
        LabelNode escapeNode = new LabelNode();
        AbstractInsnNode target = methodNode.instructions.getFirst();
        methodNode.instructions.insertBefore(target, new JumpInsnNode(GOTO, escapeNode));
        methodNode.instructions.insertBefore(target, lb);
        Type returnType = Type.getReturnType(methodNode.desc);
        switch (returnType.getSort()) {
            case Type.VOID:
                methodNode.instructions.insertBefore(target, new InsnNode(RETURN));
                break;
            case Type.BOOLEAN:
                methodNode.instructions.insertBefore(target, BytecodeUtils.getNumberInsn(RandomUtils.getRandomInt(2)));
                methodNode.instructions.insertBefore(target, new InsnNode(IRETURN));
                break;
            case Type.CHAR:
                methodNode.instructions.insertBefore(target, BytecodeUtils.getNumberInsn(RandomUtils
                        .getRandomInt(Character.MAX_VALUE + 1)));
                methodNode.instructions.insertBefore(target, new InsnNode(IRETURN));
                break;
            case Type.BYTE:
                methodNode.instructions.insertBefore(target, BytecodeUtils.getNumberInsn(RandomUtils
                        .getRandomInt(Byte.MAX_VALUE + 1)));
                methodNode.instructions.insertBefore(target, new InsnNode(IRETURN));
                break;
            case Type.SHORT:
                methodNode.instructions.insertBefore(target, BytecodeUtils.getNumberInsn(RandomUtils
                        .getRandomInt(Short.MAX_VALUE + 1)));
                methodNode.instructions.insertBefore(target, new InsnNode(IRETURN));
                break;
            case Type.INT:
                methodNode.instructions.insertBefore(target, BytecodeUtils.getNumberInsn(RandomUtils.getRandomInt()));
                methodNode.instructions.insertBefore(target, new InsnNode(IRETURN));
                break;
            case Type.LONG:
                methodNode.instructions.insertBefore(target, BytecodeUtils.getNumberInsn(RandomUtils.getRandomLong()));
                methodNode.instructions.insertBefore(target, new InsnNode(LRETURN));
                break;
            case Type.FLOAT:
                methodNode.instructions.insertBefore(target, BytecodeUtils.getNumberInsn(RandomUtils.getRandomFloat()));
                methodNode.instructions.insertBefore(target, new InsnNode(FRETURN));
                break;
            case Type.DOUBLE:
                methodNode.instructions.insertBefore(target, BytecodeUtils.getNumberInsn(RandomUtils.getRandomDouble()));
                methodNode.instructions.insertBefore(target, new InsnNode(DRETURN));
                break;
            default:
                methodNode.instructions.insertBefore(target, new InsnNode(ACONST_NULL));
                methodNode.instructions.insertBefore(target, new InsnNode(ARETURN));
                break;
        }
        methodNode.instructions.insertBefore(target, escapeNode);

        return lb;
    }
}
