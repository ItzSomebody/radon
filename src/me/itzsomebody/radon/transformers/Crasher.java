package me.itzsomebody.radon.transformers;

import me.itzsomebody.radon.asm.tree.ClassNode;
import me.itzsomebody.radon.utils.LoggerUtils;
import me.itzsomebody.radon.utils.StringUtils;

import java.util.*;

/**
 * Transformer that applies a crashing technique by exploiting class signature parsing.
 * <p>
 * Crashes:
 * - JD-GUI
 * - ProCyon
 * - CFR
 * </p>
 *
 * @author ItzSomebody
 */
public class Crasher {
    /**
     * The {@link ClassNode} that will be obfuscated.
     */
    private ClassNode classNode;

    /**
     * {@link List} of {@link String}s to add to log.
     */
    private List<String> logStrings;

    /**
     * Constructor used to create a {@link Crasher} object.
     *
     * @param classNode     the {@link ClassNode} object to obfuscate.
     */
    public Crasher(ClassNode classNode) {
        this.classNode = classNode;
        logStrings = new ArrayList<>();
        obfuscate();
    }

    /**
     * Applies obfuscation to {@link Crasher#classNode}.
     */
    private void obfuscate() {
        if (classNode.signature == null) {
            classNode.signature = StringUtils.crazyString();
            logStrings.add(LoggerUtils.stdOut("Added crasher"));
        }
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
