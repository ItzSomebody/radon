package xyz.itzsomebody.codegen.instructions;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.TypeInsnNode;
import xyz.itzsomebody.codegen.WrappedType;

public class NewArrayNode implements CompilableNode {
    private final WrappedType wrappedType;

    public NewArrayNode(WrappedType wrappedType) {
        this.wrappedType = wrappedType;
    }

    @Override
    public AbstractInsnNode getNode() {
        if (wrappedType.isPrimitive()) {
            return new IntInsnNode(Opcodes.NEWARRAY, wrappedType.getSort());
        } else {
            return new TypeInsnNode(Opcodes.ANEWARRAY, wrappedType.getInternalName());
        }
    }
}
