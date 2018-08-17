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

import org.objectweb.asm.tree.MethodNode;

/**
 * Wrapper for MethodNodes.
 *
 * @author ItzSomebody
 */
public class MethodWrapper {
    /**
     * Attached MethodNode.
     */
    public MethodNode methodNode;

    /**
     * Owner of the method this MethodWrapper represents.
     */
    public ClassWrapper owner;

    /**
     * Original method name;
     */
    public String originalName;

    /**
     * Original method description.
     */
    public String originalDescription;

    /**
     * Creates a MethodWrapper object.
     *
     * @param methodNode the {@link MethodNode} this wrapper represents.
     * @param owner the owner of this represented method.
     * @param originalName the original method name.
     * @param originalDescription the original method description.
     */
    MethodWrapper(MethodNode methodNode, ClassWrapper owner, String originalName, String originalDescription) {
        this.methodNode = methodNode;
        this.owner = owner;
        this.originalName = originalName;
        this.originalDescription = originalDescription;
    }
}
