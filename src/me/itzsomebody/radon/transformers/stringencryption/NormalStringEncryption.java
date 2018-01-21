package me.itzsomebody.radon.transformers.stringencryption;

import me.itzsomebody.radon.asm.Opcodes;
import me.itzsomebody.radon.asm.tree.*;
import me.itzsomebody.radon.utils.BytecodeUtils;
import me.itzsomebody.radon.utils.LoggerUtils;
import me.itzsomebody.radon.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Transformer that encrypts strings with AES.
 *
 * @author ItzSomebody
 */
public class NormalStringEncryption {
    /**
     * The {@link ClassNode} that will be obfuscated.
     */
    private ClassNode classNode;

    /**
     * Indication to not encrypt strings containing Spigot placeholders (%%__USER__%%, %%__RESOURCE__%% and %%__NONCE__%%).
     */
    private boolean spigotMode;

    /**
     * Path to the decryption method.
     */
    private String decryptionMethodName;

    /**
     * Methods protected from obfuscation.
     */
    private ArrayList<String> exemptMethods;

    /**
     * {@link List} of {@link String}s to add to log.
     */
    private List<String> logStrings;

    /**
     * Constructor used to create a {@link NormalStringEncryption} object.
     *
     * @param classNode            the {@link ClassNode} object to obfuscate.
     * @param decryptionMethodName the path to the decryption method.
     * @param spigotMode           indication to not encrypt strings containing Spigot placeholders (%%__USER__%%, %%__RESOURCE__%% and %%__NONCE__%%).
     * @param exemptMethods        {@link ArrayList} of protected {@link MethodNode}s.
     */
    public NormalStringEncryption(ClassNode classNode, String decryptionMethodName, boolean spigotMode, ArrayList<String> exemptMethods) {
        this.classNode = classNode;
        this.decryptionMethodName = decryptionMethodName;
        this.spigotMode = spigotMode;
        this.exemptMethods = exemptMethods;
        logStrings = new ArrayList<>();
        obfuscate();
    }

    /**
     * Applies obfuscation to {@link NormalStringEncryption#classNode}.
     */
    private void obfuscate() {
        String[] split = decryptionMethodName.split("\\.");
        logStrings.add(LoggerUtils.stdOut("Starting light string encryption transformer"));
        int count = 0;
        for (MethodNode methodNode : classNode.methods) {
            if (exemptMethods.contains(classNode.name + "/" + methodNode.name)) continue;
            if (BytecodeUtils.isAbstractMethod(methodNode.access)) continue;
            if (methodNode.instructions.size() < 4) continue;

            for (AbstractInsnNode insn : methodNode.instructions.toArray()) {
                if (insn instanceof LdcInsnNode) {
                    Object cst = ((LdcInsnNode) insn).cst;

                    if (cst instanceof String) {
                        if (spigotMode &&
                                ((String) cst).contains("%%__USER__%%")
                                || ((String) cst).contains("%%__RESOURCE__%%")
                                || ((String) cst).contains("%%__NONCE__%%")) continue;

                        String junkLDC = StringUtils.crazyString();
                        ((LdcInsnNode) insn).cst = StringUtils.aesEncrypt(((String) ((LdcInsnNode) insn).cst), junkLDC);
                        methodNode.instructions.insert(insn, new MethodInsnNode(Opcodes.INVOKESTATIC, split[0], split[1], "(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;", false));
                        methodNode.instructions.insert(insn, new LdcInsnNode(junkLDC));
                        count++;
                    }
                }
            }
        }
        logStrings.add(LoggerUtils.stdOut("Finished encrypting strings"));
        logStrings.add(LoggerUtils.stdOut("Encrypted " + String.valueOf(count) + " strings"));
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
