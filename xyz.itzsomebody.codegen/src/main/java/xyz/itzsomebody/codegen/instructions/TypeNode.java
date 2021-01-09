package xyz.itzsomebody.codegen.instructions;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.TypeInsnNode;
import xyz.itzsomebody.codegen.WrappedType;

public class TypeNode implements CompilableNode {
    private final int opcode;
    private final WrappedType type;

    public TypeNode(int opcode, WrappedType type) {
        this.opcode = opcode;
        this.type = type;
    }

    @Override
    public AbstractInsnNode getNode() {
        return new TypeInsnNode(opcode, type.getInternalName());
    }

    public static TypeNode newInstance(WrappedType type) {
        return new TypeNode(Opcodes.NEW, type);
    }

    public static TypeNode cast(WrappedType type) {
        return new TypeNode(Opcodes.CHECKCAST, type);
    }

    public static TypeNode instanceOf(WrappedType type) {
        return new TypeNode(Opcodes.INSTANCEOF, type);
    }
}
