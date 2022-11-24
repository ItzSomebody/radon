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

package xyz.itzsomebody.codegen.instructions;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.TypeInsnNode;
import xyz.itzsomebody.codegen.WrappedType;

public class TypeNode implements CompilableNode {
    private final int opcode;
    private final WrappedType type;

    public TypeNode(int opcode, WrappedType type) {
        this.opcode = opcode;
        this.type = type;
    }

    @Override
    public AbstractInsnNode getNode() {
        return new TypeInsnNode(opcode, type.getInternalName());
    }

    public static TypeNode newInstance(WrappedType type) {
        return new TypeNode(Opcodes.NEW, type);
    }

    public static TypeNode cast(WrappedType type) {
        return new TypeNode(Opcodes.CHECKCAST, type);
    }

    public static TypeNode instanceOf(WrappedType type) {
        return new TypeNode(Opcodes.INSTANCEOF, type);
    }
}
