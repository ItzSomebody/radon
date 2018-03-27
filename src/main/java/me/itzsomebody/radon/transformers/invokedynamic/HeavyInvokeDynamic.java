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

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Transformer that applies an InvokeDynamic obfuscation to field and
 * (virtual and static) method access.
 *
 * @author ItzSomebody.
 */
public class HeavyInvokeDynamic extends AbstractTransformer {
    // Magic numbers
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
        Handle bsmHandle = new Handle(H_INVOKESTATIC,
                bsmPath[0],
                bsmPath[1],
                "(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;" +
                        "Ljava/lang/Object;Ljava/lang/Object;" +
                        "Ljava/lang/Object;Ljava/lang/Object;" +
                        "Ljava/lang/Object;)Ljava/lang/Object;",
                false);

        ArrayList<String> finals = new ArrayList<>();
        this.classNodes().forEach(classNode -> {
            classNode.fields.stream().filter(fieldNode -> Modifier.isFinal(fieldNode.access)).forEach(fieldNode -> {
                finals.add(classNode.name + '.' + fieldNode.name);
            });
        });
        this.classNodes().stream().filter(classNode -> !this.classExempted(classNode.name)
                && classNode.version >= 51).forEach(classNode -> {
            classNode.methods.stream().filter(methodNode -> !this.methodExempted(classNode.name + '.' + methodNode.name + methodNode.desc)
                    && !BytecodeUtils.isAbstractMethod(methodNode.access)).forEach(methodNode -> {
                for (AbstractInsnNode insn : methodNode.instructions.toArray()) {
                    if (this.methodSize(methodNode) > 60000) break;
                    if (insn instanceof MethodInsnNode) {
                        MethodInsnNode methodInsnNode = (MethodInsnNode) insn;
                        boolean isStatic = (methodInsnNode.getOpcode() == INVOKESTATIC);
                        String newSig =
                                isStatic ? methodInsnNode.desc : methodInsnNode.desc.replace("(", "(Ljava/lang/Object;");
                        switch (methodInsnNode.getOpcode()) {
                            case INVOKESTATIC: // invokestatic opcode
                                methodNode.instructions.set(insn, new InvokeDynamicInsnNode(
                                        StringUtils.crazyString(),
                                        newSig,
                                        bsmHandle,
                                        this.METHOD_INVOCATION,
                                        this.STATIC_INVOCATION,
                                        this.encOwner(methodInsnNode.owner.replaceAll("/", ".")),
                                        this.encName(methodInsnNode.name),
                                        this.encDesc(methodInsnNode.desc)));
                                counter.incrementAndGet();
                                break;
                            case INVOKEVIRTUAL: // invokevirtual opcode
                            case INVOKEINTERFACE: // invokeinterface opcode
                                methodNode.instructions.set(insn, new InvokeDynamicInsnNode(
                                        StringUtils.crazyString(),
                                        newSig,
                                        bsmHandle,
                                        this.METHOD_INVOCATION,
                                        this.VIRTUAL_INVOCATION,
                                        this.encOwner(methodInsnNode.owner.replaceAll("/", ".")),
                                        this.encName(methodInsnNode.name),
                                        this.encDesc(methodInsnNode.desc)));
                                counter.incrementAndGet();
                                break;
                            default:
                                break;
                        }
                    } else if (insn instanceof FieldInsnNode) {
                        FieldInsnNode fieldInsnNode = (FieldInsnNode) insn;

                        if (finals.contains(fieldInsnNode.owner + '.' + fieldInsnNode.name)) {
                            continue;
                        }
                        boolean isStatic = (fieldInsnNode.getOpcode() == GETSTATIC
                                || fieldInsnNode.getOpcode() == PUTSTATIC);
                        boolean isSetter = (fieldInsnNode.getOpcode() == PUTFIELD
                                || fieldInsnNode.getOpcode() == PUTSTATIC);
                        String newSig
                                = (isSetter) ? "(" + fieldInsnNode.desc + ")V" : "()" + fieldInsnNode.desc;
                        if (!isStatic)
                            newSig = newSig.replace("(", "(Ljava/lang/Object;");
                        Type type = Type.getType(fieldInsnNode.desc);
                        String wrappedDescription = type.getClassName();
                        switch (fieldInsnNode.getOpcode()) {
                            case GETFIELD:
                                methodNode.instructions.set(insn, new InvokeDynamicInsnNode(
                                        StringUtils.crazyString(),
                                        newSig,
                                        bsmHandle,
                                        this.FIELD_INVOCATION,
                                        this.VIRTUAL_GETTER,
                                        this.encOwner(fieldInsnNode.owner.replaceAll("/", ".")),
                                        this.encName(fieldInsnNode.name),
                                        this.encDesc(wrappedDescription)));
                                counter.incrementAndGet();
                                break;
                            case GETSTATIC:
                                methodNode.instructions.set(insn, new InvokeDynamicInsnNode(
                                        StringUtils.crazyString(),
                                        newSig,
                                        bsmHandle,
                                        this.FIELD_INVOCATION,
                                        this.STATIC_GETTER,
                                        this.encOwner(fieldInsnNode.owner.replaceAll("/", ".")),
                                        this.encName(fieldInsnNode.name),
                                        this.encDesc(wrappedDescription)));
                                counter.incrementAndGet();
                                break;
                            case PUTFIELD:
                                methodNode.instructions.set(insn, new InvokeDynamicInsnNode(
                                        StringUtils.crazyString(),
                                        newSig,
                                        bsmHandle,
                                        this.FIELD_INVOCATION,
                                        this.VIRTUAL_SETTER,
                                        this.encOwner(fieldInsnNode.owner.replaceAll("/", ".")),
                                        this.encName(fieldInsnNode.name),
                                        this.encDesc(wrappedDescription)));
                                counter.incrementAndGet();
                                break;
                            case PUTSTATIC:
                                methodNode.instructions.set(insn, new InvokeDynamicInsnNode(
                                        StringUtils.crazyString(),
                                        newSig,
                                        bsmHandle,
                                        this.FIELD_INVOCATION,
                                        this.STATIC_SETTER,
                                        this.encOwner(fieldInsnNode.owner.replaceAll("/", ".")),
                                        this.encName(fieldInsnNode.name),
                                        this.encDesc(wrappedDescription)));
                                counter.incrementAndGet();
                                break;
                            default:
                                break;
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

    /**
     * Returns string with a simple encryption.
     *
     * @param msg inputed string to be encrypted.
     * @return string with a simple encryption.
     */
    private String encOwner(String msg) {
        char[] chars = msg.toCharArray();
        char[] encChars = new char[chars.length];

        for (int i = 0; i < chars.length; i++) {
            encChars[i] = (char) (chars[i] ^ 4382);
        }

        return new String(encChars);
    }

    /**
     * Returns string with a simple encryption.
     *
     * @param msg inputed string to be encrypted.
     * @return string with a simple encryption.
     */
    private String encName(String msg) {
        char[] chars = msg.toCharArray();
        char[] encChars = new char[chars.length];

        for (int i = 0; i < chars.length; i++) {
            encChars[i] = (char) (chars[i] ^ 3940);
        }

        return new String(encChars);
    }

    /**
     * Returns string with a simple encryption.
     *
     * @param msg inputed string to be encrypted.
     * @return string with a simple encryption.
     */
    private String encDesc(String msg) {
        char[] chars = msg.toCharArray();
        char[] encChars = new char[chars.length];

        for (int i = 0; i < chars.length; i++) {
            encChars[i] = (char) (chars[i] ^ 5739);
        }

        return new String(encChars);
    }
}