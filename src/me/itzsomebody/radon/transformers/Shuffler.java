package me.itzsomebody.radon.transformers;

import me.itzsomebody.radon.utils.LoggerUtils;

import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Transformer which shuffles class members.
 *
 * @author ItzSomebody
 */
public class Shuffler extends AbstractTransformer {
    /**
     * Applies obfuscation.
     */
    public void obfuscate() {
        logStrings.add(LoggerUtils.stdOut("------------------------------------------------"));
        logStrings.add(LoggerUtils.stdOut("Starting shuffle transformer"));
        AtomicInteger counter = new AtomicInteger();
        long current = System.currentTimeMillis();
        classNodes().stream().filter(classNode -> !classExempted(classNode.name)).forEach(classNode -> {
            Collections.shuffle(classNode.methods);
            counter.addAndGet(classNode.methods.size());
            if (classNode.fields != null) {
                Collections.shuffle(classNode.fields);
                counter.addAndGet(classNode.fields.size());
            }
        });
        logStrings.add(LoggerUtils.stdOut("Shuffled " + counter + " members."));
        logStrings.add(LoggerUtils.stdOut("Finished. [" + tookThisLong(current) + "ms]"));
    }
}
