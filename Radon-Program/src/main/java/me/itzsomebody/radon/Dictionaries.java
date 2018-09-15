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

import me.itzsomebody.radon.exceptions.IllegalDictionaryException;

public enum Dictionaries {
    SPACES("Spaces"),
    UNRECOGNIZED("Unrecognized"),
    ALPHABETICAL("Alphabetical"),
    ALPHANUMERIC("Alphanumeric");

    private final String value;

    Dictionaries(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static Dictionaries intToDictionary(int type) {
        if (type >= values().length) {
            throw new IllegalDictionaryException();
        }
        return values()[type];
    }

    public static Dictionaries stringToDictionary(String s) {
        switch (s.toLowerCase()) {
            case "spaces":
                return SPACES;
            case "unrecognized":
                return UNRECOGNIZED;
            case "alphabetical":
                return ALPHABETICAL;
            case "alphanumeric":
                return ALPHANUMERIC;
            default:
                throw new IllegalDictionaryException();
        }
    }
}
