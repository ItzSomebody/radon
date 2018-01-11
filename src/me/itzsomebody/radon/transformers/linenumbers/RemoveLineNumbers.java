package me.itzsomebody.radon.transformers.linenumbers;

import me.itzsomebody.radon.asm.tree.*;
import me.itzsomebody.radon.utils.LoggerUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Transformer that applies a line number obfuscation by removing them.
 *
 * @author ItzSomebody
 */
public class RemoveLineNumbers {
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
     * Constructor used to create a {@link RemoveLineNumbers} object.
     *
     * @param classNode     the {@link ClassNode} to be obfuscated.
     * @param exemptMethods {@link ArrayList} of protected {@link MethodNode}s.
     */
    public RemoveLineNumbers(ClassNode classNode, ArrayList<String> exemptMethods) {
        this.classNode = classNode;
        this.exemptMethods = exemptMethods;
        logStrings = new ArrayList<>();
        obfuscate();
    }

    /**
     * Applies obfuscation to {@link RemoveLineNumbers#classNode}.
     */
    private void obfuscate() {
        logStrings.add(LoggerUtils.stdOut("Starting line removal transformer"));
        int count = 0;
        for (MethodNode methodNode : classNode.methods) {
            if (exemptMethods.contains(classNode.name + "." + methodNode.name + methodNode.desc)) continue;

            for (AbstractInsnNode insn : methodNode.instructions.toArray()) {
                if (insn instanceof LineNumberNode) {
                    methodNode.instructions.remove(insn);
                    count++;
                }
            }
        }
        logStrings.add(LoggerUtils.stdOut("Finished removing line numbers"));
        logStrings.add(LoggerUtils.stdOut("Removed " + String.valueOf(count) + " line numbers"));
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
