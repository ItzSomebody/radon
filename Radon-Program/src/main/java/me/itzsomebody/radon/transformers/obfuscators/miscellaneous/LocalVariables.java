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

package me.itzsomebody.radon.transformers.obfuscators.miscellaneous;

import java.util.concurrent.atomic.AtomicInteger;
import me.itzsomebody.radon.exclusions.ExclusionType;
import me.itzsomebody.radon.transformers.Transformer;
import me.itzsomebody.radon.utils.LoggerUtils;
import me.itzsomebody.radon.utils.RandomUtils;
import me.itzsomebody.radon.utils.StringUtils;

public class LocalVariables extends Transformer {
    private boolean remove;

    public LocalVariables(boolean remove) {
        this.remove = remove;
    }

    public boolean isRemove() {
        return remove;
    }

    @Override
    public void transform() {
        AtomicInteger counter = new AtomicInteger();

        this.getClassWrappers().parallelStream().filter(classWrapper -> !excluded(classWrapper)).forEach(classWrapper ->
            classWrapper.classNode.methods.parallelStream().filter(methodNode -> methodNode.localVariables != null).forEach(methodNode -> {
                counter.addAndGet(methodNode.localVariables.size());

                if (remove) {
                    methodNode.localVariables = null;
                } else {
                    methodNode.localVariables.forEach(localVariableNode -> {
                        localVariableNode.name = randomString(4);
                        localVariableNode.desc = "L" + localVariableNode.name + ";";
                    });
                }
            })
        );

        LoggerUtils.stdOut(String.format("Removed %d local variables.", counter.get()));
    }

    @Override
    protected ExclusionType getExclusionType() {
        return ExclusionType.LINE_NUMBERS;
    }

    @Override
    public String getName() {
        return "Local variables";
    }
}
