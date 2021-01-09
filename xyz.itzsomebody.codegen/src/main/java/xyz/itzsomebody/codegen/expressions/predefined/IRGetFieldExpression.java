package xyz.itzsomebody.codegen.expressions.predefined;

import xyz.itzsomebody.codegen.BytecodeBlock;
import xyz.itzsomebody.codegen.WrappedType;
import xyz.itzsomebody.codegen.expressions.IRExpression;
import xyz.itzsomebody.codegen.instructions.FieldAccessNode;

import java.lang.reflect.Field;

public class IRGetFieldExpression extends IRExpression {
    private final IRExpression instance;
    private final WrappedType owner;
    private final String name;

    public IRGetFieldExpression(IRExpression instance, WrappedType owner, String name, WrappedType type) {
        super(type);
        this.instance = instance;
        this.owner = owner;
        this.name = name;
    }

    public IRGetFieldExpression(IRExpression instance, Field field) {
        this(instance, WrappedType.from(field.getDeclaringClass()), field.getName(), WrappedType.from(field.getType()));
    }

    @Override
    public BytecodeBlock getInstructions() {
        if (instance == null) {
            return new BytecodeBlock().append(FieldAccessNode.getStatic(owner, name, getType()));
        } else {
            return new BytecodeBlock()
                    .append(instance.getInstructions())
                    .append(FieldAccessNode.getField(owner, name, getType()));
        }
    }
}
