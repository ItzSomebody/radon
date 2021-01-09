package xyz.itzsomebody.codegen;

import org.objectweb.asm.Type;
import xyz.itzsomebody.codegen.exceptions.UncompilableNodeException;

public class WrappedType {
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

    public boolean isArray() {
        return type.getSize() == Type.ARRAY;
    }

    public String unwrap() {
        if (type.getSort() == Type.OBJECT || type.getSort() == Type.ARRAY) {
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

    public static WrappedType fromClassName(String className) {
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
                internalName.append(className.replace('.', '/'));
        }

        return new WrappedType(Type.getType(internalName.toString()));
    }

    public static WrappedType fromInternalName(String internalName, boolean isInterface) {
        return new WrappedType(Type.getType(internalName), isInterface);
    }

    public static WrappedType from(Class<?> clazz) {
        return new WrappedType(Type.getType(clazz), clazz.isInterface());
    }

    @Override
    public String toString() {
        return "WrappedType{type=" + type + '}';
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
        public boolean isArray() {
            throw new UncompilableNodeException("Attempted to determine array flag of AbsentWrappedType");
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
