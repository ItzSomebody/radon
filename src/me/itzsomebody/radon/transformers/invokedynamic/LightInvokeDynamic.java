package me.itzsomebody.radon.transformers.invokedynamic;

import me.itzsomebody.radon.asm.Handle;
import me.itzsomebody.radon.asm.Opcodes;
import me.itzsomebody.radon.asm.Type;
import me.itzsomebody.radon.asm.tree.*;
import me.itzsomebody.radon.methods.InvokeDynamicBSM;
import me.itzsomebody.radon.transformers.AbstractTransformer;
import me.itzsomebody.radon.utils.BytecodeUtils;
import me.itzsomebody.radon.utils.LoggerUtils;
import me.itzsomebody.radon.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Transformer that applies an InvokeDynamic obfuscation which
 * produces opcode via string concatenation.
 * <p>
 * i.e. -> "1" + "8" + "2" = "182"
 *
 * @author ItzSomebody
 * @author Licel (transformer based off of an old design of Stringer's)
 */
public class LightInvokeDynamic extends AbstractTransformer {
    /**
     * {@link List} of {@link String}s to add to log.
     */
    private List<String> logStrings;

    /**
     * Applies obfuscation.
     */
    public void obfuscate() {
        logStrings = new ArrayList<>();
        logStrings.add(LoggerUtils.stdOut("------------------------------------------------"));
        logStrings.add(LoggerUtils.stdOut("Starting light invokedynamic transformer"));
        AtomicInteger counter = new AtomicInteger();
        long current = System.currentTimeMillis();
        String[] bsmPath = new String[]{StringUtils.randomClass(classNames()), StringUtils.crazyString()};
        Handle bsmHandle = new Handle(Opcodes.H_INVOKESTATIC,
                bsmPath[0],
                bsmPath[1],
                "(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;",
                false);

        classNodes().stream().filter(classNode -> !classExempted(classNode.name)).filter(classNode -> classNode.version >= 51).forEach(classNode -> {
            classNode.methods.stream().filter(methodNode -> !methodExempted(classNode.name + '.' + methodNode.name + methodNode.desc))
                    .filter(methodNode -> !BytecodeUtils.isAbstractMethod(methodNode.access)).forEach(methodNode -> {
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
                                    counter.incrementAndGet();
                                    break;
                                default:
                                    break;
                            }
                        }
                    }
                }
            });
        });

        classNodes().stream().filter(classNode -> classNode.name.equals(bsmPath[0])).forEach(classNode -> {
            classNode.methods.add(InvokeDynamicBSM.lightBSM(bsmPath[1], classNode.name));
        });
        logStrings.add(LoggerUtils.stdOut("Replaced " + counter + " method invocations with invokedynamics."));
        logStrings.add(LoggerUtils.stdOut("Finished. [" + tookThisLong(current) + "ms]"));
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
