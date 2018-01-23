package me.itzsomebody.radon.transformers;

import me.itzsomebody.radon.asm.Handle;
import me.itzsomebody.radon.asm.MethodVisitor;
import me.itzsomebody.radon.asm.Opcodes;
import me.itzsomebody.radon.asm.tree.*;
import me.itzsomebody.radon.utils.NumberUtils;
import me.itzsomebody.radon.utils.StringUtils;

/**
 * Not really a transformer. This "transformer" generates trash classes full of useless Java bytecode that does nothing.
 *
 * @author ItzSomebody
 */
public class TrashClasses {
    /**
     * Class name for the trash class.
     */
    private String trashClassName;

    /**
     * Constructor used to create a {@link TrashClasses} object.
     *
     * @param trashClassName class name for the trash class.
     */
    public TrashClasses(String trashClassName) {
        this.trashClassName = trashClassName;
    }

    /**
     * Returns a fully built trash class.
     *
     * @return a fully built trash class.
     */
    public ClassNode returnTrashClass() {
        ClassNode classNode = createClass(trashClassName);
        int methodsToGenerate = NumberUtils.getRandomInt(3) + 2; // At least two

        for (int i = 0; i < methodsToGenerate; i++) {
            classNode.methods.add(methodGen());
        }

        return classNode;
    }

    /**
     * Builds and returns an empty class.
     *
     * @param className the string used for the class name.
     * @return an empty {@link ClassNode}.
     */
    private ClassNode createClass(String className) {
        ClassNode classNode = new ClassNode();

        classNode.visit(49, Opcodes.ACC_SUPER + Opcodes.ACC_PUBLIC, className, null, "java/lang/Object", null);

        MethodVisitor mv = classNode.visitMethod(Opcodes.ACC_PUBLIC, "<init>", "()V", null, null);
        mv.visitCode();
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
        mv.visitInsn(Opcodes.RETURN);
        mv.visitMaxs(1, 1);
        mv.visitEnd();

        classNode.visitEnd();

        return classNode;
    }

    /**
     * Builds and returns a {@link MethodNode}.
     *
     * @return a {@link MethodNode}.
     */
    private MethodNode methodGen() {
        String randDesc = descGen();
        MethodNode method = new MethodNode(Opcodes.ACC_STATIC + Opcodes.ACC_PRIVATE, StringUtils.crazyString(), randDesc, null, null);
        int instructions = NumberUtils.getRandomInt(30) + 30;

        InsnList insns = new InsnList();

        for (int i = 0; i < instructions; ++i) {
            insns.add(junkInsns());
        }

        if (randDesc.endsWith(")Ljava/lang/String;") || randDesc.endsWith(")Ljava/lang/Object;")) {
            insns.add(new VarInsnNode(Opcodes.ALOAD, NumberUtils.getRandomInt(30)));
            insns.add(new InsnNode(Opcodes.ARETURN));
        } else if (randDesc.endsWith(")Z")) {
            if (NumberUtils.getRandomInt(1) == 1) {
                insns.add(new InsnNode(Opcodes.ICONST_0));
            } else {
                insns.add(new InsnNode(Opcodes.ICONST_1));
            }

            insns.add(new InsnNode(Opcodes.IRETURN));
        } else if (randDesc.endsWith(")V")) {
            insns.add(new InsnNode(Opcodes.RETURN));
        }

        method.instructions = insns;
        return method;
    }

    /**
     * Generates a description for a trash method.
     *
     * @return generated description for a trash method.
     */
    private String descGen() {
        switch (NumberUtils.getRandomInt(7)) {
            case 0:
                return "(Ljava/lang/String;)Ljava/lang/String;";
            case 1:
                return "(Ljava/lang/Object;)Ljava/lang/Object;";
            case 2:
                return "(I)Z";
            case 3:
                return "()V";
            case 4:
                return "(B)V";
            case 5:
                return "(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;";
            case 6: // False BSM lol
            default:
                return "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;ILjava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/Object;";
        }
    }

    /**
     * Returns a random opcode instruction to add to trash methods.
     *
     * @return a random opcode instruction to add to trash methods.
     */
    private static AbstractInsnNode junkInsns() {
        int index = NumberUtils.getRandomInt(20);
        switch (index) {
            case 0:
                return new MethodInsnNode(Opcodes.INVOKESTATIC, StringUtils.crazyString(), StringUtils.crazyString(), "(Ljava/lang/String;)V", false);
            case 1:
                return new FieldInsnNode(Opcodes.GETFIELD, StringUtils.crazyString(), StringUtils.crazyString(), "I");
            case 2:
                return new InsnNode(NumberUtils.getRandomInt(16));
            case 3:
                return new VarInsnNode(Opcodes.ALOAD, NumberUtils.getRandomInt(30));
            case 4:
                return new IntInsnNode(Opcodes.BIPUSH, NumberUtils.getRandomInt(255));
            case 5:
                return new IntInsnNode(Opcodes.SIPUSH, NumberUtils.getRandomInt(25565));
            case 6:
            case 7:
            case 8:
                return new InsnNode(NumberUtils.getRandomInt(5));
            case 9:
                return new LdcInsnNode(StringUtils.crazyString());
            case 10:
                return new IincInsnNode(NumberUtils.getRandomInt(16), NumberUtils.getRandomInt(16));
            case 11:
                return new MethodInsnNode(Opcodes.INVOKESPECIAL, StringUtils.crazyString(), StringUtils.crazyString(), "()V", false);
            case 12:
                return new MethodInsnNode(Opcodes.INVOKEVIRTUAL, StringUtils.crazyString(), StringUtils.crazyString(), "(Ljava/lang/Object;)Ljava/lang/Object;", false);
            case 13:
                return new VarInsnNode(Opcodes.ILOAD, NumberUtils.getRandomInt(30));
            case 14:
                return new InsnNode(Opcodes.ATHROW);
            case 15:
                return new MethodInsnNode(Opcodes.INVOKEINTERFACE, StringUtils.crazyString(), StringUtils.crazyString(), "(I)I", false);
            case 16:
                Handle handle = new Handle(6, StringUtils.crazyString(), StringUtils.crazyString(), StringUtils.crazyString(), false);
                return new InvokeDynamicInsnNode(StringUtils.crazyString(), StringUtils.crazyString(), handle, NumberUtils.getRandomInt(5), NumberUtils.getRandomInt(5), NumberUtils.getRandomInt(5), NumberUtils.getRandomInt(5), NumberUtils.getRandomInt(5));
            case 17:
                return new IntInsnNode(Opcodes.ANEWARRAY, NumberUtils.getRandomInt(30));
            case 18:
                return new VarInsnNode(Opcodes.ASTORE, NumberUtils.getRandomInt(30));
            case 19:
            default:
                return new VarInsnNode(Opcodes.ISTORE, NumberUtils.getRandomInt(30));
        }
    }
}
