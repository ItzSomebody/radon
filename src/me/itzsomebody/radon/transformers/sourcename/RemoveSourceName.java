package me.itzsomebody.radon.transformers.sourcename;

import me.itzsomebody.radon.asm.tree.ClassNode;
import me.itzsomebody.radon.utils.LoggerUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Transformer that obfuscates the source name attribute by removing the attribute entirely.
 *
 * @author ItzSomebody
 */
public class RemoveSourceName {
    /**
     * The {@link ClassNode} that will be obfuscated.
     */
    private ClassNode classNode;

    /**
     * {@link List} of {@link String}s to add to log.
     */
    private List<String> logStrings;

    /**
     * Constructor used to create a {@link RemoveSourceName} object.
     *
     * @param classNode     the {@link ClassNode} to be obfuscated.
     */
    public RemoveSourceName(ClassNode classNode) {
        this.classNode = classNode;
        logStrings = new ArrayList<>();
        obfuscate();
    }

    /**
     * Applies obfuscation to {@link RemoveSourceName#classNode}.
     */
    private void obfuscate() {
        logStrings.add(LoggerUtils.stdOut("Starting source name removal transformer"));
        classNode.sourceFile = null;
        logStrings.add(LoggerUtils.stdOut("Finished removing source name"));
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
