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

package me.itzsomebody.radon.asm;

import java.util.List;
import me.itzsomebody.radon.asm.accesses.Access;
import me.itzsomebody.radon.asm.accesses.MethodAccess;
import org.objectweb.asm.commons.CodeSizeEvaluator;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TryCatchBlockNode;

/**
 * Wrapper for MethodNodes.
 *
 * @author ItzSomebody
 */
public class MethodWrapper {
    // https://docs.oracle.com/javase/specs/jvms/se12/html/jvms-4.html#jvms-4.7.3
    private static final int MAX_CODE_SIZE = 65535;

    private MethodNode methodNode;
    private final String originalName;
    private final String originalDescription;

    private final Access access;
    private final ClassWrapper owner;

    /**
     * Creates a MethodWrapper object.
     *
     * @param methodNode the {@link MethodNode} this wrapper represents.
     * @param owner      the owner of this represented method.
     */
    public MethodWrapper(MethodNode methodNode, ClassWrapper owner) {
        this.methodNode = methodNode;
        this.originalName = methodNode.name;
        this.originalDescription = methodNode.desc;
        this.access = new MethodAccess(this);
        this.owner = owner;
    }

    /**
     * Attached MethodNode.
     */
    public MethodNode getMethodNode() {
        return methodNode;
    }

    public void setMethodNode(MethodNode methodNode) {
        this.methodNode = methodNode;
    }

    /**
     * @return owner of this wrapper.
     */
    public ClassWrapper getOwner() {
        return owner;
    }

    /**
     * @return original name of wrapped {@link MethodNode}.
     */
    public String getOriginalName() {
        return originalName;
    }

    /**
     * @return original description of wrapped {@link MethodNode}
     */
    public String getOriginalDescription() {
        return originalDescription;
    }

    /**
     * @return the current name of wrapped {@link MethodNode}.
     */
    public String getName() {
        return methodNode.name;
    }

    /**
     * @return the current description of wrapped {@link MethodNode}.
     */
    public String getDescription() {
        return methodNode.desc;
    }

    /**
     * @return the current {@link InsnList} of wrapped {@link MethodNode}.
     */
    public InsnList getInstructions() {
        return methodNode.instructions;
    }

    public void setInstructions(InsnList instructions) {
        methodNode.instructions = instructions;
    }

    /**
     * @return the current {@link TryCatchBlockNode}s of wrapped {@link MethodNode}.
     */
    public List<TryCatchBlockNode> getTryCatchBlocks() {
        return methodNode.tryCatchBlocks;
    }

    /**
     * @return {@link MethodAccess} wrapper of represented {@link MethodNode}'s access flags.
     */
    public Access getAccess() {
        return access;
    }

    /**
     * @return raw access flags of wrapped {@link MethodNode}.
     */
    public int getAccessFlags() {
        return methodNode.access;
    }

    /**
     * @param access access flags to set.
     */
    public void setAccessFlags(int access) {
        methodNode.access = access;
    }

    /**
     * @return the current max allocation of local variables (registers) of wrapped {@link MethodNode}.
     */
    public int getMaxLocals() {
        return methodNode.maxLocals;
    }

    public void setMaxLocals(int maxLocals) {
        methodNode.maxLocals = maxLocals;
    }

    /**
     * @return true if the wrapped {@link MethodNode} represented by this wrapper contains instructions.
     */
    public boolean hasInstructions() {
        return methodNode.instructions != null && methodNode.instructions.size() > 0;
    }

    /**
     * @return computes and returns the size of the wrapped {@link MethodNode}.
     */
    public int getCodeSize() {
        CodeSizeEvaluator cse = new CodeSizeEvaluator(null);
        methodNode.accept(cse);
        return cse.getMaxSize();
    }

    /**
     * @return the leeway between the current size of the wrapped {@link MethodNode} and the max allowed size.
     */
    public int getLeewaySize() {
        return MAX_CODE_SIZE - getCodeSize();
    }
}
