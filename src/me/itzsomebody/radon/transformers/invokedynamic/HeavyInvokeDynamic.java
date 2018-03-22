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
    /*
     * Magic numbers.
     */
    private int METHOD_INVOCATION = 1;
    private int FIELD_INVOCATION = 0;

    private int STATIC_INVOCATION = 1;
    private int VIRTUAL_INVOCATION = 0;

    private int VIRTUAL_GETTER = 0;
    private int STATIC_GETTER = 1;
    private int VIRTUAL_SETTER = 2;
    private int STATIC_SETTER = 3;

    /**
     * Applies obfuscation.
     */
    public void obfuscate() {
        AtomicInteger counter = new AtomicInteger();
        long current = System.currentTimeMillis();
        this.logStrings.add(LoggerUtils.stdOut("------------------------------------------------"));
        this.logStrings.add(LoggerUtils.stdOut("Started heavy invokedynamic transformer"));
        String[] bsmPath = new String[]{StringUtils.randomClass(classNames()), StringUtils.crazyString()};
        Handle bsmHandle = new Handle(Opcodes.H_INVOKESTATIC,
                bsmPath[0],
                bsmPath[1],
                "(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;",
                false);

        List<String> finalFields = new ArrayList<>();
        this.classNodes().stream().filter(classNode -> classNode.fields != null).forEach(classNode -> {
            classNode.fields.stream().filter(fieldNode -> (fieldNode.access & ACC_FINAL) != 0).forEach(fieldNode -> {
                finalFields.add(classNode.name + '.' + fieldNode.name);
            });
        });
        this.classNodes().stream().filter(classNode -> !this.classExempted(classNode.name) && classNode.version >= 51).forEach(classNode -> {
            classNode.methods.stream().filter(methodNode -> !this.methodExempted(classNode.name + '.' + methodNode.name + methodNode.desc)
                    && !BytecodeUtils.isAbstractMethod(methodNode.access)).forEach(methodNode -> {
                for (AbstractInsnNode insn : methodNode.instructions.toArray()) {
                    if (this.methodSize(methodNode) > 60000) break;
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
                                        this.METHOD_INVOCATION,
                                        this.STATIC_INVOCATION,
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
                                        this.METHOD_INVOCATION,
                                        this.VIRTUAL_INVOCATION,
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
                                            this.FIELD_INVOCATION,
                                            this.VIRTUAL_GETTER,
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
                                            this.FIELD_INVOCATION,
                                            this.STATIC_GETTER,
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
                                            this.FIELD_INVOCATION,
                                            this.VIRTUAL_SETTER,
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
                                            this.FIELD_INVOCATION,
                                            this.STATIC_SETTER,
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

        this.classNodes().stream().filter(classNode -> classNode.name.equals(bsmPath[0])).forEach(classNode -> {
            classNode.methods.add(InvokeDynamicBSM.heavyBSM(bsmPath[1], classNode.name));
            classNode.access = BytecodeUtils.accessFixer(classNode.access);
        });
        this.logStrings.add(LoggerUtils.stdOut("Hid " + counter + " field and/or method accesses with invokedynamics."));
        this.logStrings.add(LoggerUtils.stdOut("Finished. [" + tookThisLong(current) + "ms]"));
    }
}