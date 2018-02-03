package me.itzsomebody.radon.transformers;

import me.itzsomebody.radon.asm.tree.ClassNode;
import me.itzsomebody.radon.utils.LoggerUtils;
import me.itzsomebody.radon.utils.StringUtils;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Transformer that applies a crashing technique by exploiting class signature parsing.
 * <p>
 * Crashes:
 * - JD-GUI
 * - ProCyon
 * - CFR
 * </p>
 *
 * @author ItzSomebody
 */
public class Crasher extends AbstractTransformer {
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
        logStrings.add(LoggerUtils.stdOut("Starting crasher transformer."));
        AtomicInteger counter = new AtomicInteger();
        long current = System.currentTimeMillis();
        classNodes().stream().filter(classNode -> !classExempted(classNode.name)).filter(classNode -> classNode.signature == null)
                .forEach(classNode -> {
            classNode.signature = StringUtils.crazyString();
            counter.incrementAndGet();
        });
        logStrings.add(LoggerUtils.stdOut("Added " + counter + " crashers."));
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
