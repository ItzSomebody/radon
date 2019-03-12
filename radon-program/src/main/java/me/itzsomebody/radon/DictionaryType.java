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

package me.itzsomebody.radon;

import me.itzsomebody.radon.exceptions.RadonException;

/**
 * The dictionaries used for string generation.
 * <p>
 * {@link DictionaryType#SPACES} generates a string which is seen as spaces or related. The intent is to make the string
 * "invisible".
 * {@link DictionaryType#UNRECOGNIZED} generates a string full of unicode which the JVM is unable to parse. This leads to
 * strings showing as white boxes.
 * {@link DictionaryType#ALPHABETICAL} generates a string composed only of letters from the English alphabet.
 * {@link DictionaryType#ALPHANUMERIC} generates a string composed only of letters from the English alphabet and integers
 * 0 to 9.
 *
 * @author ItzSomebody
 */
public enum DictionaryType {
    SPACES,
    UNRECOGNIZED,
    ALPHABETICAL,
    ALPHANUMERIC;

    public static DictionaryType intToDictionary(int type) {
        if (type >= values().length)
            throw new RadonException("Illegal dictionary type: " + type);

        return values()[type];
    }
}
