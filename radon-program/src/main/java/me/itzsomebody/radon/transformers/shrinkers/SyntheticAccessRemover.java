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
 * Strips out synthetic/bridge access flags.
 *
 * @author ItzSomebody
 */
public class SyntheticAccessRemover extends Shrinker {
    @Override
    public void transform() {
        AtomicInteger counter = new AtomicInteger();

        getClassWrappers().stream().filter(classWrapper -> !excluded(classWrapper)).forEach(classWrapper -> {
            ClassNode classNode = classWrapper.classNode;

            if (AccessUtils.isSynthetic(classNode.access)) {
                classNode.access &= ~ACC_SYNTHETIC;
                counter.incrementAndGet();
            }

            classWrapper.methods.stream().filter(methodWrapper -> !excluded(methodWrapper)).forEach(methodWrapper -> {
                MethodNode methodNode = methodWrapper.methodNode;

                if (AccessUtils.isSynthetic(methodNode.access) || AccessUtils.isBridge(methodNode.access)) {
                    methodNode.access &= ~(ACC_SYNTHETIC | ACC_BRIDGE);
                    counter.incrementAndGet();
                }
            });

            classWrapper.fields.stream().filter(fieldWrapper -> !excluded(fieldWrapper)).forEach(fieldWrapper -> {
                FieldNode fieldNode = fieldWrapper.fieldNode;

                if (AccessUtils.isSynthetic(fieldNode.access)) {
                    fieldNode.access &= ~ACC_SYNTHETIC;
                    counter.incrementAndGet();
                }
            });
        });
    }

    @Override
    public String getName() {
        return "Synthetic Access Remover";
    }
}
