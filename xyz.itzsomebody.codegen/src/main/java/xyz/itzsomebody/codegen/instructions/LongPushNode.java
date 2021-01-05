package xyz.itzsomebody.codegen.instructions;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LdcInsnNode;

public class LongPushNode implements CompilableNode {
    private final long operand;

    public LongPushNode(long operand) {
        this.operand = operand;
    }

    @Override
    public AbstractInsnNode getNode() {
        if (operand == 0L || operand == 1L) {
            return new InsnNode((int) operand + 9);
        } else {
            return new LdcInsnNode(operand);
        }
    }
}
