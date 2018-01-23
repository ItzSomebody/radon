package me.itzsomebody.radon.transformers;

import me.itzsomebody.radon.asm.Opcodes;
import me.itzsomebody.radon.asm.tree.*;
import me.itzsomebody.radon.utils.BytecodeUtils;
import me.itzsomebody.radon.utils.LoggerUtils;
import me.itzsomebody.radon.utils.NumberUtils;
import me.itzsomebody.radon.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Transformer that splits up integers into simple arithmetic evaluations.
 *
 * @author ItzSomebody
 * @author VincBreaker (Sorry Vinc, I just had to steal the idea of Smoke's number obfuscation lol)
 */
public class NumberObfuscation {
    /**
     * The {@link ClassNode} that will be obfuscated.
     */
    private ClassNode classNode;

    /**
     * Methods protected from obfuscation.
     */
    private ArrayList<String> exemptMethods;

    /**
     * {@link List} of {@link String}s to add to log.
     */
    private List<String> logStrings;

    /**
     * Constructor used to create a {@link NumberObfuscation} object.
     *
     * @param classNode     the {@link ClassNode} object to obfuscate.
     * @param exemptMethods {@link ArrayList} of protected {@link MethodNode}s.
     */
    public NumberObfuscation(ClassNode classNode, ArrayList<String> exemptMethods) {
        this.classNode = classNode;
        this.exemptMethods = exemptMethods;
        logStrings = new ArrayList<>();
        obfuscate();
    }

    /**
     * Applies obfuscation to {@link NumberObfuscation#classNode}.
     */
    private void obfuscate() {
        logStrings.add(LoggerUtils.stdOut("Starting number obfuscation transformer"));
        ArrayList<MethodNode> methods = new ArrayList<>();
        int count = 0;
        for (MethodNode methodNode : classNode.methods) {
            if (exemptMethods.contains(classNode.name + "/" + methodNode.name)) continue;
            if (BytecodeUtils.isAbstractMethod(methodNode.access)) continue;

            for (AbstractInsnNode insn : methodNode.instructions.toArray()) {
                if (BytecodeUtils.isNumberNode(insn)) {
                    int originalNum = Math.abs(BytecodeUtils.getNumber(insn));

                    int value1 = NumberUtils.getRandomInt(90) + 20;
                    int value2 = NumberUtils.getRandomInt(value1) + value1;
                    int value3 = NumberUtils.getRandomInt(value2);
                    int value4 = originalNum - (value1 - value2 + value3); // You kids say algebra is useless???

                    InsnList insnList = new InsnList();
                    insnList.add(BytecodeUtils.getNumberInsn(value1));
                    insnList.add(BytecodeUtils.getNumberInsn(value2));
                    insnList.add(new InsnNode(Opcodes.ISUB));
                    insnList.add(BytecodeUtils.getNumberInsn(value3));
                    insnList.add(new InsnNode(Opcodes.IADD));
                    insnList.add(BytecodeUtils.getNumberInsn(value4));
                    insnList.add(new InsnNode(Opcodes.IADD));
                    String methodName = StringUtils.crazyString();
                    MethodNode method = new MethodNode(Opcodes.ACC_PRIVATE + Opcodes.ACC_BRIDGE + Opcodes.ACC_SYNTHETIC + Opcodes.ACC_STATIC, methodName, "()I", null, null);
                    method.instructions = insnList;
                    method.instructions.add(new InsnNode(Opcodes.IRETURN));
                    methods.add(method);

                    methodNode.instructions.set(insn, new MethodInsnNode(Opcodes.INVOKESTATIC, classNode.name, methodName, "()I", false));
                    count++;
                }
            }
        }
        classNode.methods.addAll(methods);

        logStrings.add(LoggerUtils.stdOut("Finished obfuscating numbers"));
        logStrings.add(LoggerUtils.stdOut("Obfuscated " + String.valueOf(count) + " numbers"));
    }

    /**
     * Returns {@link String}s to add to log.
     *
     * @return {@link String}s to add to log.
     */
    public List<String> getLogStrings() {
        return this.logStrings;
    }
}
