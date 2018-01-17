package me.itzsomebody.radon.transformers;

import me.itzsomebody.radon.asm.Label;
import me.itzsomebody.radon.asm.Opcodes;
import me.itzsomebody.radon.asm.tree.*;
import me.itzsomebody.radon.utils.BytecodeUtils;
import me.itzsomebody.radon.utils.LoggerUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Transformer that adds an expiration block of code to <init> methods.
 *
 * @author ItzSomebody
 */
public class Expiry {
    /**
     * The {@link ClassNode} that will be obfuscated.
     */
    private ClassNode classNode;

    /**
     * The expiry time as a {@link Long}.
     */
    private long expiryTime;

    /**
     * The expiry message to display when expiry time is exceeded.
     */
    private String expiryMsg;

    /**
     * Methods protected from obfuscation.
     */
    private ArrayList<String> exemptMethods;

    /**
     * {@link List} of {@link String}s to add to log.
     */
    private List<String> logStrings;

    /**
     * Constructor used to create an {@link Expiry} object.
     *
     * @param classNode     the {@link ClassNode} object to obfuscate.
     */
    public Expiry(ClassNode classNode, long expiryTime, String expiryMsg, ArrayList<String> exemptMethods) {
        this.classNode = classNode;
        this.expiryTime = expiryTime;
        this.expiryMsg = expiryMsg;
        this.exemptMethods = exemptMethods;
        logStrings = new ArrayList<>();
        obfuscate();
    }

    /**
     * Applies obfuscation to {@link Expiry#classNode}.
     */
    private void obfuscate() {
        logStrings.add(LoggerUtils.stdOut("Starting expiry transformer"));
        int count = 0;
        for (MethodNode methodNode : classNode.methods) {
            if (exemptMethods.contains(classNode.name + "/" + methodNode.name)) continue;
            if (methodNode.name.equals("<init>")) {
                for (AbstractInsnNode ain : methodNode.instructions.toArray()) {
                    if (BytecodeUtils.isReturn(ain)) {
                        InsnList expiryCode = new InsnList();
                        LabelNode injectedLabel = new LabelNode(new Label());

                        expiryCode.add(new TypeInsnNode(Opcodes.NEW, "java/util/Date"));
                        expiryCode.add(new InsnNode(Opcodes.DUP));
                        expiryCode.add(new LdcInsnNode(expiryTime));
                        expiryCode.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "java/util/Date", "<init>", "(J)V", false));
                        expiryCode.add(new TypeInsnNode(Opcodes.NEW, "java/util/Date"));
                        expiryCode.add(new InsnNode(Opcodes.DUP));
                        expiryCode.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "java/util/Date", "<init>", "()V", false));
                        expiryCode.add(new InsnNode(Opcodes.SWAP));
                        expiryCode.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/util/Date", "after", "(Ljava/util/Date;)Z", false));
                        expiryCode.add(new JumpInsnNode(Opcodes.IFEQ, injectedLabel));
                        expiryCode.add(new TypeInsnNode(Opcodes.NEW, "java/lang/Throwable"));
                        expiryCode.add(new InsnNode(Opcodes.DUP));
                        expiryCode.add(new LdcInsnNode(expiryMsg));
                        expiryCode.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "java/lang/Throwable", "<init>", "(Ljava/lang/String;)V", false));
                        expiryCode.add(new InsnNode(Opcodes.ATHROW));
                        expiryCode.add(injectedLabel);
                        methodNode.instructions.insertBefore(ain, BytecodeUtils.returnExpiry(expiryTime, expiryMsg));
                        methodNode.instructions.insertBefore(ain, new InsnNode(Opcodes.NOP));
                        count++;
                    }
                }
            }
        }
        logStrings.add(LoggerUtils.stdOut("Finished applying expiry code"));
        logStrings.add(LoggerUtils.stdOut("Inserted " + count + " expiration code blocks"));
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
