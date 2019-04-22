package me.itzsomebody.radon.analysis.constant.values;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.analysis.Value;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public abstract class AbstractValue implements Value {
    private final AbstractInsnNode insnNode;
    private final Type type;
    private final Set<AbstractInsnNode> usages;

    AbstractValue(AbstractInsnNode insnNode, Type type) {
        this.insnNode = insnNode;
        this.type = type;
        usages = new HashSet<>();
    }

    public final Set<AbstractInsnNode> getUsages() {
        return Collections.unmodifiableSet(usages);
    }

    public final void addUsage(AbstractInsnNode insnNode) {
        usages.add(insnNode);
    }

    public final int getSize() {
        return type == Type.LONG_TYPE || type == Type.DOUBLE_TYPE ? 2 : 1;
    }

    public abstract boolean isConstant();

    public AbstractInsnNode getInsnNode() {
        return insnNode;
    }

    public Type getType() {
        return type;
    }
}
