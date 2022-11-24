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

import org.objectweb.asm.tree.TryCatchBlockNode;
import xyz.itzsomebody.codegen.BytecodeBlock;
import xyz.itzsomebody.codegen.WrappedType;
import xyz.itzsomebody.codegen.instructions.BytecodeLabel;
import xyz.itzsomebody.codegen.instructions.JumpNode;

public class IRTryCatchStructure extends IRFlowStructure {
    private final BytecodeBlock trapRange;
    private final BytecodeBlock handler;
    private final WrappedType exceptionType;
    private final BytecodeLabel trapStartLabel = new BytecodeLabel();
    private final BytecodeLabel trapEndLabel = new BytecodeLabel();
    private final BytecodeLabel handlerLabel = new BytecodeLabel();
    private final BytecodeLabel exitLabel = new BytecodeLabel();

    public IRTryCatchStructure(BytecodeBlock trapRange, BytecodeBlock handler, WrappedType exceptionType) {
        this.trapRange = trapRange;
        this.handler = handler;
        this.exceptionType = exceptionType;
    }

    public TryCatchBlockNode getTryCatchBlocKNode() {
        return new TryCatchBlockNode(trapStartLabel.getLabel(), trapEndLabel.getLabel(), handlerLabel.getLabel(), exceptionType.getInternalName());
    }

    @Override
    public BytecodeBlock getInstructions() {
        return new BytecodeBlock()
                // Try block
                .append(trapStartLabel)
                .append(trapRange)
                .append(trapEndLabel)
                .append(JumpNode.jumpUnconditionally(exitLabel)) // No exception has been thrown so skip the handler

                // Catch block
                .append(handlerLabel)
                .append(handler)

                // Exit
                .append(exitLabel);
    }

    public BytecodeLabel getTrapStartLabel() {
        return trapStartLabel;
    }

    public BytecodeLabel getTrapEndLabel() {
        return trapEndLabel;
    }

    public BytecodeLabel getHandlerLabel() {
        return handlerLabel;
    }

    public BytecodeLabel getExitLabel() {
        return exitLabel;
    }
}
