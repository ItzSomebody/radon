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
import xyz.itzsomebody.codegen.WrappedType;
import xyz.itzsomebody.codegen.expressions.IRExpression;
import xyz.itzsomebody.codegen.instructions.InvokeNode;
import xyz.itzsomebody.codegen.instructions.SimpleNode;
import xyz.itzsomebody.codegen.instructions.TypeNode;

import java.util.List;

public class IRNewInstanceExpression extends IRExpression {
    private final List<WrappedType> argumentTypes;
    private final List<IRExpression> arguments;

    public IRNewInstanceExpression(WrappedType type, List<WrappedType> argumentTypes, List<IRExpression> arguments) {
        super(type);
        this.argumentTypes = argumentTypes;
        this.arguments = arguments;
    }

    @Override
    public BytecodeBlock getInstructions() {
        var block = new BytecodeBlock()
                .append(TypeNode.newInstance(getType()))
                .append(SimpleNode.DUP);
        for (var expr : arguments) {
            block.append(expr.getInstructions());
        }
        block.append(InvokeNode.invokeConstructor(getType(), argumentTypes));
        return block;
    }
}
