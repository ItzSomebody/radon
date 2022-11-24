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

package xyz.itzsomebody.radon.exceptions;

/**
 * Subclass of {@link PreventableRadonException} which is thrown when Radon is unable to find classes that should have
 * been in either the input JAR or in one of the user-provided libraries in the config.
 *
 * @author itzsomebody
 */
public class MissingClassException extends PreventableRadonException {
    private MissingClassException(String className, boolean library) {
        super("Do NOT report this as an issue unless you have ensured the supposedly missing class is actually in at " +
                "least one of the provided libraries in your config.\n" + String.format(
                "Could not find \"%s\" in the %s.", className, library ? "classpath" : "input JAR classes"));
    }

    public static MissingClassException forInputClass(String className) {
        return new MissingClassException(className, false);
    }

    public static MissingClassException forLibraryClass(String className) {
        return new MissingClassException(className, true);
    }
}
