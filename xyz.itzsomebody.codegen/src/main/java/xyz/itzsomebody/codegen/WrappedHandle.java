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

import org.objectweb.asm.Handle;
import org.objectweb.asm.Opcodes;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

public class WrappedHandle {
    private final int tag;
    private final WrappedType owner;
    private final String name;
    private final List<WrappedType> parameterTypes;
    private final WrappedType returnType;

    public WrappedHandle(int tag, WrappedType owner, String name, List<WrappedType> parameterTypes, WrappedType returnType) {
        this.tag = tag;
        this.owner = owner;
        this.name = name;
        this.parameterTypes = parameterTypes;
        this.returnType = returnType;
    }

    public int getTag() {
        return tag;
    }

    public WrappedType getOwner() {
        return owner;
    }

    public String getName() {
        return name;
    }

    public List<WrappedType> getParameterTypes() {
        return parameterTypes;
    }

    public WrappedType getReturnType() {
        return returnType;
    }

    public Handle constructHandle() {
        return new Handle(tag, owner.getInternalName(), name, Utils.unwrapMethodDescriptor(parameterTypes, returnType), owner.isInterface());
    }

    // Fields

    public static WrappedHandle getFieldHandle(WrappedType owner, String name, WrappedType type) {
        return new WrappedHandle(Opcodes.H_GETFIELD, owner, name, Collections.emptyList(), type);
    }

    public static WrappedHandle getFieldHandle(Field field) {
        return getFieldHandle(WrappedType.from(field.getDeclaringClass()), field.getName(), WrappedType.from(field.getType()));
    }

    public static WrappedHandle getStaticHandle(WrappedType owner, String name, WrappedType type) {
        return new WrappedHandle(Opcodes.H_GETSTATIC, owner, name, Collections.emptyList(), type);
    }

    public static WrappedHandle getStaticHandle(Field field) {
        return getFieldHandle(WrappedType.from(field.getDeclaringClass()), field.getName(), WrappedType.from(field.getType()));
    }

    public static WrappedHandle putFieldHandle(WrappedType owner, String name, WrappedType type) {
        return new WrappedHandle(Opcodes.H_PUTFIELD, owner, name, Collections.emptyList(), type);
    }

    public static WrappedHandle putFieldHandle(Field field) {
        return getFieldHandle(WrappedType.from(field.getDeclaringClass()), field.getName(), WrappedType.from(field.getType()));
    }

    public static WrappedHandle putStaticHandle(WrappedType owner, String name, WrappedType type) {
        return new WrappedHandle(Opcodes.H_PUTSTATIC, owner, name, Collections.emptyList(), type);
    }

    public static WrappedHandle putStaticHandle(Field field) {
        return getFieldHandle(WrappedType.from(field.getDeclaringClass()), field.getName(), WrappedType.from(field.getType()));
    }

    // Methods

    public static WrappedHandle getInvokeVirtualHandle(WrappedType owner, String name, List<WrappedType> parameterTypes, WrappedType returnType) {
        return new WrappedHandle(Opcodes.H_INVOKEVIRTUAL, owner, name, parameterTypes, returnType);
    }

    public static WrappedHandle getInvokeVirtualHandle(Method method) {
        return getInvokeVirtualHandle(WrappedType.from(method.getDeclaringClass()), method.getName(), Utils.wrapMethodParameters(method), WrappedType.from(method.getReturnType()));
    }

    public static WrappedHandle getInvokeStaticHandle(WrappedType owner, String name, List<WrappedType> parameterTypes, WrappedType returnType) {
        return new WrappedHandle(Opcodes.H_INVOKESTATIC, owner, name, parameterTypes, returnType);
    }

    public static WrappedHandle getInvokeStaticHandle(Method method) {
        return getInvokeStaticHandle(WrappedType.from(method.getDeclaringClass()), method.getName(), Utils.wrapMethodParameters(method), WrappedType.from(method.getReturnType()));
    }

    public static WrappedHandle getInvokeSpecialHandle(WrappedType owner, String name, List<WrappedType> parameterTypes, WrappedType returnType) {
        return new WrappedHandle(Opcodes.H_INVOKESPECIAL, owner, name, parameterTypes, returnType);
    }

    public static WrappedHandle getInvokeSpecialHandle(Method method) {
        return getInvokeSpecialHandle(WrappedType.from(method.getDeclaringClass()), method.getName(), Utils.wrapMethodParameters(method), WrappedType.from(method.getReturnType()));
    }

    public static WrappedHandle getNewInvokeSpecialHandle(WrappedType owner, String name, List<WrappedType> parameterTypes, WrappedType returnType) {
        return new WrappedHandle(Opcodes.H_NEWINVOKESPECIAL, owner, name, parameterTypes, returnType);
    }

    public static WrappedHandle getNewInvokeSpecialHandle(Method method) {
        return getNewInvokeSpecialHandle(WrappedType.from(method.getDeclaringClass()), method.getName(), Utils.wrapMethodParameters(method), WrappedType.from(method.getReturnType()));
    }

    public static WrappedHandle getInvokeInterfaceHandle(WrappedType owner, String name, List<WrappedType> parameterTypes, WrappedType returnType) {
        return new WrappedHandle(Opcodes.H_INVOKEVIRTUAL, owner, name, parameterTypes, returnType);
    }

    public static WrappedHandle getInvokeInterfaceHandle(Method method) {
        return getInvokeInterfaceHandle(WrappedType.from(method.getDeclaringClass()), method.getName(), Utils.wrapMethodParameters(method), WrappedType.from(method.getReturnType()));
    }
}
