package xyz.itzsomebody.codegen;

import org.objectweb.asm.Type;

public class WrappedType {
    private final Type type;

    public WrappedType(Type type) {
        this.type = type;
    }

    public Type getType() {
        return type;
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

    public static WrappedType fromInternalName(String internalName) {
        return new WrappedType(Type.getType(internalName));
    }

    public static WrappedType from(Class<?> clazz) {
        return new WrappedType(Type.getType(clazz));
    }

    @Override
    public String toString() {
        return "WrappedType{type=" + type + '}';
    }
}
