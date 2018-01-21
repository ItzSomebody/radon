package me.itzsomebody.radon.transformers.invokedynamic;

import me.itzsomebody.radon.asm.Handle;
import me.itzsomebody.radon.asm.Opcodes;
import me.itzsomebody.radon.asm.Type;
import me.itzsomebody.radon.asm.tree.*;
import me.itzsomebody.radon.utils.BytecodeUtils;
import me.itzsomebody.radon.utils.LoggerUtils;
import me.itzsomebody.radon.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Transformer that applies an InvokeDynamic obfuscation to {@link LightInvokeDynamic#classNode} that
 * produces opcode via string concatenation.
 * <p>
 * i.e. -> "1" + "8" + "2" = "182"
 *
 * @author ItzSomebody
 * @author Licel (transformer based off of an old design of Stringer's)
 */
public class LightInvokeDynamic {
    /**
     * The {@link ClassNode} that will be obfuscated.
     */
    private ClassNode classNode;

    /**
     * The {@link Handle} used to produce an InvokeDynamic handle to the bootstrap method.
     */
    private Handle bsmHandle;

    /**
     * Methods protected from obfuscation.
     */
    private ArrayList<String> exemptMethods;

    /**
     * {@link List} of {@link String}s to add to log.
     */
    private List<String> logStrings;

    /**
     * Constructor used to create a {@link LightInvokeDynamic} object.
     *
     * @param classNode     the {@link ClassNode} object to obfuscate.
     * @param bsmName       the path of the bootstrap method.
     * @param spigotMode    indication to apply a handle that is compatible with Spigot.
     * @param exemptMethods {@link ArrayList} of protected {@link MethodNode}s.
     */
    public LightInvokeDynamic(ClassNode classNode, String bsmName, boolean spigotMode, ArrayList<String> exemptMethods) {
        String[] split = bsmName.split("\\.");
        this.classNode = classNode;
        this.bsmHandle = new Handle(Opcodes.H_INVOKESTATIC,
                (!spigotMode) ? "L" + split[0] + ";" : split[0],
                split[1],
                "(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;",
                false);
        this.exemptMethods = exemptMethods;
        logStrings = new ArrayList<>();
        obfuscate();
    }

    /**
     * Applies obfuscation to {@link LightInvokeDynamic#classNode}.
     */
    private void obfuscate() {
        if (classNode.version < 51) return; // Java 6 doesn't support InvokeDynamic
        logStrings.add(LoggerUtils.stdOut("Starting light invokedynamic transformer"));
        int count = 0;
        for (MethodNode methodNode : classNode.methods) {
            if (exemptMethods.contains(classNode.name + "." + methodNode.name + methodNode.desc)) continue;
            if (BytecodeUtils.isAbstractMethod(methodNode.access)) continue;

            for (AbstractInsnNode insn : methodNode.instructions.toArray()) {
                if (insn instanceof MethodInsnNode) {
                    MethodInsnNode methodInsnNode = (MethodInsnNode) insn;
                    if (!methodInsnNode.owner.startsWith("java/lang/reflect")
                            && !methodInsnNode.owner.startsWith("java/lang/Class")) {
                        boolean isStatic = (methodInsnNode.getOpcode() == Opcodes.INVOKESTATIC);
                        String newSig = isStatic ? methodInsnNode.desc : methodInsnNode.desc.replace("(", "(Ljava/lang/Object;");
                        Type origReturnType = Type.getReturnType(newSig);
                        Type[] args = Type.getArgumentTypes(newSig);
                        for (int j = 0; j < args.length; j++) {
                            args[j] = BytecodeUtils.genericType(args[j]);
                        }
                        newSig = Type.getMethodDescriptor(origReturnType, args);
                        String opcode1 = String.valueOf((insn.getOpcode() / 100));
                        String opcode2 = String.valueOf(((insn.getOpcode() / 10) % 10));
                        String opcode3 = String.valueOf((insn.getOpcode() % 10));

                        switch (methodInsnNode.getOpcode()) {
                            case Opcodes.INVOKESTATIC: // invokestatic opcode
                            case Opcodes.INVOKEVIRTUAL: // invokevirtual opcode
                            case Opcodes.INVOKEINTERFACE: // invokeinterface opcode
                                methodNode.instructions.set(insn, new InvokeDynamicInsnNode(
                                        StringUtils.crazyString(),
                                        newSig,
                                        bsmHandle,
                                        opcode1,
                                        opcode2,
                                        opcode3,
                                        methodInsnNode.owner.replaceAll("/", "."),
                                        methodInsnNode.name,
                                        methodInsnNode.desc));
                                count++;
                                break;
                            default:
                                break;
                        }
                    }
                }
            }
        }
        logStrings.add(LoggerUtils.stdOut("Finished adding light invokedynamics"));
        logStrings.add(LoggerUtils.stdOut("Added light invokedynamics " + String.valueOf(count) + " times"));
    }

    /**
     * Returns {@link String}s to add to log.
     *
     * @return {@link String}s to add to log.
     */
    public List<String> getLogStrings() {
        return this.logStrings;
    }

    /* Method used for ASMifier
     private static Object LightInvokeDynamic(
            //
             * MethodHandles.Lookup lookup,
             * String callerName,
             * MethodType callerType,
             * int originalOpcode,
             * String originalClassName,
             * String originalMethodName,
             * String originalMethodSignature
            //

            Object lookupName,
            Object callerName,
            Object callerType,
            Object opcode1,
            Object opcode2,
            Object opcode3,
            Object originalClassName,
            Object originalMethodName,
            Object originalMethodSignature

            ) {

        MethodHandle mh = null;
        try {
            // variables initialization
            Class clazz = Class.forName(originalClassName.toString());
            ClassLoader currentClassLoader = InvokeDynamic.class.getClassLoader();
            MethodType originalMethodType = MethodType.fromMethodDescriptorString(originalMethodSignature.toString(), currentClassLoader);
            // lookup method handle
            int originalOpcode = Integer.valueOf(String.valueOf(opcode1) + String.valueOf(opcode2) + String.valueOf(opcode3));

            MethodHandles.Lookup lookup = (MethodHandles.Lookup) lookupName;

            switch (originalOpcode) {
                case Opcodes.INVOKESTATIC: // invokestatic opcode
                    mh = lookup.findStatic(clazz, originalMethodName.toString(), originalMethodType);
                    break;
                case Opcodes.INVOKEVIRTUAL: // invokevirtual opcode
                case Opcodes.INVOKEINTERFACE: // invokeinterface opcode
                    mh = lookup.findVirtual(clazz, originalMethodName.toString(), originalMethodType);
                    break;
                default:
                    throw new BootstrapMethodError();
            }
            mh = mh.asType((MethodType)callerType);
        } catch (Exception ex) {
            throw new BootstrapMethodError();
        }
        return new ConstantCallSite(mh);
    }
    */
}
