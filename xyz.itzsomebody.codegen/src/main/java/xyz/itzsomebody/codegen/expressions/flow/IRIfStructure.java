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

package xyz.itzsomebody.codegen.expressions.flow;

import xyz.itzsomebody.codegen.BytecodeBlock;
import xyz.itzsomebody.codegen.instructions.BytecodeLabel;
import xyz.itzsomebody.codegen.instructions.JumpNode;

public class IRIfStructure extends IRFlowStructure {
    private final BytecodeBlock condition;
    private final BytecodeBlock ifTrue;
    private final BytecodeBlock ifFalse;
    private final BytecodeLabel trueLabel = new BytecodeLabel();
    private final BytecodeLabel exitLabel = new BytecodeLabel();

    public IRIfStructure(BytecodeBlock condition, BytecodeBlock ifTrue, BytecodeBlock ifFalse) {
        this.condition = condition;
        this.ifTrue = ifTrue;
        this.ifFalse = ifFalse;
    }

    @Override
    public BytecodeBlock getInstructions() {
        return new BytecodeBlock()
                // Condition
                .append(condition)
                .append(JumpNode.jumpIfNotZero(trueLabel))

                // Execute this if false
                .append(ifFalse)
                .append(JumpNode.jumpUnconditionally(exitLabel)) // Don't execute the ifTrue block

                // Execute this if true
                .append(trueLabel)
                .append(ifTrue)

                // Exit
                .append(exitLabel);

    }

    public BytecodeLabel getTrueLabel() {
        return trueLabel;
    }

    public BytecodeLabel getExitLabel() {
        return exitLabel;
    }
}
