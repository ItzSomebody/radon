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

package me.itzsomebody.radon.dictionaries;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import me.itzsomebody.radon.utils.RandomUtils;
import me.itzsomebody.radon.utils.StrSequence;

/**
 * Generates strings based on custom user-defined dictionary.
 *
 * @author ItzSomebody
 */
public class CustomDictionary implements Dictionary {
    private final StrSequence CHARSET;
    private final Set<String> cache = new HashSet<>();
    private int index;
    private int cachedLength;
    private String lastGenerated;

    public CustomDictionary(String charset) {
        this(new StrSequence(charset.toCharArray()));
    }
    
    public CustomDictionary(List<String> charset) {
        this(new StrSequence(charset));
    }
    
    public CustomDictionary(StrSequence strSequence) {
        CHARSET = strSequence;
    }

    @Override
    public String randomString(int length) {
        String[] c = new String[length];

        for (int i = 0; i < length; i++)
            c[i] = CHARSET.strAt(RandomUtils.getRandomInt(CHARSET.length()));

        return String.join("", c);
    }

    @Override
    public String uniqueRandomString(int length) {
        if (cachedLength > length)
            length = cachedLength;

        int count = 0;
        int arrLen = CHARSET.length();
        String s;

        do {
            s = randomString(length);

            if (count++ >= arrLen) {
                length++;
                count = 0;
            }
        } while (cache.contains(s));

        cache.add(s);
        cachedLength = length;
        return s;
    }

    @Override
    public String nextUniqueString() {
    
        String out = intToStr(index, CHARSET);
        if(cache.contains(out))
            throw new IllegalStateException("Cache contained string " + out);
        
        cache.add(out);
        index++;
        return out;
    }
    
    /**
     * @param index A unique positive integer
     * @param charset A dictionary to permutate through
     * @return A unique string from for the given integer using permutations of the given charset
     */
    private String intToStr(int index, final StrSequence charset)
    {
        String[] buf = new String[100];
        int charPos = 99;
        
        index = -index; // Negate
        
        while (index <= -charset.length())
        {
            buf[charPos--] = charset.strAt(-(index % charset.length()));
            index = index / charset.length();
        }
        buf[charPos] = charset.strAt(-index);
        
        String[] out = new String[100-charPos];
        System.arraycopy(buf, charPos, out, 0, (100-charPos));
        return String.join("", out);
    }

    @Override
    public String lastUniqueString() {
        return lastGenerated;
    }

    @Override
    public String getDictionaryName() {
        return CHARSET.toString();
    }

    @Override
    public void reset() {
        cache.clear();
        index = 0;
        lastGenerated = null;
    }

    @Override
    public Dictionary copy() {
        return new CustomDictionary(CHARSET);
    }
}
