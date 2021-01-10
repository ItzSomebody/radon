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
import xyz.itzsomebody.codegen.instructions.ConstantNode;
import xyz.itzsomebody.codegen.instructions.NewArrayNode;
import xyz.itzsomebody.codegen.instructions.SimpleNode;

public class IRNewArrayExpression extends IRExpression {
    private final IRExpression length;
    private final WrappedType type;
    private final IRExpression[] elements;

    public IRNewArrayExpression(IRExpression length, WrappedType type, IRExpression[] elements) {
        super(type);
        this.length = length;
        this.type = type;
        this.elements = elements;
    }

    @Override
    public BytecodeBlock getInstructions() {
        BytecodeBlock block = new BytecodeBlock()
                .append(length.getInstructions())
                .append(new NewArrayNode(type));
        for (int i = 0; i < elements.length; i++) {
            IRExpression element = elements[i];
            block.append(SimpleNode.DUP)
                    .append(ConstantNode.intConst(i))
                    .append(element.getInstructions())
                    .append(SimpleNode.getArrayStoreOp(type));
        }
        return block;
    }
}
