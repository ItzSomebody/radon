package xyz.itzsomebody.codegen.expressions;

import xyz.itzsomebody.codegen.BytecodeBlock;

public abstract class IRExpression {
    public abstract BytecodeBlock getInstructions();
}
