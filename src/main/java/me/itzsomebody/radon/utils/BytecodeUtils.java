package me.itzsomebody.radon.utils;

import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

/**
 * Utils used for operating on bytecode.
 *
 * @author ItzSomebody
 */
public class BytecodeUtils {
    /**
     * Checks if the input class is a main method.
     *
     * @param clazz      {@link ClassNode} to check for main methods.
     * @param spigotMode if obfuscator should consider the input
     *                   {@link ClassNode} as a Spigot/Bukkit/Bungee plugin.
     * @return true if the input {@link ClassNode} contains a main method,
     * false if not.
     */
    public static boolean isMain(ClassNode clazz, boolean spigotMode) {
        if (spigotMode) {
            if (clazz.superName.equals("org/bukkit/plugin/java/JavaPlugin")
                    || clazz.superName.equals("net/md_5/bungee/api/plugin/Plugin")) {
                return true;
            }
        }

        for (MethodNode methodNode : clazz.methods) {
            if (methodNode.name.equals("main")
                    || methodNode.name.equals("premain")) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns access modifier without private or protected so that class
     * renaming works properly.
     *
     * @param access input access as {@link Integer}.
     * @return new {@link Integer} without restrictive flags.
     */
    public static int accessFixer(int access) {
        int a = access;
        if ((a & Opcodes.ACC_PRIVATE) != 0) {
            a ^= Opcodes.ACC_PRIVATE;
        }
        if ((a & Opcodes.ACC_PROTECTED) != 0) {
            a ^= Opcodes.ACC_PROTECTED;
        }
        if ((a & Opcodes.ACC_PUBLIC) == 0) {
            a |= Opcodes.ACC_PUBLIC;
        }
        return a;
    }

    /**
     * Returns an {@link InsnList} with bytecode instructions for expiration.
     *
     * @param expiryTime a {@link Long} representation of the expiration date.
     * @return an {@link InsnList} with bytecode instructions for expiration.
     */
    public static InsnList returnExpiry(long expiryTime, String expiredMsg) {
        InsnList expiryCode = new InsnList();
        LabelNode injectedLabel = new LabelNode(new Label());

        expiryCode.add(new TypeInsnNode(Opcodes.NEW, "java/util/Date"));
        expiryCode.add(new InsnNode(Opcodes.DUP));
        expiryCode.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "java/util/Date", "<init>", "()V", false));
        expiryCode.add(new TypeInsnNode(Opcodes.NEW, "java/util/Date"));
        expiryCode.add(new InsnNode(Opcodes.DUP));
        expiryCode.add(new LdcInsnNode(expiryTime));
        expiryCode.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "java/util/Date", "<init>", "(J)V", false));
        expiryCode.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/util/Date", "after", "(Ljava/util/Date;)Z", false));
        expiryCode.add(new JumpInsnNode(Opcodes.IFEQ, injectedLabel));
        expiryCode.add(new TypeInsnNode(Opcodes.NEW, "java/lang/Throwable"));
        expiryCode.add(new InsnNode(Opcodes.DUP));
        expiryCode.add(new LdcInsnNode(expiredMsg));
        expiryCode.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "java/lang/Throwable", "<init>", "(Ljava/lang/String;)V", false));
        expiryCode.add(new InsnNode(Opcodes.ATHROW));
        expiryCode.add(injectedLabel);

        return expiryCode;
    }

    /**
     * Returns a bytecode instruction representing an int.
     *
     * @param number the {@link Integer} for the obfuscator to contemplate.
     * @return a bytecode instruction representing an int.
     */
    public static AbstractInsnNode getNumberInsn(int number) {
        if (number >= -1 && number <= 5) {
            return new InsnNode(number + 3);
        } else if (number >= -128 && number <= 127) {
            return new IntInsnNode(Opcodes.BIPUSH, number);
        } else if (number >= -32768 && number <= 32767) {
            return new IntInsnNode(Opcodes.SIPUSH, number);
        } else {
            return new LdcInsnNode(number);
        }
    }

    /**
     * Returns a bytecode instruction representing a long.
     *
     * @param number the {@link Long} for the obfuscator to contemplate.
     * @return a bytecode instruction representing a long.
     */
    public static AbstractInsnNode getNumberInsn(long number) {
        if (number >= 0 && number <= 1) {
            return new InsnNode((int) (number + 9));
        } else {
            return new LdcInsnNode(number);
        }
    }

    /**
     * Returns true if access has native modifier.
     *
     * @param access method access to check.
     * @return true if access has native modifier.
     */
    public static boolean isNativeMethod(int access) {
        return (access & Opcodes.ACC_NATIVE) != 0;
    }

    /**
     * Returns true if access has abstract modifier.
     *
     * @param access method access to check.
     * @return true if access has abstract modifier.
     */
    public static boolean isAbstractMethod(int access) {
        return (access & Opcodes.ACC_ABSTRACT) != 0;
    }

    /**
     * Returns true if access has synthetic modifier.
     *
     * @param access method access to check.
     * @return true if access has abstract modifier.
     */
    public static boolean isSyntheticMethod(int access) {
        return (access & Opcodes.ACC_SYNTHETIC) != 0;
    }

    /**
     * Returns true if access has bride modifier.
     *
     * @param access method access to check.
     * @return true if access has abstract modifier.
     */
    public static boolean isBridgeMethod(int access) {
        return (access & Opcodes.ACC_BRIDGE) != 0;
    }

    /**
     * Returns true if input pushes an integer.
     *
     * @param insn {@link AbstractInsnNode} to check.
     * @return true if input pushes an integer.
     */
    public static boolean isIntInsn(AbstractInsnNode insn) {
        int opcode = insn.getOpcode();
        return ((opcode >= Opcodes.ICONST_M1 && opcode <= Opcodes.ICONST_5)
                || opcode == Opcodes.BIPUSH
                || opcode == Opcodes.SIPUSH
                || (insn instanceof LdcInsnNode
                && ((LdcInsnNode) insn).cst instanceof Integer));
    }

    /**
     * Returns true if input pushes a long.
     *
     * @param insn {@link AbstractInsnNode} to check.
     * @return true if input pushes a long.
     */
    public static boolean isLongInsn(AbstractInsnNode insn) {
        int opcode = insn.getOpcode();
        return (opcode == Opcodes.LCONST_0
                || opcode == Opcodes.LCONST_1
                || (insn instanceof LdcInsnNode
                && ((LdcInsnNode) insn).cst instanceof Long));
    }

    /**
     * Returns {@link Integer} represented by bytecode instruction and/or
     * operand.
     *
     * @param insn {@link AbstractInsnNode} to check.
     * @return {@link Integer} represented by bytecode instruction and/or
     * operand.
     */
    public static int getIntNumber(AbstractInsnNode insn) {
        int opcode = insn.getOpcode();

        if (opcode >= Opcodes.ICONST_M1 && opcode <= Opcodes.ICONST_5) {
            return opcode - 3;
        } else if (insn instanceof IntInsnNode
                && insn.getOpcode() != Opcodes.NEWARRAY) {
            return ((IntInsnNode) insn).operand;
        } else if (insn instanceof LdcInsnNode
                && ((LdcInsnNode) insn).cst instanceof Integer) {
            return (Integer) ((LdcInsnNode) insn).cst;
        }

        throw new IllegalStateException("Unexpected instruction");
    }

    /**
     * Returns {@link Long} represented by bytecode instruction and/or
     * operand.
     *
     * @param insn {@link AbstractInsnNode} to check.
     * @return {@link Long} represented by bytecode instruction and/or
     * operand.
     */
    public static long getLongNumber(AbstractInsnNode insn) {
        int opcode = insn.getOpcode();

        if (opcode >= Opcodes.LCONST_0 && opcode <= Opcodes.LCONST_1) {
            return opcode - 9;
        } else if (insn instanceof LdcInsnNode
                && ((LdcInsnNode) insn).cst instanceof Long) {
            return (Long) ((LdcInsnNode) insn).cst;
        }

        throw new IllegalStateException("Unexpected instruction");
    }

    /**
     * Checks if input methodNode contains goto opcode.
     *
     * @param methodNode input methodNode.
     * @return true if input methodNode contains goto opcode.
     */
    public static boolean containsGoto(MethodNode methodNode) {
        for (int i = 0; i < methodNode.instructions.size(); i++) {
            AbstractInsnNode insn = methodNode.instructions.get(i);
            if (insn instanceof JumpInsnNode && insn.getOpcode() == Opcodes.GOTO) {
                return true;
            }
        }

        return false;
    }
}
