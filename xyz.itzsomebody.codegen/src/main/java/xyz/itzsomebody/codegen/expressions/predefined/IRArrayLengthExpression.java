package xyz.itzsomebody.codegen.expressions.predefined;

import xyz.itzsomebody.codegen.BytecodeBlock;
import xyz.itzsomebody.codegen.WrappedType;
import xyz.itzsomebody.codegen.expressions.IRExpression;
import xyz.itzsomebody.codegen.instructions.SimpleNode;

public class IRArrayLengthExpression extends IRExpression {
    public IRArrayLengthExpression() {
        super(WrappedType.from(int.class));
    }

    @Override
    public BytecodeBlock getInstructions() {
        return new BytecodeBlock().append(SimpleNode.ARRAY_LENGTH);
    }
}
