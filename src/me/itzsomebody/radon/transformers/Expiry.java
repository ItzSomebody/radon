package me.itzsomebody.radon.transformers;

import me.itzsomebody.radon.asm.tree.*;
import me.itzsomebody.radon.utils.BytecodeUtils;
import me.itzsomebody.radon.utils.LoggerUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Transformer that adds an expiration block of code to <init> methods.
 *
 * @author ItzSomebody
 * @author Allatori Dev Team
 */
public class Expiry extends AbstractTransformer {
    /**
     * {@link List} of {@link String}s to add to log.
     */
    private List<String> logStrings;

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
     * @param expiryMsg expiration message as a {@link String}.
     */
    public Expiry(long expiryTime, String expiryMsg) {
        this.expiryTime = expiryTime;
        this.expiryMsg = expiryMsg;
    }

    /**
     * Applies obfuscation.
     */
    public void obfuscate() {
        logStrings = new ArrayList<>();
        logStrings.add(LoggerUtils.stdOut("------------------------------------------------"));
        logStrings.add(LoggerUtils.stdOut("Starting expiry transformer"));
        AtomicInteger counter = new AtomicInteger();
        long current = System.currentTimeMillis();
        classNodes().stream().filter(classNode -> !classExempted(classNode.name)).forEach(classNode -> {
            classNode.methods.stream().filter(methodNode -> !methodExempted(classNode.name + '.' + methodNode.name + methodNode.desc))
                    .filter(methodNode -> methodNode.name.equals("<init>")).forEach(methodNode -> {
                for (AbstractInsnNode insn : methodNode.instructions.toArray()) {
                    methodNode.instructions.insertBefore(insn, BytecodeUtils.returnExpiry(expiryTime, expiryMsg));
                    counter.incrementAndGet();
                }
            });
        });
        logStrings.add(LoggerUtils.stdOut("Added " + counter + " expiration code blocks."));
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
