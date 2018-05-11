package me.itzsomebody.radon.transformers.misc;

import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;
import me.itzsomebody.radon.transformers.AbstractTransformer;
import me.itzsomebody.radon.utils.LoggerUtils;

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
        AtomicInteger counter = new AtomicInteger();
        long current = System.currentTimeMillis();
        this.logStrings.add(LoggerUtils.stdOut("------------------------------------------------"));
        this.logStrings.add(LoggerUtils.stdOut("Started shuffle transformer"));
        this.classNodes().stream().filter(classNode -> !this.exempted(classNode.name, "Shuffler")).forEach(classNode -> {
            Collections.shuffle(classNode.methods);
            counter.addAndGet(classNode.methods.size());
            if (classNode.fields != null) {
                Collections.shuffle(classNode.fields);
                counter.addAndGet(classNode.fields.size());
            }
        });
        this.logStrings.add(LoggerUtils.stdOut("Shuffled " + counter + " members."));
        this.logStrings.add(LoggerUtils.stdOut("Finished. [" + tookThisLong(current) + "ms]"));
    }
}
