package me.itzsomebody.radon.transformers.misc;

import me.itzsomebody.radon.transformers.AbstractTransformer;
import org.objectweb.asm.tree.*;
import me.itzsomebody.radon.utils.BytecodeUtils;
import me.itzsomebody.radon.utils.LoggerUtils;
import me.itzsomebody.radon.utils.NumberUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Transformer that splits up integers into simple arithmetic evaluations.
 *
 * @author ItzSomebody
 * @author VincBreaker (Sorry Vinc, I just had to steal the idea of Smoke's number obfuscation lol)
 */
public class NumberObfuscation extends AbstractTransformer {
    /**
     * Applies obfuscation.}.
     */
    public void obfuscate() {
        logStrings.add(LoggerUtils.stdOut("------------------------------------------------"));
        logStrings.add(LoggerUtils.stdOut("Starting number obfuscation transformer"));
        AtomicInteger counter = new AtomicInteger();
        long current = System.currentTimeMillis();
        classNodes().stream().filter(classNode -> !classExempted(classNode.name)).forEach(classNode -> {
            classNode.methods.stream().filter(methodNode -> !methodExempted(classNode.name + '.' + methodNode.name + methodNode.desc))
                    .filter(methodNode -> !BytecodeUtils.isAbstractMethod(methodNode.access)).forEach(methodNode -> {
                for (AbstractInsnNode insn : methodNode.instructions.toArray()) {
                    if (methodSize(methodNode) > 60000) break;
                    if (BytecodeUtils.isNumberNode(insn)) {
                        int originalNum = BytecodeUtils.getNumber(insn);
                        switch (NumberUtils.getRandomInt(3)) {
                            case 0: {
                                int value1 = NumberUtils.getRandomInt(255) + 20;
                                int value2 = NumberUtils.getRandomInt(value1) + value1;
                                int value3 = originalNum - value1 + value2; // You kids say algebra is useless???

                                InsnList insnList = new InsnList();
                                insnList.add(BytecodeUtils.getNumberInsn(value1));
                                insnList.add(BytecodeUtils.getNumberInsn(value2));
                                insnList.add(new InsnNode(ISUB));
                                insnList.add(BytecodeUtils.getNumberInsn(value3));
                                insnList.add(new InsnNode(IADD));

                                methodNode.instructions.insertBefore(insn, insnList);
                                methodNode.instructions.remove(insn);
                                counter.incrementAndGet();
                                break;
                            }
                            case 1: {
                                int value1 = NumberUtils.getRandomInt(255) + 20;
                                int value2 = NumberUtils.getRandomInt(value1) + value1;
                                int value3 = NumberUtils.getRandomInt(value2 + 1);
                                int value4 = originalNum - value1 + value2 - value3; // You kids say algebra is useless???

                                InsnList insnList = new InsnList();
                                insnList.add(BytecodeUtils.getNumberInsn(value1));
                                insnList.add(BytecodeUtils.getNumberInsn(value2));
                                insnList.add(new InsnNode(ISUB));
                                insnList.add(BytecodeUtils.getNumberInsn(value3));
                                insnList.add(new InsnNode(IADD));
                                insnList.add(BytecodeUtils.getNumberInsn(value4));
                                insnList.add(new InsnNode(IADD));

                                methodNode.instructions.insertBefore(insn, insnList);
                                methodNode.instructions.remove(insn);
                                counter.incrementAndGet();
                                break;
                            }
                            case 2: {
                                int value1 = NumberUtils.getRandomInt(255) + 20;
                                int value2 = NumberUtils.getRandomInt(value1) + value1;
                                int value3 = NumberUtils.getRandomInt(value2 + 1);
                                int value4 = NumberUtils.getRandomInt(value3 + 1);
                                int value5 = originalNum - value1 + value2 - value3 + value4; // You kids say algebra is useless???

                                InsnList insnList = new InsnList();
                                insnList.add(BytecodeUtils.getNumberInsn(value1));
                                insnList.add(BytecodeUtils.getNumberInsn(value2));
                                insnList.add(new InsnNode(ISUB));
                                insnList.add(BytecodeUtils.getNumberInsn(value3));
                                insnList.add(new InsnNode(IADD));
                                insnList.add(BytecodeUtils.getNumberInsn(value4));
                                insnList.add(new InsnNode(ISUB));
                                insnList.add(BytecodeUtils.getNumberInsn(value5));
                                insnList.add(new InsnNode(IADD));

                                methodNode.instructions.insertBefore(insn, insnList);
                                methodNode.instructions.remove(insn);
                                counter.incrementAndGet();
                                break;
                            }
                        }
                    }
                }
            });
        });
        logStrings.add(LoggerUtils.stdOut("Split " + counter + " integers into math instructions."));
        logStrings.add(LoggerUtils.stdOut("Finished. [" + tookThisLong(current) + "ms]"));
    }
}
