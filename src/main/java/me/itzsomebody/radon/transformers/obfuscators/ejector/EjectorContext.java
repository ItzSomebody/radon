package me.itzsomebody.radon.transformers.obfuscators.ejector;

import me.itzsomebody.radon.asm.ClassWrapper;
import me.itzsomebody.radon.utils.RandomUtils;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public final class EjectorContext {
    private final AtomicInteger counter;
    private final ClassWrapper classWrapper;
    private final Set<Integer> ids;
    private final boolean junkArguments;
    private final int junkArgumentStrength;

    EjectorContext(AtomicInteger counter, ClassWrapper classWrapper, boolean junkArguments, int junkArgumentStrength) {
        this.counter = counter;
        this.classWrapper = classWrapper;
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

    public int getNextId() {
        int id = RandomUtils.getRandomInt();
        while (!ids.add(id))
            id = RandomUtils.getRandomInt();
        return id;
    }
}
