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

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodNode;
import xyz.itzsomebody.codegen.instructions.BytecodeLabel;
import xyz.itzsomebody.codegen.instructions.ConstantNode;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class Utils {
    public static List<WrappedType> wrapMethodNodeParameters(MethodNode methodNode) {
        var wrappedTypes = new ArrayList<WrappedType>();
        List.of(Type.getArgumentTypes(methodNode.desc)).forEach(type -> wrappedTypes.add(new WrappedType(type)));
        return wrappedTypes;
    }

    public static List<WrappedType> wrapMethodParameters(Method method) {
        var wrappedTypes = new ArrayList<WrappedType>();
        List.of(method.getParameterTypes()).forEach(clazz -> wrappedTypes.add(WrappedType.from(clazz)));
        return wrappedTypes;
    }

    public static List<WrappedType> wrapConstructorParameters(Constructor<?> constructor) {
        var wrappedTypes = new ArrayList<WrappedType>();
        List.of(constructor.getParameterTypes()).forEach(clazz -> wrappedTypes.add(WrappedType.from(clazz)));
        return wrappedTypes;
    }

    public static String unwrapMethodDescriptor(List<WrappedType> parameterTypes, WrappedType returnType) {
        var sb = new StringBuilder("(");
        parameterTypes.forEach(type -> sb.append(type.unwrap()));
        sb.append(')').append(returnType.unwrap());
        return sb.toString();
    }

    public static ArrayList<LabelNode> unwrapLabels(List<BytecodeLabel> wrappedLabels) {
        var unwrappedLabels = new ArrayList<LabelNode>(wrappedLabels.size());
        wrappedLabels.forEach(wrappedLabel -> unwrappedLabels.add(wrappedLabel.getLabel()));
        return unwrappedLabels;
    }

    public static Object[] unpackConstants(List<ConstantNode> constants) {
        var unpacked = new Object[constants.size()];
        for (var i = 0; i < unpacked.length; i++) {
            unpacked[i] = constants.get(i).getValue();
        }
        return unpacked;
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
