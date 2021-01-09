package xyz.itzsomebody.codegen.expressions.predefined;

import xyz.itzsomebody.codegen.BytecodeBlock;
import xyz.itzsomebody.codegen.expressions.IRExpression;
import xyz.itzsomebody.codegen.instructions.SimpleNode;

public class IRGetArrayElementExpression extends IRExpression {
    private final IRExpression array;
    private final IRExpression index;

    public IRGetArrayElementExpression(IRExpression array, IRExpression index) {
        super(array.getType());
        this.array = array;
        this.index = index;
    }

    @Override
    public BytecodeBlock getInstructions() {
        return new BytecodeBlock()
                .append(array.getInstructions())
                .append(index.getInstructions())
                .append(SimpleNode.getArrayLoadOp(getType()));
    }
}
