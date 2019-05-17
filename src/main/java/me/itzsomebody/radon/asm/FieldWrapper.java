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

import me.itzsomebody.radon.asm.accesses.Access;
import me.itzsomebody.radon.asm.accesses.FieldAccess;
import org.objectweb.asm.tree.FieldNode;

/**
 * Wrapper for FieldNodes.
 *
 * @author ItzSomebody.
 */
public class FieldWrapper {
    private FieldNode fieldNode;
    private final String originalName;
    private final String originalDescription;

    private final Access access;
    private final ClassWrapper owner;

    /**
     * Creates a FieldWrapper object.
     *
     * @param fieldNode the {@link FieldNode} attached to this FieldWrapper.
     * @param owner     the owner of this represented field.
     */
    public FieldWrapper(FieldNode fieldNode, ClassWrapper owner) {
        this.fieldNode = fieldNode;
        this.originalName = fieldNode.name;
        this.originalDescription = fieldNode.desc;
        this.access = new FieldAccess(this);
        this.owner = owner;
    }

    /**
     * Attached FieldNode.
     */
    public FieldNode getFieldNode() {
        return fieldNode;
    }

    public void setFieldNode(FieldNode fieldNode) {
        this.fieldNode = fieldNode;
    }

    /**
     * Owner of this represented field.
     */
    public ClassWrapper getOwner() {
        return owner;
    }

    /**
     * Original field name.
     */
    public String getOriginalName() {
        return originalName;
    }

    /**
     * Original field description.
     */
    public String getOriginalDescription() {
        return originalDescription;
    }

    public String getName() {
        return fieldNode.name;
    }

    public String getDescription() {
        return fieldNode.desc;
    }

    public Access getAccess() {
        return access;
    }

    public int getAccessFlags() {
        return fieldNode.access;
    }

    public void setAccessFlags(int access) {
        fieldNode.access = access;
    }
}
