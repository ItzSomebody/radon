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
import me.itzsomebody.radon.utils.AccessUtils;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

/**
 * Strips out deprecated access flags.
 *
 * @author ItzSomebody
 */
public class DeprecatedAccessRemover extends Shrinker {
    @Override
    public void transform() {
        AtomicInteger counter = new AtomicInteger();

        getClassWrappers().stream().filter(classWrapper -> !excluded(classWrapper)).forEach(classWrapper -> {
            ClassNode classNode = classWrapper.classNode;

            if (AccessUtils.isDeprecated(classNode.access)) {
                classNode.access &= ~ACC_DEPRECATED;
                counter.incrementAndGet();
            }

            classWrapper.methods.stream().filter(methodWrapper -> !excluded(methodWrapper)
                    && AccessUtils.isDeprecated(methodWrapper.methodNode.access)).forEach(methodWrapper -> {
                methodWrapper.methodNode.access &= ~ACC_DEPRECATED;
                counter.incrementAndGet();
            });

            classWrapper.fields.stream().filter(fieldWrapper -> !excluded(fieldWrapper)
                    && AccessUtils.isDeprecated(fieldWrapper.fieldNode.access)).forEach(fieldWrapper -> {
                fieldWrapper.fieldNode.access &= ~ACC_DEPRECATED;
                counter.incrementAndGet();
            });
        });
    }

    @Override
    public String getName() {
        return "Useless Access Flags Remover";
    }
}
