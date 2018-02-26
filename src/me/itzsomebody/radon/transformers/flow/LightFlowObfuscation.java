package me.itzsomebody.radon.transformers.flow;

import me.itzsomebody.radon.utils.StringUtils;
import org.objectweb.asm.tree.*;
import me.itzsomebody.radon.transformers.AbstractTransformer;
import me.itzsomebody.radon.utils.BytecodeUtils;
import me.itzsomebody.radon.utils.LoggerUtils;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Transformer that sets GOTO->LABEL instructions as a condition which is always true.
 * 
 * getstatic injectedBool (true)
 * iconst_1
 * if_icmpeq LABEL
 * aconst_null
 * athrow
 *
 * @author ItzSomebody
 */
public class LightFlowObfuscation extends AbstractTransformer {
    /**
     * Applies obfuscation.
     */
    public void obfuscate() {
        logStrings.add(LoggerUtils.stdOut("------------------------------------------------"));
        logStrings.add(LoggerUtils.stdOut("Starting light flow obfuscation transformer."));
        AtomicInteger counter = new AtomicInteger();
        long current = System.currentTimeMillis();
        classNodes().stream().filter(classNode -> !classExempted(classNode.name)).forEach(classNode -> {
            String fieldName = StringUtils.crazyString();
            classNode.methods.stream().filter(methodNode -> !BytecodeUtils.isAbstractMethod(methodNode.access)
                    && !methodExempted(classNode.name + '.' + methodNode.name + methodNode.desc)
                    && containsJumpNodes(methodNode)
                    && methodNode.localVariables != null && methodNode.localVariables.size() >= 1).forEach(methodNode -> {
                for (AbstractInsnNode ain : methodNode.instructions.toArray()) {
                    if (methodSize(methodNode) > 60000) break;
                    if (ain.getOpcode() == GOTO) {
                        methodNode.instructions.insertBefore(ain, new FieldInsnNode(GETSTATIC, classNode.name, fieldName, "Z"));
                        methodNode.instructions.insertBefore(ain, new InsnNode(ICONST_1));
                        methodNode.instructions.insert(ain, new InsnNode(ATHROW));
                        methodNode.instructions.insert(ain, new InsnNode(ACONST_NULL));
                        methodNode.instructions.set(ain, new JumpInsnNode(IF_ICMPEQ, ((JumpInsnNode) ain).label));
                        counter.incrementAndGet();
                    }
                }
            });
            classNode.fields.add(new FieldNode(ACC_PUBLIC + ACC_STATIC + ACC_FINAL, fieldName, "Z", null, true));
        });
        logStrings.add(LoggerUtils.stdOut("Added " + counter + " instruction sets."));
        logStrings.add(LoggerUtils.stdOut("Finished. [" + tookThisLong(current) + "ms]"));
    }

    private boolean containsJumpNodes(MethodNode methodNode) {
        for (int i = 0; i < methodNode.instructions.size(); i++) {
            AbstractInsnNode insn = methodNode.instructions.get(i);
            if (insn instanceof JumpInsnNode && insn.getOpcode() == GOTO) {
                return true;
            }
        }

        return false;
    }
}
