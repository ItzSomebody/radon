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

public class CustomCharsetDictionary implements Dictionary {
    private final char[] charset;
    private int index = 1;

    public CustomCharsetDictionary(String charsetString) {
        charset = charsetString.toCharArray();
    }

    @Override
    public String next() {
        return Dictionary.toBijectiveBase(charset, index++);
    }

    @Override
    public String randomStr(int length) {
        return Dictionary.randomString(charset, length);
    }

    @Override
    public String configName() {
        // This is intentional
        return null;
    }

    @Override
    public Dictionary copy() {
        return new AlphaNumericDictionary();
    }
}
