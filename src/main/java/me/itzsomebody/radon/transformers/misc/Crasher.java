package me.itzsomebody.radon.transformers.misc;

import me.itzsomebody.radon.transformers.AbstractTransformer;
import me.itzsomebody.radon.utils.LoggerUtils;
import me.itzsomebody.radon.utils.StringUtils;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Transformer that applies a crashing technique by exploiting class signature parsing.
 * <p>
 * Crashes:
 * - JD-MainGUI
 * - ProCyon
 * - CFR
 * </p>
 *
 * @author ItzSomebody
 */
public class Crasher extends AbstractTransformer {
    /**
     * Applies obfuscation.
     */
    public void obfuscate() {
        AtomicInteger counter = new AtomicInteger();
        long current = System.currentTimeMillis();
        this.logStrings.add(LoggerUtils.stdOut("------------------------------------------------"));
        this.logStrings.add(LoggerUtils.stdOut("Started crasher transformer."));
        this.classNodes().stream().filter(classNode -> !this.exempted(classNode.name, "Crasher")).forEach(classNode -> {
            if (classNode.signature == null) {
                classNode.signature = StringUtils.crazyString();
                counter.incrementAndGet();
            }
        });
        this.logStrings.add(LoggerUtils.stdOut("Added " + counter + " crashers."));
        this.logStrings.add(LoggerUtils.stdOut("Finished. [" + tookThisLong(current) + "ms]"));
    }
}
