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

package xyz.itzsomebody.radon.utils.asm;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;

public class ASMUtils {
    public static AbstractInsnNode getNumberInsn(int number) {
        if (number >= -1 && number <= 5)
            return new InsnNode(number + 3);
        else if (number >= -128 && number <= 127)
            return new IntInsnNode(Opcodes.BIPUSH, number);
        else if (number >= -32768 && number <= 32767)
            return new IntInsnNode(Opcodes.SIPUSH, number);
        else
            return new LdcInsnNode(number);
    }

    public static AbstractInsnNode getNumberInsn(long number) {
        if (number >= 0 && number <= 1)
            return new InsnNode((int) (number + 9));
        else
            return new LdcInsnNode(number);
    }

    public static AbstractInsnNode getNumberInsn(float number) {
        if (number >= 0 && number <= 2) {
            return new InsnNode((int) (number + 11));
        } else {
            return new LdcInsnNode(number);
        }
    }

    public static AbstractInsnNode getNumberInsn(double number) {
        if (number >= 0 && number <= 1)
            return new InsnNode((int) (number + 14));
        else
            return new LdcInsnNode(number);
    }
}
