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

import java.util.HashSet;
import java.util.Set;

/**
 * Used to keep information on the hierarchy of all loaded classes.
 *
 * @author ItzSomebody
 */
public class ClassTree {
    private final ClassWrapper classWrapper;
    private final Set<String> parentClasses = new HashSet<>();
    private final Set<String> subClasses = new HashSet<>();

    /**
     * Creates a ClassTree object.
     *
     * @param classWrapper the ClassWraper attached to this ClassTree.
     */
    public ClassTree(ClassWrapper classWrapper) {
        this.classWrapper = classWrapper;
    }

    /**
     * Attached ClassWrapper.
     */
    public ClassWrapper getClassWrapper() {
        return classWrapper;
    }

    /**
     * Names of classes this represented class inherits from.
     */
    public Set<String> getParentClasses() {
        return parentClasses;
    }

    /**
     * Names of classes this represented class is inherited by.
     */
    public Set<String> getSubClasses() {
        return subClasses;
    }
}
