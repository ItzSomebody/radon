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

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import xyz.itzsomebody.codegen.Utils;
import xyz.itzsomebody.codegen.WrappedType;

import java.util.List;

public class InvokeNode implements CompilableNode {
    private final int opcode;
    private final WrappedType owner;
    private final String name;
    private final List<WrappedType> parameterTypes;
    private final WrappedType returnType;

    public InvokeNode(int opcode, WrappedType owner, String name, List<WrappedType> parameterTypes, WrappedType returnType) {
        this.opcode = opcode;
        this.owner = owner;
        this.name = name;
        this.parameterTypes = parameterTypes;
        this.returnType = returnType;
    }

    @Override
    public AbstractInsnNode getNode() {
        return new MethodInsnNode(opcode, owner.getInternalName(), name, Utils.unwrapMethodDescriptor(parameterTypes, returnType), owner.isInterface());
    }

    public static InvokeNode invokeStatic(WrappedType owner, String name, List<WrappedType> parameterTypes, WrappedType returnType) {
        return new InvokeNode(Opcodes.INVOKESTATIC, owner, name, parameterTypes, returnType);
    }

    public static InvokeNode invokeVirtual(WrappedType owner, String name, List<WrappedType> parameterTypes, WrappedType returnType) {
        return new InvokeNode(Opcodes.INVOKEVIRTUAL, owner, name, parameterTypes, returnType);
    }

    public static InvokeNode invokeInterface(WrappedType owner, String name, List<WrappedType> parameterTypes, WrappedType returnType) {
        return new InvokeNode(Opcodes.INVOKEINTERFACE, owner, name, parameterTypes, returnType);
    }

    public static InvokeNode invokeSpecial(WrappedType owner, String name, List<WrappedType> parameterTypes, WrappedType returnType) {
        return new InvokeNode(Opcodes.INVOKESPECIAL, owner, name, parameterTypes, returnType);
    }

    public static InvokeNode invokeConstructor(WrappedType owner, List<WrappedType> parameterTypes) {
        return new InvokeNode(Opcodes.INVOKESPECIAL, owner, "<init>", parameterTypes, WrappedType.from(void.class));
    }
}
