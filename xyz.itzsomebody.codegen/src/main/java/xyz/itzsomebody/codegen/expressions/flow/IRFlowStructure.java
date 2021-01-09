package xyz.itzsomebody.codegen.expressions.flow;

import xyz.itzsomebody.codegen.WrappedType;
import xyz.itzsomebody.codegen.expressions.IRExpression;

public abstract class IRFlowStructure extends IRExpression {
    public IRFlowStructure() {
        super(WrappedType.getAbsent());
    }
}
