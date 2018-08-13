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

import java.util.ArrayList;
import org.objectweb.asm.tree.ClassNode;

public class ClassWrapper {
    public ClassNode classNode;
    public String originalName;
    public boolean libraryNode;

    public ArrayList<MethodWrapper> methods = new ArrayList<>();
    public ArrayList<FieldWrapper> fields = new ArrayList<>();

    public ClassWrapper(ClassNode classNode, boolean libraryNode) {
        this.classNode = classNode;
        this.originalName = classNode.name;
        this.libraryNode = libraryNode;

        ClassWrapper instance = this;
        classNode.methods.forEach(methodNode -> methods.add(new MethodWrapper(methodNode, instance, methodNode.name, methodNode.desc)));
        if (classNode.fields != null) {
            classNode.fields.forEach(fieldNode -> fields.add(new FieldWrapper(fieldNode, instance, fieldNode.name, fieldNode.desc)));
        }
    }
}
