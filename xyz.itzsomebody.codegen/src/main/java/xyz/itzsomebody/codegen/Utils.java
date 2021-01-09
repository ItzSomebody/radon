package xyz.itzsomebody.codegen;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.LabelNode;
import xyz.itzsomebody.codegen.instructions.BytecodeLabel;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class Utils {
    public static List<WrappedType> wrapMethodDescriptor(Method method) {
        var wrappedTypes = new ArrayList<WrappedType>();
        List.of(method.getParameterTypes()).forEach(clazz -> wrappedTypes.add(WrappedType.from(clazz)));
        return wrappedTypes;
    }

    public static String unwrapMethodDescriptor(List<WrappedType> parameterTypes, WrappedType returnType) {
        var sb = new StringBuilder("(");
        parameterTypes.forEach(type -> sb.append(type.unwrap()));
        sb.append(')').append(returnType.unwrap());
        return sb.toString();
    }

    public static ArrayList<LabelNode> unwrapLabels(ArrayList<BytecodeLabel> wrappedLabels) {
        var unwrappedLabels = new ArrayList<LabelNode>(wrappedLabels.size());
        wrappedLabels.forEach(wrappedLabel -> unwrappedLabels.add(wrappedLabel.getLabel()));
        return unwrappedLabels;
    }

    public static WrappedType box(WrappedType primitive) {
        if (!primitive.isPrimitive()) {
            throw new IllegalArgumentException("Attempted to box non-primitive type: " + primitive);
        }

        switch (primitive.getSort()) {
            case Type.BOOLEAN:
                return WrappedType.from(Boolean.class);
            case Type.CHAR:
                return WrappedType.from(Character.class);
            case Type.BYTE:
                return WrappedType.from(Byte.class);
            case Type.SHORT:
                return WrappedType.from(Short.class);
            case Type.INT:
                return WrappedType.from(Integer.class);
            case Type.LONG:
                return WrappedType.from(Long.class);
            case Type.FLOAT:
                return WrappedType.from(Float.class);
            case Type.DOUBLE:
                return WrappedType.from(Double.class);
            default:
                throw new IllegalArgumentException("Unknown primitive type: " + primitive);
        }
    }
}
