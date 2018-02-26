package me.itzsomebody.radon.transformers.invokedynamic;

import org.objectweb.asm.Handle;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;
import me.itzsomebody.radon.methods.InvokeDynamicBSM;
import me.itzsomebody.radon.transformers.AbstractTransformer;
import me.itzsomebody.radon.utils.BytecodeUtils;
import me.itzsomebody.radon.utils.LoggerUtils;
import me.itzsomebody.radon.utils.NumberUtils;
import me.itzsomebody.radon.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Transformer that applies an InvokeDynamic obfuscation to which produces
 * produces opcodes via an int lookup.
 * <p>
 * 0 = InvokeStatic
 * -1 = InvokeVirtual
 * 1 = InvokeInterface
 *
 * @author ItzSomebody
 * @author Licel (transformer based off of IndyProtector)
 */
public class NormalInvokeDynamic extends AbstractTransformer {
    /**
     * Applies obfuscation.
     */
    public void obfuscate() {
        logStrings.add(LoggerUtils.stdOut("------------------------------------------------"));
        logStrings.add(LoggerUtils.stdOut("Starting normal invokedynamic transformer"));
        AtomicInteger counter = new AtomicInteger();
        long current = System.currentTimeMillis();
        String[] bsmPath = new String[]{StringUtils.randomClass(classNames()), StringUtils.crazyString()};
        Handle bsmHandle = new Handle(Opcodes.H_INVOKESTATIC,
                bsmPath[0],
                bsmPath[1],
                "(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;",
                false);
        classNodes().stream().filter(classNode -> !classExempted(classNode.name) && classNode.version >= 51).forEach(classNode -> {
            classNode.methods.stream().filter(methodNode -> !methodExempted(classNode.name + '.' + methodNode.name + methodNode.desc))
                    .filter(methodNode -> !BytecodeUtils.isAbstractMethod(methodNode.access)).forEach(methodNode -> {
                for (AbstractInsnNode insn : methodNode.instructions.toArray()) {
                    if (methodSize(methodNode) > 60000) break;
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
            classNode.methods.add(InvokeDynamicBSM.normalBSM(bsmPath[1], classNode.name));
            classNode.access = BytecodeUtils.accessFixer(classNode.access);
        });
        logStrings.add(LoggerUtils.stdOut("Replaced " + counter + " method invocations with invokedynamics."));
        logStrings.add(LoggerUtils.stdOut("Finished. [" + tookThisLong(current) + "ms]"));
    }
}
