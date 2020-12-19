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
import org.objectweb.asm.tree.VarInsnNode;

public class RegisterNode implements CompilableNode {
    private final int opcode;
    private final int slot;

    public RegisterNode(int opcode, int slot) {
        this.opcode = opcode;
        this.slot = slot;
    }

    @Override
    public AbstractInsnNode getNode() {
        return new VarInsnNode(opcode, slot);
    }

    public static RegisterNode loadInt(int slot) {
        return new RegisterNode(Opcodes.ILOAD, slot);
    }

    public static RegisterNode loadLong(int slot) {
        return new RegisterNode(Opcodes.LLOAD, slot);
    }

    public static RegisterNode loadFloat(int slot) {
        return new RegisterNode(Opcodes.FLOAD, slot);
    }

    public static RegisterNode loadDouble(int slot) {
        return new RegisterNode(Opcodes.DLOAD, slot);
    }

    public static RegisterNode loadObject(int slot) {
        return new RegisterNode(Opcodes.ALOAD, slot);
    }

    public static RegisterNode storeInt(int slot) {
        return new RegisterNode(Opcodes.ISTORE, slot);
    }

    public static RegisterNode storeLong(int slot) {
        return new RegisterNode(Opcodes.LSTORE, slot);
    }

    public static RegisterNode storeFloat(int slot) {
        return new RegisterNode(Opcodes.FSTORE, slot);
    }

    public static RegisterNode storeDouble(int slot) {
        return new RegisterNode(Opcodes.DSTORE, slot);
    }

    public static RegisterNode storeObject(int slot) {
        return new RegisterNode(Opcodes.ASTORE, slot);
    }

    public static RegisterNode ret(int slot) {
        return new RegisterNode(Opcodes.RET, slot);
    }
}
