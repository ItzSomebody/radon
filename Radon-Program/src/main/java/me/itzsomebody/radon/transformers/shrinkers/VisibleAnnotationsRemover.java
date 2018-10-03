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

/**
 * Removes annotations visible to the runtime from classes, methods and fields.
 *
 * @author ItzSomebody
 */
public class VisibleAnnotationsRemover extends Shrinker {
    @Override
    public void transform() {
        AtomicInteger classAnnotations = new AtomicInteger();
        AtomicInteger methodAnnotations = new AtomicInteger();
        AtomicInteger fieldAnnotations = new AtomicInteger();

        getClassWrappers().parallelStream().filter(classWrapper -> !excluded(classWrapper)).forEach(classWrapper -> {
            ClassNode classNode = classWrapper.classNode;

            if (classNode.visibleAnnotations != null) {
                classAnnotations.addAndGet(classNode.visibleAnnotations.size());
                classNode.visibleAnnotations.clear();
            }

            classWrapper.fields.parallelStream().filter(fieldWrapper -> !excluded(fieldWrapper)
                    && fieldWrapper.fieldNode.visibleAnnotations != null).forEach(fieldWrapper -> {
                fieldAnnotations.addAndGet(fieldWrapper.fieldNode.visibleAnnotations.size());
                fieldWrapper.fieldNode.visibleAnnotations.clear();
            });

            classWrapper.methods.parallelStream().filter(methodWrapper -> !excluded(methodWrapper)
                    && methodWrapper.methodNode.visibleAnnotations != null).forEach(methodWrapper -> {
                methodAnnotations.addAndGet(methodWrapper.methodNode.visibleAnnotations.size());
                methodWrapper.methodNode.visibleAnnotations.clear();
            });
        });

        LoggerUtils.stdOut(String.format("Removed %d class, %d method and %d field invisible annotations.",
                classAnnotations.get(), methodAnnotations.get(), fieldAnnotations.get()));
    }

    @Override
    public String getName() {
        return "Visible Annotations Remover";
    }
}
