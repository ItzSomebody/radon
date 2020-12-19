package xyz.itzsomebody.radon.dictionaries.defined;

import xyz.itzsomebody.radon.dictionaries.Dictionary;
import xyz.itzsomebody.radon.utils.RandomUtils;

public class AlphaNumericDictionary implements Dictionary {
    private static final char[] CHARSET = "1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();
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
