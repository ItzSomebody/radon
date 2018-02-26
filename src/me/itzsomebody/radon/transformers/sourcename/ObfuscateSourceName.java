package me.itzsomebody.radon.transformers.sourcename;

import me.itzsomebody.radon.transformers.AbstractTransformer;
import me.itzsomebody.radon.utils.LoggerUtils;
import me.itzsomebody.radon.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Transformer that obfuscates the source name attribute by changing the corresponding value.
 *
 * @author ItzSomebody
 */
public class ObfuscateSourceName extends AbstractTransformer {
    /**
     * Applies obfuscation.
     */
    public void obfuscate() {
        logStrings.add(LoggerUtils.stdOut("------------------------------------------------"));
        logStrings.add(LoggerUtils.stdOut("Starting source name obfuscation transformer"));
        AtomicInteger counter = new AtomicInteger();
        long current = System.currentTimeMillis();
        String newName = StringUtils.crazyString() + ".java";
        classNodes().stream().filter(classNode -> !classExempted(classNode.name)).forEach(classNode -> {
            classNode.sourceFile = newName;
            counter.incrementAndGet();
        });
        logStrings.add(LoggerUtils.stdOut("Obfuscated " + counter + " source name attributes."));
        logStrings.add(LoggerUtils.stdOut("Finished. [" + tookThisLong(current) + "ms]"));
    }
}
