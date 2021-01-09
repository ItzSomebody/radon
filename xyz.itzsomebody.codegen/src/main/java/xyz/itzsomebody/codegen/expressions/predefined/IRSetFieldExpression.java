package xyz.itzsomebody.codegen.expressions.predefined;

import xyz.itzsomebody.codegen.BytecodeBlock;
import xyz.itzsomebody.codegen.WrappedType;
import xyz.itzsomebody.codegen.expressions.IRExpression;
import xyz.itzsomebody.codegen.instructions.FieldAccessNode;

import java.lang.reflect.Field;

public class IRSetFieldExpression extends IRExpression {
    private final IRExpression instance;
    private final IRExpression value;
    private final WrappedType owner;
    private final String name;

    public IRSetFieldExpression(IRExpression instance, IRExpression value, WrappedType owner, String name, WrappedType type) {
        super(type);
        this.instance = instance;
        this.value = value;
        this.owner = owner;
        this.name = name;
    }

    public IRSetFieldExpression(IRExpression instance, IRExpression value, Field field) {
        this(instance, value, WrappedType.from(field.getDeclaringClass()), field.getName(), WrappedType.from(field.getType()));
    }

    @Override
    public BytecodeBlock getInstructions() {
        if (instance == null) {
            return new BytecodeBlock()
                    .append(value.getInstructions())
                    .append(FieldAccessNode.putStatic(owner, name, getType()));
        } else {
            return new BytecodeBlock()
                    .append(instance.getInstructions())
                    .append(value.getInstructions())
                    .append(FieldAccessNode.putField(owner, name, getType()));
        }
    }
}
