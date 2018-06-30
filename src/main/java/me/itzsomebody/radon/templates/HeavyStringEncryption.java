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

package me.itzsomebody.radon.templates;

import java.util.WeakHashMap;

public class HeavyStringEncryption {
    private static WeakHashMap<Integer, String> decrypted;
    private static int key1;
    private static int key2;

    static {
        decrypted = new WeakHashMap<>();
        StackTraceElement ste = Thread.currentThread().getStackTrace()[1];
        key1 = ste.getClassName().hashCode();
        key2 = ste.getMethodName().hashCode();
    }

    private static int getHashy(char[] chars) {
        int hash = 1;
        for (int i = 0; i < chars.length; i++) {
            int thisChar = chars[i];
            int var1 = thisChar & 255;
            int var2 = thisChar | 255;
            int var3 = thisChar ^ 255;
            int var4 = var1 << 4 | var2 >>> 4;
            int var5 = var3 << 3 | var4 >>> 6;
            var1 &= var4 << 2 | var1 >>> 2;
            var3 |= var1 >> 4 | var2 << 2;
            var2 ^= var5 >>> 4 | var3 << 6;
            var4 += var2;
            var5 = var1 >>> 5 | var3 << 2;
            hash ^= var1 ^ var2 ^ var3 ^ var4 ^ var5;
        }

        return hash;
    }

    private static String returnCache(int hashy) {
        return decrypted.get(hashy);
    }

    private static void cacheString(String string, int hashy) {
        decrypted.put(hashy, string);
    }

    public static String decrypt(Object encryptedString, Object useless, int key5) {
        char[] chars = ((String) encryptedString).toCharArray();
        int hash = getHashy(chars);
        String cacheResult = returnCache(hash);
        if (cacheResult != null)
            return cacheResult;

        StackTraceElement[] stes = Thread.currentThread().getStackTrace();
        int key3 = stes[2].getClassName().hashCode();
        int key4 = stes[2].getMethodName().hashCode();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < chars.length; i++) {
            switch (i % 8) {
                case 0:
                    sb.append((char) (key5 ^ key3 ^ chars[i]));
                    break;
                case 1:
                    sb.append((char) (key4 ^ key2 ^ chars[i]));
                    break;
                case 2:
                    sb.append((char) (key3 ^ key1 ^ chars[i]));
                    break;
                case 3:
                    sb.append((char) (key2 ^ key5 ^ chars[i]));
                    break;
                case 4:
                    sb.append((char) (key1 ^ key4 ^ chars[i]));
                    break;
                case 5:
                    sb.append((char) (key2 ^ key3 ^ chars[i]));
                    break;
                case 6:
                    sb.append((char) (key3 ^ key4 ^ chars[i]));
                    break;
                case 7:
                    sb.append((char) (key4 ^ key5 ^ chars[i]));
                    break;
            }
        }
        String result = sb.toString();
        cacheString(result, hash);
        return result;
    }
}
