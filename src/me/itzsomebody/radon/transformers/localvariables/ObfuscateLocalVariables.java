package me.itzsomebody.radon.transformers.localvariables;

import me.itzsomebody.radon.asm.tree.ClassNode;
import me.itzsomebody.radon.asm.tree.LocalVariableNode;
import me.itzsomebody.radon.asm.tree.MethodNode;
import me.itzsomebody.radon.utils.LoggerUtils;
import me.itzsomebody.radon.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Transformer that applies a local variable obfuscation by changing the names.
 *
 * @author ItzSomebody
 */
public class ObfuscateLocalVariables {
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
     * Constructor used to create an {@link ObfuscateLocalVariables} object.
     *
     * @param classNode     the {@link ClassNode} to be obfuscated.
     * @param exemptMethods {@link ArrayList} of protected {@link MethodNode}s.
     */
    public ObfuscateLocalVariables(ClassNode classNode, ArrayList<String> exemptMethods) {
        this.classNode = classNode;
        this.exemptMethods = exemptMethods;
        logStrings = new ArrayList<>();
        obfuscate();
    }

    /**
     * Applies obfuscation to {@link ObfuscateLocalVariables#classNode}.
     */
    private void obfuscate() {
        logStrings.add(LoggerUtils.stdOut("Starting local variable obfuscation transformer"));
        int count = 0;
        for (MethodNode methodNode : classNode.methods) {
            if (methodNode.localVariables != null) {
                if (exemptMethods.contains(classNode.name + "." + methodNode.name + methodNode.desc)) continue;
                for (LocalVariableNode localVariableNode : methodNode.localVariables) {
                    localVariableNode.name = StringUtils.crazyString();
                    count++;
                }
            }
        }
        logStrings.add(LoggerUtils.stdOut("Finished obfuscating local variables"));
        logStrings.add(LoggerUtils.stdOut("Obfuscated " + String.valueOf(count) + " local variables"));
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
