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

import xyz.itzsomebody.codegen.BytecodeBlock;
import xyz.itzsomebody.codegen.WrappedType;
import xyz.itzsomebody.codegen.expressions.predefined.IRSetVariableExpression;
import xyz.itzsomebody.codegen.instructions.RegisterNode;

public class IRVariable extends IRExpression {
    private final WrappedType wrappedType;
    private final int slot;

    public IRVariable(WrappedType wrappedType, int slot) {
        super(wrappedType);
        this.wrappedType = wrappedType;
        this.slot = slot;
    }

    @Override
    public BytecodeBlock getInstructions() {
        return new BytecodeBlock().append(RegisterNode.loadVar(this));
    }

    public WrappedType getWrappedType() {
        return wrappedType;
    }

    public int getSlot() {
        return slot;
    }

    public IRExpression set(IRExpression expression) {
        return new IRSetVariableExpression(this, expression);
    }
}
