package me.itzsomebody.radon.transformers.localvariables;

import me.itzsomebody.radon.asm.tree.ClassNode;
import me.itzsomebody.radon.asm.tree.MethodNode;
import me.itzsomebody.radon.utils.LoggerUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Transformer that applies a local variable obfuscation by removing t
 *
 * @author ItzSomebody
 */
public class RemoveLocalVariables {
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
     * Constructor used to create a {@link RemoveLocalVariables} object.
     *
     * @param classNode     the {@link ClassNode} to be obfuscated.
     * @param exemptMethods {@link ArrayList} of protected {@link MethodNode}s.
     */
    public RemoveLocalVariables(ClassNode classNode, ArrayList<String> exemptMethods) {
        this.classNode = classNode;
        this.exemptMethods = exemptMethods;
        logStrings = new ArrayList<>();
        obfuscate();
    }

    /**
     * Applies obfuscation to {@link RemoveLocalVariables#classNode}.
     */
    private void obfuscate() {
        logStrings.add(LoggerUtils.stdOut("Starting local variable removal transformer"));
        int count = 0;
        for (MethodNode methodNode : classNode.methods) {
            if (exemptMethods.contains(classNode.name + "." + methodNode.name + methodNode.desc)) continue;
            if (methodNode.localVariables != null) {
                count += methodNode.localVariables.size();
                methodNode.localVariables = null;
            }
        }
        logStrings.add(LoggerUtils.stdOut("Finished removing local variables"));
        logStrings.add(LoggerUtils.stdOut("Removed " + String.valueOf(count) + " local variables"));
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
