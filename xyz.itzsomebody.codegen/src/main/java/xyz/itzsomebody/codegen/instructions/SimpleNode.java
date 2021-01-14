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

package xyz.itzsomebody.codegen.instructions;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnNode;
import xyz.itzsomebody.codegen.WrappedType;
import xyz.itzsomebody.codegen.exceptions.UncompilableNodeException;

public enum SimpleNode implements CompilableNode {
    // Tux, will you nop sled with me?
    NOP(Opcodes.NOP),

    // Null push
    PUSH_NULL(Opcodes.ACONST_NULL),

    // Integer pushes
    PUSH_M1I(Opcodes.ICONST_M1),
    PUSH_0I(Opcodes.ICONST_0),
    PUSH_1I(Opcodes.ICONST_1),
    PUSH_2I(Opcodes.ICONST_2),
    PUSH_3I(Opcodes.ICONST_3),
    PUSH_4I(Opcodes.ICONST_4),
    PUSH_5I(Opcodes.ICONST_5),

    // Long pushes
    PUSH_0L(Opcodes.LCONST_0),
    PUSH_1L(Opcodes.LCONST_1),

    // Float pushes
    PUSH_0F(Opcodes.FCONST_0),
    PUSH_1F(Opcodes.FCONST_1),
    PUSH_2F(Opcodes.FCONST_2),

    // Double pushes
    PUSH_0D(Opcodes.DCONST_0),
    PUSH_1D(Opcodes.DCONST_1),

    // Array loads
    INT_ARRAY_LOAD(Opcodes.IALOAD),
    LONG_ARRAY_LOAD(Opcodes.LALOAD),
    FLOAT_ARRAY_LOAD(Opcodes.FALOAD),
    DOUBLE_ARRAY_LOAD(Opcodes.DALOAD),
    OBJECT_ARRAY_LOAD(Opcodes.AALOAD),
    BYTE_ARRAY_LOAD(Opcodes.BALOAD),
    CHAR_ARRAY_LOAD(Opcodes.CALOAD),
    SHORT_ARRAY_LOAD(Opcodes.SALOAD),

    // Array stores
    INT_ARRAY_STORE(Opcodes.IASTORE),
    LONG_ARRAY_STORE(Opcodes.LASTORE),
    FLOAT_ARRAY_STORE(Opcodes.FASTORE),
    DOUBLE_ARRAY_STORE(Opcodes.DASTORE),
    OBJECT_ARRAY_STORE(Opcodes.AASTORE),
    BYTE_ARRAY_STORE(Opcodes.BASTORE),
    CHAR_ARRAY_STORE(Opcodes.CASTORE),
    SHORT_ARRAY_STORE(Opcodes.SASTORE),

    // Stack stuff
    POP(Opcodes.POP),
    POP2(Opcodes.POP2),
    DUP(Opcodes.DUP),
    DUP_X1(Opcodes.DUP_X1),
    DUP_X2(Opcodes.DUP_X2),
    DUP2(Opcodes.DUP2),
    DUP2_X1(Opcodes.DUP2_X1),
    DUP2_X2(Opcodes.DUP2_X2),
    SWAP(Opcodes.SWAP),

    // Int math
    INT_ADD(Opcodes.IADD),
    INT_SUB(Opcodes.ISUB),
    INT_MUL(Opcodes.IMUL),
    INT_DIV(Opcodes.IDIV),
    INT_MOD(Opcodes.IREM),
    INT_NEG(Opcodes.INEG),
    INT_SHIFT_LEFT(Opcodes.ISHL),
    INT_SHIFT_RIGHT(Opcodes.ISHR),
    INT_UNSIGNED_SHIFT_RIGHT(Opcodes.IUSHR),
    INT_AND(Opcodes.IAND),
    INT_OR(Opcodes.IOR),
    INT_XOR(Opcodes.IXOR),

    // Long math
    LONG_ADD(Opcodes.LADD),
    LONG_SUB(Opcodes.LSUB),
    LONG_MUL(Opcodes.LMUL),
    LONG_DIV(Opcodes.LDIV),
    LONG_MOD(Opcodes.LREM),
    LONG_NEG(Opcodes.LNEG),
    LONG_SHIFT_LEFT(Opcodes.LSHL),
    LONG_SHIFT_RIGHT(Opcodes.LSHR),
    LONG_UNSIGNED_SHIFT_RIGHT(Opcodes.LUSHR),
    LONG_AND(Opcodes.LAND),
    LONG_OR(Opcodes.LOR),
    LONG_XOR(Opcodes.LXOR),

    // Float math
    FLOAT_ADD(Opcodes.FADD),
    FLOAT_SUB(Opcodes.FSUB),
    FLOAT_MUL(Opcodes.FMUL),
    FLOAT_DIV(Opcodes.FDIV),
    FLOAT_MOD(Opcodes.FREM),
    FLOAT_NEG(Opcodes.FNEG),

    // Double math
    DOUBLE_ADD(Opcodes.DADD),
    DOUBLE_SUB(Opcodes.DSUB),
    DOUBLE_MUL(Opcodes.DMUL),
    DOUBLE_DIV(Opcodes.DDIV),
    DOUBLE_MOD(Opcodes.DREM),
    DOUBLE_NEG(Opcodes.DNEG),

    // Int 2 XXX casts
    CAST_INT_TO_LONG(Opcodes.I2L),
    CAST_INT_TO_FLOAT(Opcodes.I2F),
    CAST_INT_TO_DOUBLE(Opcodes.I2D),
    CAST_INT_TO_BYTE(Opcodes.I2B),
    CAST_INT_TO_CHAR(Opcodes.I2C),
    CAST_INT_TO_SHORT(Opcodes.I2S),

    // Long 2 XXX casts
    CAST_LONG_TO_INT(Opcodes.L2I),
    CAST_LONG_TO_FLOAT(Opcodes.L2F),
    CAST_LONG_TO_DOUBLE(Opcodes.L2D),

    // Float 2 XXX casts
    CAST_FLOAT_TO_INT(Opcodes.F2I),
    CAST_FLOAT_TO_LONG(Opcodes.F2L),
    CAST_FLOAT_TO_DOUBLE(Opcodes.F2D),

    // Double 2 XXX casts
    CAST_DOUBLE_TO_INT(Opcodes.D2I),
    CAST_DOUBLE_TO_LONG(Opcodes.D2L),
    CAST_DOUBLE_TO_FLOAT(Opcodes.D2F),

    // Long.compare(first, second)
    COMPARE_LONGS(Opcodes.LCMP),

    // Float.compare(first, second)
    COMPARE_FLOAT_OR_M1I(Opcodes.FCMPL), // pushes -1 if 'first' or 'second' are NaN
    COMPARE_FLOAT_OR_1I(Opcodes.FCMPG), // pushes 1 if 'first' or 'second' are NaN

    // Double.compare(first, second)
    COMPARE_DOUBLE_OR_M1I(Opcodes.DCMPL), // pushes -1 if 'first' or 'second' are NaN
    COMPARE_DOUBLE_OR_1I(Opcodes.DCMPG), // pushes 1 if 'first' or 'second' are NaN

    // Returns
    RETURN_INT(Opcodes.IRETURN),
    RETURN_LONG(Opcodes.LRETURN),
    RETURN_FLOAT(Opcodes.FRETURN),
    RETURN_DOUBLE(Opcodes.DRETURN),
    RETURN_OBJECT(Opcodes.ARETURN),
    RETURN_VOID(Opcodes.RETURN),

    // array.length
    ARRAY_LENGTH(Opcodes.ARRAYLENGTH),

    // throw exceptionInstance
    THROW_EXCEPTION(Opcodes.ATHROW),

    // Monitor enter/exit
    ENTER_MONITOR(Opcodes.MONITORENTER),
    EXIT_MONITOR(Opcodes.MONITOREXIT);

    private int opcode;

    SimpleNode(int opcode) {
        this.opcode = opcode;
    }

    @Override
    public AbstractInsnNode getNode() {
        return new InsnNode(opcode);
    }

    public static SimpleNode getArrayStoreOp(WrappedType type) { // fixme maybe move to Utils?
        switch (type.getSort()) {
            case Type.BOOLEAN:
            case Type.INT:
                return INT_ARRAY_STORE;
            case Type.CHAR:
                return CHAR_ARRAY_STORE;
            case Type.BYTE:
                return BYTE_ARRAY_STORE;
            case Type.SHORT:
                return SHORT_ARRAY_STORE;
            case Type.FLOAT:
                return FLOAT_ARRAY_STORE;
            case Type.LONG:
                return LONG_ARRAY_STORE;
            case Type.DOUBLE:
                return DOUBLE_ARRAY_STORE;
            case Type.ARRAY:
            case Type.OBJECT:
                return OBJECT_ARRAY_STORE;
            default:
                throw new UncompilableNodeException("Attempted to get array store opcode for " + type);
        }
    }

    public static SimpleNode getArrayLoadOp(WrappedType type) {
        switch (type.getSort()) {
            case Type.BOOLEAN:
            case Type.INT:
                return INT_ARRAY_LOAD;
            case Type.CHAR:
                return CHAR_ARRAY_LOAD;
            case Type.BYTE:
                return BYTE_ARRAY_LOAD;
            case Type.SHORT:
                return SHORT_ARRAY_LOAD;
            case Type.FLOAT:
                return FLOAT_ARRAY_LOAD;
            case Type.LONG:
                return LONG_ARRAY_LOAD;
            case Type.DOUBLE:
                return DOUBLE_ARRAY_LOAD;
            case Type.ARRAY:
            case Type.OBJECT:
                return OBJECT_ARRAY_LOAD;
            default:
                throw new UncompilableNodeException("Attempted to get array load opcode for " + type);
        }
    }

    public static SimpleNode negateOpcodeFor(WrappedType type) {
        switch (type.getSort()) {
            case Type.BOOLEAN:
            case Type.CHAR:
            case Type.BYTE:
            case Type.SHORT:
            case Type.INT:
                return INT_NEG;
            case Type.FLOAT:
                return FLOAT_NEG;
            case Type.LONG:
                return LONG_NEG;
            case Type.DOUBLE:
                return DOUBLE_NEG;
            default:
                throw new UncompilableNodeException("Attempted to get negate opcode for " + type);
        }
    }
}
