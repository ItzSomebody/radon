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

package me.itzsomebody.radon.transformers.shrinkers;

import java.util.concurrent.atomic.AtomicInteger;
import me.itzsomebody.radon.Main;

/**
 * Removes outer methods.
 *
 * @author ItzSomebody
 */
public class OuterMethodRemover extends Shrinker {
    @Override
    public void transform() {
        AtomicInteger counter = new AtomicInteger();

        getClassWrappers().stream().filter(classWrapper -> !excluded(classWrapper)
                && classWrapper.getClassNode().outerClass != null).forEach(classWrapper -> {
            classWrapper.getClassNode().outerClass = null;
            classWrapper.getClassNode().outerMethod = null;
            classWrapper.getClassNode().outerMethodDesc = null;

            counter.incrementAndGet();
        });

        Main.info(String.format("Removed %d outer methods.", counter.get()));
    }

    @Override
    public String getName() {
        return "Outer Method Remover";
    }
}
