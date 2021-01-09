package xyz.itzsomebody.codegen.instructions;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
import xyz.itzsomebody.codegen.WrappedType;

public class FieldAccessNode implements CompilableNode {
    private final int opcode;
    private final WrappedType owner;
    private final String name;
    private final WrappedType type;

    public FieldAccessNode(int opcode, WrappedType owner, String name, WrappedType type) {
        this.opcode = opcode;
        this.owner = owner;
        this.name = name;
        this.type = type;
    }

    @Override
    public AbstractInsnNode getNode() {
        return new FieldInsnNode(opcode, owner.getInternalName(), name, type.getInternalName());
    }

    public static FieldAccessNode getStatic(WrappedType owner, String name, WrappedType type) {
        return new FieldAccessNode(Opcodes.GETSTATIC, owner, name, type);
    }

    public static FieldAccessNode putStatic(WrappedType owner, String name, WrappedType type) {
        return new FieldAccessNode(Opcodes.PUTSTATIC, owner, name, type);
    }

    public static FieldAccessNode getField(WrappedType owner, String name, WrappedType type) {
        return new FieldAccessNode(Opcodes.GETFIELD, owner, name, type);
    }

    public static FieldAccessNode putField(WrappedType owner, String name, WrappedType type) {
        return new FieldAccessNode(Opcodes.PUTFIELD, owner, name, type);
    }
}
