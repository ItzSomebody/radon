package me.itzsomebody.radon.transformers.sourcedebug;

import me.itzsomebody.radon.transformers.AbstractTransformer;
import me.itzsomebody.radon.utils.LoggerUtils;
import me.itzsomebody.radon.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Transformer that obfuscates the source debug attribute by changing the corresponding value.
 *
 * @author ItzSomebody
 */
public class ObfuscateSourceDebug extends AbstractTransformer {
    /**
     * Applies obfuscation.
     */
    public void obfuscate() {
        logStrings.add(LoggerUtils.stdOut("------------------------------------------------"));
        logStrings.add(LoggerUtils.stdOut("Starting source debug obfuscation transformer"));
        AtomicInteger counter = new AtomicInteger();
        long current = System.currentTimeMillis();
        classNodes().stream().filter(classNode -> !classExempted(classNode.name) && classNode.sourceDebug != null).forEach(classNode -> {
            classNode.sourceDebug = StringUtils.crazyKey();
            counter.incrementAndGet();
        });
        logStrings.add(LoggerUtils.stdOut("Obfuscated " + counter + " source debug attributes."));
        logStrings.add(LoggerUtils.stdOut("Finished. [" + tookThisLong(current) + "ms]"));
    }
}
