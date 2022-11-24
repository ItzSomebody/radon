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

package xyz.itzsomebody.codegen.instructions;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.JumpInsnNode;

public class JumpNode implements CompilableNode {
    private final int opcode;
    private final BytecodeLabel target;

    public JumpNode(int opcode, BytecodeLabel target) {
        this.opcode = opcode;
        this.target = target;
    }

    @Override
    public AbstractInsnNode getNode() {
        return new JumpInsnNode(opcode, target.getLabel());
    }

    public static JumpNode jumpIfZero(BytecodeLabel target) {
        return new JumpNode(Opcodes.IFEQ, target);
    }

    public static JumpNode jumpIfNotZero(BytecodeLabel target) {
        return new JumpNode(Opcodes.IFNE, target);
    }

    public static JumpNode jumpIfLessThanZero(BytecodeLabel target) {
        return new JumpNode(Opcodes.IFLT, target);
    }

    public static JumpNode jumpIfLessThanOrEqualToZero(BytecodeLabel target) {
        return new JumpNode(Opcodes.IFLE, target);
    }

    public static JumpNode jumpIfGreaterThanZero(BytecodeLabel target) {
        return new JumpNode(Opcodes.IFGT, target);
    }

    public static JumpNode jumpIfGreaterThanOrEqualToZero(BytecodeLabel target) {
        return new JumpNode(Opcodes.IFGE, target);
    }

    public static JumpNode jumpIfIntEqual(BytecodeLabel target) {
        return new JumpNode(Opcodes.IF_ICMPEQ, target);
    }

    public static JumpNode jumpIfIntNotEqual(BytecodeLabel target) {
        return new JumpNode(Opcodes.IF_ICMPNE, target);
    }

    public static JumpNode jumpIfIntLessThan(BytecodeLabel target) {
        return new JumpNode(Opcodes.IF_ICMPLT, target);
    }

    public static JumpNode jumpIfIntLessThanOrEqualToZero(BytecodeLabel target) {
        return new JumpNode(Opcodes.IF_ICMPLE, target);
    }

    public static JumpNode jumpIfIntGreaterThan(BytecodeLabel target) {
        return new JumpNode(Opcodes.IF_ICMPGT, target);
    }

    public static JumpNode jumpIfIntGreaterThanOrEqualToZero(BytecodeLabel target) {
        return new JumpNode(Opcodes.IF_ICMPGE, target);
    }

    public static JumpNode jumpIfObjectEqual(BytecodeLabel target) {
        return new JumpNode(Opcodes.IF_ACMPEQ, target);
    }

    public static JumpNode jumpIfObjectNotEqual(BytecodeLabel target) {
        return new JumpNode(Opcodes.IF_ACMPNE, target);
    }

    public static JumpNode jumpUnconditionally(BytecodeLabel target) {
        return new JumpNode(Opcodes.GOTO, target);
    }

    public static JumpNode jsr(BytecodeLabel target) {
        return new JumpNode(Opcodes.JSR, target);
    }

    public static JumpNode jumpIfNull(BytecodeLabel target) {
        return new JumpNode(Opcodes.IFNULL, target);
    }

    public static JumpNode jumpIfNotNull(BytecodeLabel target) {
        return new JumpNode(Opcodes.IFNONNULL, target);
    }
}
