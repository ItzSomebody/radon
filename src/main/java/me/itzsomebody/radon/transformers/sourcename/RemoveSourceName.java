package me.itzsomebody.radon.transformers.sourcename;

import java.util.concurrent.atomic.AtomicInteger;
import me.itzsomebody.radon.transformers.AbstractTransformer;
import me.itzsomebody.radon.utils.LoggerUtils;

/**
 * Transformer that obfuscates the source name attribute by removing the
 * attribute entirely.
 *
 * @author ItzSomebody
 */
public class RemoveSourceName extends AbstractTransformer {
    /**
     * Applies obfuscation to.
     */
    public void obfuscate() {
        AtomicInteger counter = new AtomicInteger();
        long current = System.currentTimeMillis();
        this.logStrings.add(LoggerUtils.stdOut("------------------------------------------------"));
        this.logStrings.add(LoggerUtils.stdOut("Started source name removal transformer"));
        this.classNodes().stream().filter(classNode -> !this.exempted(classNode.name, "SourceName")
                && classNode.sourceFile != null).forEach(classNode -> {
            classNode.sourceFile = null;
            counter.incrementAndGet();
        });
        this.logStrings.add(LoggerUtils.stdOut("Removed " + counter + " source name attributes."));
        this.logStrings.add(LoggerUtils.stdOut("Finished. [" + tookThisLong(current) + "ms]"));
    }
}
