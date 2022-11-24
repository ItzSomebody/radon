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

package xyz.itzsomebody.codegen;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import xyz.itzsomebody.codegen.exceptions.UncompilableNodeException;

import java.util.HashMap;
import java.util.Map;

public class WrappedType {
    private static final Map<String, Class<?>> BOXED_TYPES = new HashMap<>() {
        {
            put(Type.getInternalName(Boolean.class), boolean.class);
            put(Type.getInternalName(Character.class), char.class);
            put(Type.getInternalName(Byte.class), byte.class);
            put(Type.getInternalName(Short.class), short.class);
            put(Type.getInternalName(Integer.class), int.class);
            put(Type.getInternalName(Float.class), float.class);
            put(Type.getInternalName(Long.class), long.class);
            put(Type.getInternalName(Double.class), double.class);
        }
    };
    private static AbsentWrappedType absent;
    private final Type type;
    private final boolean isInterface;

    public WrappedType(Type type) {
        this.type = type;
        this.isInterface = false;
    }

    public WrappedType(Type type, boolean isInterface) {
        this.type = type;
        this.isInterface = isInterface;
    }

    public static AbsentWrappedType getAbsent() {
        if (absent == null) {
            absent = new AbsentWrappedType();
        }

        return absent;
    }

    public Type getType() {
        return type;
    }

    public boolean isInterface() {
        return isInterface;
    }

    public int getSort() {
        return type.getSort();
    }

    /**
     * For NEWARRAY instructions
     */
    public int getNewArraySort() {
        switch (type.getSort()) {
            case Type.BOOLEAN:
                return Opcodes.T_BOOLEAN;
            case Type.CHAR:
                return Opcodes.T_CHAR;
            case Type.FLOAT:
                return Opcodes.T_FLOAT;
            case Type.DOUBLE:
                return Opcodes.T_DOUBLE;
            case Type.BYTE:
                return Opcodes.T_BYTE;
            case Type.SHORT:
                return Opcodes.T_SHORT;
            case Type.INT:
                return Opcodes.T_INT;
            case Type.LONG:
                return Opcodes.T_LONG;
            default:
                throw new UncompilableNodeException("Attempted to get primitive array type of " + this);
        }
    }

    public boolean isPrimitive() {
        switch (type.getSort()) {
            case Type.BOOLEAN:
            case Type.CHAR:
            case Type.BYTE:
            case Type.SHORT:
            case Type.INT:
            case Type.FLOAT:
            case Type.LONG:
            case Type.DOUBLE:
                return true;
            default:
                return false;
        }
    }

    public WrappedType getPrimitiveType() {
        var primitiveClass = BOXED_TYPES.get(getInternalName());
        if (primitiveClass == null) {
            throw new UncompilableNodeException("Attempted to get primitive type of " + this);
        }

        return WrappedType.from(primitiveClass);
    }

    public boolean isBoxed() {
        return BOXED_TYPES.containsKey(getInternalName());
    }

    public boolean isArray() {
        return type.getSort() == Type.ARRAY;
    }

    public boolean isIntType() {
        var sort = getSort();
        return (sort == Type.BOOLEAN)
                || (sort == Type.CHAR)
                || (sort == Type.BYTE)
                || (sort == Type.SHORT)
                || (sort == Type.INT);
    }

    public String unwrap() {
        if (type.getSort() == Type.OBJECT) {
            return 'L' + type.getInternalName() + ';';
        } else {
            return type.getInternalName();
        }
    }

    public String getInternalName() {
        return type.getInternalName();
    }

    public String getClassName() {
        return type.getClassName();
    }

    public static WrappedType fromClassName(String className, boolean isInterface) {
        StringBuilder internalName = new StringBuilder();

        if (className.endsWith("[]")) {
            className = className.substring(0, className.length() - 2);
            internalName.append('[');
        }
        switch (className) {
            case "int":
                internalName.append("I");
                break;
            case "long":
                internalName.append("J");
                break;
            case "float":
                internalName.append("F");
                break;
            case "double":
                internalName.append("D");
                break;
            case "boolean":
                internalName.append("Z");
                break;
            case "byte":
                internalName.append("B");
                break;
            case "short":
                internalName.append("S");
                break;
            case "char":
                internalName.append("C");
                break;
            default:
                internalName.append('L').append(className.replace('.', '/')).append(';');
        }

        return new WrappedType(Type.getType(internalName.toString()), isInterface);
    }

    public static WrappedType fromInternalName(String internalName, boolean isInterface) {
        if ("ZBCSIJFDV".contains(internalName) || internalName.startsWith("[")) {
            return new WrappedType(Type.getType(internalName), isInterface);
        } else {
            return new WrappedType(Type.getType("L" + internalName + ";"), isInterface);
        }
    }

    public static WrappedType from(Class<?> clazz) {
        return new WrappedType(Type.getType(clazz), clazz.isInterface());
    }

    public static WrappedType from(ClassNode classNode) {
        return new WrappedType(Type.getType("L" + classNode.name + ";"), (classNode.access & Opcodes.ACC_INTERFACE) != 0);
    }

    @Override
    public String toString() {
        return "WrappedType{" +
                "type=" + type +
                ", isInterface=" + isInterface +
                '}';
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof WrappedType)) {
            return false;
        }

        return getType().equals(((WrappedType) other).getType());
    }

    static class AbsentWrappedType extends WrappedType {
        private AbsentWrappedType() {
            super(null);
        }

        @Override
        public Type getType() {
            throw new UncompilableNodeException("Attempted to get type of AbsentWrappedType");
        }

        @Override
        public boolean isInterface() {
            throw new UncompilableNodeException("Attempted to determine interface flag of AbsentWrappedType");
        }

        @Override
        public int getSort() {
            throw new UncompilableNodeException("Attempted to get sort of AbsentWrappedType");
        }

        @Override
        public boolean isPrimitive() {
            throw new UncompilableNodeException("Attempted to determine primitive flag of AbsentWrappedType");
        }

        @Override
        public boolean isBoxed() {
            throw new UncompilableNodeException("Attempted to determine boxed flag of AbsentWrappedType");
        }

        @Override
        public boolean isArray() {
            throw new UncompilableNodeException("Attempted to determine array flag of AbsentWrappedType");
        }

        @Override
        public boolean isIntType() {
            throw new UncompilableNodeException("Attempted to determine isIntType flag of AbsentWrappedType");
        }

        @Override
        public String unwrap() {
            throw new UncompilableNodeException("Attempted to unwrap AbsentWrappedType");
        }

        @Override
        public String getInternalName() {
            throw new UncompilableNodeException("Attempted to get internal name of AbsentWrappedType");
        }

        @Override
        public String getClassName() {
            throw new UncompilableNodeException("Attempted to get class name of AbsentWrappedType");
        }
    }
}
