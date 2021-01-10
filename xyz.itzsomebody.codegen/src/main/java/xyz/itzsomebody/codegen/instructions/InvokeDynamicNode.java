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

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InvokeDynamicInsnNode;
import xyz.itzsomebody.codegen.Utils;
import xyz.itzsomebody.codegen.WrappedHandle;
import xyz.itzsomebody.codegen.WrappedType;

import java.lang.reflect.Method;
import java.util.List;

public class InvokeDynamicNode implements CompilableNode {
    private final String name;
    private final List<WrappedType> parameterTypes;
    private final WrappedType returnType;
    private final WrappedHandle bootstrapMethod;
    private final List<ConstantNode> bootstrapArgs;

    public InvokeDynamicNode(String name, List<WrappedType> parameterTypes, WrappedType returnType, WrappedHandle bootstrapMethod, List<ConstantNode> bootstrapArgs) {
        this.name = name;
        this.parameterTypes = parameterTypes;
        this.returnType = returnType;
        this.bootstrapMethod = bootstrapMethod;
        this.bootstrapArgs = bootstrapArgs;
    }

    @Override
    public AbstractInsnNode getNode() {
        return new InvokeDynamicInsnNode(name, Utils.unwrapMethodDescriptor(parameterTypes, returnType), bootstrapMethod.constructHandle(), Utils.unpackConstants(bootstrapArgs));
    }

    public static InvokeDynamicNode invokeDynamic(String name, List<WrappedType> parameterTypes, WrappedType returnType, WrappedHandle bootstrapMethod, List<ConstantNode> bootstrapArgs) {
        return new InvokeDynamicNode(name, parameterTypes, returnType, bootstrapMethod, bootstrapArgs);
    }

    public static InvokeDynamicNode invokeDynamic(String name, List<WrappedType> parameterTypes, WrappedType returnType, Method bootstrap, List<ConstantNode> bootstrapArgs) {
        return invokeDynamic(name, parameterTypes, returnType, WrappedHandle.getInvokeStaticHandle(bootstrap), bootstrapArgs);
    }
}
