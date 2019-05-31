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
     * @return wrapped {@link FieldNode}.
     */
    public FieldNode getFieldNode() {
        return fieldNode;
    }

    public void setFieldNode(FieldNode fieldNode) {
        this.fieldNode = fieldNode;
    }

    /**
     * @return owner of this wrapper.
     */
    public ClassWrapper getOwner() {
        return owner;
    }

    /**
     * @return original name of wrapped {@link FieldNode}.
     */
    public String getOriginalName() {
        return originalName;
    }

    /**
     * @return original description of wrapped {@link FieldNode}
     */
    public String getOriginalDescription() {
        return originalDescription;
    }

    /**
     * @return the current name of the wrapped {@link FieldNode}.
     */
    public String getName() {
        return fieldNode.name;
    }

    /**
     * @return the current description of the wrapped {@link FieldNode}.
     */
    public String getDescription() {
        return fieldNode.desc;
    }

    /**
     * @return {@link FieldAccess} wrapper of represented {@link FieldNode}'s access flags.
     */
    public Access getAccess() {
        return access;
    }

    /**
     * @return raw access flags of wrapped {@link FieldNode}.
     */
    public int getAccessFlags() {
        return fieldNode.access;
    }

    /**
     * @param access access flags to set.
     */
    public void setAccessFlags(int access) {
        fieldNode.access = access;
    }
}
