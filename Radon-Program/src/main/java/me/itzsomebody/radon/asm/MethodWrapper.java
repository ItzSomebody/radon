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

public class MethodWrapper {
    public MethodNode methodNode;
    public ClassWrapper owner;
    public String originalName;
    public String originalDescription;

    public MethodWrapper(MethodNode methodNode, ClassWrapper owner, String originalName, String originalDescription) {
        this.methodNode = methodNode;
        this.owner = owner;
        this.originalName = originalName;
        this.originalDescription = originalDescription;
    }
}
