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

package me.itzsomebody.radon.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Utilities for strings. Primarily used for string generation.
 *
 * @author ItzSomebody
 */
public class StringUtils {
    public static final int MAX_SAFE_BYTE_COUNT = 65535;

    private final static char[] ALPHA_NUM = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();
    private final static char[] ALPHA = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();

    public static String randomSpacesString(int length) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < length; i++)
            sb.append((char) (RandomUtils.getRandomIntNoOrigin(16) + '\u2000'));

        return sb.toString();
    }

    public static String randomUnrecognizedString(int length) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < length; i++)
            sb.append((char) (RandomUtils.getRandomIntNoOrigin(8) + '\ua6ac'));

        return sb.toString();
    }

    public static String randomAlphaString(int length) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < length; i++)
            sb.append(ALPHA[RandomUtils.getRandomIntNoOrigin(ALPHA.length)]);

        return sb.toString();
    }

    public static String randomAlphaNumericString(int length) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < length; i++)
            sb.append(ALPHA_NUM[RandomUtils.getRandomIntNoOrigin(ALPHA_NUM.length)]);

        return sb.toString();
    }

    public static String randomClassName(Collection<String> classNames) {
        ArrayList<String> list = new ArrayList<>(classNames);
        String first = list.get(RandomUtils.getRandomIntNoOrigin(classNames.size()));
        String second = list.get(RandomUtils.getRandomIntNoOrigin(classNames.size()));

        return first + '$' + second.substring(second.lastIndexOf("/") + 1);
    }

    public static String[] splitUtf8ToChunks(String text, int maxBytes) {
        List<String> parts = new ArrayList<>();

        char[] chars = text.toCharArray();

        int lastCharIndex = 0;
        int currentChunkSize = 0;

        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            int charSize = getUtf8CharSize(c);
            if (currentChunkSize + charSize < maxBytes) {
                currentChunkSize += charSize;
            } else {
                parts.add(text.substring(lastCharIndex, i));
                currentChunkSize = 0;
                lastCharIndex = i;
            }
        }

        if (currentChunkSize != 0) {
            parts.add(text.substring(lastCharIndex));
        }

        return parts.toArray(new String[0]);
    }

    public static int getUtf8CharSize(char c) {
        if (c >= 0x0001 && c <= 0x007F) {
            return 1;
        } else if (c <= 0x07FF) {
            return 2;
        }
        return 3;
    }

    public static int getUtf8StringSize(String string) {
        int byteLength = 0;
        for (int i = 0; i < string.length(); ++i) {
            char charValue = string.charAt(i);
            byteLength += getUtf8CharSize(charValue);
        }
        return byteLength;
    }
}
