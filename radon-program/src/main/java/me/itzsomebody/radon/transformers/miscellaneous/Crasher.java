/*
 * Copyright (C) 2018 ItzSomebody
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

package me.itzsomebody.radon.transformers.miscellaneous;

import java.util.concurrent.atomic.AtomicInteger;
import me.itzsomebody.radon.exclusions.ExclusionType;
import me.itzsomebody.radon.transformers.Transformer;
import org.objectweb.asm.tree.ClassNode;

/**
 * Sets the class signature to a random string. A known trick to work on JD, CFR, Procyon and Javap.
 *
 * @author ItzSomebody
 */
public class Crasher extends Transformer {
    @Override
    public void transform() {
        AtomicInteger counter = new AtomicInteger();

        getClassWrappers().parallelStream().filter(classWrapper -> !excluded(classWrapper)
                && classWrapper.classNode.signature == null).forEach(classWrapper -> {
            ClassNode classNode = classWrapper.classNode;
            classNode.signature = randomString(4);
            counter.incrementAndGet();
        });
    }

    @Override
    protected ExclusionType getExclusionType() {
        return ExclusionType.CRASHER;
    }

    @Override
    public String getName() {
        return "Crasher";
    }
}
