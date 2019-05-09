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

package me.itzsomebody.radon.utils;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Utilities for strings. Primarily used for string generation.
 *
 * @author ItzSomebody
 */
public class StringUtils {
    private final static char[] ALPHA_NUM = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();
    private final static char[] ALPHA = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();

    public static String randomSpacesString(int length) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < length; i++)
            sb.append((char) (RandomUtils.getRandomInt(16) + '\u2000'));

        return sb.toString();
    }

    public static String randomUnrecognizedString(int length) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < length; i++)
            sb.append((char) (RandomUtils.getRandomInt(8) + '\ua6ac'));

        return sb.toString();
    }

    public static String randomAlphaString(int length) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < length; i++)
            sb.append(ALPHA[RandomUtils.getRandomInt(ALPHA.length)]);

        return sb.toString();
    }

    public static String randomAlphaNumericString(int length) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < length; i++)
            sb.append(ALPHA_NUM[RandomUtils.getRandomInt(ALPHA_NUM.length)]);

        return sb.toString();
    }

    public static String randomClassName(Collection<String> classNames) {
        ArrayList<String> list = new ArrayList<>(classNames);

        String first = list.get(RandomUtils.getRandomInt(classNames.size()));
        String second = list.get(RandomUtils.getRandomInt(classNames.size()));

        return first + '$' + second.substring(second.lastIndexOf("/") + 1);
    }

    public static String randomUnicodeString(int length) {
        StringBuilder sb = new StringBuilder();
        for ( int i = 0; i < length; i++ ) {
            sb.append("\u009A\u009C\u0000\u0003\u2421\r\0\f\t\b\u0001\u2000\u0000\u2421\u2420\u2411\u0002\n\u0003\n\u0004\u0005\u0006\n\u0007\u0020\u0011\u0012\u0010\n\u0008\r\u0009\u2418");
        }
        String str = sb.toString();
        return str.replace(Character.toString(str.charAt(RandomUtils.getRandomInt(str.length()))), "\u2421");
    }
}
