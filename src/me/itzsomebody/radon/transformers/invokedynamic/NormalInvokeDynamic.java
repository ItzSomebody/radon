package me.itzsomebody.radon.transformers.invokedynamic;

import me.itzsomebody.radon.asm.Handle;
import me.itzsomebody.radon.asm.Opcodes;
import me.itzsomebody.radon.asm.Type;
import me.itzsomebody.radon.asm.tree.*;
import me.itzsomebody.radon.utils.BytecodeUtils;
import me.itzsomebody.radon.utils.LoggerUtils;
import me.itzsomebody.radon.utils.NumberUtils;
import me.itzsomebody.radon.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Transformer that applies an InvokeDynamic obfuscation to {@link NormalInvokeDynamic#classNode} that produces
 * produces opcodes via an int lookup.
 * <p>
 * 0 = InvokeStatic
 * -1 = InvokeVirtual
 * 1 = InvokeInterface
 *
 * @author ItzSomebody
 * @author Licel (transformer based off of an old design of Stringer's)
 */
public class NormalInvokeDynamic {
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
     * Constructor used to create a {@link NormalInvokeDynamic} object.
     *
     * @param classNode     the {@link ClassNode} object to obfuscate.
     * @param bsmName       the path of the bootstrap method.
     * @param spigotMode    indication to apply a handle that is compatible with Spigot.
     * @param exemptMethods {@link ArrayList} of protected {@link MethodNode}s.
     */
    public NormalInvokeDynamic(ClassNode classNode, String bsmName, boolean spigotMode, ArrayList<String> exemptMethods) {
        String[] split = bsmName.split("\\.");
        this.classNode = classNode;
        this.bsmHandle = new Handle(Opcodes.H_INVOKESTATIC,
                (!spigotMode) ? "L" + split[0] + ";" : split[0],
                split[1],
                "(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;",
                false);
        this.exemptMethods = exemptMethods;
        logStrings = new ArrayList<>();
        obfuscate();
    }

    /**
     * Applies obfuscation to {@link NormalInvokeDynamic#classNode}.
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
                        // 0 = invokestatic
                        // -1 = invokevirtual
                        // 1 = invokeinterface

                        String opcode1;
                        String opcode2;
                        String opcode3;
                        String opcode4;
                        String opcode5;
                        int coreOpcode;
                        switch (insn.getOpcode()) {
                            case 184: // InvokeStatic Opcode
                                coreOpcode = 0;
                                break;
                            case 182: // InvokeVirtual Opcode
                                coreOpcode = -1;
                                break;
                            case 185: // InvokeInterface Opcode
                            default:
                                coreOpcode = 1;
                                break;
                        }
                        switch (NumberUtils.getRandomInt(5)) {
                            case 0:
                                opcode1 = String.valueOf(coreOpcode);
                                opcode2 = String.valueOf(NumberUtils.getRandomInt(3) + 2);
                                opcode3 = String.valueOf(NumberUtils.getRandomInt(3) + 2);
                                opcode4 = String.valueOf(NumberUtils.getRandomInt(3) + 2);
                                opcode5 = String.valueOf(NumberUtils.getRandomInt(3) + 2);
                                break;
                            case 1:
                                opcode1 = String.valueOf(NumberUtils.getRandomInt(3) + 2);
                                opcode2 = String.valueOf(coreOpcode);
                                opcode3 = String.valueOf(NumberUtils.getRandomInt(3) + 2);
                                opcode4 = String.valueOf(NumberUtils.getRandomInt(3) + 2);
                                opcode5 = String.valueOf(NumberUtils.getRandomInt(3) + 2);
                                break;
                            case 2:
                                opcode1 = String.valueOf(NumberUtils.getRandomInt(3) + 2);
                                opcode2 = String.valueOf(NumberUtils.getRandomInt(3) + 2);
                                opcode3 = String.valueOf(coreOpcode);
                                opcode4 = String.valueOf(NumberUtils.getRandomInt(3) + 2);
                                opcode5 = String.valueOf(NumberUtils.getRandomInt(3) + 2);
                                break;
                            case 3:
                                opcode1 = String.valueOf(NumberUtils.getRandomInt(3) + 2);
                                opcode2 = String.valueOf(NumberUtils.getRandomInt(3) + 2);
                                opcode3 = String.valueOf(NumberUtils.getRandomInt(3) + 2);
                                opcode4 = String.valueOf(coreOpcode);
                                opcode5 = String.valueOf(NumberUtils.getRandomInt(3) + 2);
                                break;
                            default: // 4
                                opcode1 = String.valueOf(NumberUtils.getRandomInt(3) + 2);
                                opcode2 = String.valueOf(NumberUtils.getRandomInt(3) + 2);
                                opcode3 = String.valueOf(NumberUtils.getRandomInt(3) + 2);
                                opcode4 = String.valueOf(NumberUtils.getRandomInt(3) + 2);
                                opcode5 = String.valueOf(coreOpcode);
                                break;
                        }

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
                                        opcode4,
                                        opcode5,
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
        logStrings.add(LoggerUtils.stdOut("Finished adding normal invokedynamics"));
        logStrings.add(LoggerUtils.stdOut("Added normal invokedynamics " + String.valueOf(count) + " times"));
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
    private static Object NormalInvokeDynamic(
            /*
             * MethodHandles.Lookup lookup,
             * String callerName,
             * MethodType callerType,
             * int originalOpcode,
             * String originalClassName,
             * String originalMethodName,
             * String originalMethodSignature


            Object lookupName,
            Object callerName,
            Object callerType,
            Object opcode1,
            Object opcode2,
            Object opcode3,
            Object opcode4,
            Object opcode5,
            Object originalClassName,
            Object originalMethodName,
            Object originalMethodSignature

            ) {

        MethodHandle mh = null;
        try {
            // variables initialization
            Class clazz = Class.forName(originalClassName.toString());
            ClassLoader currentClassLoader = NormalInvokeDynamic.class.getClassLoader();
            MethodType originalMethodType = MethodType.fromMethodDescriptorString(originalMethodSignature.toString(), currentClassLoader);


            // Opcode lookup
            // 0 = invokestatic
            // -1 = invokevirtual
            // 1 = invokeinterface
            int originalOpcode;
            if (Integer.valueOf(String.valueOf(opcode1)) == 0
            		|| Integer.valueOf(String.valueOf(opcode2)) == 0
            		|| Integer.valueOf(String.valueOf(opcode3)) == 0
            		|| Integer.valueOf(String.valueOf(opcode4)) == 0
            		|| Integer.valueOf(String.valueOf(opcode5)) == 0) {
            	originalOpcode = Integer.valueOf(String.valueOf(String.valueOf(1 << Integer.valueOf(String.valueOf(0))) + String.valueOf((Integer.valueOf(String.valueOf(0)) + 1) << 3) + String.valueOf((Integer.valueOf(String.valueOf(0)) + 1) << 2)));
            	// 184
            } else if (Integer.valueOf(String.valueOf(opcode1)) == -1
            		|| Integer.valueOf(String.valueOf(opcode2)) == -1
            		|| Integer.valueOf(String.valueOf(opcode3)) == -1
            		|| Integer.valueOf(String.valueOf(opcode4)) == -1
            		|| Integer.valueOf(String.valueOf(opcode5)) == -1) {
            	originalOpcode = Integer.valueOf(String.valueOf(String.valueOf(1 << Integer.valueOf(String.valueOf(0))) + String.valueOf((Integer.valueOf(String.valueOf(0)) + 1) << 3) + String.valueOf((Integer.valueOf(String.valueOf(0)) + 1) << 1)));
            	// 182
            } else {
            	originalOpcode = Integer.valueOf(String.valueOf(String.valueOf(1 << Integer.valueOf(String.valueOf(0))) + String.valueOf((Integer.valueOf(String.valueOf(0)) + 1) << 3) + String.valueOf(((Integer.valueOf(String.valueOf(0)) + 1) << 2) + 1)));
            	// 185
            }

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
        	ex.printStackTrace();
            throw new BootstrapMethodError();
        }
        return new ConstantCallSite(mh);
    }
    */
}
