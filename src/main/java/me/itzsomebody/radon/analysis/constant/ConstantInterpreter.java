package me.itzsomebody.radon.analysis.constant;


import me.itzsomebody.radon.analysis.constant.values.AbstractValue;
import me.itzsomebody.radon.analysis.constant.values.ConstantValue;
import me.itzsomebody.radon.analysis.constant.values.NullValue;
import me.itzsomebody.radon.analysis.constant.values.UnknownValue;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;
import org.objectweb.asm.tree.analysis.AnalyzerException;
import org.objectweb.asm.tree.analysis.Interpreter;

import java.util.List;

public class ConstantInterpreter extends Interpreter<AbstractValue> implements Opcodes {
    ConstantInterpreter(final int api) {
        super(api);
    }

    private static UnknownValue intSymbolicValue(AbstractInsnNode insnNode) {
        return new UnknownValue(insnNode, Type.INT_TYPE);
    }

    private static UnknownValue floatSymbolicValue(AbstractInsnNode insnNode) {
        return new UnknownValue(insnNode, Type.FLOAT_TYPE);
    }

    private static UnknownValue longSymbolicValue(AbstractInsnNode insnNode) {
        return new UnknownValue(insnNode, Type.LONG_TYPE);
    }

    private static UnknownValue doubleSymbolicValue(AbstractInsnNode insnNode) {
        return new UnknownValue(insnNode, Type.DOUBLE_TYPE);
    }

    private static AbstractValue valueFromArrayInsn(IntInsnNode iinsn) {
        switch (iinsn.operand) {
            case T_BOOLEAN:
                return new UnknownValue(iinsn, Type.getType("[Z"));
            case T_CHAR:
                return new UnknownValue(iinsn, Type.getType("[C"));
            case T_BYTE:
                return new UnknownValue(iinsn, Type.getType("[B"));
            case T_SHORT:
                return new UnknownValue(iinsn, Type.getType("[S"));
            case T_INT:
                return new UnknownValue(iinsn, Type.getType("[I"));
            case T_FLOAT:
                return new UnknownValue(iinsn, Type.getType("[F"));
            case T_DOUBLE:
                return new UnknownValue(iinsn, Type.getType("[D"));
            case T_LONG:
                return new UnknownValue(iinsn, Type.getType("[J"));
            default:
                throw new IllegalArgumentException("Invalid array type");
        }
    }

    public AbstractValue newValue(Type type) {
        if (type == null)
            return UnknownValue.UNINITIALIZED_VALUE;
        if (type.getSort() == Type.VOID)
            return null;
        return new UnknownValue(type);
    }

    public AbstractValue newOperation(AbstractInsnNode insnNode) {
        switch (insnNode.getOpcode()) {
            case ACONST_NULL:
                return new NullValue(insnNode);
            case ICONST_M1:
                return ConstantValue.fromInteger(insnNode, -1);
            case ICONST_0:
                return ConstantValue.fromInteger(insnNode, 0);
            case ICONST_1:
                return ConstantValue.fromInteger(insnNode, 1);
            case ICONST_2:
                return ConstantValue.fromInteger(insnNode, 2);
            case ICONST_3:
                return ConstantValue.fromInteger(insnNode, 3);
            case ICONST_4:
                return ConstantValue.fromInteger(insnNode, 4);
            case ICONST_5:
                return ConstantValue.fromInteger(insnNode, 5);
            case LCONST_0:
                return ConstantValue.fromLong(insnNode, 0);
            case LCONST_1:
                return ConstantValue.fromLong(insnNode, 1);
            case FCONST_0:
                return ConstantValue.fromFloat(insnNode, 0);
            case FCONST_1:
                return ConstantValue.fromFloat(insnNode, 1);
            case FCONST_2:
                return ConstantValue.fromFloat(insnNode, 2);
            case DCONST_0:
                return ConstantValue.fromDouble(insnNode, 0);
            case DCONST_1:
                return ConstantValue.fromDouble(insnNode, 1);
            case BIPUSH:
            case SIPUSH:
                return ConstantValue.fromInteger(insnNode, ((IntInsnNode) insnNode).operand);
            case LDC: {
                final Object cst = ((LdcInsnNode) insnNode).cst;
                if (cst instanceof Integer) {
                    return ConstantValue.fromInteger(insnNode, (Integer) cst);
                } else if (cst instanceof Float) {
                    return ConstantValue.fromFloat(insnNode, (Float) cst);
                } else if (cst instanceof Long) {
                    return ConstantValue.fromLong(insnNode, (Long) cst);
                } else if (cst instanceof Double) {
                    return ConstantValue.fromDouble(insnNode, (Double) cst);
                } else if (cst instanceof String) {
                    return ConstantValue.fromString(insnNode, (String) cst);
                } else if (cst instanceof Type) {
                    final int sort = ((Type) cst).getSort();
                    if (sort == Type.OBJECT || sort == Type.ARRAY || sort == Type.METHOD) {
                        return new UnknownValue(insnNode, ((Type) cst));
                    } else {
                        throw new IllegalArgumentException("Illegal LDC constant " + cst);
                    }
                } else {
                    throw new IllegalArgumentException("Illegal LDC constant " + cst);
                }
            }
            case JSR:
                throw new UnsupportedOperationException(
                        "Do not support instruction types JSR - Deprecated in Java 6");
            case GETSTATIC: {
                final FieldInsnNode f = (FieldInsnNode) insnNode;
                return new UnknownValue(insnNode, Type.getType(f.desc));
            }
            case NEW: {
                final TypeInsnNode type = (TypeInsnNode) insnNode;
                return new UnknownValue(insnNode, Type.getObjectType(type.desc));
            }
            default:
                throw new IllegalArgumentException("Invalid instruction opcode.");
        }
    }

    public AbstractValue copyOperation(AbstractInsnNode insnNode, AbstractValue symbolicValue) {
        symbolicValue.addUsage(insnNode);
        return new UnknownValue(symbolicValue.getInsnNode(), symbolicValue.getType());
    }

    public AbstractValue unaryOperation(AbstractInsnNode insnNode, AbstractValue symbolicValue) {
        symbolicValue.addUsage(insnNode);
        switch (insnNode.getOpcode()) {
            case INEG:
                return intSymbolicValue(insnNode);
            case LNEG:
                return longSymbolicValue(insnNode);
            case FNEG:
                return floatSymbolicValue(insnNode);
            case DNEG:
                return doubleSymbolicValue(insnNode);
            case IINC:
                return intSymbolicValue(insnNode);
            case I2L:
                return longSymbolicValue(insnNode);
            case I2F:
                return floatSymbolicValue(insnNode);
            case I2D:
                return doubleSymbolicValue(insnNode);
            case L2I:
                return intSymbolicValue(insnNode);
            case L2F:
                return floatSymbolicValue(insnNode);
            case L2D:
                return doubleSymbolicValue(insnNode);
            case F2I:
                return intSymbolicValue(insnNode);
            case F2L:
                return longSymbolicValue(insnNode);
            case F2D:
                return doubleSymbolicValue(insnNode);
            case D2I:
                return intSymbolicValue(insnNode);
            case D2L:
                return longSymbolicValue(insnNode);
            case D2F:
                return floatSymbolicValue(insnNode);
            case I2B:
                return new UnknownValue(insnNode, Type.BYTE_TYPE);
            case I2C:
                return new UnknownValue(insnNode, Type.CHAR_TYPE);
            case I2S:
                return new UnknownValue(insnNode, Type.SHORT_TYPE);
            case IFEQ:
            case IFNE:
            case IFLT:
            case IFGE:
            case IFGT:
            case IFLE:
            case TABLESWITCH:
            case LOOKUPSWITCH:
            case IRETURN:
            case LRETURN:
            case FRETURN:
            case DRETURN:
            case ARETURN:
            case PUTSTATIC:
                return null;
            case GETFIELD: {
                final FieldInsnNode f = (FieldInsnNode) insnNode;
                return new UnknownValue(insnNode, Type.getType(f.desc));
            }
            case NEWARRAY: {
                final IntInsnNode iinsn = (IntInsnNode) insnNode;
                return valueFromArrayInsn(iinsn);
            }
            case ANEWARRAY: {
                final TypeInsnNode tinsn = (TypeInsnNode) insnNode;
                return new UnknownValue(insnNode, Type.getType("[" + Type.getObjectType(tinsn.desc)));
            }
            case ARRAYLENGTH:
                return intSymbolicValue(insnNode);
            case ATHROW:
                return null;
            case CHECKCAST: {
                final TypeInsnNode tinsn = (TypeInsnNode) insnNode;
                return new UnknownValue(insnNode, Type.getObjectType(tinsn.desc));
            }
            case INSTANCEOF:
                return intSymbolicValue(insnNode);
            case MONITORENTER:
            case MONITOREXIT:
            case IFNULL:
            case IFNONNULL:
                return null;
            default:
                throw new IllegalArgumentException("Invalid instruction opcode.");
        }
    }

    public AbstractValue binaryOperation(AbstractInsnNode insnNode, AbstractValue symbolicValue1, AbstractValue symbolicValue2) throws AnalyzerException {
        symbolicValue1.addUsage(insnNode);
        symbolicValue2.addUsage(insnNode);
        switch (insnNode.getOpcode()) {
            case IALOAD:
                return intSymbolicValue(insnNode);
            case LALOAD:
                return longSymbolicValue(insnNode);
            case FALOAD:
                return floatSymbolicValue(insnNode);
            case DALOAD:
                return doubleSymbolicValue(insnNode);
            case AALOAD: {
                Type arrayType = symbolicValue1.getType();
                if (arrayType == null)
                    return new UnknownValue(insnNode, null);
                if (arrayType.getSort() != Type.ARRAY)
                    throw new AnalyzerException(insnNode, symbolicValue1.toString() + " is not array");
                return new UnknownValue(insnNode, arrayType.getElementType());
            }
            case BALOAD:
                return new UnknownValue(insnNode, Type.BYTE_TYPE);
            case CALOAD:
                return new UnknownValue(insnNode, Type.CHAR_TYPE);
            case SALOAD:
                return new UnknownValue(insnNode, Type.SHORT_TYPE);
            case IADD:
            case ISUB:
            case IMUL:
            case IDIV:
            case IREM:
            case ISHL:
            case ISHR:
            case IUSHR:
            case IAND:
            case IOR:
            case IXOR:
            case LCMP:
            case FCMPL:
            case FCMPG:
            case DCMPL:
            case DCMPG:
                return intSymbolicValue(insnNode);
            case LADD:
            case LSUB:
            case LMUL:
            case LDIV:
            case LREM:
            case LSHL:
            case LSHR:
            case LUSHR:
            case LAND:
            case LOR:
            case LXOR:
                return longSymbolicValue(insnNode);
            case FADD:
            case FSUB:
            case FMUL:
            case FDIV:
            case FREM:
                return floatSymbolicValue(insnNode);
            case DADD:
            case DSUB:
            case DMUL:
            case DDIV:
            case DREM:
                return doubleSymbolicValue(insnNode);
            case IF_ICMPEQ:
            case IF_ICMPNE:
            case IF_ICMPLT:
            case IF_ICMPGE:
            case IF_ICMPGT:
            case IF_ICMPLE:
            case IF_ACMPEQ:
            case IF_ACMPNE:
            case PUTFIELD:
                return null;
            default:
                throw new IllegalArgumentException("Invalid instruction opcode.");
        }
    }

    public AbstractValue ternaryOperation(AbstractInsnNode abstractInsnNode, AbstractValue symbolicValue1, AbstractValue symbolicValue2, AbstractValue symbolicValue3) {
        symbolicValue1.addUsage(abstractInsnNode);
        symbolicValue2.addUsage(abstractInsnNode);
        symbolicValue3.addUsage(abstractInsnNode);
        return null;
    }

    public AbstractValue naryOperation(AbstractInsnNode insnNode, List<? extends AbstractValue> list) {
        for (AbstractValue abstractValue : list) {
            abstractValue.addUsage(insnNode);
        }
        switch (insnNode.getOpcode()) {
            case INVOKEVIRTUAL:
            case INVOKESPECIAL:
            case INVOKESTATIC:
            case INVOKEINTERFACE: {
                final MethodInsnNode invoke = (MethodInsnNode) insnNode;
                return new UnknownValue(insnNode, Type.getReturnType(invoke.desc));
            }
            case INVOKEDYNAMIC: {
                final InvokeDynamicInsnNode invoke = (InvokeDynamicInsnNode) insnNode;
                return new UnknownValue(insnNode, Type.getReturnType(invoke.desc));
            }
            case MULTIANEWARRAY: {
                final MultiANewArrayInsnNode arr = (MultiANewArrayInsnNode) insnNode;
                return new UnknownValue(insnNode, Type.getType(arr.desc));
            }
            default:
                throw new IllegalArgumentException("Invalid instruction opcode.");
        }
    }

    public void returnOperation(AbstractInsnNode abstractInsnNode, AbstractValue symbolicValue, AbstractValue expectedSymbolicValue) {
        symbolicValue.addUsage(abstractInsnNode);
    }

    public AbstractValue merge(AbstractValue symbolicValue1, AbstractValue symbolicValue2) {
        if (!symbolicValue1.equals(symbolicValue2)) {
            return UnknownValue.UNINITIALIZED_VALUE;
        }
        return symbolicValue1;
    }
}
