package me.itzsomebody.radon.transformers.flow;

import java.util.Stack;
import java.util.concurrent.atomic.AtomicInteger;
import me.itzsomebody.radon.analyzer.StackAnalyzer;
import me.itzsomebody.radon.transformers.AbstractTransformer;
import me.itzsomebody.radon.utils.BytecodeUtils;
import me.itzsomebody.radon.utils.LoggerUtils;
import me.itzsomebody.radon.utils.StringUtils;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

/**
 * Transformer that applies multiple (skidded) flow obfuscations.
 *
 * @author ItzSomebody
 */
public class NormalFlowObfuscation extends AbstractTransformer {
    /**
     * Applies obfuscation.
     */
    public void obfuscate() {
        AtomicInteger counter = new AtomicInteger();
        long current = System.currentTimeMillis();
        this.logStrings.add(LoggerUtils.stdOut("------------------------------------------------"));
        this.logStrings.add(LoggerUtils.stdOut("Started normal flow obfuscation transformer"));
        classNodes().stream().filter(classNode -> !this.exempted(classNode.name, "Flow")).forEach(classNode -> {
            FieldNode field = new FieldNode(ACC_PUBLIC + ACC_STATIC +
                    ACC_FINAL, StringUtils.randomString(this.dictionary), "Z", null, null);
            classNode.fields.add(field);
            classNode.methods.stream().filter(methodNode ->
                    !this.exempted(classNode.name + '.' + methodNode.name + methodNode.desc, "Flow")
                            && !BytecodeUtils.isAbstractMethod(methodNode.access)).forEach(methodNode -> {
                int varIndex = methodNode.maxLocals;
                methodNode.maxLocals++;
                methodNode.owner = classNode.name;
                AbstractInsnNode[] untouchedList = methodNode.instructions.toArray();
                LabelNode labelNode = exitLabel(methodNode);
                boolean calledSuper = false;
                for (AbstractInsnNode insn : untouchedList) {
                    if (this.methodSize(methodNode) > 60000) break;
                    if (methodNode.name.equals("<init>")) {
                        if (insn instanceof MethodInsnNode) {
                            if (insn.getOpcode() == INVOKESPECIAL
                                    && insn.getPrevious() instanceof VarInsnNode
                                    && ((VarInsnNode) insn.getPrevious()).var == 0) {
                                calledSuper = true;
                            }
                        }
                    }
                    if (insn != methodNode.instructions.getFirst()
                        //&& !(insn instanceof LineNumberNode)
                        //&& insn.getPrevious() != null
                        //&& insn.getPrevious().getOpcode() != ASTORE
                        /*&& insn.getOpcode() != ASTORE*/) {
                        if (methodNode.name.equals("<init>") && !calledSuper)
                            continue;
                        StackAnalyzer sa = new StackAnalyzer(methodNode, insn);
                        Stack<Object> stack = sa.returnStackAtBreak();
                        if (stack.isEmpty()) { // We need to make sure stack is empty before making jumps
                            methodNode.instructions.insertBefore(insn, new VarInsnNode(ILOAD, varIndex));
                            methodNode.instructions.insertBefore(insn,
                                    new JumpInsnNode(IFNE, labelNode));
                            counter.incrementAndGet();
                        }
                    }
                    if (insn instanceof JumpInsnNode) {
                        if (insn.getOpcode() == GOTO) {
                            methodNode.instructions.insertBefore(insn,
                                    new VarInsnNode(ILOAD, varIndex));
                            methodNode.instructions.insertBefore(insn,
                                    new InsnNode(ICONST_0));
                            methodNode.instructions.insert(insn,
                                    new InsnNode(ATHROW));
                            methodNode.instructions.insert(insn,
                                    new InsnNode(ACONST_NULL));
                            methodNode.instructions.set(insn,
                                    new JumpInsnNode(IF_ICMPEQ,
                                            ((JumpInsnNode) insn).label));
                            counter.incrementAndGet();
                        }
                    }
                }
                methodNode.instructions.insertBefore(methodNode.instructions
                        .getFirst(), new VarInsnNode(ISTORE, varIndex));
                methodNode.instructions.insertBefore(methodNode.instructions
                        .getFirst(), new FieldInsnNode(GETSTATIC,
                        classNode.name, field.name, "Z"));
            });
        });
        this.logStrings.add(LoggerUtils.stdOut("Added " + counter + " instruction sets."));
        this.logStrings.add(LoggerUtils.stdOut("Finished. [" + tookThisLong(current) + "ms]"));
    }

    /**
     * Inserts an "exit" label into the start of the method node which is used
     * by the transformer to branch the stack into a false jump.
     *
     * @param methodNode current {@link MethodNode} to insert an exit code
     *                   block into.
     * @return a {@link LabelNode} used to branch the stack with a false
     * conditional.
     */
    private LabelNode exitLabel(MethodNode methodNode) {
        LabelNode lb = new LabelNode();
        LabelNode escapeNode = new LabelNode();
        AbstractInsnNode target = methodNode.instructions.getFirst();
        methodNode.instructions.insertBefore(target, new JumpInsnNode(GOTO, escapeNode));
        methodNode.instructions.insertBefore(target, lb);
        if (methodNode.desc.endsWith(")V")) {
            methodNode.instructions.insertBefore(target, new InsnNode(RETURN));
        } else if (methodNode.desc.endsWith(")B")
                || methodNode.desc.endsWith(")S")
                || methodNode.desc.endsWith(")I")
                || methodNode.desc.endsWith(")Z")
                || methodNode.desc.endsWith(")C")) {
            methodNode.instructions.insertBefore(target, new InsnNode(ICONST_0));
            methodNode.instructions.insertBefore(target, new InsnNode(IRETURN));
        } else if (methodNode.desc.endsWith(")J")) {
            methodNode.instructions.insertBefore(target, new InsnNode(LCONST_0));
            methodNode.instructions.insertBefore(target, new InsnNode(LRETURN));
        } else if (methodNode.desc.endsWith(")F")) {
            methodNode.instructions.insertBefore(target, new InsnNode(FCONST_0));
            methodNode.instructions.insertBefore(target, new InsnNode(FRETURN));
        } else if (methodNode.desc.endsWith(")D")) {
            methodNode.instructions.insertBefore(target, new InsnNode(DCONST_0));
            methodNode.instructions.insertBefore(target, new InsnNode(DRETURN));
        } else {
            methodNode.instructions.insertBefore(target, new InsnNode(ACONST_NULL));
            methodNode.instructions.insertBefore(target, new InsnNode(ARETURN));
        }
        methodNode.instructions.insert(target, escapeNode);

        return lb;
    }
}
