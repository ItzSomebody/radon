package xyz.itzsomebody.codegen.instructions;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LdcInsnNode;

public class DoublePushNode implements CompilableNode {
    private final double operand;

    public DoublePushNode(double operand) {
        this.operand = operand;
    }

    @Override
    public AbstractInsnNode getNode() {
        if (operand == 0D || operand == 1D) {
            return new InsnNode((int) operand + 14);
        } else {
            return new LdcInsnNode(operand);
        }
    }
}
