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

package xyz.itzsomebody.codegen.expressions;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import xyz.itzsomebody.codegen.Utils;
import xyz.itzsomebody.codegen.WrappedHandle;
import xyz.itzsomebody.codegen.WrappedType;
import xyz.itzsomebody.codegen.expressions.predefined.*;
import xyz.itzsomebody.codegen.instructions.ConstantNode;
import xyz.itzsomebody.codegen.instructions.SimpleNode;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

public class IRExpressions {
    // ARITHMETIC

    public static IRExpression intAdd(IRExpression left, IRExpression right) {
        return new IRArithmeticExpression(SimpleNode.INT_ADD, left, right);
    }

    public static IRExpression intSub(IRExpression left, IRExpression right) {
        return new IRArithmeticExpression(SimpleNode.INT_SUB, left, right);
    }

    public static IRExpression intMul(IRExpression left, IRExpression right) {
        return new IRArithmeticExpression(SimpleNode.INT_MUL, left, right);
    }

    public static IRExpression intDiv(IRExpression left, IRExpression right) {
        return new IRArithmeticExpression(SimpleNode.INT_DIV, left, right);
    }

    public static IRExpression intMod(IRExpression left, IRExpression right) {
        return new IRArithmeticExpression(SimpleNode.INT_MOD, left, right);
    }

    public static IRExpression intShiftLeft(IRExpression left, IRExpression right) {
        return new IRArithmeticExpression(SimpleNode.INT_SHIFT_LEFT, left, right);
    }

    public static IRExpression intShiftRight(IRExpression left, IRExpression right) {
        return new IRArithmeticExpression(SimpleNode.INT_SHIFT_RIGHT, left, right);
    }

    public static IRExpression intUnsignedShiftRight(IRExpression left, IRExpression right) {
        return new IRArithmeticExpression(SimpleNode.INT_UNSIGNED_SHIFT_RIGHT, left, right);
    }

    public static IRExpression intAnd(IRExpression left, IRExpression right) {
        return new IRArithmeticExpression(SimpleNode.INT_AND, left, right);
    }

    public static IRExpression intOr(IRExpression left, IRExpression right) {
        return new IRArithmeticExpression(SimpleNode.INT_OR, left, right);
    }

    public static IRExpression intXor(IRExpression left, IRExpression right) {
        return new IRArithmeticExpression(SimpleNode.INT_XOR, left, right);
    }

    public static IRExpression longAdd(IRExpression left, IRExpression right) {
        return new IRArithmeticExpression(SimpleNode.LONG_ADD, left, right);
    }

    public static IRExpression longSub(IRExpression left, IRExpression right) {
        return new IRArithmeticExpression(SimpleNode.LONG_SUB, left, right);
    }

    public static IRExpression longMul(IRExpression left, IRExpression right) {
        return new IRArithmeticExpression(SimpleNode.LONG_MUL, left, right);
    }

    public static IRExpression longDiv(IRExpression left, IRExpression right) {
        return new IRArithmeticExpression(SimpleNode.LONG_DIV, left, right);
    }

    public static IRExpression longMod(IRExpression left, IRExpression right) {
        return new IRArithmeticExpression(SimpleNode.LONG_MOD, left, right);
    }

    public static IRExpression longShiftLeft(IRExpression left, IRExpression right) {
        return new IRArithmeticExpression(SimpleNode.LONG_SHIFT_LEFT, left, right);
    }

    public static IRExpression longShiftRight(IRExpression left, IRExpression right) {
        return new IRArithmeticExpression(SimpleNode.LONG_SHIFT_RIGHT, left, right);
    }

    public static IRExpression longUnsignedShiftRight(IRExpression left, IRExpression right) {
        return new IRArithmeticExpression(SimpleNode.LONG_UNSIGNED_SHIFT_RIGHT, left, right);
    }

    public static IRExpression longAnd(IRExpression left, IRExpression right) {
        return new IRArithmeticExpression(SimpleNode.LONG_AND, left, right);
    }

    public static IRExpression longOr(IRExpression left, IRExpression right) {
        return new IRArithmeticExpression(SimpleNode.LONG_OR, left, right);
    }

    public static IRExpression longXor(IRExpression left, IRExpression right) {
        return new IRArithmeticExpression(SimpleNode.LONG_XOR, left, right);
    }

    public static IRExpression floatAdd(IRExpression left, IRExpression right) {
        return new IRArithmeticExpression(SimpleNode.FLOAT_ADD, left, right);
    }

    public static IRExpression floatSub(IRExpression left, IRExpression right) {
        return new IRArithmeticExpression(SimpleNode.FLOAT_SUB, left, right);
    }

    public static IRExpression floatMul(IRExpression left, IRExpression right) {
        return new IRArithmeticExpression(SimpleNode.FLOAT_MUL, left, right);
    }

    public static IRExpression floatDiv(IRExpression left, IRExpression right) {
        return new IRArithmeticExpression(SimpleNode.FLOAT_DIV, left, right);
    }

    public static IRExpression floatMod(IRExpression left, IRExpression right) {
        return new IRArithmeticExpression(SimpleNode.FLOAT_MOD, left, right);
    }

    public static IRExpression doubleAdd(IRExpression left, IRExpression right) {
        return new IRArithmeticExpression(SimpleNode.DOUBLE_ADD, left, right);
    }

    public static IRExpression doubleSub(IRExpression left, IRExpression right) {
        return new IRArithmeticExpression(SimpleNode.DOUBLE_SUB, left, right);
    }

    public static IRExpression doubleMul(IRExpression left, IRExpression right) {
        return new IRArithmeticExpression(SimpleNode.DOUBLE_MUL, left, right);
    }

    public static IRExpression doubleDiv(IRExpression left, IRExpression right) {
        return new IRArithmeticExpression(SimpleNode.DOUBLE_DIV, left, right);
    }

    public static IRExpression doubleMod(IRExpression left, IRExpression right) {
        return new IRArithmeticExpression(SimpleNode.DOUBLE_MOD, left, right);
    }

    // ARRAYLENGTH

    public static IRExpression arrayLength(IRExpression array) {
        return new IRArrayLengthExpression(array);
    }

    // CAST

    public static IRExpression cast(IRExpression source, WrappedType targetType) {
        return new IRCastExpression(source, targetType);
    }

    // CONSTANT DYNAMIC

    public static IRExpression dynamicConst(String name, WrappedType type, WrappedHandle bootstrapMethod, List<ConstantNode> bootstrapArgs) {
        return new IRConstantDynamicExpression(name, type, bootstrapMethod, bootstrapArgs);
    }

    public static IRExpression dynamicConst(String name, WrappedType type, Method bootstrap, List<ConstantNode> bootstrapArgs) {
        return dynamicConst(name, type, WrappedHandle.getInvokeStaticHandle(bootstrap), bootstrapArgs);
    }

    // CONSTANTS

    public static IRExpression nullConst(Class<?> type) {
        return new IRConstantExpression(ConstantNode.nullConst(), WrappedType.from(type));
    }

    public static IRExpression nullConst(ClassNode type) {
        return new IRConstantExpression(ConstantNode.nullConst(), WrappedType.from(type));
    }

    public static IRExpression nullConst(WrappedType type) {
        return new IRConstantExpression(ConstantNode.nullConst(), type);
    }

    public static IRExpression booleanConst(boolean z) {
        return new IRConstantExpression(ConstantNode.booleanConst(z), WrappedType.from(boolean.class));
    }

    public static IRExpression trueConst() {
        return booleanConst(true);
    }

    public static IRExpression falseConst() {
        return booleanConst(false);
    }

    public static IRExpression intConst(int i) {
        return new IRConstantExpression(ConstantNode.intConst(i), WrappedType.from(int.class));
    }

    public static IRExpression longConst(long j) {
        return new IRConstantExpression(ConstantNode.longConst(j), WrappedType.from(long.class));
    }

    public static IRExpression floatConst(float f) {
        return new IRConstantExpression(ConstantNode.floatConst(f), WrappedType.from(float.class));
    }

    public static IRExpression doubleConst(double d) {
        return new IRConstantExpression(ConstantNode.doubleConst(d), WrappedType.from(double.class));
    }

    public static IRExpression stringConst(String str) {
        return new IRConstantExpression(ConstantNode.stringConst(str), WrappedType.from(String.class));
    }

    public static IRExpression classConst(Class<?> clazz) {
        return new IRConstantExpression(ConstantNode.classConst(WrappedType.from(clazz)), WrappedType.from(Class.class));
    }

    public static IRExpression classConst(WrappedType type) {
        return new IRConstantExpression(ConstantNode.classConst(type), WrappedType.from(Class.class));
    }

    // GET ARRAY ELEMENT

    public static IRExpression getArrayElement(IRExpression array, int index) {
        return new IRGetArrayElementExpression(array, intConst(index));
    }

    public static IRExpression getArrayElement(IRExpression array, IRExpression index) {
        return new IRGetArrayElementExpression(array, index);
    }

    // GET FIELD

    public static IRExpression getField(IRExpression instance, Field field) {
        return new IRGetFieldExpression(instance, field);
    }

    public static IRExpression getField(IRExpression instance, Class<?> owner, String name, Class<?> type) {
        return new IRGetFieldExpression(instance, WrappedType.from(owner), name, WrappedType.from(type));
    }

    public static IRExpression getField(IRExpression instance, WrappedType owner, String name, WrappedType type) {
        return new IRGetFieldExpression(instance, owner, name, type);
    }

    public static IRExpression getStatic(Field field) {
        return new IRGetFieldExpression(null, field);
    }

    public static IRExpression getStatic(Class<?> owner, String name, Class<?> type) {
        return new IRGetFieldExpression(null, WrappedType.from(owner), name, WrappedType.from(type));
    }

    public static IRExpression getStatic(WrappedType owner, String name, WrappedType type) {
        return new IRGetFieldExpression(null, owner, name, type);
    }

    // INSTANCE OF

    public static IRExpression instanceOf(IRExpression instance, Class<?> type) {
        return new IRInstanceOfExpression(instance, WrappedType.from(type));
    }

    public static IRExpression instanceOf(IRExpression instance, WrappedType type) {
        return new IRInstanceOfExpression(instance, type);
    }

    // INVOCATIONS

    public static IRExpression invokeVirtual(IRExpression instance, Method method, IRExpression... args) {
        return new IRInvocationExpression(instance, method, List.of(args));
    }

    public static IRExpression invokeVirtual(IRExpression instance, MethodNode methodNode, IRExpression... arguments) {
        return new IRInvocationExpression(instance, instance.getType(), methodNode.name, List.of(arguments), Utils.wrapMethodNodeParameters(methodNode), new WrappedType(Type.getReturnType(methodNode.desc)));
    }

    public static IRExpression invokeVirtual(IRExpression instance, String name, List<WrappedType> parameterTypes, WrappedType returnType, IRExpression... arguments) {
        return new IRInvocationExpression(instance, instance.getType(), name, List.of(arguments), parameterTypes, returnType);
    }

    public static IRExpression invokeVirtual(IRExpression instance, WrappedType owner, String name, List<IRExpression> args, List<WrappedType> argTypes, WrappedType returnType) {
        return new IRInvocationExpression(instance, owner, name, args, argTypes, returnType);
    }

    public static IRExpression invokeStatic(Method method, IRExpression... args) {
        return new IRInvocationExpression(null, method, List.of(args));
    }

    public static IRExpression invokeStatic(ClassNode owner, MethodNode methodNode, IRExpression... arguments) {
        return new IRInvocationExpression(null, WrappedType.from(owner), methodNode.name, List.of(arguments), Utils.wrapMethodNodeParameters(methodNode), new WrappedType(Type.getReturnType(methodNode.desc)));
    }

    public static IRExpression invokeStatic(String owner, String name, List<WrappedType> parameterTypes, WrappedType returnType, IRExpression... arguments) {
        return new IRInvocationExpression(null, WrappedType.fromInternalName(owner, false), name, List.of(arguments), parameterTypes, returnType);
    }

    public static IRExpression invokeStatic(WrappedType owner, String name, List<IRExpression> args, List<WrappedType> argTypes, WrappedType returnType) {
        return new IRInvocationExpression(null, owner, name, args, argTypes, returnType);
    }

    // NEGATIONS

    public static IRExpression negate(IRExpression operand) {
        return new IRNegateExpression(operand);
    }

    // NEW ARRAY

    public static IRExpression newArray(Class<?> type, IRExpression... elements) {
        return new IRNewArrayExpression(intConst(elements.length), WrappedType.from(type), elements);
    }

    public static IRExpression newArray(WrappedType type, IRExpression... elements) {
        return new IRNewArrayExpression(intConst(elements.length), type, elements);
    }

    public static IRExpression newArray(int length, Class<?> type, IRExpression... elements) {
        return new IRNewArrayExpression(intConst(length), WrappedType.from(type), elements);
    }

    public static IRExpression newArray(int length, WrappedType type, IRExpression... elements) {
        return new IRNewArrayExpression(intConst(length), type, elements);
    }

    public static IRExpression newArray(IRExpression length, Class<?> type, IRExpression... elements) {
        return new IRNewArrayExpression(length, WrappedType.from(type), elements);
    }

    public static IRExpression newArray(IRExpression length, WrappedType type, IRExpression... elements) {
        return new IRNewArrayExpression(length, type, elements);
    }

    // NEW INSTANCE (CONSTRUCTOR INVOCATIONS)

    public static IRExpression newInstance(Constructor<?> constructor, IRExpression... args) {
        return new IRNewInstanceExpression(WrappedType.from(constructor.getDeclaringClass()), Utils.wrapConstructorParameters(constructor), List.of(args));
    }

    public static IRExpression newInstance(WrappedType type, List<WrappedType> argumentTypes, List<IRExpression> arguments) {
        return new IRNewInstanceExpression(type, argumentTypes, arguments);
    }

    // RETURNS

    public static IRExpression returnMe(IRExpression operand) {
        return new IRReturnExpression(operand);
    }

    // SET ARRAY ELEMENT

    public static IRExpression setArrayElement(IRExpression array, int index, IRExpression value) {
        return new IRSetArrayElementExpression(array, intConst(index), value);
    }

    public static IRExpression setArrayElement(IRExpression array, IRExpression index, IRExpression value) {
        return new IRSetArrayElementExpression(array, index, value);
    }

    // SET FIELD

    public static IRExpression setField(IRExpression instance, Field field, IRExpression value) {
        return new IRSetFieldExpression(instance, value, field);
    }

    public static IRExpression setField(IRExpression instance, Class<?> owner, String name, Class<?> type, IRExpression value) {
        return new IRSetFieldExpression(instance, value, WrappedType.from(owner), name, WrappedType.from(type));
    }

    public static IRExpression setField(IRExpression instance, WrappedType owner, String name, WrappedType type, IRExpression value) {
        return new IRSetFieldExpression(instance, value, owner, name, type);
    }

    public static IRExpression setField(Field field, IRExpression value) {
        return new IRSetFieldExpression(null, value, field);
    }

    public static IRExpression setField(Class<?> owner, String name, Class<?> type, IRExpression value) {
        return new IRSetFieldExpression(null, value, WrappedType.from(owner), name, WrappedType.from(type));
    }

    public static IRExpression setField(WrappedType owner, String name, WrappedType type, IRExpression value) {
        return new IRSetFieldExpression(null, value, owner, name, type);
    }
}
