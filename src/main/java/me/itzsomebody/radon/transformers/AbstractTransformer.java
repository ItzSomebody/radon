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
import me.itzsomebody.radon.internal.Bootstrap;
import me.itzsomebody.radon.utils.MatchUtils;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.CodeSizeEvaluator;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

/**
 * Abstract class used to make transformers.
 *
 * @author ItzSomebody
 */
public abstract class AbstractTransformer implements Opcodes {
    /**
     * Bootstrap instance.
     */
    private Bootstrap bootstrap;

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
     * Dependency injection method.
     *
     * @param bootstrap instance of {@link Bootstrap}.
     * @param exempts exempt information.
     */
    public void init(Bootstrap bootstrap,
                     List<String> exempts, int dictionary) {
        this.bootstrap = bootstrap;
        this.exempts = exempts;
        this.dictionary = dictionary;
        this.logStrings = new ArrayList<>();
    }

    /**
     * Returns the loaded input {@link ClassNode}s.
     *
     * @return the loaded input {@link ClassNode}s.
     */
    protected Map<String, ClassNode> getClassMap() {
        return this.bootstrap.getClasses();
    }

    /**
     * Returns the map of extra classes.
     *
     * @return the map of extra classes.
     */
    protected Map<String, ClassNode> getExtraClassesMap() {
        return this.bootstrap.getExtraClasses();
    }

    /**
     * Returns the loaded class path.
     *
     * @return the loaded class path.
     */
    protected Map<String, ClassNode> getClassPathMap() {
        return this.bootstrap.getClassPath();
    }

    /**
     * Returns the loaded resources.
     *
     * @return the loaded resources.
     */
    protected Map<String, byte[]> getPassThru() {
        return this.bootstrap.getPassThru();
    }

    /**
     * Returns only the loaded {@link ClassNode}s.
     *
     * @return only the loaded {@link ClassNode}s.
     */
    protected Collection<ClassNode> classNodes() {
        return this.getClassMap().values();
    }

    /**
     * Returns only the names of the loaded {@link ClassNode}s.
     *
     * @return only the names of the loaded {@link ClassNode}s.
     */
    protected Collection<String> classNames() {
        return this.getClassMap().keySet();
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
                if (MatchUtils.isMatched(exempt.replace(exemptKey, ""), checkThis)) {
                    return true;
                }
            } else if (exempt.startsWith("Class: ")) {
                if (MatchUtils.isMatched(exempt.replace("Class: ", ""), checkThis)) {
                    return true;
                }
            } else if (exempt.startsWith("Method: ")) {
                if (MatchUtils.isMatched(exempt.replace("Method: ", ""), checkThis)) {
                    return true;
                }
            } else if (exempt.startsWith("Field: ")) {
                if (MatchUtils.isMatched(exempt.replace("Field: ", ""), checkThis)) {
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
        return (!Modifier.isNative(methodNode.access) && !Modifier.isAbstract(methodNode.access));
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
