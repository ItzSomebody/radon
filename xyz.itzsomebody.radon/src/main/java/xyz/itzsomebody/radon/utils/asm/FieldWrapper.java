/*
 * Radon - An open-source Java obfuscator
 * Copyright (C) 2020 ItzSomebody
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

package xyz.itzsomebody.radon.utils.asm;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.FieldNode;

/**
 * Wrapper around {@link FieldNode}.
 *
 * @author itzsomebody
 */
public class FieldWrapper implements Opcodes {
    private FieldNode fieldNode;
    private final String originalName;
    private final String originalType;
    private final ClassWrapper owner;

    public FieldWrapper(FieldNode fieldNode, ClassWrapper owner) {
        this.fieldNode = fieldNode;
        this.originalName = fieldNode.name;
        this.originalType = fieldNode.desc;
        this.owner = owner;
    }

    // -----------------
    // Getters / Setters
    // -----------------

    public FieldNode getFieldNode() {
        return fieldNode;
    }

    public void setFieldNode(FieldNode fieldNode) {
        this.fieldNode = fieldNode;
    }

    public String getOriginalName() {
        return originalName;
    }

    public String getOriginalType() {
        return originalType;
    }

    public ClassWrapper getOwner() {
        return owner;
    }

    // ------------
    // Access stuff
    // ------------

    public void addAccessFlags(int flags) {
        fieldNode.access |= flags;
    }

    public void removeAccessFlags(int flags) {
        fieldNode.access &= ~flags;
    }

    public boolean isPublic() {
        return (ACC_PUBLIC & fieldNode.access) != 0;
    }

    public boolean isPrivate() {
        return (ACC_PRIVATE & fieldNode.access) != 0;
    }

    public boolean isProtected() {
        return (ACC_PROTECTED & fieldNode.access) != 0;
    }

    public boolean isStatic() {
        return (ACC_STATIC & fieldNode.access) != 0;
    }

    public boolean isFinal() {
        return (ACC_FINAL & fieldNode.access) != 0;
    }

    public boolean isVolatile() {
        return (ACC_PUBLIC & fieldNode.access) != 0;
    }

    public boolean isTransient() {
        return (ACC_PUBLIC & fieldNode.access) != 0;
    }

    public boolean isSynthetic() {
        return (ACC_SYNTHETIC & fieldNode.access) != 0;
    }

    public boolean isDeprecated() {
        return (ACC_DEPRECATED & fieldNode.access) != 0;
    }

    // -----
    // Misc.
    // -----

    public static FieldWrapper from(FieldNode FieldNode, ClassWrapper owner) {
        return new FieldWrapper(FieldNode, owner);
    }

    public boolean hasVisibleAnnotations() {
        return fieldNode.visibleAnnotations != null && fieldNode.visibleAnnotations.size() > 0;
    }
}
