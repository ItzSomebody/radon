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
import me.itzsomebody.radon.utils.LoggerUtils;
import org.objectweb.asm.tree.ClassNode;

public class DebugInfoRemover extends Shrinker {
    @Override
    public void transform() {
        AtomicInteger outerClasses = new AtomicInteger();
        AtomicInteger outerMethods = new AtomicInteger();
        AtomicInteger innerClasses = new AtomicInteger();

        getClassWrappers().parallelStream().filter(classWrapper -> !excluded(classWrapper)).forEach(classWrapper -> {
            ClassNode classNode = classWrapper.classNode;

            if (classNode.outerClass != null) {
                outerClasses.incrementAndGet();
                classNode.outerClass = null;
            }

            if (classNode.outerMethod != null) {
                outerMethods.incrementAndGet();
                classNode.outerMethod = null;
                classNode.outerMethodDesc = null;
            }

            if (classNode.innerClasses != null) {
                innerClasses.addAndGet(classNode.innerClasses.size());
                classNode.innerClasses.clear();
            }
        });

        LoggerUtils.stdOut(String.format("Removed %d inner classes, %d outer classes and %d outer methods.", innerClasses.get(), outerClasses.get(), outerMethods.get()));
    }

    @Override
    public String getName() {
        return "Debug Info Remover";
    }
}
