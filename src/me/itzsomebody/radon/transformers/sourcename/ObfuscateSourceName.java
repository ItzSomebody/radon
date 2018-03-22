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
        AtomicInteger counter = new AtomicInteger();
        long current = System.currentTimeMillis();
        this.logStrings.add(LoggerUtils.stdOut("------------------------------------------------"));
        this.logStrings.add(LoggerUtils.stdOut("Started source name obfuscation transformer"));
        String newName = StringUtils.crazyString() + ".java";
        this.classNodes().stream().filter(classNode -> !this.classExempted(classNode.name)).forEach(classNode -> {
            classNode.sourceFile = newName;
            counter.incrementAndGet();
        });
        this.logStrings.add(LoggerUtils.stdOut("Obfuscated " + counter + " source name attributes."));
        this.logStrings.add(LoggerUtils.stdOut("Finished. [" + tookThisLong(current) + "ms]"));
    }
}
