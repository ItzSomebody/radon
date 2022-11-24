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

package xyz.itzsomebody.codegen.expressions.predefined;

import xyz.itzsomebody.codegen.BytecodeBlock;
import xyz.itzsomebody.codegen.Utils;
import xyz.itzsomebody.codegen.WrappedType;
import xyz.itzsomebody.codegen.expressions.IRExpression;
import xyz.itzsomebody.codegen.instructions.InvokeNode;

import java.lang.reflect.Method;
import java.util.List;

public class IRInvocationExpression extends IRExpression {
    private final IRExpression instance;
    private final WrappedType owner;
    private final String name;
    private final List<IRExpression> args;
    private final List<WrappedType> argTypes;

    public IRInvocationExpression(IRExpression instance, WrappedType owner, String name, List<IRExpression> args, List<WrappedType> argTypes, WrappedType returnType) {
        super(returnType);
        this.instance = instance;
        this.owner = owner;
        this.name = name;
        this.args = args;
        this.argTypes = argTypes;
    }

    public IRInvocationExpression(IRExpression instance, Method method, List<IRExpression> args) {
        this(instance, WrappedType.from(method.getDeclaringClass()), method.getName(), args, Utils.wrapMethodParameters(method), WrappedType.from(method.getReturnType()));
    }

    @Override
    public BytecodeBlock getInstructions() {
        var block = new BytecodeBlock();

        if (instance != null) {
            block.append(instance.getInstructions());
        }

        args.forEach(arg -> block.append(arg.getInstructions()));

        if (instance == null) {
            block.append(InvokeNode.invokeStatic(owner, name, argTypes, getType()));
        } else if (owner.isInterface()) {
            block.append(InvokeNode.invokeInterface(owner, name, argTypes, getType()));
        } else {
            block.append(InvokeNode.invokeVirtual(owner, name, argTypes, getType()));
        }

        return block;
    }
}
