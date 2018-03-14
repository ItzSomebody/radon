package me.itzsomebody.radon.transformers.flow;

import me.itzsomebody.radon.transformers.AbstractTransformer;
import me.itzsomebody.radon.utils.BytecodeUtils;
import me.itzsomebody.radon.utils.LoggerUtils;
import me.itzsomebody.radon.utils.NumberUtils;
import me.itzsomebody.radon.utils.StringUtils;
import org.objectweb.asm.tree.*;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * This transformer attempts to obscure decompiler output.
 * TODO: FINISH THIS
 *
 * @author ItzSomebody
 */
public class HeavyFlowObfuscation extends AbstractTransformer {
    /**
     * Applies obfuscation.
     */
    public void obfuscate() {
        logStrings.add(LoggerUtils.stdOut("------------------------------------------------"));
        logStrings.add(LoggerUtils.stdOut("Starting heavy flow obfuscation transformer"));
        AtomicInteger counter = new AtomicInteger();
        long current = System.currentTimeMillis();
        classNodes().stream().filter(classNode -> !classExempted(classNode.name)).forEach(classNode -> {
            FieldNode field = new FieldNode(ACC_PUBLIC + ACC_STATIC, StringUtils.crazyString(), "Z", null, true);
            classNode.fields.add(field);
            classNode.methods.stream().filter(methodNode -> !methodExempted(classNode.name + '.' + methodNode.name + methodNode.desc) && !BytecodeUtils.isAbstractMethod(methodNode.access))
                    .forEach(methodNode -> {
                        for (AbstractInsnNode insn : methodNode.instructions.toArray()) {
                            if (insn instanceof JumpInsnNode
                                    && insn.getOpcode() == GOTO) {
                                if (BytecodeUtils.lastInsnIInc(insn)) {
                                    methodNode.instructions.insertBefore(insn, new FieldInsnNode(GETSTATIC, classNode.name, field.name, "Z"));
                                    methodNode.instructions.insertBefore(insn, new InsnNode(ICONST_1));
                                    methodNode.instructions.set(insn, new JumpInsnNode(IF_ICMPEQ, ((JumpInsnNode) insn).label));
                                } else {
                                    methodNode.instructions.insertBefore(insn, new FieldInsnNode(GETSTATIC, classNode.name, field.name, "Z"));
                                    methodNode.instructions.insertBefore(insn, new InsnNode(ICONST_1));
                                    methodNode.instructions.insert(insn, new InsnNode(ATHROW));
                                    methodNode.instructions.insert(insn, new InsnNode(ACONST_NULL));
                                    methodNode.instructions.set(insn, new JumpInsnNode(IF_ICMPEQ, ((JumpInsnNode) insn).label));
                                }
                                counter.incrementAndGet();
                            }
                            // Add more stuff
                        }
                    });
        });
        logStrings.add(LoggerUtils.stdOut("Added " + counter + " instruction sets."));
        logStrings.add(LoggerUtils.stdOut("Finished. [" + tookThisLong(current) + "ms]"));
    }
}
