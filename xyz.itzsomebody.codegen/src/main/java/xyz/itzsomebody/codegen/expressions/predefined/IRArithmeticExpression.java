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

public class IRArithmeticExpression extends IRExpression {
    private final SimpleNode operation;
    private final IRExpression left;
    private final IRExpression right;

    public IRArithmeticExpression(SimpleNode operation, IRExpression left, IRExpression right) {
        super(left.getType()); // Left expression should ALWAYS be the resultant type (i.e. LSHL/LSHR/LUSHR)
        this.operation = operation;
        this.left = left;
        this.right = right;
    }

    @Override
    public BytecodeBlock getInstructions() {
        return new BytecodeBlock()
                .append(left.getInstructions())
                .append(right.getInstructions())
                .append(operation);

    }
}
