/*
 * Copyright (C) 2018 ItzSomebody
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package me.itzsomebody.radon.transformers;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import me.itzsomebody.radon.utils.CustomRegexUtils;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.CodeSizeEvaluator;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

/**
 * Abstract class used to make transformers.
 * TODO: Allow dependency injection of class trees.
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
     * Resources.
     */
    protected Map<String, byte[]> passThru;

    /**
     * Exempt information.
     */
    private List<String> exempts;

    /**
     * Dictionary for naming.
     */
    protected int dictionary;

    /**
     * Logged strings from transformer console output.
     */
    protected List<String> logStrings;

    /**
     * Init method.
     *
     * @param classes the classes.
     * @param exempts exempt information.
     */
    public void init(Map<String, ClassNode> classes,
                     List<String> exempts, int dictionary) {
        this.classes = classes;
        this.exempts = exempts;
        this.dictionary = dictionary;
        this.logStrings = new ArrayList<>();
    }

    /**
     * The other init method.
     *
     * @param classes   the classes.
     * @param classPath the almighty classpath. (Bow down to it)
     * @param exempts   the exempted classes.
     */
    public void init(Map<String, ClassNode> classes,
                     Map<String, ClassNode> classPath,
                     List<String> exempts, int dictionary) {
        this.classes = classes;
        this.classPath = classPath;
        this.exempts = exempts;
        this.dictionary = dictionary;
        this.logStrings = new ArrayList<>();
    }

    /**
     * The other-other init method.
     *
     * @param classes   the classes.
     * @param classPath the almighty classpath. (Bow down to it)
     * @param passThru  the manifest.
     * @param exempts   the exempted classes.
     */
    public void init(Map<String, ClassNode> classes,
                     Map<String, ClassNode> classPath,
                     Map<String, byte[]> passThru,
                     List<String> exempts,
                     int dictionary) {
        this.classes = classes;
        this.classPath = classPath;
        this.exempts = exempts;
        this.passThru = passThru;
        this.dictionary = dictionary;
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
     * Returns a {@link Long} which indicates how long a transformer
     * processed the classes.
     *
     * @param started time started.
     * @return a {@link Long} which indicates how long a transformer
     * processed the classes
     */
    protected long tookThisLong(long started) {
        return System.currentTimeMillis() - started;
    }

    /**
     * Returns true if member is exempted from obfuscation.
     *
     * @param checkThis string to check for exempt.
     * @param exemptId  per-transformer exempt identifier.
     * @return true if member is exempted from obfuscation.
     */
    protected boolean exempted(String checkThis, String exemptId) {
        String exemptKey = exemptId + ": ";
        for (String exempt : this.exempts) {
            if (exempt.startsWith(exemptKey)) {
                if (CustomRegexUtils.isMatched(exempt.replace(exemptKey, ""), checkThis)) {
                    return true;
                }
            } else if (exempt.startsWith("Class: ")) {
                if (CustomRegexUtils.isMatched(exempt.replace("Class: ", ""), checkThis)) {
                    return true;
                }
            } else if (exempt.startsWith("Method: ")) {
                if (CustomRegexUtils.isMatched(exempt.replace("Method: ", ""), checkThis)) {
                    return true;
                }
            } else if (exempt.startsWith("Field: ")) {
                if (CustomRegexUtils.isMatched(exempt.replace("Field: ", ""), checkThis)) {
                    return true;
                }
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
     * Returns true if this is not either an abstract method, or a native method.
     *
     * @param methodNode the {@link MethodNode} to check.
     * @return true if this is not either an abstract method, or a native method.
     */
    protected boolean hasInstructions(MethodNode methodNode) {
        return (Modifier.isNative(methodNode.access) || Modifier.isAbstract(methodNode.access));
    }

    /**
     * Returns a {@link List} of {@link String}s that were outputted into the
     * console by transformer.
     *
     * @return a {@link List} of {@link String}s that were outputted into the
     * console by transformer.
     */
    public List<String> getLogStrings() {
        return this.logStrings;
    }

    /**
     * Obfuscation time.
     */
    public abstract void obfuscate();
}
