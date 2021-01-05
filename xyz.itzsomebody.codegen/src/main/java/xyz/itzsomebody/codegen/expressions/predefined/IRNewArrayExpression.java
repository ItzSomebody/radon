package xyz.itzsomebody.codegen.expressions.predefined;

import xyz.itzsomebody.codegen.BytecodeBlock;
import xyz.itzsomebody.codegen.WrappedType;
import xyz.itzsomebody.codegen.expressions.IRExpression;
import xyz.itzsomebody.codegen.instructions.IntPushNode;
import xyz.itzsomebody.codegen.instructions.NewArrayNode;
import xyz.itzsomebody.codegen.instructions.SimpleNode;

public class IRNewArrayExpression extends IRExpression {
    private final IRExpression length;
    private final WrappedType type;
    private final IRExpression[] elements;

    public IRNewArrayExpression(IRExpression length, WrappedType type, IRExpression[] elements) {
        this.length = length;
        this.type = type;
        this.elements = elements;
    }

    @Override
    public BytecodeBlock getInstructions() {
        BytecodeBlock block = new BytecodeBlock()
                .append(length.getInstructions())
                .append(new NewArrayNode(type));
        for (int i = 0; i < elements.length; i++) {
            IRExpression element = elements[i];
            block.append(SimpleNode.DUP)
                    .append(new IntPushNode(i))
                    .append(element.getInstructions())
                    .append(SimpleNode.getArrayStoreOp(type));
        }
        return block;
    }
}
