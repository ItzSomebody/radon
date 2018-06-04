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

package me.itzsomebody.radon.analyzer;

import java.util.EmptyStackException;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InvokeDynamicInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.MultiANewArrayInsnNode;

/**
 * Attempts to emulate the stack in a method up to a breakpoint.
 *
 * @author ItzSomebody
 */
public class StackAnalyzer implements Opcodes {
    /**
     * {@link MethodNode} we are checking.
     */
    private MethodNode methodNode;
    /**
     * {@link AbstractInsnNode} opcode which is the breakpoint.
     */
    private AbstractInsnNode breakPoint;
    /**
     * Debug boolean;
     */
    private boolean DEBUG = false;

    /**
     * Constructor to create a {@link StackAnalyzer} object.
     *
     * @param methodNode the method node we want to check.
     * @param breakPoint the opcode we want to break on.
     */
    public StackAnalyzer(MethodNode methodNode, AbstractInsnNode breakPoint) {
        this.methodNode = methodNode;
        this.breakPoint = breakPoint;
    }

    /**
     * Returns a simulated stack of the Java bytecode instructions up to the
     * specified breakpoint.
     *
     * @return a simulated stack of the Java bytecode instructions up to the
     * specified breakpoint.
     */
    public Stack<Object> returnStackAtBreak() {
        if (DEBUG)
            System.out.println("Entering " + this.methodNode.owner + '.'
                    + this.methodNode.name + this.methodNode.desc);
        Stack<Object> stack = new Stack<>(); // Simulated stack
        Set<LabelNode> excHandlers = new HashSet<>();
        methodNode.tryCatchBlocks.forEach(tryCatchBlockNode -> {
            excHandlers.add(tryCatchBlockNode.handler);
        });
        for (int i = 0; i < this.methodNode.instructions.size(); i++) {
            AbstractInsnNode insn = this.methodNode.instructions.get(i);
            if (insn instanceof LabelNode
                    && excHandlers.contains(insn)) {
                stack.clear(); // Stack gets cleared and exception is pushed.
                stack.push(null);
            }
            if (this.breakPoint == insn)
                break;
            try {
                switch (insn.getOpcode()) {
                    case NOP:
                    case LALOAD: // (index, arrayref) -> (long, long_top)
                    case DALOAD: // (index, arrayref) -> (double, double_top)
                    case SWAP: // (value1, value2) -> (value2, value1)
                    case INEG:
                    case LNEG:
                    case FNEG:
                    case DNEG:
                    case IINC:
                    case I2F:
                    case L2D:
                    case F2I:
                    case D2L:
                    case I2B:
                    case I2C:
                    case I2S:
                    case GOTO:
                    case RET:
                    case RETURN:
                    case NEWARRAY:
                    case ANEWARRAY:
                    case ARRAYLENGTH:
                    case CHECKCAST:
                    case INSTANCEOF: {
                        // Does nothing
                        break;
                    }
                    case ACONST_NULL:
                    case ICONST_M1:
                    case ICONST_0:
                    case ICONST_1:
                    case ICONST_2:
                    case ICONST_3:
                    case ICONST_4:
                    case ICONST_5:
                    case FCONST_0:
                    case FCONST_1:
                    case FCONST_2:
                    case BIPUSH:
                    case SIPUSH:
                    case ILOAD:
                    case FLOAD:
                    case ALOAD:
                    case DUP:
                    case DUP_X1:
                    case DUP_X2:
                    case I2L:
                    case I2D:
                    case F2L:
                    case F2D:
                    case JSR:
                    case NEW: {
                        // Pushes one-word constant to stack
                        stack.push(null);
                        break;
                    }
                    case LDC: {
                        LdcInsnNode ldc = (LdcInsnNode) insn;
                        if (ldc.cst instanceof Long || ldc.cst instanceof Double)
                            stack.push(null);

                        stack.push(null);
                        break;
                    }
                    case LCONST_0:
                    case LCONST_1:
                    case DCONST_0:
                    case DCONST_1:
                    case LLOAD:
                    case DLOAD:
                    case DUP2:
                    case DUP2_X1:
                    case DUP2_X2: {
                        // Pushes two-word constant or two one-word constants to stack
                        stack.push(null);
                        stack.push(null);
                        break;
                    }
                    case IALOAD: // (index, arrayref) -> (int)
                    case FALOAD: // (index, arrayref) -> (float)
                    case AALOAD: // (index, arrayref) -> (Object)
                    case BALOAD: // (index, arrayref) -> (byte)
                    case CALOAD: // (index, arrayref) -> (char)
                    case SALOAD: // (index, arrayref) -> (short)
                    case ISTORE:
                    case FSTORE:
                    case ASTORE:
                    case POP:
                    case IADD:
                    case FADD:
                    case ISUB:
                    case FSUB:
                    case IMUL:
                    case FMUL:
                    case IDIV:
                    case FDIV:
                    case IREM:
                    case FREM:
                    case ISHL:
                    case ISHR:
                    case IUSHR:
                    case LSHL:
                    case LSHR:
                    case LUSHR:
                    case IAND:
                    case IOR:
                    case IXOR:
                    case L2I:
                    case L2F:
                    case D2I:
                    case D2F:
                    case FCMPL:
                    case FCMPG:
                    case IFEQ:
                    case IFNE:
                    case IFLT:
                    case IFGE:
                    case IFGT:
                    case IFLE:
                    case TABLESWITCH:
                    case LOOKUPSWITCH:
                    case IRETURN:
                    case FRETURN:
                    case ATHROW:
                    case MONITORENTER:
                    case MONITOREXIT:
                    case IFNULL:
                    case IFNONNULL:
                    case ARETURN: {
                        // Pops one-word constant off stack
                        stack.pop();
                        break;
                    }
                    case LSTORE:
                    case DSTORE:
                    case POP2:
                    case LADD:
                    case DADD:
                    case LSUB:
                    case DSUB:
                    case LMUL:
                    case DMUL:
                    case LDIV:
                    case DDIV:
                    case LREM:
                    case DREM:
                    case LAND:
                    case LOR:
                    case LXOR:
                    case IF_ICMPEQ:
                    case IF_ICMPNE:
                    case IF_ICMPLT:
                    case IF_ICMPGE:
                    case IF_ICMPGT:
                    case IF_ICMPLE:
                    case IF_ACMPEQ:
                    case IF_ACMPNE:
                    case LRETURN:
                    case DRETURN: {
                        // Pops two-word or two one-word constant(s) off stack
                        stack.pop();
                        stack.pop();
                        break;
                    }
                    case IASTORE:
                    case FASTORE:
                    case AASTORE:
                    case BASTORE:
                    case CASTORE:
                    case SASTORE:
                    case LCMP:
                    case DCMPL:
                    case DCMPG: {
                        // Pops three one-word constants off stack
                        stack.pop();
                        stack.pop();
                        stack.pop();
                        break;
                    }
                    case LASTORE:
                    case DASTORE: {
                        // Pops two one-word constants and one two-word constant off stack
                        stack.pop();
                        stack.pop();
                        stack.pop();
                        stack.pop();
                        break;
                    }
                    case GETSTATIC: {
                        Type type = Type.getType(((FieldInsnNode) insn).desc);
                        stack.push(null);

                        if (type.getSort() == Type.LONG || type.getSort() == Type.DOUBLE)
                            stack.push(null);
                        break;
                    }
                    case PUTSTATIC: {
                        Type type = Type.getType(((FieldInsnNode) insn).desc);
                        stack.pop();

                        if (type.getSort() == Type.LONG || type.getSort() == Type.DOUBLE)
                            stack.pop();
                        break;
                    }
                    case GETFIELD: {
                        stack.pop(); // Objectref
                        Type type = Type.getType(((FieldInsnNode) insn).desc);
                        stack.push(null);

                        if (type.getSort() == Type.LONG || type.getSort() == Type.DOUBLE)
                            stack.push(null);
                        break;
                    }
                    case PUTFIELD: {
                        stack.pop(); // Objectref
                        Type type = Type.getType(((FieldInsnNode) insn).desc);
                        stack.pop();

                        if (type.getSort() == Type.LONG || type.getSort() == Type.DOUBLE)
                            stack.pop();
                        break;
                    }
                    case INVOKEVIRTUAL:
                    case INVOKESPECIAL:
                    case INVOKEINTERFACE: {
                        stack.pop(); // Objectref
                        Type[] args = Type.getArgumentTypes(((MethodInsnNode) insn).desc);
                        Type returnType = Type.getReturnType(((MethodInsnNode) insn).desc);
                        for (Type type : args) {
                            if (type.getSort() == Type.LONG
                                    || type.getSort() == Type.DOUBLE)
                                stack.pop();

                            stack.pop();
                        }
                        if (returnType.getSort() == Type.LONG
                                || returnType.getSort() == Type.DOUBLE)
                            stack.push(null);
                        if (returnType.getSort() != Type.VOID)
                            stack.push(null);
                        break;
                    }
                    case INVOKESTATIC: {
                        Type[] args = Type.getArgumentTypes(((MethodInsnNode) insn).desc);
                        Type returnType = Type.getReturnType(((MethodInsnNode) insn).desc);
                        for (Type type : args) {
                            if (type.getSort() == Type.LONG
                                    || type.getSort() == Type.DOUBLE)
                                stack.pop();

                            stack.pop();
                        }
                        if (returnType.getSort() == Type.LONG
                                || returnType.getSort() == Type.DOUBLE)
                            stack.push(null);
                        if (returnType.getSort() != Type.VOID)
                            stack.push(null);
                        break;
                    }
                    case INVOKEDYNAMIC: {
                        Type[] args = Type.getArgumentTypes(((InvokeDynamicInsnNode) insn).desc);
                        Type returnType = Type.getReturnType(((InvokeDynamicInsnNode) insn).desc);
                        for (Type type : args) {
                            if (type.getSort() == Type.LONG
                                    || type.getSort() == Type.DOUBLE)
                                stack.pop();

                            stack.pop();
                        }
                        if (returnType.getSort() == Type.LONG
                                || returnType.getSort() == Type.DOUBLE)
                            stack.push(null);
                        if (returnType.getSort() != Type.VOID)
                            stack.push(null);
                        break;
                    }
                    case MULTIANEWARRAY: {
                        for (int j = 0; j < ((MultiANewArrayInsnNode) insn).dims; j++) {
                            stack.pop();
                        }

                        stack.push(null); // arrayref
                        break;
                    }
                }
            } catch (EmptyStackException empty) {
                if (DEBUG) empty.printStackTrace();
            }
        }
        return stack;
    }
}