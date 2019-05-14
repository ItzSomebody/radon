package me.itzsomebody.radon.analysis.constant.values;

import org.objectweb.asm.tree.AbstractInsnNode;

public final class NullValue extends AbstractValue {
    public NullValue(AbstractInsnNode insnNode) {
        super(insnNode, null);
    }

    @Override
    public boolean isConstant() {
        return true;
    }
}
