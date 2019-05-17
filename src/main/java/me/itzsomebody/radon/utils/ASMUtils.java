/*
 * Radon - An open-source Java obfuscator
 * Copyright (C) 2019 ItzSomebody
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

import me.itzsomebody.radon.exceptions.RadonException;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.FrameNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.LineNumberNode;
import org.objectweb.asm.tree.MethodNode;

/**
 * Bytecode utilities for bytecode instructions.
 *
 * @author ItzSomebody.
 */
public class ASMUtils {
    private static final Type STRING_TYPE = Type.getType(String.class);

    public static boolean isInstruction(AbstractInsnNode insn) {
        return !(insn instanceof FrameNode) && !(insn instanceof LineNumberNode) && !(insn instanceof LabelNode);
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
        return ((opcode >= Opcodes.ICONST_M1 && opcode <= Opcodes.ICONST_5)
                || opcode == Opcodes.BIPUSH
                || opcode == Opcodes.SIPUSH
                || (insn instanceof LdcInsnNode
                && ((LdcInsnNode) insn).cst instanceof Integer));
    }

    public static boolean isLongInsn(AbstractInsnNode insn) {
        int opcode = insn.getOpcode();
        return (opcode == Opcodes.LCONST_0
                || opcode == Opcodes.LCONST_1
                || (insn instanceof LdcInsnNode
                && ((LdcInsnNode) insn).cst instanceof Long));
    }

    public static boolean isFloatInsn(AbstractInsnNode insn) {
        int opcode = insn.getOpcode();
        return (opcode >= Opcodes.FCONST_0 && opcode <= Opcodes.FCONST_2)
                || (insn instanceof LdcInsnNode && ((LdcInsnNode) insn).cst instanceof Float);
    }

    public static boolean isDoubleInsn(AbstractInsnNode insn) {
        int opcode = insn.getOpcode();
        return (opcode >= Opcodes.DCONST_0 && opcode <= Opcodes.DCONST_1)
                || (insn instanceof LdcInsnNode && ((LdcInsnNode) insn).cst instanceof Double);
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

    public static int getIntegerFromInsn(AbstractInsnNode insn) {
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

        throw new RadonException("Unexpected instruction");
    }

    public static long getLongFromInsn(AbstractInsnNode insn) {
        int opcode = insn.getOpcode();

        if (opcode >= Opcodes.LCONST_0 && opcode <= Opcodes.LCONST_1) {
            return opcode - 9;
        } else if (insn instanceof LdcInsnNode
                && ((LdcInsnNode) insn).cst instanceof Long) {
            return (Long) ((LdcInsnNode) insn).cst;
        }

        throw new RadonException("Unexpected instruction");
    }

    public static float getFloatFromInsn(AbstractInsnNode insn) {
        int opcode = insn.getOpcode();

        if (opcode >= Opcodes.FCONST_0 && opcode <= Opcodes.FCONST_2) {
            return opcode - 11;
        } else if (insn instanceof LdcInsnNode
                && ((LdcInsnNode) insn).cst instanceof Float) {
            return (Float) ((LdcInsnNode) insn).cst;
        }

        throw new RadonException("Unexpected instruction");
    }

    public static double getDoubleFromInsn(AbstractInsnNode insn) {
        int opcode = insn.getOpcode();

        if (opcode >= Opcodes.DCONST_0 && opcode <= Opcodes.DCONST_1) {
            return opcode - 14;
        } else if (insn instanceof LdcInsnNode
                && ((LdcInsnNode) insn).cst instanceof Double) {
            return (Double) ((LdcInsnNode) insn).cst;
        }

        throw new RadonException("Unexpected instruction");
    }

    public static String getGenericMethodDesc(String desc) {
        Type returnType = Type.getReturnType(desc);
        Type[] args = Type.getArgumentTypes(desc);
        for (int i = 0; i < args.length; i++) {
            Type arg = args[i];

            if (arg.getSort() == Type.OBJECT)
                args[i] = Type.getType("Ljava/lang/Object;");
        }

        return Type.getMethodDescriptor(returnType, args);
    }

    public static int getReturnOpcode(Type type) {
        switch (type.getSort()) {
            case Type.BOOLEAN:
            case Type.CHAR:
            case Type.BYTE:
            case Type.SHORT:
            case Type.INT:
                return Opcodes.IRETURN;
            case Type.FLOAT:
                return Opcodes.FRETURN;
            case Type.LONG:
                return Opcodes.LRETURN;
            case Type.DOUBLE:
                return Opcodes.DRETURN;
            case Type.ARRAY:
            case Type.OBJECT:
                return Opcodes.ARETURN;
            case Type.VOID:
                return Opcodes.RETURN;
            default:
                throw new AssertionError("Unknown type sort: " + type.getClassName());
        }
    }

    public static int getVarOpcode(Type type, boolean store) {
        switch (type.getSort()) {
            case Type.BOOLEAN:
            case Type.CHAR:
            case Type.BYTE:
            case Type.SHORT:
            case Type.INT:
                return store ? Opcodes.ISTORE : Opcodes.ILOAD;
            case Type.FLOAT:
                return store ? Opcodes.FSTORE : Opcodes.FLOAD;
            case Type.LONG:
                return store ? Opcodes.LSTORE : Opcodes.LLOAD;
            case Type.DOUBLE:
                return store ? Opcodes.DSTORE : Opcodes.DLOAD;
            case Type.ARRAY:
            case Type.OBJECT:
                return store ? Opcodes.ASTORE : Opcodes.ALOAD;
            default:
                throw new AssertionError("Unknown type: " + type.getClassName());
        }
    }

    public static InsnList asList(AbstractInsnNode abstractInsnNode, AbstractInsnNode... abstractInsnNodes) {
        InsnList insnList = new InsnList();
        insnList.add(abstractInsnNode);
        if (abstractInsnNodes != null)
            for (AbstractInsnNode insnNode : abstractInsnNodes) {
                insnList.add(insnNode);
            }
        return insnList;
    }

    public static InsnList singletonList(AbstractInsnNode abstractInsnNode) {
        InsnList insnList = new InsnList();
        insnList.add(abstractInsnNode);
        return insnList;
    }

    public static AbstractInsnNode getDefaultValue(Type type) {
        switch (type.getSort()) {
            case Type.BOOLEAN:
            case Type.CHAR:
            case Type.BYTE:
            case Type.SHORT:
            case Type.INT:
                return ASMUtils.getNumberInsn(0);
            case Type.FLOAT:
                return ASMUtils.getNumberInsn(0f);
            case Type.LONG:
                return ASMUtils.getNumberInsn(0L);
            case Type.DOUBLE:
                return ASMUtils.getNumberInsn(0d);
            case Type.OBJECT:
                return new InsnNode(Opcodes.ACONST_NULL);
            default:
                throw new AssertionError();
        }
    }

    public static AbstractInsnNode getRandomValue(Type type) {
        switch (type.getSort()) {
            case Type.BOOLEAN:
                return ASMUtils.getNumberInsn(RandomUtils.getRandomInt(0, 2));
            case Type.CHAR:
                return ASMUtils.getNumberInsn(RandomUtils.getRandomInt(Character.MIN_VALUE, Character.MAX_VALUE));
            case Type.BYTE:
                return ASMUtils.getNumberInsn(RandomUtils.getRandomInt(Byte.MIN_VALUE, Byte.MAX_VALUE));
            case Type.SHORT:
                return ASMUtils.getNumberInsn(RandomUtils.getRandomInt(Short.MIN_VALUE, Short.MAX_VALUE));
            case Type.INT:
                return ASMUtils.getNumberInsn(RandomUtils.getRandomInt());
            case Type.FLOAT:
                return ASMUtils.getNumberInsn(RandomUtils.getRandomFloat());
            case Type.LONG:
                return ASMUtils.getNumberInsn(RandomUtils.getRandomLong());
            case Type.DOUBLE:
                return ASMUtils.getNumberInsn(RandomUtils.getRandomDouble());
            case Type.ARRAY:
            case Type.OBJECT:
                if (STRING_TYPE.equals(type) && RandomUtils.getRandomBoolean()) {
                    return new LdcInsnNode(StringUtils.randomUnrecognizedString(RandomUtils.getRandomInt(3, 10)));
                }
                return new InsnNode(Opcodes.ACONST_NULL);
            default:
                throw new AssertionError();
        }
    }
}
