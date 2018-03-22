package me.itzsomebody.radon.transformers.misc;

import me.itzsomebody.radon.transformers.AbstractTransformer;
import me.itzsomebody.radon.utils.BytecodeUtils;
import me.itzsomebody.radon.utils.LoggerUtils;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Transformer that adds an expiration block of code to <init> methods.
 *
 * @author ItzSomebody
 * @author Allatori Dev Team
 */
public class Expiry extends AbstractTransformer {
    /**
     * The expiry time as a {@link Long}.
     */
    private long expiryTime;

    /**
     * The expiry message to display when expiry time is exceeded.
     */
    private String expiryMsg;

    /**
     * Constructor used to create an {@link Expiry} object.
     *
     * @param expiryTime expiration time as a {@link Long}.
     * @param expiryMsg  expiration message as a {@link String}.
     */
    public Expiry(long expiryTime, String expiryMsg) {
        this.expiryTime = expiryTime;
        this.expiryMsg = expiryMsg;
    }

    /**
     * Applies obfuscation.
     */
    public void obfuscate() {
        AtomicInteger counter = new AtomicInteger();
        long current = System.currentTimeMillis();
        this.logStrings.add(LoggerUtils.stdOut("------------------------------------------------"));
        this.logStrings.add(LoggerUtils.stdOut("Started expiry transformer"));
        this.classNodes().stream().filter(classNode -> !this.classExempted(classNode.name)).forEach(classNode -> {
            classNode.methods.stream().filter(methodNode -> !this.methodExempted(classNode.name + '.' + methodNode.name + methodNode.desc)
                    && methodNode.name.equals("<init>") && methodSize(methodNode) < 60000).forEach(methodNode -> {
                methodNode.instructions.insertBefore(methodNode.instructions.getFirst(),
                        BytecodeUtils.returnExpiry(this.expiryTime, this.expiryMsg));
                counter.incrementAndGet();
            });
        });
        this.logStrings.add(LoggerUtils.stdOut("Added " + counter + " expiration code blocks."));
        this.logStrings.add(LoggerUtils.stdOut("Finished. [" + tookThisLong(current) + "ms]"));
    }
}
