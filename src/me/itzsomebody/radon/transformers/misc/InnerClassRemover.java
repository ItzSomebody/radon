package me.itzsomebody.radon.transformers.misc;

import me.itzsomebody.radon.transformers.AbstractTransformer;
import me.itzsomebody.radon.utils.LoggerUtils;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Transformer which removes innerclass infomation.
 *
 * @author ItzSomebody
 */
public class InnerClassRemover extends AbstractTransformer {
    /**
     * Applies obfuscation.
     */
    public void obfuscate() {
        logStrings.add(LoggerUtils.stdOut("------------------------------------------------"));
        logStrings.add(LoggerUtils.stdOut("Starting inner class removal transformer"));
        AtomicInteger counter = new AtomicInteger();
        long current = System.currentTimeMillis();
        classNodes().stream().filter(classNode -> !classExempted(classNode.name) && classNode.innerClasses != null).forEach(classNode -> {
            counter.addAndGet(classNode.innerClasses.size());
            classNode.innerClasses.clear();
        });
        logStrings.add(LoggerUtils.stdOut("Removed " + counter + " inner class infos."));
        logStrings.add(LoggerUtils.stdOut("Finished. [" + tookThisLong(current) + "ms]"));
    }
}
