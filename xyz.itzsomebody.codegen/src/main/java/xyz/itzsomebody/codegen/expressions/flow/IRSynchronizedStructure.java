package xyz.itzsomebody.codegen.expressions.flow;

import xyz.itzsomebody.codegen.BytecodeBlock;
import xyz.itzsomebody.codegen.expressions.IRExpression;
import xyz.itzsomebody.codegen.instructions.SimpleNode;

public class IRSynchronizedStructure extends IRFlowStructure {
    private final IRExpression instance;
    private final BytecodeBlock body;

    public IRSynchronizedStructure(IRExpression instance, BytecodeBlock body) {
        this.instance = instance;
        this.body = body;
    }

    @Override
    public BytecodeBlock getInstructions() {
        return new BytecodeBlock()
                .append(instance.getInstructions())
                .append(SimpleNode.ENTER_MONITOR)
                .append(body)
                .append(instance.getInstructions())
                .append(SimpleNode.EXIT_MONITOR);
    }
}
