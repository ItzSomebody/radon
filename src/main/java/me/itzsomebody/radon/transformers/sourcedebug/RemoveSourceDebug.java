package me.itzsomebody.radon.transformers.sourcedebug;

import java.util.concurrent.atomic.AtomicInteger;
import me.itzsomebody.radon.transformers.AbstractTransformer;
import me.itzsomebody.radon.utils.LoggerUtils;

/**
 * Transformer that obfuscates the source debug attribute by changing the
 * corresponding value.
 *
 * @author ItzSomebody
 */
public class RemoveSourceDebug extends AbstractTransformer {
    /**
     * Applies obfuscation.
     */
    public void obfuscate() {
        AtomicInteger counter = new AtomicInteger();
        long current = System.currentTimeMillis();
        this.logStrings.add(LoggerUtils.stdOut("------------------------------------------------"));
        this.logStrings.add(LoggerUtils.stdOut("Started source debug removal transformer"));
        this.classNodes().stream().filter(classNode -> !this.exempted(classNode.name, "SourceDebug")
                && classNode.sourceDebug != null).forEach(classNode -> {
            classNode.sourceDebug = null;
            counter.incrementAndGet();
        });
        this.logStrings.add(LoggerUtils.stdOut("Removed " + counter + " source debug attributes."));
        this.logStrings.add(LoggerUtils.stdOut("Finished. [" + tookThisLong(current) + "ms]"));
    }
}
