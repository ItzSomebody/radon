package me.itzsomebody.radon.transformers.flow;

import me.itzsomebody.radon.asm.Opcodes;
import me.itzsomebody.radon.asm.tree.*;
import me.itzsomebody.radon.transformers.AbstractTransformer;
import me.itzsomebody.radon.utils.BytecodeUtils;
import me.itzsomebody.radon.utils.LoggerUtils;
import me.itzsomebody.radon.utils.NumberUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Transformer that adds random GOTO->Label.
 * This is total trash, consider using it as a last resort if at all.
 *
 * @author ItzSomebody
 */
public class LightFlowObfuscation extends AbstractTransformer {
    /**
     * {@link List} of {@link String}s to add to log.
     */
    private List<String> logStrings;

    /**
     * Applies obfuscation.
     */
    public void obfuscate() {
        logStrings = new ArrayList<>();
        logStrings.add(LoggerUtils.stdOut("------------------------------------------------"));
        logStrings.add(LoggerUtils.stdOut("Starting light flow obfuscation transformer."));
        AtomicInteger counter = new AtomicInteger();
        long current = System.currentTimeMillis();
        classNodes().stream().filter(classNode -> !classExempted(classNode.name)).forEach(classNode -> {
            classNode.methods.stream().filter(methodNode -> !BytecodeUtils.isAbstractMethod(methodNode.access))
                    .filter(methodNode -> !methodExempted(classNode.name + '.' + methodNode.name + methodNode.desc)).forEach(methodNode -> {
                for (AbstractInsnNode ain : methodNode.instructions.toArray()) {
                    LabelNode labelNode = new LabelNode();
                    if (ain.getOpcode() != Opcodes.GOTO && !(ain instanceof LabelNode)
                            && NumberUtils.getRandomInt(20) < 10) {
                        methodNode.instructions.add(new JumpInsnNode(Opcodes.GOTO, labelNode));
                        methodNode.instructions.add(labelNode);
                        counter.incrementAndGet();
                    }
                }
            });
        });
        logStrings.add(LoggerUtils.stdOut("Added " + counter + " instruction sets."));
        logStrings.add(LoggerUtils.stdOut("Finished. [" + tookThisLong(current) + "ms]"));
    }

    /**
     * Returns {@link String}s to add to log.
     *
     * @return {@link String}s to add to log.
     */
    public List<String> getLogStrings() {
        return this.logStrings;
    }
}
