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
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.TypeInsnNode;
import xyz.itzsomebody.codegen.WrappedType;

public class NewArrayNode implements CompilableNode {
    private final WrappedType wrappedType;

    public NewArrayNode(WrappedType wrappedType) {
        this.wrappedType = wrappedType;
    }

    @Override
    public AbstractInsnNode getNode() {
        if (wrappedType.isPrimitive()) {
            return new IntInsnNode(Opcodes.NEWARRAY, wrappedType.getNewArraySort());
        } else {
            return new TypeInsnNode(Opcodes.ANEWARRAY, wrappedType.getInternalName());
        }
    }
}
