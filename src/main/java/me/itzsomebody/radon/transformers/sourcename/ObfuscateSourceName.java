package me.itzsomebody.radon.transformers.sourcename;

import java.util.concurrent.atomic.AtomicInteger;
import me.itzsomebody.radon.transformers.AbstractTransformer;
import me.itzsomebody.radon.utils.LoggerUtils;
import me.itzsomebody.radon.utils.StringUtils;

/**
 * Transformer that obfuscates the source name attribute by changing the
 * corresponding value.
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
        this.classNodes().stream().filter(classNode -> !this.exempted(classNode.name, "SourceName")).forEach(classNode -> {
            classNode.sourceFile = newName;
            counter.incrementAndGet();
        });
        this.logStrings.add(LoggerUtils.stdOut("Obfuscated " + counter + " source name attributes."));
        this.logStrings.add(LoggerUtils.stdOut("Finished. [" + tookThisLong(current) + "ms]"));
    }
}
