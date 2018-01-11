package me.itzsomebody.radon.utils;

import me.itzsomebody.radon.asm.Opcodes;
import me.itzsomebody.radon.asm.Type;
import me.itzsomebody.radon.asm.tree.*;

import java.util.HashMap;

/**
 * Utils used for operating on bytecode.
 *
 * @author ItzSomebody
 * @author Stringer dev team (who made {@link BytecodeUtils#genericType(Type)})
 */
public class BytecodeUtils {
    /**
     * Returns the generic {@link Type}.
     *
     * @param type input {@link Type}.
     * @return the generic {@link Type}.
     */
    public static Type genericType(Type type) {
        Type newType;
        switch (type.getSort()) {
            case Type.OBJECT:
                newType = Type.getType(Object.class);
                break;
            default:
                newType = type;
                break;
        }
        return newType;
    }

    /**
     * Returns ICONST_0 or ICONST_1 based on {@link MiscUtils#getRandomInt(int)}.
     *
     * @return ICONST_0 or ICONST_1 based on {@link MiscUtils#getRandomInt(int)}.
     */
    public static InsnNode randTrueFalse() {
        return (MiscUtils.getRandomInt(2) == 1) ? new InsnNode(Opcodes.ICONST_1) : new InsnNode(Opcodes.ICONST_0);
    }

    /**
     * Returns true if input is a known ICONST else return false.
     *
     * @param ain the {@link AbstractInsnNode} to check.
     * @return true if input is a known ICONST else return false.
     */
    public static boolean isIConst(AbstractInsnNode ain) {
        switch (ain.getOpcode()) {
            case Opcodes.ICONST_0:
            case Opcodes.ICONST_1:
            case Opcodes.ICONST_2:
            case Opcodes.ICONST_3:
            case Opcodes.ICONST_4:
            case Opcodes.ICONST_5:
            case Opcodes.ICONST_M1:
                return true;
            default:
                return false;
        }
    }

    /**
     * Check if the input {@link MethodNode} has the same name as another method in the classpath.
     *
     * @param methodNode the {@link MethodNode} to check.
     * @param clazz      the {@link ClassNode} in which the input {@link MethodNode} is contained.
     * @param classes    the classpath to check.
     * @return true if input {@link MethodNode} name and description match another one in the classpath.
     */
    public static boolean hasSameMethod(MethodNode methodNode, ClassNode clazz, HashMap<String, ClassNode> classes) {
        for (ClassNode classNode : classes.values()) {
            if (classNode.name.equals(clazz.name)) continue;
            for (MethodNode method : classNode.methods) {
                if (methodNode.name.equals(method.name)) {
                    if (methodNode.desc.equals(method.desc)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Checks if the input class is a main method.
     *
     * @param clazz      {@link ClassNode} to check for main methods.
     * @param spigotMode if obfuscator should consider the input {@link ClassNode} as a Spigot/Bukkit/Bungee plugin.
     * @return true if the input {@link ClassNode} contains a main method, false if not.
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
     * Returns access modifier without private or protected so that class renaming works properly.
     *
     * @param access input access as {@link Integer}.
     * @return new {@link Integer} without restrictive flags.
     */
    public static int accessFixer(int access) {
        int a = Opcodes.ACC_PUBLIC;
        if ((access & Opcodes.ACC_NATIVE) != 0) a |= Opcodes.ACC_NATIVE;
        if ((access & Opcodes.ACC_ABSTRACT) != 0) a |= Opcodes.ACC_ABSTRACT;
        if ((access & Opcodes.ACC_ANNOTATION) != 0) a |= Opcodes.ACC_ANNOTATION;
        if ((access & Opcodes.ACC_BRIDGE) != 0) a |= Opcodes.ACC_BRIDGE;
        if ((access & Opcodes.ACC_DEPRECATED) != 0) a |= Opcodes.ACC_DEPRECATED;
        if ((access & Opcodes.ACC_ENUM) != 0) a |= Opcodes.ACC_ENUM;
        if ((access & Opcodes.ACC_FINAL) != 0) a |= Opcodes.ACC_FINAL;
        if ((access & Opcodes.ACC_INTERFACE) != 0) a |= Opcodes.ACC_INTERFACE;
        if ((access & Opcodes.ACC_MANDATED) != 0) a |= Opcodes.ACC_MANDATED;
        if ((access & Opcodes.ACC_MODULE) != 0) a |= Opcodes.ACC_MODULE;
        if ((access & Opcodes.ACC_OPEN) != 0) a |= Opcodes.ACC_OPEN;
        if ((access & Opcodes.ACC_STATIC) != 0) a |= Opcodes.ACC_STATIC;
        if ((access & Opcodes.ACC_STATIC_PHASE) != 0) a |= Opcodes.ACC_STATIC_PHASE;
        if ((access & Opcodes.ACC_STRICT) != 0) a |= Opcodes.ACC_STRICT;
        if ((access & Opcodes.ACC_SUPER) != 0) a |= Opcodes.ACC_SUPER;
        if ((access & Opcodes.ACC_SYNCHRONIZED) != 0) a |= Opcodes.ACC_SYNCHRONIZED;
        if ((access & Opcodes.ACC_SYNTHETIC) != 0) a |= Opcodes.ACC_SYNTHETIC;
        if ((access & Opcodes.ACC_TRANSIENT) != 0) a |= Opcodes.ACC_TRANSIENT;
        if ((access & Opcodes.ACC_VARARGS) != 0) a |= Opcodes.ACC_VARARGS;
        if ((access & Opcodes.ACC_VOLATILE) != 0) a |= Opcodes.ACC_VOLATILE;
        return a;
    }
}
