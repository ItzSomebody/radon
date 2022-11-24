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

import org.objectweb.asm.ClassWriter;
import xyz.itzsomebody.radon.Radon;

import java.util.ArrayDeque;
import java.util.HashSet;

/**
 * Custom-implemented version of {@link ClassWriter} which doesn't use the internal JVM classpath when computing
 * stackmap frames.
 *
 * TODO: Perhaps consider creating fully-worked out hierarchies for JVM runtime classes so we don't have to keep checking huge jars
 * TODO: Implement shadow classes
 *
 * @author itzsomebody
 */
public class RadonClassWriter extends ClassWriter {
    private static final Radon radon = Radon.getInstance();

    public RadonClassWriter(int flags) {
        super(flags);
    }

    @Override
    protected String getCommonSuperClass(final String type1, final String type2) {
        if ("java/lang/Object".equals(type1) || "java/lang/Object".equals(type2))
            return "java/lang/Object";

        String first = deriveCommonSuperName(type1, type2);
        String second = deriveCommonSuperName(type2, type1);
        if (!"java/lang/Object".equals(first)) {
            return first;
        }
        if (!"java/lang/Object".equals(second)) {
            return second;
        }

        return getCommonSuperClass(radon.getClasspathWrapper(type1).getSuperName(), radon.getClasspathWrapper(type2).getSuperName());
    }

    private String deriveCommonSuperName(final String type1, final String type2) {
        ClassWrapper first = radon.getClasspathWrapper(type1);
        ClassWrapper second = radon.getClasspathWrapper(type2);
        if (isAssignableFrom(type1, type2)) {
            return type1;
        } else if (isAssignableFrom(type2, type1)) {
            return type2;
        } else if (first.isInterface() || second.isInterface()) {
            return "java/lang/Object";
        } else {
            String temp;

            do {
                temp = first.getSuperName();
                first = radon.getClasspathWrapper(temp);
            } while (!isAssignableFrom(temp, type2));
            return temp;
        }
    }

    private boolean isAssignableFrom(String type1, String type2) {
        if ("java/lang/Object".equals(type1)) {
            return true;
        }
        if (type1.equals(type2)) {
            return true;
        }

        ClassWrapper first = radon.getClasspathWrapper(type1);
        radon.getClasspathWrapper(type2); // Ensure type2 was loaded at some point

        var allChildren = new HashSet<String>();
        var toProcess = new ArrayDeque<String>();
        first.getChildren().forEach(child -> {
            toProcess.add(child.getName());
        });

        while (!toProcess.isEmpty()) {
            String next = toProcess.poll();

            if (allChildren.add(next)) {
                ClassWrapper temp = radon.getClasspathWrapper(next);
                temp.getChildren().forEach(child -> {
                    toProcess.add(child.getName());
                });
            }
        }
        return allChildren.contains(type2);
    }
}
