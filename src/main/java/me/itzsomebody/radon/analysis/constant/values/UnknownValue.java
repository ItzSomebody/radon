package me.itzsomebody.radon.analysis.constant.values;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;

public final class UnknownValue extends AbstractValue {
    public static AbstractValue UNINITIALIZED_VALUE = new UnknownValue(null, null);

    public UnknownValue(Type type) {
        super(null, type);
    }

    public UnknownValue(AbstractInsnNode insnNode, Type type) {
        super(insnNode, type);
    }

    @Override
    public boolean isConstant() {
        return false;
    }
}
