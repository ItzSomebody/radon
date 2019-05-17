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
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TryCatchBlockNode;

/**
 * Wrapper for MethodNodes.
 *
 * @author ItzSomebody
 */
public class MethodWrapper {
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
     * Owner of the method this MethodWrapper represents.
     */
    public ClassWrapper getOwner() {
        return owner;
    }

    /**
     * Original method name;
     */
    public String getOriginalName() {
        return originalName;
    }

    /**
     * Original method description.
     */
    public String getOriginalDescription() {
        return originalDescription;
    }

    public String getName() {
        return methodNode.name;
    }

    public String getDescription() {
        return methodNode.desc;
    }

    public InsnList getInstructions() {
        return methodNode.instructions;
    }

    public void setInstructions(InsnList instructions) {
        methodNode.instructions = instructions;
    }

    public List<TryCatchBlockNode> getTryCatchBlocks() {
        return methodNode.tryCatchBlocks;
    }

    public Access getAccess() {
        return access;
    }

    public int getAccessFlags() {
        return methodNode.access;
    }

    public void setAccessFlags(int access) {
        methodNode.access = access;
    }

    public int getMaxLocals() {
        return methodNode.maxLocals;
    }

    public void setMaxLocals(int maxLocals) {
        methodNode.maxLocals = maxLocals;
    }
}
