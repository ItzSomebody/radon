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
