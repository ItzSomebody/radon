/*
 * Radon - An open-source Java obfuscator
 * Copyright (C) 2019 ItzSomebody
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

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
