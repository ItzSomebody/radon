package me.itzsomebody.radon.utils;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.Optional;

/**
 * MethodNode utilities
 *
 * @author vovanre
 */
public class MethodNodeUtils {
    public static MethodNode getOrCreateStaticInit(ClassNode classNode) {
        Optional<MethodNode> clinit = classNode.methods.stream().filter(m -> m.name.equals("<clinit>")).findFirst();
        if (clinit.isPresent())
            return clinit.get();
        MethodNode methodNode = new MethodNode(Opcodes.ACC_STATIC, "<clinit>", "()V", null, null);
        methodNode.instructions.add(new InsnNode(Opcodes.RETURN));

        classNode.methods.add(methodNode);
        return methodNode;
    }
}
