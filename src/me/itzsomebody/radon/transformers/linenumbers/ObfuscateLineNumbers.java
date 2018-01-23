package me.itzsomebody.radon.transformers.linenumbers;

import me.itzsomebody.radon.asm.tree.*;
import me.itzsomebody.radon.utils.LoggerUtils;
import me.itzsomebody.radon.utils.NumberUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Transformer that applies a line number obfuscation by changing the correspondng numbers linked to labels
 * to random numbers.
 *
 * @author ItzSomebody
 */
public class ObfuscateLineNumbers {
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
     * Constructor used to create an {@link ObfuscateLineNumbers} object.
     *
     * @param classNode     the {@link ClassNode} to be obfuscated.
     * @param exemptMethods {@link ArrayList} of protected {@link MethodNode}s.
     */
    public ObfuscateLineNumbers(ClassNode classNode, ArrayList<String> exemptMethods) {
        this.classNode = classNode;
        this.exemptMethods = exemptMethods;
        logStrings = new ArrayList<>();
        obfuscate();
    }

    /**
     * Applies obfuscation to {@link ObfuscateLineNumbers#classNode}.
     */
    private void obfuscate() {
        logStrings.add(LoggerUtils.stdOut("Starting line obfuscation transformer"));
        int count = 0;
        for (MethodNode methodNode : classNode.methods) {
            if (exemptMethods.contains(classNode.name + "." + methodNode.name + methodNode.desc)) continue;

            for (AbstractInsnNode insn : methodNode.instructions.toArray()) {
                if (insn instanceof LineNumberNode) {
                    LineNumberNode lineNumberNode = (LineNumberNode) insn;
                    methodNode.instructions.set(insn, new LineNumberNode(NumberUtils.getRandomInt(Integer.MAX_VALUE), lineNumberNode.start));
                    count++;
                }
            }
        }
        logStrings.add(LoggerUtils.stdOut("Finished obfuscating line numbers"));
        logStrings.add(LoggerUtils.stdOut("Obfuscated " + String.valueOf(count) + " line numbers"));
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
