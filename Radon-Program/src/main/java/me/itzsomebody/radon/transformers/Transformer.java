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

import java.util.Collection;
import java.util.Map;
import me.itzsomebody.radon.Radon;
import me.itzsomebody.radon.asm.ClassWrapper;
import me.itzsomebody.radon.asm.FieldWrapper;
import me.itzsomebody.radon.asm.MethodWrapper;
import me.itzsomebody.radon.exclusions.ExclusionType;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.CodeSizeEvaluator;
import org.objectweb.asm.tree.MethodNode;

public abstract class Transformer implements Opcodes {
    protected Radon radon;

    public void init(Radon radon) {
        this.radon = radon;
    }

    protected boolean excluded(String str) {
        return this.radon.sessionInfo.getExclusions().isExcluded(str, getExclusionType());
    }

    protected boolean excluded(ClassWrapper classWrapper) {
        return this.excluded(classWrapper.originalName);
    }

    protected boolean excluded(MethodWrapper methodWrapper) {
        return this.excluded(methodWrapper.owner.originalName + '.' + methodWrapper.originalName + methodWrapper.originalDescription);
    }

    protected boolean excluded(FieldWrapper fieldWrapper) {
        return this.excluded(fieldWrapper.owner.originalName + '.' + fieldWrapper.originalName + '.' + fieldWrapper.originalDescription);
    }

    protected int getSizeLeeway(MethodNode methodNode) {
        CodeSizeEvaluator cse = new CodeSizeEvaluator(null);
        methodNode.accept(cse);
        // Max allowed method size is 65534 (https://docs.oracle.com/javase/specs/jvms/se10/html/jvms-4.html#jvms-4.7.3)
        return (65534 - cse.getMaxSize());
    }

    protected boolean hasInstructions(MethodNode methodNode) {
        return methodNode.instructions != null && methodNode.instructions.size() > 0;
    }

    protected long tookThisLong(long from) {
        return System.currentTimeMillis() - from;
    }

    protected Map<String, ClassWrapper> getClasses() {
        return this.radon.classes;
    }

    protected Collection<ClassWrapper> getClassWrappers() {
        return this.radon.classes.values();
    }

    protected Map<String, ClassWrapper> getClassPath() {
        return this.radon.classPath;
    }

    protected Map<String, byte[]> getResources() {
        return this.radon.resources;
    }

    public abstract void transform();

    public abstract String getName();

    protected abstract ExclusionType getExclusionType();
}
