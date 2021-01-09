package xyz.itzsomebody.codegen.expressions.predefined;

import xyz.itzsomebody.codegen.BytecodeBlock;
import xyz.itzsomebody.codegen.WrappedType;
import xyz.itzsomebody.codegen.expressions.IRExpression;
import xyz.itzsomebody.codegen.instructions.InvokeNode;
import xyz.itzsomebody.codegen.instructions.SimpleNode;
import xyz.itzsomebody.codegen.instructions.TypeNode;

import java.util.List;

public class IRNewInstanceExpression extends IRExpression {
    private final WrappedType type;
    private final List<WrappedType> argumentTypes;
    private final List<IRExpression> arguments;

    public IRNewInstanceExpression(WrappedType type, List<WrappedType> argumentTypes, List<IRExpression> arguments) {
        super(type);
        this.type = type;
        this.argumentTypes = argumentTypes;
        this.arguments = arguments;
    }

    @Override
    public BytecodeBlock getInstructions() {
        var block = new BytecodeBlock()
                .append(TypeNode.newInstance(type))
                .append(SimpleNode.DUP);
        for (var expr : arguments) {
            block.append(expr.getInstructions());
        }
        block.append(InvokeNode.invokeConstructor(type, argumentTypes));
        return block;
    }
}
