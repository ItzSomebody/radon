package me.itzsomebody.radon.transformers.misc;

import java.util.concurrent.atomic.AtomicInteger;
import me.itzsomebody.radon.transformers.AbstractTransformer;
import me.itzsomebody.radon.utils.LoggerUtils;

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
        AtomicInteger counter = new AtomicInteger();
        long current = System.currentTimeMillis();
        this.logStrings.add(LoggerUtils.stdOut("------------------------------------------------"));
        this.logStrings.add(LoggerUtils.stdOut("Started inner class removal transformer"));
        this.classNodes().stream().filter(classNode -> !this.exempted(classNode.name, "InnerClasses")
                && classNode.innerClasses != null).forEach(classNode -> {
            counter.addAndGet(classNode.innerClasses.size());
            classNode.innerClasses.clear();
        });
        this.logStrings.add(LoggerUtils.stdOut("Removed " + counter + " inner class infos."));
        this.logStrings.add(LoggerUtils.stdOut("Finished. [" + tookThisLong(current) + "ms]"));
    }
}
