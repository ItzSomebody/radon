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
import xyz.itzsomebody.codegen.WrappedHandle;
import xyz.itzsomebody.codegen.WrappedType;
import xyz.itzsomebody.codegen.expressions.IRExpression;
import xyz.itzsomebody.codegen.instructions.ConstantNode;
import xyz.itzsomebody.codegen.instructions.InvokeDynamicNode;

import java.util.List;

public class IRInvokeDynamicExpression extends IRExpression {
    private final String name;
    private final List<IRExpression> args;
    private final List<WrappedType> argTypes;
    private final WrappedType returnType;
    private final WrappedHandle bootstrap;
    private final List<ConstantNode> bootstrapArgs;

    public IRInvokeDynamicExpression(String name, List<IRExpression> args, List<WrappedType> argTypes, WrappedType returnType, WrappedHandle bootstrap, List<ConstantNode> bootstrapArgs) {
        super(returnType);
        this.name = name;
        this.args = args;
        this.argTypes = argTypes;
        this.returnType = returnType;
        this.bootstrap = bootstrap;
        this.bootstrapArgs = bootstrapArgs;
    }

    @Override
    public BytecodeBlock getInstructions() {
        var block = new BytecodeBlock();
        args.forEach(arg -> block.append(arg.getInstructions()));
        block.append(InvokeDynamicNode.invokeDynamic(name, argTypes, returnType, bootstrap, bootstrapArgs));
        return block;
    }
}
