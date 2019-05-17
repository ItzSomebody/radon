/*
 * Radon - An open-source Java obfuscator
 * Copyright (C) 2019 ItzSomebody
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

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import me.itzsomebody.radon.Radon;
import me.itzsomebody.radon.asm.ClassWrapper;
import me.itzsomebody.radon.asm.FieldWrapper;
import me.itzsomebody.radon.asm.MethodWrapper;
import me.itzsomebody.radon.exceptions.RadonException;
import me.itzsomebody.radon.exclusions.ExclusionType;
import me.itzsomebody.radon.utils.StringUtils;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.CodeSizeEvaluator;
import org.objectweb.asm.tree.MethodNode;

/**
 * Abstract transformer for all the transformers. \o/
 *
 * @author ItzSomebody
 */
public abstract class Transformer implements Opcodes {
    protected Radon radon;

    /**
     * Strings which have already been generated and used.
     */
    private HashSet<String> usedStrings = new HashSet<>();

    public final void init(Radon radon) {
        this.radon = radon;
    }

    protected final boolean excluded(String str) {
        return this.radon.getConfig().getExclusionManager().isExcluded(str, getExclusionType());
    }

    protected final boolean excluded(ClassWrapper classWrapper) {
        return this.excluded(classWrapper.getOriginalName());
    }

    protected final boolean excluded(MethodWrapper methodWrapper) {
        return this.excluded(methodWrapper.getOwner().getOriginalName() + '.' + methodWrapper.getOriginalName()
                + methodWrapper.getOriginalDescription());
    }

    protected final boolean excluded(FieldWrapper fieldWrapper) {
        return this.excluded(fieldWrapper.getOwner().getOriginalName() + '.' + fieldWrapper.getOriginalName() + '.'
                + fieldWrapper.getOriginalDescription());
    }

    /**
     * Returns the remaining leeway of a method's allowed size.
     *
     * @param methodNode the {@link MethodNode} to check.
     * @return the remaining leeway of a method's allowed size.
     */
    protected int getSizeLeeway(MethodNode methodNode) {
        CodeSizeEvaluator cse = new CodeSizeEvaluator(null);
        methodNode.accept(cse);
        // Max allowed method size is 65535
        // https://docs.oracle.com/javase/specs/jvms/se10/html/jvms-4.html#jvms-4.7.3
        return (65535 - cse.getMaxSize());
    }

    protected int getSizeLeeway(MethodWrapper wrapper) {
        return getSizeLeeway(wrapper.getMethodNode());
    }

    protected final boolean hasInstructions(MethodNode methodNode) {
        return methodNode.instructions != null && methodNode.instructions.size() > 0;
    }

    protected final boolean hasInstructions(MethodWrapper methodWrapper) {
        return hasInstructions(methodWrapper.getMethodNode());
    }

    protected final long tookThisLong(long from) {
        return System.currentTimeMillis() - from;
    }

    protected String randomString() {
        return getRandomString(radon.getConfig().getRandomizedStringLength());
    }

    protected String uniqueRandomString() {
        String str;
        int count = 0;

        do {
            if (count++ > 20) {
                //throw new RadonException("Unable to generate an unused string (try increasing randomized string length)");
                radon.getConfig().setRandomizedStringLength(radon.getConfig().getRandomizedStringLength() + 1);
                count = 0;
            }

            str = getRandomString(radon.getConfig().getRandomizedStringLength());
        } while (!usedStrings.add(str));

        return str;
    }

    private String getRandomString(int length) {
        switch (radon.getConfig().getDictionaryType()) {
            case SPACES:
                return StringUtils.randomSpacesString(length);
            case UNRECOGNIZED:
                return StringUtils.randomUnrecognizedString(length);
            case ALPHABETICAL:
                return StringUtils.randomAlphaString(length);
            case ALPHANUMERIC:
                return StringUtils.randomAlphaNumericString(length);
            case UNICODE:
                return StringUtils.randomUnicodeString(length);
            default: {
                throw new RadonException("Illegal dictionary type: " + radon.getConfig().getDictionaryType());
            }
        }
    }

    protected final Map<String, ClassWrapper> getClasses() {
        return this.radon.classes;
    }

    protected final Collection<ClassWrapper> getClassWrappers() {
        return this.radon.classes.values();
    }

    protected final Map<String, ClassWrapper> getClassPath() {
        return this.radon.classPath;
    }

    protected final Map<String, byte[]> getResources() {
        return this.radon.resources;
    }

    public abstract void transform();

    public abstract String getName();

    public abstract ExclusionType getExclusionType();

    // Might return a boolean / integer / string / whatever instead of Map.
    public abstract Object getConfiguration();

    public abstract void setConfiguration(Map<String, Object> config);

    public abstract void verifyConfiguration(Map<String, Object> config);

    /**
     * Insertion sorts the provided {@link List<Transformer>} using the {@link ExclusionType} ordinal as the priority
     * key. O(n^2) here we come \o/
     *
     * @param transformers @link List<Transformer>} to be sorted.
     */
    public static void sort(List<Transformer> transformers) {
        if (transformers.size() < 2) // Already sorted
            return;

        for (int i = 1; i < transformers.size(); i++) {
            Transformer transformer = transformers.get(i);
            int key = transformer.getExclusionType().ordinal();

            int j = i - 1;
            while (j >= 0 && transformers.get(j).getExclusionType().ordinal() > key) {
                transformers.set(j + 1, transformers.get(j));
                j -= 1;
            }

            transformers.set(j + 1, transformer);
        }
    }
}
