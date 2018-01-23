package me.itzsomebody.radon.transformers.flow;

import me.itzsomebody.radon.asm.Label;
import me.itzsomebody.radon.asm.Opcodes;
import me.itzsomebody.radon.asm.tree.*;
import me.itzsomebody.radon.utils.BytecodeUtils;
import me.itzsomebody.radon.utils.LoggerUtils;
import me.itzsomebody.radon.utils.NumberUtils;
import me.itzsomebody.radon.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Transformer that applies multiple flow obfuscations to {@link NormalFlowObfuscation#classNode}.
 *
 * @author ItzSomebody
 * @author LordPancake (author of Skidfuscator)
 * @author Allatori Developers
 * @author VincBreaker (author of Smoke)
 * @author samczsun (author of the many ways to crash the various decompilers)
 */
public class NormalFlowObfuscation {
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
     * Constructor used to create a {@link NormalFlowObfuscation} object.
     *
     * @param classNode     the {@link ClassNode} object to obfuscate.
     * @param exemptMethods {@link ArrayList} of protected {@link MethodNode}s.
     */
    public NormalFlowObfuscation(ClassNode classNode, ArrayList<String> exemptMethods) {
        this.classNode = classNode;
        this.exemptMethods = exemptMethods;
        logStrings = new ArrayList<>();
        obfuscate();
    }

    /**
     * Applies obfuscation to {@link NormalFlowObfuscation#classNode}.
     */
    private void obfuscate() {
        String s = StringUtils.bigLDC();
        logStrings.add(LoggerUtils.stdOut("Starting normal flow obfuscation transformer"));
        int count = 0;
        for (MethodNode methodNode : classNode.methods) {
            if (exemptMethods.contains(classNode.name + "." + methodNode.name + methodNode.desc)) continue;
            if (BytecodeUtils.isAbstractMethod(methodNode.access)) continue;

            for (AbstractInsnNode ain : methodNode.instructions.toArray()) {
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
                        count++;
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
                        count++;
                    }
                } else if (ain.getOpcode() == Opcodes.SIPUSH || ain.getOpcode() == Opcodes.BIPUSH) {
                    /*
                     * (SI|BI)PUSH 255
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
                    int howMany = (NumberUtils.getRandomInt(6) + 3) * 2; // Odd number times even number = even number
                    for (int i = 0; i < howMany; i++) {
                        insnList.add(new InsnNode(Opcodes.INEG));
                    }
                    methodNode.instructions.insert(ain, insnList);
                    count++;
                } else if (ain instanceof LdcInsnNode) {
                    /*
                     * LDC 1830289439
                     * INEG
                     * INEG
                     * INEG
                     * INEG
                     * INEG
                     * INEG
                     * INEG
                     * INEG
                     */
                    if (((LdcInsnNode) ain).cst instanceof Integer) {
                        InsnList insnList = new InsnList();
                        int howMany = (NumberUtils.getRandomInt(6) + 3) * 2; // Odd number times even number = even number
                        for (int i = 0; i < howMany; i++) {
                            insnList.add(new InsnNode(Opcodes.INEG));
                        }
                        methodNode.instructions.insert(ain, insnList);
                        count++;
                    }
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
                    count++;
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
                    count++;
                } else if (ain.getOpcode() == Opcodes.NOP) {
                    /*
                     * NOP
                     * NOP
                     * NOP
                     * NOP
                     * NOP
                     */
                    int howMany = (NumberUtils.getRandomInt(5) + 1) * 2;
                    for (int i = 0; i < howMany; i++) {
                        methodNode.instructions.insert(ain, new InsnNode(Opcodes.NOP));
                    }
                    count++;
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
                    count++;
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
            int howMany = (NumberUtils.getRandomInt(5) + 3) * 2;
            for (int i = 0; i < howMany; i++) {
                insnList.add(new InsnNode(Opcodes.NOP));
            }
            insnList.add(new InsnNode(Opcodes.ATHROW));
            insnList.add(l7);
            //insnList.add(new FrameNode(Opcodes.F_FULL, 0, new Object[]{}, 1, new Object[]{"javax/crypto/BadPaddingException"}));
            insnList.add(new InsnNode(Opcodes.ATHROW));
            insnList.add(l5);
            //insnList.add(new FrameNode(Opcodes.F_SAME, 0, null, 0, null));

            methodNode.instructions.insertBefore(methodNode.instructions.getFirst(), insnList);
            methodNode.tryCatchBlocks.add(new TryCatchBlockNode(l1, l2, l2, "java/lang/Throwable"));
            methodNode.tryCatchBlocks.add(new TryCatchBlockNode(l2, l0, l1, "java/lang/Throwable"));
            methodNode.tryCatchBlocks.add(new TryCatchBlockNode(l3, l6, l7, "javax/crypto/BadPaddingException"));
            count++;
        }
        logStrings.add(LoggerUtils.stdOut("Finished adding normal flow obfuscation"));
        logStrings.add(LoggerUtils.stdOut("Added normal flow obfuscation " + String.valueOf(count) + " times"));
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
