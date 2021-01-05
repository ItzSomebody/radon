package xyz.itzsomebody.codegen.expressions.predefined;

import xyz.itzsomebody.codegen.BytecodeBlock;
import xyz.itzsomebody.codegen.expressions.IRExpression;
import xyz.itzsomebody.codegen.expressions.IRVariable;
import xyz.itzsomebody.codegen.instructions.RegisterNode;

public class IRSetVariableExpression extends IRExpression {
    private final IRVariable variable;
    private final IRExpression expression;

    public IRSetVariableExpression(IRVariable variable, IRExpression expression) {
        this.variable = variable;
        this.expression = expression;
    }

    @Override
    public BytecodeBlock getInstructions() {
        return new BytecodeBlock()
                .append(expression.getInstructions())
                .append(RegisterNode.storeVar(variable));
    }
}
