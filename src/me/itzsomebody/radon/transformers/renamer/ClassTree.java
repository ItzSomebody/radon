package me.itzsomebody.radon.transformers.renamer;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.HashSet;
import java.util.Set;

/**
 * Specifies subclasses and parents of a class.
 *
 * @author ItzSomebody
 */
public class ClassTree {
    /**
     * Class name.
     */
    public String className;

    public ClassNode classNode;

    /**
     * Set of classes that inherit this class.
     */
    public Set<String> subClasses = new HashSet<>();

    /**
     * Set of classes that this class inherits.
     */
    public Set<String> parentClasses = new HashSet<>();

    public Set<MethodNode> methods = new HashSet<>();
    public Set<FieldNode> fields = new HashSet<>();

    public boolean libraryNode;

    /**
     * Constructor to make a {@link ClassTree} object.
     *
     * @param className name to assign to this {@link ClassTree}.
     */
    public ClassTree(String className, boolean libraryNode) {
        this.className = className;
        this.libraryNode = libraryNode;
    }
}
