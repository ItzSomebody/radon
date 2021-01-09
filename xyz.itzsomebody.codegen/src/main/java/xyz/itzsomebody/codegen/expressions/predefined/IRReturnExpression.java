package xyz.itzsomebody.codegen.expressions.predefined;

import org.objectweb.asm.Type;
import xyz.itzsomebody.codegen.BytecodeBlock;
import xyz.itzsomebody.codegen.expressions.IRExpression;
import xyz.itzsomebody.codegen.instructions.SimpleNode;

public class IRReturnExpression extends IRExpression {
    private final IRExpression target;

    public IRReturnExpression(IRExpression target) {
        super(target.getType());
        this.target = target;
    }

    @Override
    public BytecodeBlock getInstructions() {
        var block = new BytecodeBlock().append(target.getInstructions());
        var type = target.getType();

        switch (type.getSort()) {
            case Type.VOID:
                block.append(SimpleNode.RETURN_VOID);
                break;
            case Type.BOOLEAN:
            case Type.CHAR:
            case Type.BYTE:
            case Type.SHORT:
            case Type.INT:
                block.append(SimpleNode.RETURN_INT);
                break;
            case Type.FLOAT:
                block.append(SimpleNode.RETURN_FLOAT);
                break;
            case Type.LONG:
                block.append(SimpleNode.RETURN_LONG);
                break;
            case Type.DOUBLE:
                block.append(SimpleNode.RETURN_DOUBLE);
                break;
            default:
                block.append(SimpleNode.RETURN_OBJECT);
        }
        return block;
    }
}
