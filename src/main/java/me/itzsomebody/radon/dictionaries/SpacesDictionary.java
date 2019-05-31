package me.itzsomebody.radon.dictionaries;

import java.util.HashSet;
import java.util.Set;
import me.itzsomebody.radon.utils.RandomUtils;

public class SpacesDictionary implements Dictionary {
    private static final char[] CHARSET = new char[0xF + 1];
    private final Set<String> cache = new HashSet<>();
    private int index;
    private int cachedLength;
    private String lastGenerated;

    static {
        for (int i = 0; i < CHARSET.length; i++)
            CHARSET[i] = (char) ('\u2000' + i);
    }

    @Override
    public String randomString(int length) {
        char[] c = new char[length];

        for (int i = 0; i < length; i++)
            c[i] = CHARSET[RandomUtils.getRandomInt(CHARSET.length)];

        return new String(c);
    }

    @Override
    public String uniqueRandomString(int length) {
        if (cachedLength > length)
            length = cachedLength;

        int count = 0;
        int arrLen = CHARSET.length;
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
        // copy-pasted from Integer.toString(int i, int radix)
        int charsetLength = CHARSET.length;
        int i = index;
        char[] buf = new char[33];
        boolean negative = (i < 0);
        int charPos = 32;

        if (!negative) {
            i = -i;
        }

        while (i <= -charsetLength) {
            buf[charPos--] = CHARSET[-(i % charsetLength)];
            i /= charsetLength;
        }
        buf[charPos] = CHARSET[-i];

        String s = new String(buf, charPos, (33 - charPos));
        lastGenerated = s;
        index++;
        return s;
    }

    @Override
    public String lastUniqueString() {
        return lastGenerated;
    }

    @Override
    public String getDictionaryName() {
        return "spaces";
    }

    @Override
    public void reset() {
        cache.clear();
        index = 0;
        lastGenerated = null;
    }

    @Override
    public Dictionary copy() {
        return new SpacesDictionary();
    }
}
