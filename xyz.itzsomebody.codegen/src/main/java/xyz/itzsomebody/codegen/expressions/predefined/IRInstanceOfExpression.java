package xyz.itzsomebody.codegen.expressions.predefined;

import xyz.itzsomebody.codegen.BytecodeBlock;
import xyz.itzsomebody.codegen.WrappedType;
import xyz.itzsomebody.codegen.expressions.IRExpression;
import xyz.itzsomebody.codegen.instructions.TypeNode;

public class IRInstanceOfExpression extends IRExpression {
    private final IRExpression instance;
    private final WrappedType type;

    public IRInstanceOfExpression(IRExpression instance, WrappedType type) {
        super(WrappedType.from(boolean.class));
        this.instance = instance;
        this.type = type;
    }

    @Override
    public BytecodeBlock getInstructions() {
        return new BytecodeBlock()
                .append(instance.getInstructions())
                .append(TypeNode.instanceOf(type));
    }
}
