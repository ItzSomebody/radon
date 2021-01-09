package xyz.itzsomebody.codegen.expressions.predefined;

import xyz.itzsomebody.codegen.BytecodeBlock;
import xyz.itzsomebody.codegen.expressions.IRExpression;
import xyz.itzsomebody.codegen.instructions.SimpleNode;

public class IRNegateExpression extends IRExpression {
    private final IRExpression operand;

    public IRNegateExpression(IRExpression operand) {
        super(operand.getType());
        this.operand = operand;
    }

    @Override
    public BytecodeBlock getInstructions() {
        return new BytecodeBlock()
                .append(operand.getInstructions())
                .append(SimpleNode.negateOpcodeFor(operand.getType()));
    }
}
