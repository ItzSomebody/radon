package me.itzsomebody.radon.transformers.flow;

import me.itzsomebody.radon.utils.NumberUtils;
import me.itzsomebody.radon.utils.StringUtils;
import org.objectweb.asm.tree.*;
import me.itzsomebody.radon.transformers.AbstractTransformer;
import me.itzsomebody.radon.utils.BytecodeUtils;
import me.itzsomebody.radon.utils.LoggerUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Transformer that sets GOTO->LABEL instructions as a condition which is always true.
 * <p>
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
        AtomicInteger counter = new AtomicInteger();
        long current = System.currentTimeMillis();
        this.logStrings.add(LoggerUtils.stdOut("------------------------------------------------"));
        this.logStrings.add(LoggerUtils.stdOut("Started light flow obfuscation transformer."));
        classNodes().stream().filter(classNode -> !this.exempted(classNode.name, "Flow")).forEach(classNode -> {
            String fieldName = StringUtils.crazyString();
            classNode.methods.stream().filter(methodNode -> !BytecodeUtils.isAbstractMethod(methodNode.access)
                    && !this.exempted(classNode.name + '.' + methodNode.name + methodNode.desc, "Flow")
                    && BytecodeUtils.containsGoto(methodNode)).forEach(methodNode -> {
                for (AbstractInsnNode ain : methodNode.instructions.toArray()) {
                    if (this.methodSize(methodNode) > 60000) break;
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
            classNode.fields.add(new FieldNode(ACC_PUBLIC + ACC_STATIC +
                    ACC_FINAL, fieldName, "Z", null, true));
        });
        this.logStrings.add(LoggerUtils.stdOut("Added " + counter + " instruction sets."));
        this.logStrings.add(LoggerUtils.stdOut("Finished. [" + tookThisLong(current) + "ms]"));
    }
}
