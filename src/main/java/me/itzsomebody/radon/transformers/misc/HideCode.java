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

package me.itzsomebody.radon.transformers.misc;

import java.util.concurrent.atomic.AtomicInteger;
import me.itzsomebody.radon.transformers.AbstractTransformer;
import me.itzsomebody.radon.utils.BytecodeUtils;
import me.itzsomebody.radon.utils.LoggerUtils;

/**
 * Transformer that applies a code hiding technique by applying synthetic modifiers to the class, fields, and methods.
 *
 * @author ItzSomebody
 */
public class HideCode extends AbstractTransformer {
    /**
     * Applies obfuscation.
     */
    public void obfuscate() {
        AtomicInteger counter = new AtomicInteger();
        long current = System.currentTimeMillis();
        this.logStrings.add(LoggerUtils.stdOut("------------------------------------------------"));
        this.logStrings.add(LoggerUtils.stdOut("Started hide code transformer"));
        this.classNodes().stream().filter(classNode -> !this.exempted(classNode.name, "HideCode")).forEach(classNode -> {
            if (!BytecodeUtils.isSyntheticMethod(classNode.access)
                    && !BytecodeUtils.hasAnnotations(classNode)) {
                classNode.access |= ACC_SYNTHETIC;
                counter.incrementAndGet();
            }

            classNode.methods.stream().filter(methodNode ->
                    !this.exempted(classNode.name + '.' + methodNode.name + methodNode.desc, "HideCode")
                            && !BytecodeUtils.hasAnnotations(methodNode)).forEach(methodNode -> {
                boolean hidOnce = false;
                if (!BytecodeUtils.isSyntheticMethod(methodNode.access)) {
                    methodNode.access |= ACC_SYNTHETIC;
                    hidOnce = true;
                }

                if (!BytecodeUtils.isBridgeMethod(methodNode.access)
                        && !methodNode.name.startsWith("<")) {
                    methodNode.access |= ACC_BRIDGE;
                    hidOnce = true;
                }

                if (hidOnce) counter.incrementAndGet();
            });

            if (classNode.fields != null)
                classNode.fields.stream().filter(fieldNode ->
                        !exempted(classNode.name + '.' + fieldNode.name, "HideCode")
                                && !BytecodeUtils.hasAnnotations(fieldNode)).forEach(fieldNode -> {
                    if (!BytecodeUtils.isSyntheticMethod(fieldNode.access)) {
                        fieldNode.access |= ACC_SYNTHETIC;
                        counter.incrementAndGet();
                    }
                });
        });
        this.logStrings.add(LoggerUtils.stdOut("Hid " + counter + " members."));
        this.logStrings.add(LoggerUtils.stdOut("Finished. [" + tookThisLong(current) + "ms]"));
    }
}
