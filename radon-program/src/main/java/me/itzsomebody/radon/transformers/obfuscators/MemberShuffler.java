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

package me.itzsomebody.radon.transformers.obfuscators;

import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;
import me.itzsomebody.radon.exclusions.ExclusionType;
import me.itzsomebody.radon.transformers.Transformer;
import me.itzsomebody.radon.Logger;

/**
 * Randomizes the order of methods and fields in a class.
 */
public class MemberShuffler extends Transformer {
    @Override
    public void transform() {
        AtomicInteger counter = new AtomicInteger();

        getClassWrappers().stream().filter(classWrapper -> !excluded(classWrapper)).forEach(classWrapper -> {
            Collections.shuffle(classWrapper.classNode.methods);
            counter.addAndGet(classWrapper.classNode.methods.size());
            if (classWrapper.classNode.fields != null) {
                Collections.shuffle(classWrapper.classNode.fields);
                counter.addAndGet(classWrapper.classNode.fields.size());
            }
        });

        Logger.stdOut(String.format("Shuffled %d members.", counter.get()));
    }

    @Override
    protected ExclusionType getExclusionType() {
        return ExclusionType.SHUFFLER;
    }

    @Override
    public String getName() {
        return "Member Shuffler";
    }
}
