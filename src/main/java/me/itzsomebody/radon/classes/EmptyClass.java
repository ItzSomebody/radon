package me.itzsomebody.radon.classes;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;

/**
 * Class that statically returns a ClassNode as an empty class.
 *
 * @author ItzSomebody
 */
public class EmptyClass {
    /**
     * Creates and returns a class object as {@link ClassNode}
     * with a super class of java/lang/Object.
     *
     * @param name the {@link String} to be used as the empty class name.
     * @return Returns a class object as {@link ClassNode} with a super class
     * of  java/lang/Object
     */
    public static ClassNode emptyClass(String name) {
        ClassNode classNode = new ClassNode();
        classNode.visit(51, Opcodes.ACC_PUBLIC, name, null,
                "java/lang/Object", null);

        MethodVisitor mv = classNode.visitMethod(Opcodes.ACC_PUBLIC,
                "<init>", "()V", null, null);
        mv.visitCode();
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/Object",
                "<init>", "()V", false);
        mv.visitInsn(Opcodes.RETURN);
        mv.visitMaxs(1, 1);
        mv.visitEnd();

        classNode.visitEnd();

        return classNode;
    }
}
