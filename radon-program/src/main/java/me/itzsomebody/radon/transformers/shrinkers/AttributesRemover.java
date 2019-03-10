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

package me.itzsomebody.radon.transformers.shrinkers;

import java.util.concurrent.atomic.AtomicInteger;
import me.itzsomebody.radon.Logger;
import org.objectweb.asm.tree.ClassNode;

/**
 * Removes all attributes from the classes.
 *
 * @author ItzSomebody
 */
public class AttributesRemover extends Shrinker {
    @Override
    public void transform() {
        AtomicInteger counter = new AtomicInteger();

        getClassWrappers().stream().filter(classWrapper -> excluded(classWrapper)
                && classWrapper.classNode.attrs != null).forEach(classWrapper -> {
            ClassNode classNode = classWrapper.classNode;

            counter.addAndGet(classNode.attrs.size());
            classNode.attrs.clear();
        });

        Logger.stdOut(String.format("Removed %d attributes.", counter.get()));
    }

    @Override
    public String getName() {
        return "Attributes Remover";
    }
}
