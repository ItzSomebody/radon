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
    public static int makePublic(int access) {
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
    public static InsnList createExpiry(long expiryTime, String expiredMsg) {
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
    public static AbstractInsnNode createNumberInsn(int number) {
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
    public static AbstractInsnNode createNumberInsn(long number) {
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
        return methodNode.visibleAnnotations != null && !methodNode.visibleAnnotations.isEmpty();
    }

    /**
     * Returns true if provided {@link FieldNode} has any annotations. Otherwise, false.
     *
     * @param fieldNode {@link FieldNode} to check.
     * @return true if provided {@link FieldNode} has any annotations. Otherwise, false.
     */
    public static boolean hasAnnotations(FieldNode fieldNode) {
        return fieldNode.visibleAnnotations != null && !fieldNode.visibleAnnotations.isEmpty();
    }

    /**
     * Returns true if provided {@link ClassNode} has any annotations. Otherwise, false.
     *
     * @param classNode {@link ClassNode} to check.
     * @return true if provided {@link ClassNode} has any annotations. Otherwise, false.
     */
    public static boolean hasAnnotations(ClassNode classNode) {
        return classNode.visibleAnnotations != null && !classNode.visibleAnnotations.isEmpty();
    }

    // TODO: Unused, this can be removed.
    /**
     * Returns a {@link String} representation of inputted opcode.
     *
     * @param opcode the opcode to get a name from.
     * @return a {@link String} representation of inputted opcode.
     */
    public static String getOpcodeName(int opcode) {
        switch (opcode) {
            case Opcodes.NOP:
                return "nop";
            case Opcodes.ACONST_NULL:
                return "aconst_null";
            case Opcodes.ICONST_M1:
                return "iconst_m1";
            case Opcodes.ICONST_0:
                return "iconst_0";
            case Opcodes.ICONST_1:
                return "iconst_1";
            case Opcodes.ICONST_2:
                return "iconst_2";
            case Opcodes.ICONST_3:
                return "iconst_3";
            case Opcodes.ICONST_4:
                return "iconst_4";
            case Opcodes.ICONST_5:
                return "iconst_5";
            case Opcodes.LCONST_0:
                return "lconst_0";
            case Opcodes.LCONST_1:
                return "lconst_1";
            case Opcodes.FCONST_0:
                return "fconst_0";
            case Opcodes.FCONST_1:
                return "fconst_1";
            case Opcodes.FCONST_2:
                return "fconst_2";
            case Opcodes.DCONST_0:
                return "dconst_0";
            case Opcodes.DCONST_1:
                return "dconst_1";
            case Opcodes.BIPUSH:
                return "bipush";
            case Opcodes.SIPUSH:
                return "sipush";
            case Opcodes.LDC:
                return "ldc";
            case Opcodes.ILOAD:
                return "iload";
            case Opcodes.LLOAD:
                return "lload";
            case Opcodes.FLOAD:
                return "fload";
            case Opcodes.DLOAD:
                return "dload";
            case Opcodes.ALOAD:
                return "aload";
            case Opcodes.IALOAD:
                return "iaload";
            case Opcodes.LALOAD:
                return "laload";
            case Opcodes.FALOAD:
                return "faload";
            case Opcodes.DALOAD:
                return "daload";
            case Opcodes.AALOAD:
                return "aaload";
            case Opcodes.BALOAD:
                return "baload";
            case Opcodes.CALOAD:
                return "caload";
            case Opcodes.SALOAD:
                return "saload";
            case Opcodes.ISTORE:
                return "istore";
            case Opcodes.LSTORE:
                return "lstore";
            case Opcodes.FSTORE:
                return "fstore";
            case Opcodes.DSTORE:
                return "dstore";
            case Opcodes.ASTORE:
                return "astore";
            case Opcodes.IASTORE:
                return "iastore";
            case Opcodes.LASTORE:
                return "lastore";
            case Opcodes.FASTORE:
                return "fastore";
            case Opcodes.DASTORE:
                return "dastore";
            case Opcodes.AASTORE:
                return "aastore";
            case Opcodes.BASTORE:
                return "bastore";
            case Opcodes.CASTORE:
                return "castore";
            case Opcodes.SASTORE:
                return "sastore";
            case Opcodes.POP:
                return "pop";
            case Opcodes.POP2:
                return "pop2";
            case Opcodes.DUP:
                return "dup";
            case Opcodes.DUP_X1:
                return "dup_x1";
            case Opcodes.DUP_X2:
                return "dup_x2";
            case Opcodes.DUP2:
                return "dup2";
            case Opcodes.DUP2_X1:
                return "dup2_x1";
            case Opcodes.DUP2_X2:
                return "dup2_x2";
            case Opcodes.SWAP:
                return "swap";
            case Opcodes.IADD:
                return "iadd";
            case Opcodes.LADD:
                return "ladd";
            case Opcodes.FADD:
                return "fadd";
            case Opcodes.DADD:
                return "dadd";
            case Opcodes.ISUB:
                return "isub";
            case Opcodes.LSUB:
                return "lsub";
            case Opcodes.FSUB:
                return "fsub";
            case Opcodes.DSUB:
                return "dsub";
            case Opcodes.IMUL:
                return "imul";
            case Opcodes.LMUL:
                return "lmul";
            case Opcodes.FMUL:
                return "fmul";
            case Opcodes.DMUL:
                return "dmul";
            case Opcodes.IDIV:
                return "idiv";
            case Opcodes.LDIV:
                return "ldiv";
            case Opcodes.FDIV:
                return "fdiv";
            case Opcodes.DDIV:
                return "ddiv";
            case Opcodes.IREM:
                return "irem";
            case Opcodes.LREM:
                return "lrem";
            case Opcodes.FREM:
                return "frem";
            case Opcodes.DREM:
                return "drem";
            case Opcodes.INEG:
                return "ineg";
            case Opcodes.LNEG:
                return "lneg";
            case Opcodes.FNEG:
                return "fneg";
            case Opcodes.DNEG:
                return "dneg";
            case Opcodes.ISHL:
                return "ishl";
            case Opcodes.LSHL:
                return "lshl";
            case Opcodes.ISHR:
                return "ishr";
            case Opcodes.LSHR:
                return "lshr";
            case Opcodes.IUSHR:
                return "iushr";
            case Opcodes.LUSHR:
                return "lushr";
            case Opcodes.IAND:
                return "iand";
            case Opcodes.LAND:
                return "land";
            case Opcodes.IOR:
                return "ior";
            case Opcodes.LOR:
                return "lor";
            case Opcodes.IXOR:
                return "ixor";
            case Opcodes.LXOR:
                return "lxor";
            case Opcodes.IINC:
                return "iinc";
            case Opcodes.I2L:
                return "i2l";
            case Opcodes.I2F:
                return "i2f";
            case Opcodes.I2D:
                return "i2d";
            case Opcodes.L2I:
                return "l2i";
            case Opcodes.L2F:
                return "l2f";
            case Opcodes.L2D:
                return "l2d";
            case Opcodes.F2I:
                return "f2i";
            case Opcodes.F2L:
                return "f2l";
            case Opcodes.F2D:
                return "f2d";
            case Opcodes.D2I:
                return "d2i";
            case Opcodes.D2L:
                return "d2l";
            case Opcodes.D2F:
                return "d2f";
            case Opcodes.I2B:
                return "i2b";
            case Opcodes.I2C:
                return "i2c";
            case Opcodes.I2S:
                return "i2s";
            case Opcodes.LCMP:
                return "lcmp";
            case Opcodes.FCMPL:
                return "fcmpl";
            case Opcodes.FCMPG:
                return "fcmpg";
            case Opcodes.DCMPL:
                return "dcmpl";
            case Opcodes.DCMPG:
                return "dcmpg";
            case Opcodes.IFEQ:
                return "ifeq";
            case Opcodes.IFNE:
                return "ifne";
            case Opcodes.IFLT:
                return "iflt";
            case Opcodes.IFGE:
                return "ifge";
            case Opcodes.IFGT:
                return "ifgt";
            case Opcodes.IFLE:
                return "ifle";
            case Opcodes.IF_ICMPEQ:
                return "if_icmpeq";
            case Opcodes.IF_ICMPNE:
                return "if_icmpne";
            case Opcodes.IF_ICMPLT:
                return "if_icmplt";
            case Opcodes.IF_ICMPGE:
                return "if_icmpge";
            case Opcodes.IF_ICMPGT:
                return "if_icmpgt";
            case Opcodes.IF_ICMPLE:
                return "if_icmple";
            case Opcodes.IF_ACMPEQ:
                return "if_acmpeq";
            case Opcodes.IF_ACMPNE:
                return "if_acmpne";
            case Opcodes.GOTO:
                return "goto";
            case Opcodes.JSR:
                return "jsr";
            case Opcodes.RET:
                return "ret";
            case Opcodes.TABLESWITCH:
                return "tableswitch";
            case Opcodes.LOOKUPSWITCH:
                return "lookupswitch";
            case Opcodes.IRETURN:
                return "ireturn";
            case Opcodes.LRETURN:
                return "lreturn";
            case Opcodes.FRETURN:
                return "freturn";
            case Opcodes.DRETURN:
                return "dreturn";
            case Opcodes.ARETURN:
                return "areturn";
            case Opcodes.RETURN:
                return "return";
            case Opcodes.GETSTATIC:
                return "getstatic";
            case Opcodes.PUTSTATIC:
                return "putstatic";
            case Opcodes.GETFIELD:
                return "getfield";
            case Opcodes.PUTFIELD:
                return "putfield";
            case Opcodes.INVOKEVIRTUAL:
                return "invokevirtual";
            case Opcodes.INVOKESPECIAL:
                return "invokespecial";
            case Opcodes.INVOKESTATIC:
                return "invokestatic";
            case Opcodes.INVOKEINTERFACE:
                return "invokeinterface";
            case Opcodes.INVOKEDYNAMIC:
                return "invokedynamic";
            case Opcodes.NEW:
                return "new";
            case Opcodes.NEWARRAY:
                return "newarray";
            case Opcodes.ANEWARRAY:
                return "anewarray";
            case Opcodes.ARRAYLENGTH:
                return "arraylength";
            case Opcodes.ATHROW:
                return "athrow";
            case Opcodes.CHECKCAST:
                return "checkcast";
            case Opcodes.INSTANCEOF:
                return "instanceof";
            case Opcodes.MONITORENTER:
                return "monitorenter";
            case Opcodes.MONITOREXIT:
                return "monitorexit";
            case Opcodes.MULTIANEWARRAY:
                return "multianewarray";
            case Opcodes.IFNULL:
                return "ifnull";
            case Opcodes.IFNONNULL:
                return "ifnonnull";
            case -1:
                return "debugging";
        }
        throw new IllegalArgumentException("Unknown opcode");
    }
}
