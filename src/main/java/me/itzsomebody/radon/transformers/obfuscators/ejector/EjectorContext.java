package me.itzsomebody.radon.transformers.obfuscators.ejector;

import me.itzsomebody.radon.analysis.constant.values.AbstractValue;
import me.itzsomebody.radon.asm.ClassWrapper;
import me.itzsomebody.radon.asm.MethodWrapper;
import me.itzsomebody.radon.utils.RandomUtils;
import org.objectweb.asm.tree.analysis.Frame;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public final class EjectorContext {
    private final AtomicInteger counter;
    private final ClassWrapper classWrapper;
    private final MethodWrapper methodWrapper;
    private final Set<Integer> ids;
    private final Frame<AbstractValue>[] frames;
    private final boolean junkArguments;
    private final int junkArgumentStrength;

    EjectorContext(AtomicInteger counter, ClassWrapper classWrapper, MethodWrapper methodWrapper, Frame<AbstractValue>[] frames, boolean junkArguments, int junkArgumentStrength) {
        this.counter = counter;
        this.classWrapper = classWrapper;
        this.methodWrapper = methodWrapper;
        this.frames = frames;
        this.junkArguments = junkArguments;
        this.junkArgumentStrength = junkArgumentStrength;
        this.ids = new HashSet<>();
    }

    public int getJunkArgumentStrength() {
        return junkArgumentStrength;
    }

    public boolean isJunkArguments() {
        return junkArguments;
    }

    public AtomicInteger getCounter() {
        return counter;
    }

    public ClassWrapper getClassWrapper() {
        return classWrapper;
    }

    public MethodWrapper getMethodWrapper() {
        return methodWrapper;
    }

    public Frame<AbstractValue>[] getFrames() {
        return frames;
    }

    public int getNextId() {
        int id = RandomUtils.getRandomInt();
        while (!ids.add(id))
            id = RandomUtils.getRandomInt();
        return id;
    }
}
