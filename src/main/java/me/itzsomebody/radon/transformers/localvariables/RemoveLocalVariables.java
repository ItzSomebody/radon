package me.itzsomebody.radon.transformers.localvariables;

import java.util.concurrent.atomic.AtomicInteger;
import me.itzsomebody.radon.transformers.AbstractTransformer;
import me.itzsomebody.radon.utils.LoggerUtils;

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
        AtomicInteger counter = new AtomicInteger();
        long current = System.currentTimeMillis();
        this.logStrings.add(LoggerUtils.stdOut("------------------------------------------------"));
        this.logStrings.add(LoggerUtils.stdOut("Started local variable removal transformer"));
        this.classNodes().stream().filter(classNode -> !this.exempted(classNode.name, "LocalVars")).forEach(classNode -> {
            classNode.methods.stream().filter(methodNode ->
                    !this.exempted(classNode.name + '.' + methodNode.name + methodNode.desc, "LocalVars"))
                    .filter(methodNode -> methodNode.localVariables != null).forEach(methodNode -> {
                counter.addAndGet(methodNode.localVariables.size());
                methodNode.localVariables = null;
            });
        });
        this.logStrings.add(LoggerUtils.stdOut("Removed " + counter + " local variables."));
        this.logStrings.add(LoggerUtils.stdOut("Finished. [" + tookThisLong(current) + "ms]"));
    }
}
