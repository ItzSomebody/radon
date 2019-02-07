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

package me.itzsomebody.radon.asm;

import org.objectweb.asm.tree.FieldNode;

/**
 * Wrapper for FieldNodes.
 *
 * @author ItzSomebody.
 */
public class FieldWrapper {
    /**
     * Attached FieldNode.
     */
    public FieldNode fieldNode;

    /**
     * Owner of this represented field.
     */
    public ClassWrapper owner;

    /**
     * Original field name.
     */
    public String originalName;

    /**
     * Original field description.
     */
    public String originalDescription;

    /**
     * Creates a FieldWrapper object.
     *
     * @param fieldNode           the {@link FieldNode} attached to this FieldWrapper.
     * @param owner               the owner of this represented field.
     * @param originalName        the original name of the field represented.
     * @param originalDescription the original description of the field represented.
     */
    public FieldWrapper(FieldNode fieldNode, ClassWrapper owner, String originalName, String originalDescription) {
        this.fieldNode = fieldNode;
        this.owner = owner;
        this.originalName = originalName;
        this.originalDescription = originalDescription;
    }
}
