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
import org.objectweb.asm.tree.ClassNode;

/**
 * Strips out invisible type annotations.
 *
 * @author ItzSomebody
 */
public class InvisibleTypeAnnotationsRemover extends Shrinker {
    @Override
    public void transform() {
        AtomicInteger counter = new AtomicInteger();

        getClassWrappers().stream().filter(classWrapper -> !excluded(classWrapper)).forEach(classWrapper -> {
            ClassNode classNode = classWrapper.getClassNode();

            if (classNode.invisibleTypeAnnotations != null) {
                counter.addAndGet(classNode.invisibleTypeAnnotations.size());
                classNode.invisibleTypeAnnotations = null;
            }

            classWrapper.getFields().stream().filter(fieldWrapper -> !excluded(fieldWrapper)
                    && fieldWrapper.getFieldNode().invisibleTypeAnnotations != null).forEach(fieldWrapper -> {
                counter.addAndGet(fieldWrapper.getFieldNode().invisibleTypeAnnotations.size());
                fieldWrapper.getFieldNode().invisibleTypeAnnotations = null;
            });

            classWrapper.getMethods().stream().filter(methodWrapper -> !excluded(methodWrapper)
                    && methodWrapper.getMethodNode().invisibleTypeAnnotations != null).forEach(methodWrapper -> {
                counter.addAndGet(methodWrapper.getMethodNode().invisibleTypeAnnotations.size());
                methodWrapper.getMethodNode().invisibleTypeAnnotations = null;
            });
        });

        Main.info(String.format("Removed %d invisible type annotations.", counter.get()));
    }

    @Override
    public String getName() {
        return "Invisible Type Annotations Remover";
    }
}
