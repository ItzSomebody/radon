/*
 * Radon - An open-source Java obfuscator
 * Copyright (C) 2021 ItzSomebody
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

package xyz.itzsomebody.radon.dictionaries.defined;

import xyz.itzsomebody.radon.dictionaries.Dictionary;

public class SpacesDictionary implements Dictionary {
    private static final char[] CHARSET = new char[0xF + 1];
    private int index = 1;

    static {
        for (int i = 0; i < CHARSET.length; i++) {
            CHARSET[i] = (char) ('\u2000' + i);
        }
    }

    @Override
    public String next() {
        return Dictionary.toBijectiveBase(CHARSET, index++);
    }

    @Override
    public String randomStr(int length) {
        return Dictionary.randomString(CHARSET, length);
    }

    @Override
    public String configName() {
        return "spaces";
    }

    @Override
    public Dictionary copy() {
        return new SpacesDictionary();
    }
}
