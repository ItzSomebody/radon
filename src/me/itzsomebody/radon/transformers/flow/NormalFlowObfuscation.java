package me.itzsomebody.radon.transformers.flow;

import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;
import me.itzsomebody.radon.transformers.AbstractTransformer;
import me.itzsomebody.radon.utils.BytecodeUtils;
import me.itzsomebody.radon.utils.LoggerUtils;
import me.itzsomebody.radon.utils.NumberUtils;
import me.itzsomebody.radon.utils.StringUtils;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Transformer that applies multiple flow obfuscations.
 *
 * @author ItzSomebody
 * @author LordPancake (author of Skidfuscator)
 * @author Allatori Developers
 * @author VincBreaker (author of Smoke)
 * @author samczsun (author of the many ways to crash the various decompilers)
 */
public class NormalFlowObfuscation extends AbstractTransformer {
    /**
     * Applies obfuscation.
     */
    public void obfuscate() {
        logStrings.add(LoggerUtils.stdOut("------------------------------------------------"));
        logStrings.add(LoggerUtils.stdOut("Starting normal flow obfuscation transformer"));
        AtomicInteger counter = new AtomicInteger();
        long current = System.currentTimeMillis();
        String s = StringUtils.bigLDC();
        classNodes().stream().filter(classNode -> !classExempted(classNode.name)).forEach(classNode -> {
            classNode.methods.stream().filter(methodNode -> !methodExempted(classNode.name + '.' + methodNode.name + methodNode.desc))
                    .filter(methodNode -> !BytecodeUtils.isAbstractMethod(methodNode.access)).forEach(methodNode -> {
                for (AbstractInsnNode ain : methodNode.instructions.toArray()) {
                    if (methodSize(methodNode) > 40000) break;
                    int op = ain.getOpcode();
                    if (op == Opcodes.ALOAD || op == Opcodes.ILOAD || op == Opcodes.FLOAD) {
                        VarInsnNode vin = (VarInsnNode) ain;
                        if (NumberUtils.getRandomInt(2) == 1) {
                            /*
                             * ALOAD_X
                             * ALOAD_X
                             * ALOAD_X
                             * POP2
                             */
                            methodNode.instructions.insert(vin, new InsnNode(Opcodes.POP2));
                            methodNode.instructions.insertBefore(vin, new VarInsnNode(op, vin.var));
                            methodNode.instructions.insertBefore(vin, new VarInsnNode(op, vin.var));
                            counter.incrementAndGet();
                        } else {
                            /*
                             * ALOAD_X
                             * ALOAD_X
                             * SWAP
                             * POP
                             */
                            methodNode.instructions.insert(vin, new InsnNode(Opcodes.POP));
                            methodNode.instructions.insert(vin, new InsnNode(Opcodes.SWAP));
                            methodNode.instructions.insertBefore(vin, new VarInsnNode(op, vin.var));
                            counter.incrementAndGet();
                        }
                    } else if (BytecodeUtils.isNumberNode(ain)) {
                        /*
                         * ((SI|BI)PUSH|LDC) 123
                         * INEG
                         * INEG
                         * INEG
                         * INEG
                         * INEG
                         * INEG
                         * INEG
                         * INEG
                         */
                        InsnList insnList = new InsnList();
                        int howMany = (NumberUtils.getRandomInt(2) + 2) * 2; // Odd number times even number = even number
                        for (int i = 0; i < howMany; i++) {
                            insnList.add(new InsnNode(Opcodes.INEG));
                        }
                        methodNode.instructions.insert(ain, insnList);
                        counter.incrementAndGet();
                    } else if (BytecodeUtils.isIConst(ain)) {
                        /*
                         * ICONST_X
                         * ICONST_(0|1)
                         * DUP
                         * POP2
                         */
                        methodNode.instructions.insert(ain, new InsnNode(Opcodes.POP2));
                        methodNode.instructions.insert(ain, new InsnNode(Opcodes.DUP));
                        methodNode.instructions.insert(ain, BytecodeUtils.randTrueFalse());
                        counter.incrementAndGet();
                    } else if (ain.getOpcode() == Opcodes.ASTORE) {
                        /*
                         * DUP
                         * ACONST_NULL
                         * SWAP
                         * ASTORE_X
                         * POP
                         * ASTORE_X
                         */
                        methodNode.instructions.insertBefore(ain, new InsnNode(Opcodes.DUP));
                        methodNode.instructions.insertBefore(ain, new InsnNode(Opcodes.ACONST_NULL));
                        methodNode.instructions.insertBefore(ain, new InsnNode(Opcodes.SWAP));
                        AbstractInsnNode next = ain.getNext();
                        methodNode.instructions.insertBefore(next, new InsnNode(Opcodes.POP));
                        methodNode.instructions.insertBefore(next, new VarInsnNode(Opcodes.ASTORE, ((VarInsnNode) ain).var));
                        counter.incrementAndGet();
                    }
                }

                if (NumberUtils.getRandomInt(10) < 6) {
                    for (int i = 0; i < 3; i++) {
                        /*
                         * LDC "BIGSTRING"
                         * LDC "BIGSTRING"
                         * LDC "BIGSTRING"
                         * POP
                         * SWAP
                         * POP
                         * LDC "BIGSTRING"
                         * POP2
                         */
                        if (NumberUtils.getRandomInt(10) < 6) continue;
                        methodNode.instructions.insert(new InsnNode(Opcodes.POP2));
                        methodNode.instructions.insert(new LdcInsnNode(s));
                        methodNode.instructions.insert(new InsnNode(Opcodes.POP));
                        methodNode.instructions.insert(new InsnNode(Opcodes.SWAP));
                        methodNode.instructions.insert(new InsnNode(Opcodes.POP));
                        methodNode.instructions.insert(new LdcInsnNode(s));
                        methodNode.instructions.insert(new LdcInsnNode(s));
                        methodNode.instructions.insert(new LdcInsnNode(s));
                        counter.incrementAndGet();
                    }
                }

                // Stole this from Smoke obfuscator.
                // Probably samczsun's stuff though.
                // Used to crash FernFlower (and JD-GUI).
                // https://media.discordapp.net/attachments/364898912898777098/400077646794326028/samczsun_and_smoke.PNG
                InsnList insnList = new InsnList();
                LabelNode l0 = new LabelNode(new Label());
                LabelNode l1 = new LabelNode(new Label());
                LabelNode l2 = new LabelNode(new Label());
                LabelNode l3 = new LabelNode(new Label());
                LabelNode l4 = new LabelNode(new Label());
                LabelNode l5 = new LabelNode(new Label());
                LabelNode l6 = new LabelNode(new Label());
                LabelNode l7 = new LabelNode(new Label());
                insnList.add(new InsnNode(Opcodes.ICONST_0));
                insnList.add(new JumpInsnNode(Opcodes.IFEQ, l0));
                insnList.add(new InsnNode(Opcodes.ACONST_NULL));
                insnList.add(l1);
                //insnList.add(new FrameNode(Opcodes.F_SAME1, 0, null, 1, new Object[]{"java/lang/Throwable"}));
                insnList.add(new InsnNode(Opcodes.ATHROW));
                insnList.add(l2);
                //insnList.add(new FrameNode(Opcodes.F_SAME1, 0, null, 1, new Object[]{"java/lang/Throwable"}));
                insnList.add(new InsnNode(Opcodes.ATHROW));
                insnList.add(l0);
                //insnList.add(new FrameNode(Opcodes.F_SAME, 0, null, 0, null));
                insnList.add(new InsnNode(Opcodes.ICONST_5));
                insnList.add(new JumpInsnNode(Opcodes.IFGT, l3));
                insnList.add(l4);
                //insnList.add(new FrameNode(Opcodes.F_SAME, 0, null, 0, null));
                insnList.add(new JumpInsnNode(Opcodes.GOTO, l4));
                insnList.add(l3);
                //insnList.add(new FrameNode(Opcodes.F_SAME, 0, null, 0, null));
                insnList.add(new InsnNode(Opcodes.ICONST_M1));
                insnList.add(new JumpInsnNode(Opcodes.IFLT, l5));
                insnList.add(new InsnNode(Opcodes.ACONST_NULL));
                insnList.add(new InsnNode(Opcodes.ATHROW));
                insnList.add(l6);
                //insnList.add(new FrameNode(Opcodes.F_FULL, 0, new Object[]{}, 1, new Object[]{"java/lang/Throwable"}));
                //int howMany = (NumberUtils.getRandomInt(5) + 3) * 2;
                //for (int i = 0; i < howMany; i++) {
                //    insnList.add(new InsnNode(Opcodes.NOP));
                //}
                insnList.add(new InsnNode(Opcodes.ATHROW));
                insnList.add(l7);
                //insnList.add(new FrameNode(Opcodes.F_FULL, 0, new Object[]{}, 1, new Object[]{"java/lang/Throwable"}));
                insnList.add(new InsnNode(Opcodes.ATHROW));
                insnList.add(l5);
                //insnList.add(new FrameNode(Opcodes.F_SAME, 0, null, 0, null));

                methodNode.instructions.insertBefore(methodNode.instructions.getFirst(), insnList);
                methodNode.tryCatchBlocks.add(new TryCatchBlockNode(l1, l2, l2, "java/lang/Throwable"));
                methodNode.tryCatchBlocks.add(new TryCatchBlockNode(l2, l0, l1, "java/lang/Throwable"));
                methodNode.tryCatchBlocks.add(new TryCatchBlockNode(l3, l6, l7, "java/lang/Throwable"));
                counter.incrementAndGet();
            });
        });
        logStrings.add(LoggerUtils.stdOut("Added " + counter + " instruction sets."));
        logStrings.add(LoggerUtils.stdOut("Finished. [" + tookThisLong(current) + "ms]"));
    }
}
