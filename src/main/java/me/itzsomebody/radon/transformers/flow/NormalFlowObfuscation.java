package me.itzsomebody.radon.transformers.flow;

import java.lang.reflect.Modifier;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicInteger;
import me.itzsomebody.radon.analyzer.StackAnalyzer;
import me.itzsomebody.radon.transformers.AbstractTransformer;
import me.itzsomebody.radon.utils.LoggerUtils;
import me.itzsomebody.radon.utils.StringUtils;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
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
 * Transformer that does the same as {@link LightFlowObfuscation}, but also
 * inserts conditionals which always evaluate to false where the stack is
 * empty.
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
        classNodes().parallelStream().filter(classNode -> !this.exempted(classNode.name, "Flow")).forEach(classNode -> {
            FieldNode field = new FieldNode(ACC_PUBLIC + ACC_STATIC +
                    ACC_FINAL, StringUtils.randomString(this.dictionary), "Z", null, null);
            classNode.fields.add(field);
            classNode.methods.parallelStream().filter(methodNode ->
                    !this.exempted(classNode.name + '.' + methodNode.name + methodNode.desc, "Flow")
                            && !Modifier.isAbstract(methodNode.access)).forEach(methodNode -> {
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
                            && !(insn instanceof LineNumberNode)) {
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
        Type returnType = Type.getReturnType(methodNode.desc);
        switch (returnType.getSort()) {
            case Type.VOID:
                methodNode.instructions.insertBefore(target, new InsnNode(RETURN));
                break;
            case Type.BOOLEAN:
            case Type.CHAR:
            case Type.BYTE:
            case Type.SHORT:
            case Type.INT:
                methodNode.instructions.insertBefore(target, new InsnNode(ICONST_0));
                methodNode.instructions.insertBefore(target, new InsnNode(IRETURN));
                break;
            case Type.LONG:
                methodNode.instructions.insertBefore(target, new InsnNode(LCONST_0));
                methodNode.instructions.insertBefore(target, new InsnNode(LRETURN));
                break;
            case Type.FLOAT:
                methodNode.instructions.insertBefore(target, new InsnNode(FCONST_0));
                methodNode.instructions.insertBefore(target, new InsnNode(FRETURN));
                break;
            case Type.DOUBLE:
                methodNode.instructions.insertBefore(target, new InsnNode(DCONST_0));
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
