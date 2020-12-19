/*
 * Radon - An open-source Java obfuscator
 * Copyright (C) 2020 ItzSomebody
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

package xyz.itzsomebody.codegen.compilable;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import xyz.itzsomebody.codegen.exceptions.UncompilableNodeException;

public class IntPushNode implements CompilableNode {
    private final int operand;

    public IntPushNode(int operand) {
        // Check for unsupported range
        if (!(operand >= Short.MIN_VALUE && operand <= Short.MAX_VALUE)) {
            throw new UncompilableNodeException("Attempted to create an IntPushNode with an operand of " + operand);
        }
        this.operand = operand;
    }

    @Override
    public AbstractInsnNode getNode() {
        return new IntInsnNode((operand >= Byte.MIN_VALUE && operand <= Byte.MAX_VALUE) ? Opcodes.BIPUSH : Opcodes.SIPUSH, operand);
    }
}
