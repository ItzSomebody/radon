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

package xyz.itzsomebody.codegen.instructions;

import org.objectweb.asm.ConstantDynamic;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import xyz.itzsomebody.codegen.Utils;
import xyz.itzsomebody.codegen.WrappedHandle;
import xyz.itzsomebody.codegen.WrappedType;
import xyz.itzsomebody.codegen.exceptions.UncompilableNodeException;

import java.lang.reflect.Method;
import java.util.List;

public abstract class ConstantNode implements CompilableNode { // todo: constants of type Handle
    public abstract Object getValue();

    public static ConstantNode nullConst() {
        return new NullConst();
    }

    public static ConstantNode booleanConst(boolean z) {
        return new BooleanConst(z);
    }

    public static ConstantNode intConst(int i) {
        return new IntConst(i);
    }

    public static ConstantNode floatConst(float f) {
        return new FloatConst(f);
    }

    public static ConstantNode longConst(long j) {
        return new LongConst(j);
    }

    public static ConstantNode doubleConst(double d) {
        return new DoubleConst(d);
    }

    public static ConstantNode stringConst(String s) {
        return new StringConst(s);
    }

    public static ConstantNode classConst(WrappedType type) {
        return new ClassConst(type);
    }

    public static ConstantNode dynamicConst(String name, WrappedType type, WrappedHandle bootstrapMethod, List<ConstantNode> bootstrapArgs) {
        return new DynamicConst(name, type, bootstrapMethod, bootstrapArgs);
    }

    public static ConstantNode dynamicConst(String name, WrappedType type, Method bootstrap, List<ConstantNode> bootstrapArgs) {
        return dynamicConst(name, type, WrappedHandle.getInvokeStaticHandle(bootstrap), bootstrapArgs);
    }

    public static class NullConst extends ConstantNode {
        @Override
        public Object getValue() {
            return null;
        }

        @Override
        public AbstractInsnNode getNode() {
            return new InsnNode(Opcodes.ACONST_NULL);
        }
    }

    public static class BooleanConst extends ConstantNode {
        private final boolean value;

        public BooleanConst(boolean value) {
            this.value = value;
        }

        @Override
        public Boolean getValue() {
            return value;
        }

        @Override
        public AbstractInsnNode getNode() {
            if (value) {
                return new InsnNode(Opcodes.ICONST_1);
            } else {
                return new InsnNode(Opcodes.ICONST_0);
            }
        }
    }

    public static class IntConst extends ConstantNode {
        private final int value;

        public IntConst(int value) {
            this.value = value;
        }

        @Override
        public Integer getValue() {
            return value;
        }

        @Override
        public AbstractInsnNode getNode() {
            if (value >= -1 && value <= 5) {
                return new InsnNode(value + 3);
            } else if (value >= Byte.MIN_VALUE && value <= Byte.MAX_VALUE) {
                return new IntInsnNode(Opcodes.BIPUSH, value);
            } else if (value >= Short.MIN_VALUE && value <= Short.MAX_VALUE) {
                return new IntInsnNode(Opcodes.SIPUSH, value);
            } else {
                return new LdcInsnNode(value);
            }
        }
    }

    public static class FloatConst extends ConstantNode {
        private final float value;

        public FloatConst(float value) {
            this.value = value;
        }

        @Override
        public Float getValue() {
            return value;
        }

        @Override
        public AbstractInsnNode getNode() {
            if (value == 0F || value == 1F || value == 2F) {
                return new InsnNode((int) value + 11);
            } else {
                return new LdcInsnNode(value);
            }
        }
    }

    public static class LongConst extends ConstantNode {
        private final long value;

        public LongConst(long value) {
            this.value = value;
        }

        @Override
        public Long getValue() {
            return value;
        }

        @Override
        public AbstractInsnNode getNode() {
            if (value == 0L || value == 1L) {
                return new InsnNode((int) value + 9);
            } else {
                return new LdcInsnNode(value);
            }
        }
    }

    public static class DoubleConst extends ConstantNode {
        private final double value;

        public DoubleConst(double value) {
            this.value = value;
        }

        @Override
        public Double getValue() {
            return value;
        }

        @Override
        public AbstractInsnNode getNode() {
            if (value == 0D || value == 1D) {
                return new InsnNode((int) value + 14);
            } else {
                return new LdcInsnNode(value);
            }
        }
    }

    public static class StringConst extends ConstantNode {
        private final String value;

        public StringConst(String value) {
            this.value = value;
        }

        @Override
        public String getValue() {
            return value;
        }

        @Override
        public AbstractInsnNode getNode() {
            return new LdcInsnNode(value);
        }
    }

    public static class ClassConst extends ConstantNode {
        private final WrappedType type;

        public ClassConst(WrappedType type) {
            this.type = type;
        }

        @Override
        public Object getValue() {
            return type;
        }

        @Override
        public AbstractInsnNode getNode() {
            return new LdcInsnNode(type.getType());
        }
    }

    public static class DynamicConst extends ConstantNode {
        private final String name;
        private final WrappedType type;
        private final WrappedHandle bootstrapMethod;
        private final List<ConstantNode> bootstrapArgs;

        public DynamicConst(String name, WrappedType type, WrappedHandle bootstrapMethod, List<ConstantNode> bootstrapArgs) {
            this.name = name;
            this.type = type;
            this.bootstrapMethod = bootstrapMethod;
            this.bootstrapArgs = bootstrapArgs;
        }

        @Override
        public AbstractInsnNode getNode() {
            return new LdcInsnNode(new ConstantDynamic(name, type.unwrap(), bootstrapMethod.constructHandle(), Utils.unpackConstants(bootstrapArgs)));
        }

        @Override
        public Object getValue() {
            return new UncompilableNodeException("Attempted to get value of dynamic constant");
        }
    }
}
