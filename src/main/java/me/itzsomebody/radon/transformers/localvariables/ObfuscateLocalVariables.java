package me.itzsomebody.radon.transformers.localvariables;

import me.itzsomebody.radon.transformers.AbstractTransformer;
import me.itzsomebody.radon.utils.LoggerUtils;
import me.itzsomebody.radon.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Transformer that applies a local variable obfuscation by changing the names.
 *
 * @author ItzSomebody
 */
public class ObfuscateLocalVariables extends AbstractTransformer {
    /**
     * Applies obfuscation.
     */
    public void obfuscate() {
        AtomicInteger counter = new AtomicInteger();
        long current = System.currentTimeMillis();
        this.logStrings.add(LoggerUtils.stdOut("------------------------------------------------"));
        this.logStrings.add(LoggerUtils.stdOut("Started local variable obfuscation transformer"));
        this.classNodes().stream().filter(classNode -> !this.exempted(classNode.name, "LocalVars")).forEach(classNode -> {
            classNode.methods.stream().filter(methodNode ->
                    !this.exempted(classNode.name + '.' + methodNode.name + methodNode.desc, "LocalVars"))
                    .filter(methodNode -> methodNode.localVariables != null).forEach(methodNode -> {
                methodNode.localVariables.forEach(localVariableNode -> {
                    localVariableNode.name = StringUtils.crazyString();
                    counter.incrementAndGet();
                });
            });
        });
        this.logStrings.add(LoggerUtils.stdOut("Obfuscated " + counter + " local variables."));
        this.logStrings.add(LoggerUtils.stdOut("Finished. [" + tookThisLong(current) + "ms]"));
    }
}
