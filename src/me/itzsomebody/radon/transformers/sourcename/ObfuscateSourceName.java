package me.itzsomebody.radon.transformers.sourcename;

import me.itzsomebody.radon.asm.tree.ClassNode;
import me.itzsomebody.radon.utils.LoggerUtils;
import me.itzsomebody.radon.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Transformer that obfuscates the source name attribute by changing the corresponding value.
 *
 * @author ItzSomebody
 */
public class ObfuscateSourceName {
    /**
     * The {@link ClassNode} that will be obfuscated.
     */
    private ClassNode classNode;

    /**
     * {@link List} of {@link String}s to add to log.
     */
    private List<String> logStrings;

    /**
     * Constructor used to create an {@link ObfuscateSourceName} object.
     *
     * @param classNode     the {@link ClassNode} to be obfuscated.
     */
    public ObfuscateSourceName(ClassNode classNode) {
        this.classNode = classNode;
        logStrings = new ArrayList<>();
        obfuscate();
    }

    /**
     * Applies obfuscation to {@link ObfuscateSourceName#classNode}.
     */
    private void obfuscate() {
        logStrings.add(LoggerUtils.stdOut("Starting source name obfuscation transformer"));
        classNode.sourceFile = StringUtils.crazyString() + ".java";
        logStrings.add(LoggerUtils.stdOut("Finished obfuscating source name"));
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
