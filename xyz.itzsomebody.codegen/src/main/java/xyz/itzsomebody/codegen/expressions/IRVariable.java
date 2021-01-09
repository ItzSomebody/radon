package xyz.itzsomebody.codegen.expressions;

import xyz.itzsomebody.codegen.BytecodeBlock;
import xyz.itzsomebody.codegen.WrappedType;
import xyz.itzsomebody.codegen.expressions.predefined.IRSetVariableExpression;
import xyz.itzsomebody.codegen.instructions.RegisterNode;

public class IRVariable extends IRExpression {
    private final WrappedType wrappedType;
    private final int slot; // fixme: this is cheating so fix eventually

    public IRVariable(WrappedType wrappedType, int slot) {
        super(wrappedType);
        this.wrappedType = wrappedType;
        this.slot = slot;
    }

    @Override
    public BytecodeBlock getInstructions() {
        return new BytecodeBlock().append(RegisterNode.loadVar(this));
    }

    public WrappedType getWrappedType() {
        return wrappedType;
    }

    public int getSlot() {
        return slot;
    }

    public IRExpression set(IRExpression expression) {
        return new IRSetVariableExpression(this, expression);
    }
}
