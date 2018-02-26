package me.itzsomebody.radon.transformers.sourcename;

import me.itzsomebody.radon.transformers.AbstractTransformer;
import me.itzsomebody.radon.utils.LoggerUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Transformer that obfuscates the source name attribute by removing the attribute entirely.
 *
 * @author ItzSomebody
 */
public class RemoveSourceName extends AbstractTransformer {
    /**
     * Applies obfuscation to.
     */
    public void obfuscate() {
        logStrings.add(LoggerUtils.stdOut("------------------------------------------------"));
        logStrings.add(LoggerUtils.stdOut("Starting source name removal transformer"));
        AtomicInteger counter = new AtomicInteger();
        long current = System.currentTimeMillis();
        classNodes().stream().filter(classNode -> !classExempted(classNode.name) && classNode.sourceFile != null).forEach(classNode -> {
            classNode.sourceFile = null;
            counter.incrementAndGet();
        });
        logStrings.add(LoggerUtils.stdOut("Removed " + counter + " source name attributes."));
        logStrings.add(LoggerUtils.stdOut("Finished. [" + tookThisLong(current) + "ms]"));
    }
}
