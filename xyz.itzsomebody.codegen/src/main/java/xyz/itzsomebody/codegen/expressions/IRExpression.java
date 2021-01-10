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
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;
import xyz.itzsomebody.codegen.BytecodeBlock;
import xyz.itzsomebody.codegen.Utils;
import xyz.itzsomebody.codegen.WrappedType;
import xyz.itzsomebody.codegen.expressions.predefined.*;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

public abstract class IRExpression {
    private final WrappedType type;

    public IRExpression(WrappedType type) {
        this.type = type;
    }

    public WrappedType getType() {
        return type;
    }

    public abstract BytecodeBlock getInstructions();

    public IRExpression arrayLength() {
        return new IRArrayLengthExpression(this);
    }

    public IRExpression getArrayElement(int index) {
        return new IRGetArrayElementExpression(this, IRExpressions.intConst(index));
    }

    public IRExpression getArrayElement(IRExpression index) {
        return new IRGetArrayElementExpression(this, index);
    }

    public IRExpression setArrayElement(int index, IRExpression value) {
        return new IRSetArrayElementExpression(this, IRExpressions.intConst(index), value);
    }

    public IRExpression setArrayElement(IRExpression index, IRExpression value) {
        return new IRSetArrayElementExpression(this, index, value);
    }

    public IRExpression cast(ClassNode target) {
        return new IRCastExpression(this, WrappedType.from(target));
    }

    public IRExpression cast(Class<?> target) {
        return new IRCastExpression(this, WrappedType.from(target));
    }

    public IRExpression cast(String target) {
        return new IRCastExpression(this, WrappedType.fromInternalName(target, false));
    }

    public IRExpression cast(WrappedType type) {
        return new IRCastExpression(this, type);
    }

    public IRExpression instanceOf(ClassNode target) {
        return new IRInstanceOfExpression(this, WrappedType.from(target));
    }

    public IRExpression instanceOf(Class<?> target) {
        return new IRInstanceOfExpression(this, WrappedType.from(target));
    }

    public IRExpression instanceOf(String target) {
        return new IRInstanceOfExpression(this, WrappedType.fromInternalName(target, false));
    }

    public IRExpression instanceOf(WrappedType type) {
        return new IRInstanceOfExpression(this, type);
    }

    public IRExpression getField(String name, Class<?> type) {
        return new IRGetFieldExpression(this, getType(), name, WrappedType.from(type));
    }

    public IRExpression getField(String name, WrappedType type) {
        return new IRGetFieldExpression(this, getType(), name, type);
    }

    public IRExpression getField(FieldNode fieldNode) {
        return new IRGetFieldExpression(this, getType(), fieldNode.name, WrappedType.fromInternalName(fieldNode.desc, false));
    }

    public IRExpression getField(Field field) {
        return new IRGetFieldExpression(this, getType(), field.getName(), WrappedType.from(field.getType()));
    }

    public IRExpression setField(String name, Class<?> type, IRExpression value) {
        return new IRSetFieldExpression(this, value, getType(), name, WrappedType.from(type));
    }

    public IRExpression setField(String name, WrappedType type, IRExpression value) {
        return new IRSetFieldExpression(this, value, getType(), name, type);
    }

    public IRExpression setField(FieldNode fieldNode, IRExpression value) {
        return new IRSetFieldExpression(this, value, getType(), fieldNode.name, WrappedType.fromInternalName(fieldNode.desc, false));
    }

    public IRExpression setField(Field field, IRExpression value) {
        return new IRSetFieldExpression(this, value, getType(), field.getName(), WrappedType.from(field.getType()));
    }

    public IRExpression invoke(MethodNode methodNode, IRExpression... arguments) {
        return new IRInvocationExpression(this, getType(), methodNode.name, List.of(arguments), Utils.wrapMethodNodeParameters(methodNode), new WrappedType(Type.getReturnType(methodNode.desc)));
    }

    public IRExpression invoke(Method method, IRExpression... arguments) {
        return new IRInvocationExpression(this, getType(), method.getName(), List.of(arguments), Utils.wrapMethodParameters(method), WrappedType.from(method.getReturnType()));
    }

    public IRExpression invoke(String name, List<WrappedType> parameterTypes, WrappedType returnType, IRExpression... arguments) {
        return new IRInvocationExpression(this, getType(), name, List.of(arguments), parameterTypes, returnType);
    }

    public IRExpression ret() {
        return new IRReturnExpression(this);
    }
}
