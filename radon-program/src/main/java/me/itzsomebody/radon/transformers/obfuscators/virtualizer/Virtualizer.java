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

package me.itzsomebody.radon.transformers.obfuscators.virtualizer;

import java.util.Map;
import java.util.stream.Stream;
import me.itzsomebody.radon.exclusions.ExclusionType;
import me.itzsomebody.radon.transformers.Transformer;

/**
 * Translates Java bytecode instructions into a custom bytecode instruction set which
 * can (theoretically) only be understood by a custom virtual machine.
 *
 * @author ItzSomebody
 */
public class Virtualizer extends Transformer {
    @Override
    public void transform() {
        getClassWrappers().stream().filter(classWrapper -> !excluded(classWrapper)).forEach(classWrapper ->
                classWrapper.methods.stream().filter(methodWrapper -> !excluded(methodWrapper)
                        && hasInstructions(methodWrapper)).forEach(methodWrapper -> {
                    Stream.of(methodWrapper.methodNode.instructions).forEach(insn -> {
                        // TODO
                    });
                }));
    }

    @Override
    public ExclusionType getExclusionType() {
        return ExclusionType.VIRTUALIZER; // FIXME
    }

    @Override
    public String getName() {
        return "Virtualizer";
    }

    @Override
    public Map<String, Object> getConfiguration() {
        return null; // TODO
    }

    @Override
    public void setConfiguration(Map<String, Object> config) {
        // TODO
    }

    @Override
    public void verifyConfiguration(Map<String, Object> config) {
        // TODO
    }
}
