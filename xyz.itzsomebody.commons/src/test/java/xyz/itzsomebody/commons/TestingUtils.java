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

package xyz.itzsomebody.commons;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;

public class TestingUtils {
    public static AbstractInsnNode loadInt(int i) {
        if (i >= -1 && i <= 5) {
            return new InsnNode(i + 3);
        } else if (i >= Byte.MIN_VALUE && i <= Byte.MAX_VALUE) {
            return new IntInsnNode(Opcodes.BIPUSH, i);
        } else if (i >= Short.MIN_VALUE && i <= Short.MAX_VALUE) {
            return new IntInsnNode(Opcodes.SIPUSH, i);
        } else {
            return new LdcInsnNode(i);
        }
    }

    public static AbstractInsnNode loadLong(long j) {
        if (j == 0L || j == 1L) {
            return new InsnNode((int) j + 9);
        } else {
            return new LdcInsnNode(j);
        }
    }

    public static AbstractInsnNode loadFloat(float f) {
        if (f == 0F || f == 1F || f == 2F) {
            return new InsnNode((int) f + 11);
        } else {
            return new LdcInsnNode(f);
        }
    }

    public static AbstractInsnNode loadDouble(double d) {
        if (d == 0D || d == 1D) {
            return new InsnNode((int) d + 14);
        } else {
            return new LdcInsnNode(d);
        }
    }
}
