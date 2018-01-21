package me.itzsomebody.radon.transformers;

import me.itzsomebody.radon.asm.Opcodes;
import me.itzsomebody.radon.asm.tree.*;
import me.itzsomebody.radon.utils.BytecodeUtils;
import me.itzsomebody.radon.utils.LoggerUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Transformer that splits up integers into simple arithmetic evaluations.
 *
 * @author ItzSomebody
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
        int count = 0;
        for (MethodNode methodNode : classNode.methods) {
            if (exemptMethods.contains(classNode.name + "/" + methodNode.name)) continue;
            if (BytecodeUtils.isAbstractMethod(methodNode.access)) continue;

            for (AbstractInsnNode insn : methodNode.instructions.toArray()) {
                if (insn.getOpcode() == Opcodes.BIPUSH || insn.getOpcode() == Opcodes.SIPUSH) {
                    int newNumber;
                    int number = ((IntInsnNode) insn).operand;

                    if (String.valueOf(number).endsWith("2")
                            || String.valueOf(number).endsWith("4")
                            || String.valueOf(number).endsWith("6")
                            || String.valueOf(number).endsWith("8")
                            || String.valueOf(number).endsWith("0")) { // Even number
                        newNumber = number / 2;

                        methodNode.instructions.insert(insn, new InsnNode(Opcodes.IADD));

                        if (newNumber >= -1 && newNumber <= 5) {
                            methodNode.instructions.insert(insn, BytecodeUtils.getIConst(newNumber));
                            methodNode.instructions.set(insn, BytecodeUtils.getIConst(newNumber));
                        } else if (newNumber >= -32768 && newNumber <= 32767) {
                            methodNode.instructions.insert(insn, BytecodeUtils.getIntInsn(newNumber));
                            methodNode.instructions.set(insn, BytecodeUtils.getIntInsn(newNumber));
                        } else {
                            methodNode.instructions.insert(insn, new LdcInsnNode(newNumber));
                            methodNode.instructions.set(insn, new LdcInsnNode(newNumber));
                        }
                        count++;
                    } else if (String.valueOf(number).endsWith("1")
                            || String.valueOf(number).endsWith("3")
                            || String.valueOf(number).endsWith("5")
                            || String.valueOf(number).endsWith("7")
                            || String.valueOf(number).endsWith("9")) { // Odd number
                        newNumber = number * 2;


                        methodNode.instructions.insert(insn, new InsnNode(Opcodes.IDIV));

                        if (newNumber >= -1 && newNumber <= 5) {
                            methodNode.instructions.insert(insn, new InsnNode(Opcodes.ICONST_2));
                            methodNode.instructions.set(insn, BytecodeUtils.getIConst(newNumber));
                        } else if (newNumber >= -32768 && newNumber <= 32767) {
                            methodNode.instructions.insert(insn, new InsnNode(Opcodes.ICONST_2));
                            methodNode.instructions.set(insn, BytecodeUtils.getIntInsn(newNumber));
                        } else {
                            methodNode.instructions.insert(insn, new InsnNode(Opcodes.ICONST_2));
                            methodNode.instructions.set(insn, new LdcInsnNode(newNumber));
                        }
                        count++;
                    }
                } else if (insn.getOpcode() == Opcodes.LDC) {
                    int newNumber;
                    Object cst = ((LdcInsnNode) insn).cst;
                    if (cst instanceof Integer) {
                        int number = (int) cst;
                        if (String.valueOf(number).endsWith("2")
                                || String.valueOf(number).endsWith("4")
                                || String.valueOf(number).endsWith("6")
                                || String.valueOf(number).endsWith("8")
                                || String.valueOf(number).endsWith("0")) { // Even number
                            newNumber = number / 2;

                            methodNode.instructions.insert(insn, new InsnNode(Opcodes.IADD));

                            if (newNumber >= -1 && newNumber <= 5) {
                                methodNode.instructions.insert(insn, BytecodeUtils.getIConst(newNumber));
                                methodNode.instructions.set(insn, BytecodeUtils.getIConst(newNumber));
                            } else if (newNumber >= -32768 && newNumber <= 32767) {
                                methodNode.instructions.insert(insn, BytecodeUtils.getIntInsn(newNumber));
                                methodNode.instructions.set(insn, BytecodeUtils.getIntInsn(newNumber));
                            } else {
                                methodNode.instructions.insert(insn, new LdcInsnNode(newNumber));
                                methodNode.instructions.set(insn, new LdcInsnNode(newNumber));
                            }
                            count++;
                        } else if (String.valueOf(number).endsWith("1")
                                || String.valueOf(number).endsWith("3")
                                || String.valueOf(number).endsWith("5")
                                || String.valueOf(number).endsWith("7")
                                || String.valueOf(number).endsWith("9")) { // Odd number
                            newNumber = number * 2;

                            methodNode.instructions.insert(insn, new InsnNode(Opcodes.IDIV));

                            if (newNumber >= -1 && newNumber <= 5) {
                                methodNode.instructions.insert(insn, new InsnNode(Opcodes.ICONST_2));
                                methodNode.instructions.set(insn, BytecodeUtils.getIConst(newNumber));
                            } else if (newNumber >= -32768 && newNumber <= 32767) {
                                methodNode.instructions.insert(insn, new InsnNode(Opcodes.ICONST_2));
                                methodNode.instructions.set(insn, BytecodeUtils.getIntInsn(newNumber));
                            } else {
                                methodNode.instructions.insert(insn, new InsnNode(Opcodes.ICONST_2));
                                methodNode.instructions.set(insn, new LdcInsnNode(newNumber));
                            }
                            count++;
                        }
                    }
                } /*else if (BytecodeUtils.isIConst(insn)) {
                    int newNumber;
                    int number = insn.getOpcode() - 3;

                    if (String.valueOf(number).endsWith("2")
                            || String.valueOf(number).endsWith("4")
                            || String.valueOf(number).endsWith("6")
                            || String.valueOf(number).endsWith("8")) { // Even number
                        newNumber = number / 2;

                        methodNode.instructions.insert(insn, new InsnNode(Opcodes.IADD));

                        if (newNumber >= -1 && newNumber <= 5) {
                            methodNode.instructions.insert(insn, BytecodeUtils.getIConst(newNumber));
                            methodNode.instructions.set(insn, BytecodeUtils.getIConst(newNumber));
                        } else if (newNumber >= -128 && newNumber <= 127) {
                            methodNode.instructions.insert(insn, new IntInsnNode(Opcodes.BIPUSH, number));
                            methodNode.instructions.set(insn, new IntInsnNode(Opcodes.BIPUSH, number));
                        }
                        count++;
                    } else if (String.valueOf(number).endsWith("1")
                            || String.valueOf(number).endsWith("3")
                            || String.valueOf(number).endsWith("5")
                            || String.valueOf(number).endsWith("7")
                            || String.valueOf(number).endsWith("9")) { // Odd number
                        newNumber = number * 2;


                        methodNode.instructions.insert(insn, new InsnNode(Opcodes.IDIV));

                        if (newNumber >= -1 && newNumber <= 5) {
                            methodNode.instructions.insert(insn, new InsnNode(Opcodes.ICONST_2));
                            methodNode.instructions.set(insn, BytecodeUtils.getIConst(newNumber));
                        } else if (newNumber >= -128 && newNumber <= 127) {
                            methodNode.instructions.insert(insn, new InsnNode(Opcodes.ICONST_2));
                            methodNode.instructions.set(insn, new IntInsnNode(Opcodes.BIPUSH, number));
                        }
                        count++;
                    }
                }*/ // This causes issues with normal flow obfuscation for some reason
            }
        }
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
