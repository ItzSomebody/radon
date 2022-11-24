/*
 * Radon - An open-source Java obfuscator
 * Copyright (C) 2021 ItzSomebody
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

package xyz.itzsomebody.commons.matcher.rules;

import org.objectweb.asm.tree.AbstractInsnNode;
import xyz.itzsomebody.commons.matcher.InstructionMatcher;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class OpcodeRule implements InstructionRule {
    private final Function<AbstractInsnNode, Boolean> rule;
    private final List<Integer> wantedOpcodes;

    public OpcodeRule(Function<AbstractInsnNode, Boolean> rule, int... opcodes) {
        this.rule = rule;
        this.wantedOpcodes = Arrays.stream(opcodes).boxed().collect(Collectors.toList());
    }

    public OpcodeRule(int... opcodes) {
        this.rule = null;
        this.wantedOpcodes = Arrays.stream(opcodes).boxed().collect(Collectors.toList());
    }

    @Override
    public boolean matches(InstructionMatcher matcher, AbstractInsnNode current) {
        return wantedOpcodes.contains(current.getOpcode()) && (rule == null || rule.apply(current));
    }
}
