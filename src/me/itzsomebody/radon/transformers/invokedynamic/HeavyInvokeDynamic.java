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
 * Transformer that applies an InvokeDynamic obfuscation to field and (virtual and static) method access.
 *
 * @author ItzSomebody.
 */
public class HeavyInvokeDynamic extends AbstractTransformer {
    /**
     * Applies obfuscation.
     */
    public void obfuscate() {
        logStrings.add(LoggerUtils.stdOut("------------------------------------------------"));
        logStrings.add(LoggerUtils.stdOut("Starting heavy invokedynamic transformer"));
        AtomicInteger counter = new AtomicInteger();
        long current = System.currentTimeMillis();
        String[] bsmPath = new String[]{StringUtils.randomClass(classNames()), StringUtils.crazyString()};
        Handle bsmHandle = new Handle(Opcodes.H_INVOKESTATIC,
                bsmPath[0],
                bsmPath[1],
                "(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;",
                false);

        List<String> finalFields = new ArrayList<>();
        classNodes().stream().filter(classNode -> classNode.fields != null).forEach(classNode -> {
            classNode.fields.stream().filter(fieldNode -> (fieldNode.access & ACC_FINAL) != 0).forEach(fieldNode -> {
                finalFields.add(classNode.name + '.' + fieldNode.name);
            });
        });
        classNodes().stream().filter(classNode -> !classExempted(classNode.name) && classNode.version >= 51).forEach(classNode -> {
            classNode.methods.stream().filter(methodNode -> !methodExempted(classNode.name + '.' + methodNode.name + methodNode.desc)
                    && !BytecodeUtils.isAbstractMethod(methodNode.access)).forEach(methodNode -> {
                for (AbstractInsnNode insn : methodNode.instructions.toArray()) {
                    if (methodSize(methodNode) > 60000) break;
                    if (insn instanceof MethodInsnNode) {
                        MethodInsnNode methodInsnNode = (MethodInsnNode) insn;
                        boolean isStatic = (methodInsnNode.getOpcode() == Opcodes.INVOKESTATIC);
                        String newSig = isStatic ? methodInsnNode.desc : methodInsnNode.desc.replace("(", "(Ljava/lang/Object;");
                        Type origReturnType = Type.getReturnType(newSig);
                        Type[] args = Type.getArgumentTypes(newSig);
                        for (int j = 0; j < args.length; j++) {
                            args[j] = BytecodeUtils.genericType(args[j]);
                        }
                        newSig = Type.getMethodDescriptor(origReturnType, args);
                        switch (methodInsnNode.getOpcode()) {
                            case Opcodes.INVOKESTATIC: // invokestatic opcode
                                methodNode.instructions.set(insn, new InvokeDynamicInsnNode(
                                        StringUtils.crazyString(),
                                        newSig,
                                        bsmHandle,
                                        1,
                                        1,
                                        methodInsnNode.owner.replaceAll("/", "."),
                                        methodInsnNode.name,
                                        methodInsnNode.desc));
                                counter.incrementAndGet();
                                break;
                            case Opcodes.INVOKEVIRTUAL: // invokevirtual opcode
                            case Opcodes.INVOKEINTERFACE: // invokeinterface opcode
                                methodNode.instructions.set(insn, new InvokeDynamicInsnNode(
                                        StringUtils.crazyString(),
                                        newSig,
                                        bsmHandle,
                                        1,
                                        0,
                                        methodInsnNode.owner.replaceAll("/", "."),
                                        methodInsnNode.name,
                                        methodInsnNode.desc));
                                counter.incrementAndGet();
                                break;
                            default:
                                break;
                        }
                    } else if (insn instanceof FieldInsnNode
                            && !BytecodeUtils.isPrimitiveType(((FieldInsnNode) insn).desc)
                            && !((FieldInsnNode) insn).desc.startsWith("[")
                            && !methodNode.name.equals("<init>")) {
                        FieldInsnNode fieldInsnNode = (FieldInsnNode) insn;
                        if (!finalFields.contains(fieldInsnNode.owner + '.' + fieldInsnNode.name)) {
                            boolean isStatic = (fieldInsnNode.getOpcode() == Opcodes.GETSTATIC || fieldInsnNode.getOpcode() == Opcodes.PUTSTATIC);
                            boolean isSetter = (fieldInsnNode.getOpcode() == Opcodes.PUTFIELD || fieldInsnNode.getOpcode() == Opcodes.PUTSTATIC);
                            String newSig = (isSetter) ? "(" + fieldInsnNode.desc + ")V" : "()" + fieldInsnNode.desc;
                            if (!isStatic) newSig = newSig.replace("(", "(Ljava/lang/Object;");
                            Type type = Type.getType(fieldInsnNode.desc);
                            String wrappedDescription = type.getClassName();
                            switch (fieldInsnNode.getOpcode()) {
                                case Opcodes.GETFIELD:
                                    methodNode.instructions.set(insn, new InvokeDynamicInsnNode(
                                            StringUtils.crazyString(),
                                            newSig,
                                            bsmHandle,
                                            0,
                                            0,
                                            fieldInsnNode.owner.replaceAll("/", "."),
                                            fieldInsnNode.name,
                                            wrappedDescription));
                                    counter.incrementAndGet();
                                    break;
                                case Opcodes.GETSTATIC:
                                    methodNode.instructions.set(insn, new InvokeDynamicInsnNode(
                                            StringUtils.crazyString(),
                                            newSig,
                                            bsmHandle,
                                            0,
                                            1,
                                            fieldInsnNode.owner.replaceAll("/", "."),
                                            fieldInsnNode.name,
                                            wrappedDescription));
                                    counter.incrementAndGet();
                                    break;
                                case Opcodes.PUTFIELD:
                                    methodNode.instructions.set(insn, new InvokeDynamicInsnNode(
                                            StringUtils.crazyString(),
                                            newSig,
                                            bsmHandle,
                                            0,
                                            2,
                                            fieldInsnNode.owner.replaceAll("/", "."),
                                            fieldInsnNode.name,
                                            wrappedDescription));
                                    counter.incrementAndGet();
                                    break;
                                case Opcodes.PUTSTATIC:
                                    methodNode.instructions.set(insn, new InvokeDynamicInsnNode(
                                            StringUtils.crazyString(),
                                            newSig,
                                            bsmHandle,
                                            0,
                                            3,
                                            fieldInsnNode.owner.replaceAll("/", "."),
                                            fieldInsnNode.name,
                                            wrappedDescription));
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
            classNode.methods.add(InvokeDynamicBSM.heavyBSM(bsmPath[1], classNode.name));
            classNode.access = BytecodeUtils.accessFixer(classNode.access);
        });
        logStrings.add(LoggerUtils.stdOut("Hid " + counter + " field and/or method accesses with invokedynamics."));
        logStrings.add(LoggerUtils.stdOut("Finished. [" + tookThisLong(current) + "ms]"));
    }
}