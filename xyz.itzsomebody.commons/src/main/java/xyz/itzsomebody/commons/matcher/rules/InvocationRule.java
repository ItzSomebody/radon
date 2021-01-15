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

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import xyz.itzsomebody.commons.matcher.InstructionMatcher;

import java.util.function.Function;

public class InvocationRule extends OpcodeRule {
    private final int opcode;
    private final String owner;
    private final String name;
    private final String description;

    public InvocationRule(Function<AbstractInsnNode, Boolean> rule, int opcode, String owner, String name, String description) {
        super(rule, Opcodes.INVOKEVIRTUAL, Opcodes.INVOKESPECIAL, Opcodes.INVOKESTATIC, Opcodes.INVOKEINTERFACE);
        this.opcode = opcode;
        this.owner = owner;
        this.name = name;
        this.description = description;
    }

    public InvocationRule(int opcode, String owner, String name, String description) {
        super(null, Opcodes.INVOKEVIRTUAL, Opcodes.INVOKESPECIAL, Opcodes.INVOKESTATIC, Opcodes.INVOKEINTERFACE);
        this.opcode = opcode;
        this.owner = owner;
        this.name = name;
        this.description = description;
    }

    @Override
    public boolean matches(InstructionMatcher matcher, AbstractInsnNode current) {
        if (current.getOpcode() == opcode && current instanceof MethodInsnNode) {
            var casted = (MethodInsnNode) current;
            return (owner == null || owner.equals(casted.owner))
                    && (name == null || name.equals(casted.name))
                    && (description == null || description.equals(casted.desc));
        }
        return false;
    }
}
