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
import xyz.itzsomebody.codegen.expressions.IRExpression;
import xyz.itzsomebody.codegen.instructions.BytecodeLabel;
import xyz.itzsomebody.codegen.instructions.JumpNode;
import xyz.itzsomebody.codegen.instructions.SwitchNode;

import java.util.ArrayList;

// FIXME: keys (and their cases, consequently) MUST be sorted
public class IRSwitchStructure extends IRFlowStructure {
    private final IRExpression operand;
    private final ArrayList<Integer> keys;
    private final ArrayList<BytecodeBlock> cases;
    private final BytecodeBlock defaultBody;
    private final ArrayList<BytecodeLabel> caseLabels = new ArrayList<>();
    private final BytecodeLabel defaultLabel = new BytecodeLabel();
    private final BytecodeLabel exitLabel = new BytecodeLabel();

    public IRSwitchStructure(IRExpression operand, ArrayList<Integer> keys, ArrayList<BytecodeBlock> cases, BytecodeBlock defaultBody) {
        this.operand = operand;
        this.keys = keys;
        this.cases = cases;
        this.defaultBody = defaultBody;
    }

    @Override
    public BytecodeBlock getInstructions() {
        var block = new BytecodeBlock()
                .append(operand.getInstructions())
                .append(new SwitchNode(keys, caseLabels, defaultLabel));

        // Cases
        cases.forEach(caseBody -> {
            var caseLabel = new BytecodeLabel();
            block.append(caseLabel)
                    .append(caseBody)
                    .append(JumpNode.jumpUnconditionally(exitLabel));
            caseLabels.add(caseLabel);
        });

        // Default
        block.append(defaultLabel)
                .append(defaultBody);

        // Exit
        block.append(exitLabel);
        return block;
    }

    public ArrayList<BytecodeLabel> getCaseLabels() {
        return caseLabels;
    }

    public BytecodeLabel getDefaultLabel() {
        return defaultLabel;
    }

    public BytecodeLabel getExitLabel() {
        return exitLabel;
    }
}
