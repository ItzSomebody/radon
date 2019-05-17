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

import me.itzsomebody.radon.Radon;
import org.objectweb.asm.ClassWriter;

public class CustomClassWriter extends ClassWriter {
    private Radon radon;

    public CustomClassWriter(int flags) {
        super(flags);
        this.radon = Radon.getInstance();
    }

    @Override
    protected String getCommonSuperClass(final String type1, final String type2) {
        if ("java/lang/Object".equals(type1) || "java/lang/Object".equals(type2))
            return "java/lang/Object";

        String first = deriveCommonSuperName(type1, type2);
        String second = deriveCommonSuperName(type2, type1);
        if (!"java/lang/Object".equals(first))
            return first;

        if (!"java/lang/Object".equals(second))
            return second;

        return getCommonSuperClass(radon.getClassWrapper(type1).getSuperName(), radon.getClassWrapper(type2).getSuperName());
    }

    private String deriveCommonSuperName(String type1, String type2) {
        ClassWrapper first = radon.getClassWrapper(type1);
        ClassWrapper second = radon.getClassWrapper(type2);
        if (radon.isAssignableFrom(type1, type2))
            return type1;
        else if (radon.isAssignableFrom(type2, type1))
            return type2;
        else if (first.getAccess().isInterface() || second.getAccess().isInterface())
            return "java/lang/Object";
        else {
            String temp;

            do {
                temp = first.getSuperName();
                first = radon.getClassWrapper(temp);
            } while (!radon.isAssignableFrom(temp, type2));
            return temp;
        }
    }
}
