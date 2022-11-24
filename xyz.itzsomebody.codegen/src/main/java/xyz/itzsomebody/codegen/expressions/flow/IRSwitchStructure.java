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

import org.jetbrains.annotations.NotNull;
import xyz.itzsomebody.codegen.BytecodeBlock;
import xyz.itzsomebody.codegen.expressions.IRExpression;
import xyz.itzsomebody.codegen.instructions.BytecodeLabel;
import xyz.itzsomebody.codegen.instructions.JumpNode;
import xyz.itzsomebody.codegen.instructions.SwitchNode;

import java.util.ArrayList;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class IRSwitchStructure extends IRFlowStructure {
    private final IRExpression operand;
    private final SortedSet<IRCaseStructure> cases = new TreeSet<>();
    private final BytecodeBlock defaultBody;
    private final ArrayList<BytecodeLabel> caseLabels = new ArrayList<>();
    private final BytecodeLabel defaultLabel = new BytecodeLabel();
    private final BytecodeLabel exitLabel = new BytecodeLabel();

    public IRSwitchStructure(IRExpression operand, ArrayList<Integer> keys, ArrayList<BytecodeBlock> caseBodies, BytecodeBlock defaultBody) {
        this.operand = operand;
        this.defaultBody = defaultBody;

        IntStream.range(0, keys.size()).forEach(index -> cases.add(new IRCaseStructure(keys.get(index), caseBodies.get(index))));
    }

    @Override
    public BytecodeBlock getInstructions() {
        var block = new BytecodeBlock()
                .append(operand.getInstructions())
                .append(new SwitchNode(cases.stream().map(IRCaseStructure::getKey).collect(Collectors.toList()), caseLabels, defaultLabel));

        // Cases
        cases.forEach(caseStructure -> {
            var caseLabel = new BytecodeLabel();
            block.append(caseLabel)
                    .append(caseStructure.getBody())
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

    static class IRCaseStructure implements Comparable<IRCaseStructure> {
        private final int key;
        private final BytecodeBlock body;

        IRCaseStructure(int key, BytecodeBlock body) {
            this.key = key;
            this.body = body;
        }

        public int getKey() {
            return key;
        }

        public BytecodeBlock getBody() {
            return body;
        }

        @Override
        public int compareTo(@NotNull IRSwitchStructure.IRCaseStructure other) {
            return Integer.compare(key, other.key);
        }
    }
}
