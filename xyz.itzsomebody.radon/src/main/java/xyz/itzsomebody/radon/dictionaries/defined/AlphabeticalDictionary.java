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

package xyz.itzsomebody.radon.dictionaries.defined;

import xyz.itzsomebody.radon.dictionaries.Dictionary;
import xyz.itzsomebody.radon.utils.RandomUtils;

public class AlphabeticalDictionary implements Dictionary {
    private static final char[] CHARSET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();
    private int index = 0;

    @Override
    public String next() {
        // Pasted from java.lang.Integer.toString(int i, int radix)
        // todo: make this no longer pasted
        var charsetLength = CHARSET.length;
        var i = index;
        var buf = new char[33];
        var negative = (i < 0);
        var charPos = 32;

        if (!negative) {
            i = -i;
        }

        while (i <= -charsetLength) {
            buf[charPos--] = CHARSET[-(i % charsetLength)];
            i /= charsetLength;
        }
        buf[charPos] = CHARSET[-i];

        index++;
        return new String(buf, charPos, (33 - charPos));
    }

    @Override
    public String randomStr(int length) {
        var charsetLength = CHARSET.length;
        var buf = new char[length];

        for (int i = 0; i < length; i++) {
            buf[i] = CHARSET[RandomUtils.randomInt(charsetLength)];
        }

        return new String(buf);
    }

    @Override
    public String configName() {
        return "alphabetical";
    }

    @Override
    public Dictionary copy() {
        return new AlphabeticalDictionary();
    }
}
