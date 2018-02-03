package me.itzsomebody.radon.transformers.sourcename;

import me.itzsomebody.radon.asm.tree.ClassNode;
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
     * {@link List} of {@link String}s to add to log.
     */
    private List<String> logStrings;

    /**
     * Applies obfuscation.
     */
    public void obfuscate() {
        logStrings = new ArrayList<>();
        logStrings.add(LoggerUtils.stdOut("------------------------------------------------"));
        logStrings.add(LoggerUtils.stdOut("Starting source name obfuscation transformer"));
        AtomicInteger counter = new AtomicInteger();
        long current = System.currentTimeMillis();
        classNodes().stream().filter(classNode -> !classExempted(classNode.name))
                .filter(classNode -> classNode.sourceFile != null).forEach(classNode -> {
                    classNode.sourceFile = StringUtils.crazyString() + ".java";
                    counter.incrementAndGet();
        });
        logStrings.add(LoggerUtils.stdOut("Obfuscated " + counter + " source name attributes."));
        logStrings.add(LoggerUtils.stdOut("Finished. [" + tookThisLong(current) + "ms]"));
    }

    /**
     * Returns {@link String}s to add to log.
     *
     * @return {@link String}s to add to log.
     */
    public List<String> getLogStrings() {
        return this.logStrings;
    }
}
