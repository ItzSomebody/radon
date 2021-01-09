package xyz.itzsomebody.codegen.expressions.flow;

import xyz.itzsomebody.codegen.BytecodeBlock;
import xyz.itzsomebody.codegen.instructions.BytecodeLabel;
import xyz.itzsomebody.codegen.instructions.JumpNode;

public class IRWhileStructure extends IRFlowStructure {
    private final BytecodeBlock condition;
    private final BytecodeBlock body;
    private final BytecodeLabel continueLabel = new BytecodeLabel();
    private final BytecodeLabel exitLabel = new BytecodeLabel();

    public IRWhileStructure(BytecodeBlock condition, BytecodeBlock body) {
        this.condition = condition;
        this.body = body;
    }

    @Override
    public BytecodeBlock getInstructions() {
        return new BytecodeBlock()
                // Condition
                .append(continueLabel)
                .append(condition)
                .append(JumpNode.jumpIfZero(exitLabel)) // Exit if false

                // Body
                .append(body)
                .append(JumpNode.jumpUnconditionally(continueLabel)) // Next iteration

                // Exit
                .append(exitLabel);
    }

    public BytecodeLabel getContinueLabel() {
        return continueLabel;
    }

    public BytecodeLabel getExitLabel() {
        return exitLabel;
    }
}
