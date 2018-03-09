package me.itzsomebody.radon.transformers;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.CodeSizeEvaluator;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import me.itzsomebody.radon.utils.CustomRegexUtils;

import java.util.*;

/**
 * Abstract class used to make transformers.
 *
 * @author ItzSomebody
 */
public abstract class AbstractTransformer implements Opcodes {
    /**
     * The classes in the input.
     */
    private Map<String, ClassNode> classes;

    /**
     * The almighty classpath.
     */
    private Map<String, ClassNode> classPath;

    /**
     * Classes exempted from obfuscation.
     */
    private List<String> exemptClasses;

    /**
     * Methods exempted from obfuscation.
     */
    private List<String> exemptMethods;

    /**
     * Fields exempted from obfuscation.
     */
    private List<String> exemptFields;

    /**
     * Logged strings from transformer console output.
     */
    protected List<String> logStrings;

    /**
     * Init method.
     *
     * @param classes       the classes.
     * @param exemptClasses the exempted classes.
     * @param exemptMethods the exempted methods.
     * @param exemptFields  the exempted fields.
     */
    public void init(Map<String, ClassNode> classes,
                     List<String> exemptClasses,
                     List<String> exemptMethods,
                     List<String> exemptFields) {
        this.classes = classes;
        this.exemptClasses = exemptClasses;
        this.exemptMethods = exemptMethods;
        this.exemptFields = exemptFields;
        this.logStrings = new ArrayList<>();
    }

    /**
     * The other init method.
     *
     * @param classes       the classes.
     * @param classPath     the almighty classpath. (Bow down to it)
     * @param exemptClasses the exempted classes.
     * @param exemptMethods the exempted methods.
     * @param exemptFields  the exempted fields.
     */
    public void init(Map<String, ClassNode> classes,
                     Map<String, ClassNode> classPath,
                     List<String> exemptClasses,
                     List<String> exemptMethods,
                     List<String> exemptFields) {
        this.classes = classes;
        this.classPath = classPath;
        this.exemptClasses = exemptClasses;
        this.exemptMethods = exemptMethods;
        this.exemptFields = exemptFields;
        this.logStrings = new ArrayList<>();
    }

    /**
     * Returns {@link AbstractTransformer#classes}.
     *
     * @return {@link AbstractTransformer#classes}.
     */
    protected Map<String, ClassNode> getClassMap() {
        return this.classes;
    }

    /**
     * Returns {@link AbstractTransformer#classPath}.
     *
     * @return {@link AbstractTransformer#classPath}.
     */
    protected Map<String, ClassNode> getClassPathMap() {
        return this.classPath;
    }

    /**
     * Returns the values of {@link AbstractTransformer#classes}.
     *
     * @return the values of {@link AbstractTransformer#classes}.
     */
    protected Collection<ClassNode> classNodes() {
        return this.classes.values();
    }

    /**
     * Returns the keyset of {@link AbstractTransformer#classes}.
     *
     * @return the keyset of {@link AbstractTransformer#classes}.
     */
    protected Collection<String> classNames() {
        return this.classes.keySet();
    }

    /**
     * Returns a {@link Long} which indicates how long a transformer processed the classes.
     *
     * @param started time started.
     * @return a {@link Long} which indicates how long a transformer processed the classes
     */
    protected long tookThisLong(long started) {
        return System.currentTimeMillis() - started;
    }

    /**
     * Returns true/false based on if the input is listed in {@link AbstractTransformer#exemptClasses}.
     *
     * @param name the name of the class to check.
     * @return true/false based on if the input is listed in {@link AbstractTransformer#exemptClasses}.
     */
    protected boolean classExempted(String name) {
        for (String string : exemptClasses) {
            if (CustomRegexUtils.isMatched(string, name)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Returns true/false based on if the input is listed in {@link AbstractTransformer#exemptMethods}.
     *
     * @param name the path (and description) of the method to check.
     * @return true/false based on if the input is listed in {@link AbstractTransformer#exemptMethods}.
     */
    protected boolean methodExempted(String name) {
        for (String string : exemptMethods) {
            if (CustomRegexUtils.isMatched(string, name)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Returns true/false based on if the input is listed in {@link AbstractTransformer#exemptFields}.
     *
     * @param name the path of the field to check.
     * @return true/false based on if the input is listed in {@link AbstractTransformer#exemptFields}.
     */
    protected boolean fieldExempted(String name) {
        for (String string : exemptFields) {
            if (CustomRegexUtils.isMatched(string, name)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Get's the current size of the method.
     *
     * @param methodNode the input method to evaluate the size of.
     * @return the current size of the method.
     */
    protected int methodSize(MethodNode methodNode) {
        CodeSizeEvaluator cse = new CodeSizeEvaluator(null);
        methodNode.accept(cse);
        return cse.getMaxSize();
    }

    /**
     * Grabs {@link ClassNode} via {@link AbstractTransformer#classPath}.
     *
     * @param className the key to use to get corresponding classnode.
     * @return {@link ClassNode} via {@link AbstractTransformer#classPath}.
     */
    protected ClassNode getClassNode(String className) {
        return this.classPath.get(className);
    }

    /**
     * Grabs {@link MethodNode} from input using arguments as parameters for searching.
     *
     * @param name      the name of the {@link MethodNode}
     * @param desc      the desc of the {@link MethodNode}
     * @param classNode the {@link ClassNode} to search
     * @return {@link MethodNode} from input using arguments as parameters for searching.
     */
    protected MethodNode getMethodNode(String name, String desc, ClassNode classNode) {
        for (MethodNode methodNode : classNode.methods) {
            if (methodNode.name.equals(name) && methodNode.desc.equals(desc)) {
                return methodNode;
            }
        }

        throw new IllegalStateException("Could not find the method with info " + classNode.name + '.' + name + desc);
    }

    /**
     * Returns a {@link List} of {@link String}s that were outputted into the console by transformer.
     *
     * @return a {@link List} of {@link String}s that were outputted into the console by transformer.
     */
    public List<String> getLogStrings() {
        return this.logStrings;
    }

    /**
     * Obfuscation time.
     */
    public abstract void obfuscate();
}
