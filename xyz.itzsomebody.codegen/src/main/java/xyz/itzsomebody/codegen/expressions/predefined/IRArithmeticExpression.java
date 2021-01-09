package xyz.itzsomebody.codegen.expressions.predefined;

import xyz.itzsomebody.codegen.BytecodeBlock;
import xyz.itzsomebody.codegen.expressions.IRExpression;
import xyz.itzsomebody.codegen.instructions.SimpleNode;

public class IRArithmeticExpression extends IRExpression {
    private final SimpleNode operation;
    private final IRExpression left;
    private final IRExpression right;

    public IRArithmeticExpression(SimpleNode operation, IRExpression left, IRExpression right) {
        super(left.getType()); // Left expression should ALWAYS be the resultant type (i.e. LSHL/LSHR/LUSHR)
        this.operation = operation;
        this.left = left;
        this.right = right;
    }

    @Override
    public BytecodeBlock getInstructions() {
        return new BytecodeBlock()
                .append(left.getInstructions())
                .append(right.getInstructions())
                .append(operation);

    }
}
