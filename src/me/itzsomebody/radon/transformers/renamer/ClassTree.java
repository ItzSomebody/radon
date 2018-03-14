package me.itzsomebody.radon.transformers.renamer;

import java.util.HashSet;
import java.util.Set;

/**
 * Specifies subclasses and parents of a class.
 *
 * @author samczsun (java-deobfuscator author)
 */
public class ClassTree {
    /**
     * Class name.
     */
    public String className;

    /**
     * Set of classes that inherit this class.
     */
    public Set<String> subClasses = new HashSet<>();

    /**
     * Set of classes that this class inherits.
     */
    public Set<String> parentClasses = new HashSet<>();

    /**
     * Constructor to make a {@link ClassTree} object.
     *
     * @param className name to assign to this {@link ClassTree}.
     */
    public ClassTree(String className) {
        this.className = className;
    }
}
