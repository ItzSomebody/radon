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

public class IRForStructure extends IRFlowStructure {
    private final BytecodeBlock initializer;
    private final BytecodeBlock condition;
    private final BytecodeBlock updater;
    private final BytecodeBlock body;
    private final BytecodeLabel continueLabel = new BytecodeLabel();
    private final BytecodeLabel exitLabel = new BytecodeLabel();

    public IRForStructure(BytecodeBlock initializer, BytecodeBlock condition, BytecodeBlock updater, BytecodeBlock body) {
        this.initializer = initializer;
        this.condition = condition;
        this.updater = updater;
        this.body = body;
    }

    @Override
    public BytecodeBlock getInstructions() {
        return new BytecodeBlock()
                // Initializer
                .append(initializer)

                // Condition
                .append(continueLabel)
                .append(condition)
                .append(JumpNode.jumpIfZero(exitLabel)) // Exit if false

                // Updater
                .append(updater)

                // Body
                .append(body)
                .append(JumpNode.jumpUnconditionally(continueLabel)) // Next iteration
                .append(exitLabel);
    }

    public BytecodeLabel getContinueLabel() {
        return continueLabel;
    }

    public BytecodeLabel getExitLabel() {
        return exitLabel;
    }
}
