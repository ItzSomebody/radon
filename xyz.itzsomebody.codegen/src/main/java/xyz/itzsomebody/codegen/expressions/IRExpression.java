package xyz.itzsomebody.codegen.expressions;

import xyz.itzsomebody.codegen.BytecodeBlock;
import xyz.itzsomebody.codegen.WrappedType;

public abstract class IRExpression {
    private final WrappedType type;

    public IRExpression(WrappedType type) {
        this.type = type;
    }

    public WrappedType getType() {
        return type;
    }

    public abstract BytecodeBlock getInstructions();
}
