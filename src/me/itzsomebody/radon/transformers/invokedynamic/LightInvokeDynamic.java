package me.itzsomebody.radon.transformers.invokedynamic;

import org.objectweb.asm.Handle;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;
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
 *
 * @author ItzSomebody
 * @author Licel (transformer is based on IndyProtector)
 */
public class LightInvokeDynamic extends AbstractTransformer {
    /**
     * Applies obfuscation.
     */
    public void obfuscate() {
        logStrings.add(LoggerUtils.stdOut("------------------------------------------------"));
        logStrings.add(LoggerUtils.stdOut("Starting light invokedynamic transformer"));
        AtomicInteger counter = new AtomicInteger();
        long current = System.currentTimeMillis();
        String[] bsmPath = new String[]{StringUtils.randomClass(classNames()), StringUtils.crazyString()};
        Handle bsmHandle = new Handle(Opcodes.H_INVOKESTATIC,
                bsmPath[0],
                bsmPath[1],
                "(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;",
                false);

        classNodes().stream().filter(classNode -> !classExempted(classNode.name) && classNode.version >= 51).forEach(classNode -> {
            classNode.methods.stream().filter(methodNode -> !methodExempted(classNode.name + '.' + methodNode.name + methodNode.desc)
                    && !BytecodeUtils.isAbstractMethod(methodNode.access)).forEach(methodNode -> {
                for (AbstractInsnNode insn : methodNode.instructions.toArray()) {
                    if (methodSize(methodNode) > 60000) break;
                    if (insn instanceof MethodInsnNode
                            && insn.getOpcode() != INVOKESPECIAL) {
                        MethodInsnNode methodInsnNode = (MethodInsnNode) insn;
                        boolean isStatic = (methodInsnNode.getOpcode() == Opcodes.INVOKESTATIC);

                        String newSig = isStatic ? methodInsnNode.desc : methodInsnNode.desc.replace("(", "(Ljava/lang/Object;");
                        Type origReturnType = Type.getReturnType(newSig);
                        Type[] args = Type.getArgumentTypes(newSig);
                        for (int j = 0; j < args.length; j++) {
                            args[j] = BytecodeUtils.genericType(args[j]);
                        }

                        newSig = Type.getMethodDescriptor(origReturnType, args);
                        int opcode = (isStatic) ? 0 : 1;

                        methodNode.instructions.set(insn, new InvokeDynamicInsnNode(StringUtils.crazyString(),
                                newSig,
                                bsmHandle,
                                opcode,
                                encryptOwner(methodInsnNode.owner.replaceAll("/", ".")),
                                encryptName(methodInsnNode.name),
                                encryptDesc(methodInsnNode.desc)));
                        counter.incrementAndGet();
                    }
                }
            });
        });

        classNodes().stream().filter(classNode -> classNode.name.equals(bsmPath[0])).forEach(classNode -> {
            classNode.methods.add(InvokeDynamicBSM.lightBSM(bsmPath[1], classNode.name));
            classNode.access = BytecodeUtils.accessFixer(classNode.access);
        });
        logStrings.add(LoggerUtils.stdOut("Replaced " + counter + " method invocations with invokedynamics."));
        logStrings.add(LoggerUtils.stdOut("Finished. [" + tookThisLong(current) + "ms]"));
    }

    /**
     * Returns string with a simple encryption.
     *
     * @param owner inputed string to be encrypted.
     * @return string with a simple encryption.
     */
    private String encryptOwner(String owner) {
        char[] encClassNameChars = owner.toCharArray();
        char[] classNameChars = new char[encClassNameChars.length];
        for (int i = 0; i < encClassNameChars.length; i++) {
            classNameChars[i] = (char) (encClassNameChars[i] ^ 1029);
        }

        return new String(classNameChars);
    }

    /**
     * Returns string with a simple encryption.
     *
     * @param name inputed string to be encrypted.
     * @return string with a simple encryption.
     */
    private String encryptName(String name) {
        char[] encMethodNameChars = name.toCharArray();
        char[] methodNameChars = new char[encMethodNameChars.length];
        for (int i = 0; i < encMethodNameChars.length; i++) {
            methodNameChars[i] = (char) (encMethodNameChars[i] ^ 2038);
        }

        return new String(methodNameChars);
    }

    /**
     * Returns string with a simple encryption.
     *
     * @param desc inputed string to be encrypted.
     * @return string with a simple encryption.
     */
    private String encryptDesc(String desc) {
        char[] encDescChars = desc.toCharArray();
        char[] descChars = new char[encDescChars.length];
        for (int i = 0; i < encDescChars.length; i++) {
            descChars[i] = (char) (encDescChars[i] ^ 1928);
        }

        return new String(descChars);
    }
}
