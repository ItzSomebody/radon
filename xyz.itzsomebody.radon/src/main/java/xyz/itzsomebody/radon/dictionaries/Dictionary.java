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

package xyz.itzsomebody.radon.dictionaries;

import xyz.itzsomebody.radon.utils.RandomUtils;

public interface Dictionary {
    String next();

    String randomStr(int length);

    String configName();

    Dictionary copy();

    // https://en.wikipedia.org/wiki/Bijective_numeration
    static String toBijectiveBase(char[] charset, int decimal) {
        var sb = new StringBuilder();
        while (decimal-- > 0) {
            sb.insert(0, charset[decimal % charset.length]);
            decimal /= charset.length;
        }
        return sb.toString();
    }

    static String randomString(char[] charset, int length) {
        var charsetLength = charset.length;
        var buf = new char[length];

        for (int i = 0; i < length; i++) {
            buf[i] = charset[RandomUtils.randomInt(charsetLength)];
        }

        return new String(buf);
    }
}
