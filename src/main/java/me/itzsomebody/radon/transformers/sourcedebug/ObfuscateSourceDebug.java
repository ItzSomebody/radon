package me.itzsomebody.radon.transformers.sourcedebug;

import java.util.concurrent.atomic.AtomicInteger;
import me.itzsomebody.radon.transformers.AbstractTransformer;
import me.itzsomebody.radon.utils.LoggerUtils;
import me.itzsomebody.radon.utils.StringUtils;

/**
 * Transformer that obfuscates the source debug attribute by changing the
 * corresponding value.
 *
 * @author ItzSomebody
 */
public class ObfuscateSourceDebug extends AbstractTransformer {
    /**
     * Applies obfuscation.
     */
    public void obfuscate() {
        AtomicInteger counter = new AtomicInteger();
        long current = System.currentTimeMillis();
        this.logStrings.add(LoggerUtils.stdOut("------------------------------------------------"));
        this.logStrings.add(LoggerUtils.stdOut("Started source debug obfuscation transformer"));
        String newDebug = StringUtils.crazyString();
        this.classNodes().stream().filter(classNode -> !this.exempted(classNode.name, "SourceDebug")
                && classNode.sourceDebug != null).forEach(classNode -> {
            classNode.sourceDebug = newDebug;
            counter.incrementAndGet();
        });
        this.logStrings.add(LoggerUtils.stdOut("Obfuscated " + counter + " source debug attributes."));
        this.logStrings.add(LoggerUtils.stdOut("Finished. [" + tookThisLong(current) + "ms]"));
    }
}
