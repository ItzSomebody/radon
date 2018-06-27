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

import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;

/**
 * Utils used for operating on bytecode.
 *
 * @author ItzSomebody
 */
public class BytecodeUtils {
    /**
     * Returns access modifier without private or protected so that class
     * renaming works properly.
     *
     * @param access input access as {@link Integer}.
     * @return new {@link Integer} without restrictive flags.
     */
    public static int accessFixer(int access) {
        int a = access;
        if ((a & Opcodes.ACC_PRIVATE) != 0) {
            a ^= Opcodes.ACC_PRIVATE;
        }
        if ((a & Opcodes.ACC_PROTECTED) != 0) {
            a ^= Opcodes.ACC_PROTECTED;
        }
        if ((a & Opcodes.ACC_PUBLIC) == 0) {
            a |= Opcodes.ACC_PUBLIC;
        }
        return a;
    }

    /**
     * Returns an {@link InsnList} with bytecode instructions for expiration.
     *
     * @param expiryTime a {@link Long} representation of the expiration date.
     * @return an {@link InsnList} with bytecode instructions for expiration.
     */
    public static InsnList returnExpiry(long expiryTime, String expiredMsg) {
        InsnList expiryCode = new InsnList();
        LabelNode injectedLabel = new LabelNode(new Label());

        expiryCode.add(new TypeInsnNode(Opcodes.NEW, "java/util/Date"));
        expiryCode.add(new InsnNode(Opcodes.DUP));
        expiryCode.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "java/util/Date", "<init>", "()V", false));
        expiryCode.add(new TypeInsnNode(Opcodes.NEW, "java/util/Date"));
        expiryCode.add(new InsnNode(Opcodes.DUP));
        expiryCode.add(new LdcInsnNode(expiryTime));
        expiryCode.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "java/util/Date", "<init>", "(J)V", false));
        expiryCode.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/util/Date", "after", "(Ljava/util/Date;)Z", false));
        expiryCode.add(new JumpInsnNode(Opcodes.IFEQ, injectedLabel));
        expiryCode.add(new TypeInsnNode(Opcodes.NEW, "java/lang/Throwable"));
        expiryCode.add(new InsnNode(Opcodes.DUP));
        expiryCode.add(new LdcInsnNode(expiredMsg));
        expiryCode.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "java/lang/Throwable", "<init>", "(Ljava/lang/String;)V", false));
        expiryCode.add(new InsnNode(Opcodes.ATHROW));
        expiryCode.add(injectedLabel);

        return expiryCode;
    }

    /**
     * Returns a bytecode instruction representing an int.
     *
     * @param number the {@link Integer} for the obfuscator to contemplate.
     * @return a bytecode instruction representing an int.
     */
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

    /**
     * Returns a bytecode instruction representing a long.
     *
     * @param number the {@link Long} for the obfuscator to contemplate.
     * @return a bytecode instruction representing a long.
     */
    public static AbstractInsnNode getNumberInsn(long number) {
        if (number >= 0 && number <= 1) {
            return new InsnNode((int) (number + 9));
        } else {
            return new LdcInsnNode(number);
        }
    }

    /**
     * Returns true if access has synthetic modifier.
     *
     * @param access method access to check.
     * @return true if access has synthetic modifier.
     */
    public static boolean isSynthetic(int access) {
        return (access & Opcodes.ACC_SYNTHETIC) != 0;
    }

    /**
     * Returns true if access has bridge modifier.
     *
     * @param access method access to check.
     * @return true if access has bridge modifier.
     */
    public static boolean isBridge(int access) {
        return (access & Opcodes.ACC_BRIDGE) != 0;
    }

    /**
     * Returns true if input pushes an integer.
     *
     * @param insn {@link AbstractInsnNode} to check.
     * @return true if input pushes an integer.
     */
    public static boolean isIntInsn(AbstractInsnNode insn) {
        int opcode = insn.getOpcode();
        return ((opcode >= Opcodes.ICONST_M1 && opcode <= Opcodes.ICONST_5)
                || opcode == Opcodes.BIPUSH
                || opcode == Opcodes.SIPUSH
                || (insn instanceof LdcInsnNode
                && ((LdcInsnNode) insn).cst instanceof Integer));
    }

    /**
     * Returns true if input pushes a long.
     *
     * @param insn {@link AbstractInsnNode} to check.
     * @return true if input pushes a long.
     */
    public static boolean isLongInsn(AbstractInsnNode insn) {
        int opcode = insn.getOpcode();
        return (opcode == Opcodes.LCONST_0
                || opcode == Opcodes.LCONST_1
                || (insn instanceof LdcInsnNode
                && ((LdcInsnNode) insn).cst instanceof Long));
    }

    /**
     * Returns {@link Integer} represented by bytecode instruction and/or
     * operand.
     *
     * @param insn {@link AbstractInsnNode} to check.
     * @return {@link Integer} represented by bytecode instruction and/or
     * operand.
     */
    public static int getIntNumber(AbstractInsnNode insn) {
        int opcode = insn.getOpcode();

        if (opcode >= Opcodes.ICONST_M1 && opcode <= Opcodes.ICONST_5) {
            return opcode - 3;
        } else if (insn instanceof IntInsnNode
                && insn.getOpcode() != Opcodes.NEWARRAY) {
            return ((IntInsnNode) insn).operand;
        } else if (insn instanceof LdcInsnNode
                && ((LdcInsnNode) insn).cst instanceof Integer) {
            return (Integer) ((LdcInsnNode) insn).cst;
        }

        throw new IllegalStateException("Unexpected instruction");
    }

    /**
     * Returns {@link Long} represented by bytecode instruction and/or
     * operand.
     *
     * @param insn {@link AbstractInsnNode} to check.
     * @return {@link Long} represented by bytecode instruction and/or
     * operand.
     */
    public static long getLongNumber(AbstractInsnNode insn) {
        int opcode = insn.getOpcode();

        if (opcode >= Opcodes.LCONST_0 && opcode <= Opcodes.LCONST_1) {
            return opcode - 9;
        } else if (insn instanceof LdcInsnNode
                && ((LdcInsnNode) insn).cst instanceof Long) {
            return (Long) ((LdcInsnNode) insn).cst;
        }

        throw new IllegalStateException("Unexpected instruction");
    }

    /**
     * Checks if input methodNode contains goto opcode.
     *
     * @param methodNode input methodNode.
     * @return true if input methodNode contains goto opcode.
     */
    public static boolean containsGoto(MethodNode methodNode) {
        for (int i = 0; i < methodNode.instructions.size(); i++) {
            AbstractInsnNode insn = methodNode.instructions.get(i);
            if (insn instanceof JumpInsnNode && insn.getOpcode() == Opcodes.GOTO) {
                return true;
            }
        }

        return false;
    }

    /**
     * Returns true if provided {@link MethodNode} has any annotations. Otherwise, false.
     *
     * @param methodNode {@link MethodNode} to check.
     * @return true if provided {@link MethodNode} has any annotations. Otherwise, false.
     */
    public static boolean hasAnnotations(MethodNode methodNode) {
        return (methodNode.visibleAnnotations != null && methodNode.visibleAnnotations.isEmpty());
    }

    /**
     * Returns true if provided {@link FieldNode} has any annotations. Otherwise, false.
     *
     * @param fieldNode {@link FieldNode} to check.
     * @return true if provided {@link FieldNode} has any annotations. Otherwise, false.
     */
    public static boolean hasAnnotations(FieldNode fieldNode) {
        return (fieldNode.visibleAnnotations != null && fieldNode.visibleAnnotations.isEmpty());
    }

    /**
     * Returns true if provided {@link ClassNode} has any annotations. Otherwise, false.
     *
     * @param classNode {@link ClassNode} to check.
     * @return true if provided {@link ClassNode} has any annotations. Otherwise, false.
     */
    public static boolean hasAnnotations(ClassNode classNode) {
        return (classNode.visibleAnnotations != null && classNode.visibleAnnotations.isEmpty());
    }
}
