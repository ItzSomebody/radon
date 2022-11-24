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
import xyz.itzsomebody.codegen.expressions.IRExpression;
import xyz.itzsomebody.codegen.instructions.SimpleNode;

public class IRGetArrayElementExpression extends IRExpression {
    private final IRExpression array;
    private final IRExpression index;

    public IRGetArrayElementExpression(IRExpression array, IRExpression index) {
        super(array.getType());
        this.array = array;
        this.index = index;
    }

    @Override
    public BytecodeBlock getInstructions() {
        return new BytecodeBlock()
                .append(array.getInstructions())
                .append(index.getInstructions())
                .append(SimpleNode.getArrayLoadOp(getType()));
    }
}
