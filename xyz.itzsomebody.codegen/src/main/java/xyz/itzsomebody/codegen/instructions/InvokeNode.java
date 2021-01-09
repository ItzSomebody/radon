package xyz.itzsomebody.codegen.instructions;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import xyz.itzsomebody.codegen.Utils;
import xyz.itzsomebody.codegen.WrappedType;

import java.util.List;

public class InvokeNode implements CompilableNode {
    private final int opcode;
    private final WrappedType owner;
    private final String name;
    private final List<WrappedType> parameterTypes;
    private final WrappedType returnType;

    public InvokeNode(int opcode, WrappedType owner, String name, List<WrappedType> parameterTypes, WrappedType returnType) {
        this.opcode = opcode;
        this.owner = owner;
        this.name = name;
        this.parameterTypes = parameterTypes;
        this.returnType = returnType;
    }

    @Override
    public AbstractInsnNode getNode() {
        return new MethodInsnNode(opcode, owner.getInternalName(), name, Utils.unwrapMethodDescriptor(parameterTypes, returnType), owner.isInterface());
    }

    public static InvokeNode invokeStatic(WrappedType owner, String name, List<WrappedType> parameterTypes, WrappedType returnType) {
        return new InvokeNode(Opcodes.INVOKESTATIC, owner, name, parameterTypes, returnType);
    }

    public static InvokeNode invokeVirtual(WrappedType owner, String name, List<WrappedType> parameterTypes, WrappedType returnType) {
        return new InvokeNode(Opcodes.INVOKEVIRTUAL, owner, name, parameterTypes, returnType);
    }

    public static InvokeNode invokeInterface(WrappedType owner, String name, List<WrappedType> parameterTypes, WrappedType returnType) {
        return new InvokeNode(Opcodes.INVOKEINTERFACE, owner, name, parameterTypes, returnType);
    }

    public static InvokeNode invokeSpecial(WrappedType owner, String name, List<WrappedType> parameterTypes, WrappedType returnType) {
        return new InvokeNode(Opcodes.INVOKESPECIAL, owner, name, parameterTypes, returnType);
    }

    public static InvokeNode invokeConstructor(WrappedType owner, List<WrappedType> parameterTypes) {
        return new InvokeNode(Opcodes.INVOKESPECIAL, owner, "<init>", parameterTypes, WrappedType.from(void.class));
    }
}
