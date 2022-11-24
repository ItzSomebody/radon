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
import org.objectweb.asm.tree.FieldInsnNode;
import xyz.itzsomebody.codegen.WrappedType;

public class FieldAccessNode implements CompilableNode {
    private final int opcode;
    private final WrappedType owner;
    private final String name;
    private final WrappedType type;

    public FieldAccessNode(int opcode, WrappedType owner, String name, WrappedType type) {
        this.opcode = opcode;
        this.owner = owner;
        this.name = name;
        this.type = type;
    }

    @Override
    public AbstractInsnNode getNode() {
        return new FieldInsnNode(opcode, owner.getInternalName(), name, type.unwrap());
    }

    public static FieldAccessNode getStatic(WrappedType owner, String name, WrappedType type) {
        return new FieldAccessNode(Opcodes.GETSTATIC, owner, name, type);
    }

    public static FieldAccessNode putStatic(WrappedType owner, String name, WrappedType type) {
        return new FieldAccessNode(Opcodes.PUTSTATIC, owner, name, type);
    }

    public static FieldAccessNode getField(WrappedType owner, String name, WrappedType type) {
        return new FieldAccessNode(Opcodes.GETFIELD, owner, name, type);
    }

    public static FieldAccessNode putField(WrappedType owner, String name, WrappedType type) {
        return new FieldAccessNode(Opcodes.PUTFIELD, owner, name, type);
    }
}
