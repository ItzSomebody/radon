/*
 * Copyright (C) 2018 ItzSomebody
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

package me.itzsomebody.radon.utils;

import jdk.internal.org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.FrameNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.LineNumberNode;
import org.objectweb.asm.tree.MethodNode;

public class BytecodeUtils {
    public static boolean isInstruction(AbstractInsnNode insn) {
        return !(insn instanceof FrameNode) && !(insn instanceof LineNumberNode) && !(insn instanceof LabelNode);
    }

    public static AbstractInsnNode getNext(AbstractInsnNode node) {
        AbstractInsnNode next = node.getNext();
        while (!isInstruction(next)) {
            next = next.getNext();
        }
        return next;
    }

    public static boolean isReturn(int opcode) {
        return (opcode >= Opcodes.IRETURN && opcode <= Opcodes.RETURN);
    }

    public static boolean hasAnnotations(ClassNode classNode) {
        return (classNode.visibleAnnotations != null && !classNode.visibleAnnotations.isEmpty())
            || (classNode.invisibleAnnotations != null && !classNode.invisibleAnnotations.isEmpty());
    }

    public static boolean hasAnnotations(MethodNode methodNode) {
        return (methodNode.visibleAnnotations != null && !methodNode.visibleAnnotations.isEmpty())
            || (methodNode.invisibleAnnotations != null && !methodNode.invisibleAnnotations.isEmpty());
    }

    public static boolean hasAnnotations(FieldNode fieldNode) {
        return (fieldNode.visibleAnnotations != null && !fieldNode.visibleAnnotations.isEmpty())
            || (fieldNode.invisibleAnnotations != null && !fieldNode.invisibleAnnotations.isEmpty());
    }

    public static boolean isIntInsn(AbstractInsnNode insn) {
        if (insn == null) {
            return false;
        }
        int opcode = insn.getOpcode();
        return ((opcode >= org.objectweb.asm.Opcodes.ICONST_M1 && opcode <= org.objectweb.asm.Opcodes.ICONST_5)
            || opcode == org.objectweb.asm.Opcodes.BIPUSH
            || opcode == org.objectweb.asm.Opcodes.SIPUSH
            || (insn instanceof LdcInsnNode
            && ((LdcInsnNode) insn).cst instanceof Integer));
    }

    public static boolean isLongInsn(AbstractInsnNode insn) {
        int opcode = insn.getOpcode();
        return (opcode == org.objectweb.asm.Opcodes.LCONST_0
            || opcode == org.objectweb.asm.Opcodes.LCONST_1
            || (insn instanceof LdcInsnNode
            && ((LdcInsnNode) insn).cst instanceof Long));
    }

    public static AbstractInsnNode getNumberInsn(int number) {
        if (number >= -1 && number <= 5) {
            return new InsnNode(number + 3);
        } else if (number >= -128 && number <= 127) {
            return new IntInsnNode(Opcodes.BIPUSH, number);
        } else if (number >= -32768 && number <= 32767) {
            return new IntInsnNode(Opcodes.SIPUSH, number);
        } else {
            return new LdcInsnNode(number);
        }
    }

    public static AbstractInsnNode getNumberInsn(long number) {
        if (number >= 0 && number <= 1) {
            return new InsnNode((int) (number + 9));
        } else {
            return new LdcInsnNode(number);
        }
    }

    public static AbstractInsnNode getNumberInsn(float number) {
        if (number >= 0 && number <= 2) {
            return new InsnNode((int) (number + 11));
        } else {
            return new LdcInsnNode(number);
        }
    }

    public static AbstractInsnNode getNumberInsn(double number) {
        if (number >= 0 && number <= 1) {
            return new InsnNode((int) (number + 14));
        } else {
            return new LdcInsnNode(number);
        }
    }

    public static int getIntNumber(AbstractInsnNode insn) {
        int opcode = insn.getOpcode();

        if (opcode >= org.objectweb.asm.Opcodes.ICONST_M1 && opcode <= org.objectweb.asm.Opcodes.ICONST_5) {
            return opcode - 3;
        } else if (insn instanceof IntInsnNode
            && insn.getOpcode() != org.objectweb.asm.Opcodes.NEWARRAY) {
            return ((IntInsnNode) insn).operand;
        } else if (insn instanceof LdcInsnNode
            && ((LdcInsnNode) insn).cst instanceof Integer) {
            return (Integer) ((LdcInsnNode) insn).cst;
        }

        throw new IllegalArgumentException("Unexpected instruction");
    }

    public static long getLongNumber(AbstractInsnNode insn) {
        int opcode = insn.getOpcode();

        if (opcode >= org.objectweb.asm.Opcodes.LCONST_0 && opcode <= org.objectweb.asm.Opcodes.LCONST_1) {
            return opcode - 9;
        } else if (insn instanceof LdcInsnNode
            && ((LdcInsnNode) insn).cst instanceof Long) {
            return (Long) ((LdcInsnNode) insn).cst;
        }

        throw new IllegalArgumentException("Unexpected instruction");
    }
}
