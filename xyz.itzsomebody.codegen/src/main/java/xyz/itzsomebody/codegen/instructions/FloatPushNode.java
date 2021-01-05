package xyz.itzsomebody.codegen.instructions;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LdcInsnNode;

public class FloatPushNode implements CompilableNode {
    private final float operand;

    public FloatPushNode(float operand) {
        this.operand = operand;
    }

    @Override
    public AbstractInsnNode getNode() {
        if (operand == 0F || operand == 1F || operand == 2F) {
            return new InsnNode((int) operand + 11);
        } else {
            return new LdcInsnNode(operand);
        }
    }
}
