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

package me.itzsomebody.radon.transformers.shrinkers;

public class ShrinkerSetup {
    private boolean removeVisibleAnnotations;
    private boolean removeInvisibleAnnotations;
    private boolean removeAttributes;
    private boolean removeDebug;
    private boolean removeUnusedCode;
    private boolean removeUnusedMembers;

    public ShrinkerSetup(boolean removeVisibleAnnotations, boolean removeInvisibleAnnotations, boolean removeAttributes, boolean removeDebug, boolean removeUnusedCode, boolean removeUnusedMembers) {
        this.removeVisibleAnnotations = removeVisibleAnnotations;
        this.removeInvisibleAnnotations = removeInvisibleAnnotations;
        this.removeAttributes = removeAttributes;
        this.removeDebug = removeDebug;
        this.removeUnusedCode = removeUnusedCode;
        this.removeUnusedMembers = removeUnusedMembers;
    }

    public boolean isRemoveVisibleAnnotations() {
        return this.removeVisibleAnnotations;
    }

    public boolean isRemoveInvisibleAnnotations() {
        return this.removeInvisibleAnnotations;
    }

    public boolean isRemoveAttributes() {
        return this.removeAttributes;
    }

    public boolean isRemoveDebug() {
        return this.removeDebug;
    }

    public boolean isRemoveUnusedCode() {
        return this.removeUnusedCode;
    }

    public boolean isRemoveUnusedMembers() {
        return this.removeUnusedMembers;
    }
}