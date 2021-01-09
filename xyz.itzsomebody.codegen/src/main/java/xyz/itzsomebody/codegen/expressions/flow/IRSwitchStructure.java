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
