package me.itzsomebody.radon.analyzer;

import me.itzsomebody.radon.utils.OpcodeUtils;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

import java.util.EmptyStackException;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

/**
 * Attempts to make a virtual machine-like object which attempts to mimic the
 * JVM stack.
 * TODO: MAKE IT ACTUALLY WORK LOL
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
                    case ACONST_NULL:
                    case ICONST_M1:
                    case ICONST_0:
                    case ICONST_1:
                    case ICONST_2:
                    case ICONST_3:
                    case ICONST_4:
                    case ICONST_5:
                    case LCONST_0:
                    case LCONST_1:
                    case FCONST_0:
                    case FCONST_1:
                    case FCONST_2:
                    case DCONST_0:
                    case DCONST_1:
                    case BIPUSH:
                    case SIPUSH:
                    case LDC:
                    case ILOAD:
                    case LLOAD:
                    case FLOAD:
                    case DLOAD:
                    case ALOAD:
                    case IALOAD:
                    case LALOAD:
                    case FALOAD:
                    case DALOAD:
                    case AALOAD:
                    case BALOAD:
                    case CALOAD:
                    case SALOAD:
                    case DUP:
                    case DUP_X1:
                    case DUP_X2:
                    case NEW:
                    case GETSTATIC:
                    case GETFIELD:
                        if (DEBUG)
                            System.out.println("Pushing - Opcode = " +
                                    OpcodeUtils.getOpcodeName(insn.getOpcode()));
                        stack.push(null);
                        break;
                    case ISTORE:
                    case LSTORE:
                    case FSTORE:
                    case DSTORE:
                    case ASTORE:
                    case IASTORE:
                    case LASTORE:
                    case DASTORE:
                    case AASTORE:
                    case BASTORE:
                    case CASTORE:
                    case SASTORE:
                    case POP:
                    case SWAP:
                    case IADD:
                    case LADD:
                    case FADD:
                    case DADD:
                    case ISUB:
                    case LSUB:
                    case FSUB:
                    case DSUB:
                    case IMUL:
                    case LMUL:
                    case FMUL:
                    case DMUL:
                    case IDIV:
                    case LDIV:
                    case FDIV:
                    case DDIV:
                    case IREM:
                    case LREM:
                    case FREM:
                    case DREM:
                    case ISHL:
                    case LSHL:
                    case ISHR:
                    case LSHR:
                    case IUSHR:
                    case LUSHR:
                    case IAND:
                    case LAND:
                    case IOR:
                    case LOR:
                    case IXOR:
                    case LXOR:
                    case LCMP:
                    case FCMPL:
                    case FCMPG:
                    case DCMPL:
                    case DCMPG:
                    case TABLESWITCH:
                    case LOOKUPSWITCH:
                    case IRETURN:
                    case LRETURN:
                    case FRETURN:
                    case DRETURN:
                    case ARETURN:
                    case PUTSTATIC:
                    case PUTFIELD:
                    case MONITORENTER:
                    case MONITOREXIT:
                    case IFEQ:
                    case IFNE:
                    case IFLT:
                    case IFGE:
                    case IFGT:
                    case IFLE:
                    case IF_ICMPEQ:
                    case IF_ICMPNE:
                    case IF_ICMPLT:
                    case IF_ICMPGE:
                    case IF_ICMPGT:
                    case IF_ICMPLE:
                    case IF_ACMPEQ:
                    case IF_ACMPNE:
                    case IFNULL:
                    case IFNONNULL:
                        if (DEBUG)
                            System.out.println("Popping - Opcode = " +
                                    OpcodeUtils.getOpcodeName(insn.getOpcode()));
                        stack.pop();
                        break;
                    case POP2:
                        if (DEBUG)
                            System.out.println("Double popping - Opcode = " +
                                    OpcodeUtils.getOpcodeName(insn.getOpcode()));
                        stack.pop();
                        stack.pop();
                        break;
                    case DUP2:
                    case DUP2_X1:
                    case DUP2_X2:
                        if (DEBUG)
                            System.out.println("Double pushing - Opcode = " +
                                    OpcodeUtils.getOpcodeName(insn.getOpcode()));
                        stack.push(null);
                        stack.push(null);
                        break;
                    case INEG:
                    case LNEG:
                    case FNEG:
                    case DNEG:
                    case IINC:
                    case I2L:
                    case I2F:
                    case I2D:
                    case L2I:
                    case L2F:
                    case L2D:
                    case F2I:
                    case F2L:
                    case F2D:
                    case D2I:
                    case D2L:
                    case D2F:
                    case I2B:
                    case I2C:
                    case I2S:
                    case RETURN:
                    case NEWARRAY:
                    case ANEWARRAY:
                    case ARRAYLENGTH:
                    case ATHROW:
                    case CHECKCAST:
                    case INSTANCEOF:
                    case GOTO:
                        if (DEBUG)
                            System.out.println("Doing nothing - Opcode = " +
                                    OpcodeUtils.getOpcodeName(insn.getOpcode()));
                        break;
                    case JSR:
                    case RET:
                        throw new IllegalArgumentException("Unsupported " +
                                "opcode (JSR/RET)");
                    case INVOKEVIRTUAL:
                    case INVOKESPECIAL:
                    case INVOKEINTERFACE:
                        if (DEBUG)
                            System.out.println("Processing static method " +
                                    "invocation - Opcode = " + OpcodeUtils
                                    .getOpcodeName(insn.getOpcode()));
                        MethodInsnNode virtualInvoke = (MethodInsnNode) insn;
                        stack.pop(); // Objectref
                        for (int j = 0; j < Type.getArgumentTypes
                                (virtualInvoke.desc).length; j++) {
                            stack.pop();
                        }
                        if (!virtualInvoke.desc.endsWith(")V")) {
                            stack.push(null);
                        }
                        break;
                    case INVOKESTATIC:
                        if (DEBUG)
                            System.out.println("Processing virtual method " +
                                    "invocation - Opcode = " + OpcodeUtils
                                    .getOpcodeName(insn.getOpcode()));
                        MethodInsnNode staticInvoke = (MethodInsnNode) insn;
                        for (int j = 0; j < Type.getArgumentTypes
                                (staticInvoke.desc).length; j++) {
                            stack.pop();
                        }
                        if (!staticInvoke.desc.endsWith(")V")) {
                            stack.push(null);
                        }
                        break;
                    case INVOKEDYNAMIC:
                        if (DEBUG)
                            System.out.println("Processing dynamic invocation" +
                                    " - Opcode = " + OpcodeUtils
                                    .getOpcodeName(insn.getOpcode()));
                        InvokeDynamicInsnNode indy =
                                (InvokeDynamicInsnNode) insn;
                        for (int j = 0; j < Type.getArgumentTypes(indy.desc)
                                .length; j++) {
                            stack.pop();
                        }
                        if (!indy.desc.endsWith(")V")) {
                            stack.push(null);
                        }
                        break;
                    case MULTIANEWARRAY:
                        if (DEBUG)
                            System.out.println("Processing multi-dimension " +
                                    "array - Opcode = " + OpcodeUtils.
                                    getOpcodeName(insn.getOpcode()));
                        MultiANewArrayInsnNode arrays
                                = (MultiANewArrayInsnNode) insn;
                        for (int j = 0; j < arrays.dims; j++) {
                            stack.pop();
                        }
                        stack.push(null); // Arrayref
                        break;
                }
            } catch (EmptyStackException empty) {
                if (DEBUG) empty.printStackTrace();
            }
        }
        return stack;
    }
}