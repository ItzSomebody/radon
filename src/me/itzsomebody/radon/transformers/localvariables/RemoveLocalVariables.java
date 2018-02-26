package me.itzsomebody.radon.transformers.localvariables;

import me.itzsomebody.radon.transformers.AbstractTransformer;
import me.itzsomebody.radon.utils.LoggerUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Transformer that applies a local variable obfuscation by removing t
 *
 * @author ItzSomebody
 */
public class RemoveLocalVariables extends AbstractTransformer {
    /**
     * Applies obfuscation.
     */
    public void obfuscate() {
        logStrings.add(LoggerUtils.stdOut("------------------------------------------------"));
        logStrings.add(LoggerUtils.stdOut("Starting local variable removal transformer"));
        AtomicInteger counter = new AtomicInteger();
        long current = System.currentTimeMillis();
        classNodes().stream().filter(classNode -> !classExempted(classNode.name)).forEach(classNode -> {
            classNode.methods.stream().filter(methodNode -> !methodExempted(classNode.name + '.' + methodNode.name + methodNode.desc))
                    .filter(methodNode -> methodNode.localVariables != null).forEach(methodNode -> {
                counter.addAndGet(methodNode.localVariables.size());
                methodNode.localVariables = null;
            });
        });
        logStrings.add(LoggerUtils.stdOut("Removed " + counter + " local variables."));
        logStrings.add(LoggerUtils.stdOut("Finished. [" + tookThisLong(current) + "ms]"));
    }
}
