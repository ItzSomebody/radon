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

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;
import java.util.List;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import me.itzsomebody.radon.transformers.stringencryption.LightStringEncryption;
import me.itzsomebody.radon.transformers.stringencryption.NormalStringEncryption;
import me.itzsomebody.radon.transformers.stringencryption.SuperLightStringEncryption;

/**
 * Utils for operating, and generating {@link String}s.
 *
 * @author ItzSomebody
 */
public class StringUtils {
    private final static char[] DICT_ALPHA_NUM = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789".toCharArray();
    private final static char[] DICT_SPACES = new char[]{
            '\u2000', '\u2001', '\u2002', '\u2003', '\u2004', '\u2005', '\u2006', '\u2007', '\u2008', '\u2009', '\u200A', '\u200B', '\u200C', '\u200D', '\u200E', '\u200F'
    };
    private final static char[] DICT_MISC = new char[]{
            '\ua6ac', '\ua6ea', '\ua6ba', '\ua6a3', '\ua6a4', '\ua6b5', '\ua6b0', '\ua6a8'
    };

    /**
     * Returns the proper string generation type given a dictionary type to use.
     *
     * @param dictionary an integer indicating which pre-defined string
     *                   generation type to use.
     * @param len        Length of the string to generate.
     * @return the proper string generation type given a dictionary type to use.
     */
    public static String randomString(int dictionary, int len) {
        switch (dictionary) {
            case 0:
                return crazyString(len);
            case 1:
                return crazyKey(len);
            case 2:
                return alphaNumString(len);
            default:
                throw new IllegalArgumentException("Illegal dictionary type " + dictionary);
        }
    }

    /**
     * Generates and returns a pseudo-random alpha-numeric string.
     *
     * @param len Length of the string to generate.
     * @return a pseudo-random alpha-numeric string.
     */
    public static String alphaNumString(int len) {
        char[] buildString = new char[len];
        for (int i = 0; i < len; i++) {
            buildString[i] = DICT_ALPHA_NUM[NumberUtils.getRandomInt(DICT_ALPHA_NUM.length)];
        }
        return new String(buildString);
    }

    /**
     * Generates a {@link String} consisting only of DICT_SPACES.
     * Stole this idea from NeonObf and Smoke.
     *
     * @param len Length of the string to generate.
     * @return a built {@link String} consisting of DICT_SPACES.
     */
    public static String crazyString(int len) {
        char[] buildString = new char[len];
        for (int i = 0; i < len; i++) {
            buildString[i] = DICT_SPACES[NumberUtils.getRandomInt(DICT_SPACES.length)];
        }
        return new String(buildString);
    }

    /**
     * Alternative generator to the method above.
     *
     * @param len Length of the string to generate.
     * @return a {@link String} consisting of characters the JVM doesn't
     * recognize.
     */
    public static String crazyKey(int len) {
        char[] buildString = new char[len];
        for (int i = 0; i < len; i++) {
            buildString[i] = DICT_MISC[NumberUtils.getRandomInt(DICT_MISC.length)];
        }
        return new String(buildString);
    }

    /**
     * Returns an encrypted string used by {@link SuperLightStringEncryption}.
     *
     * @param msg string to encrypt.
     * @param key random integer
     * @return an encrypted string used by {@link SuperLightStringEncryption}.
     */
    public static String superLightEncrypt(String msg, int key) {
        char[] encryptedArray = msg.toCharArray();
        char[] returnThis = new char[encryptedArray.length];

        for (int i = 0; i < returnThis.length; i++) {
            returnThis[i] = (char) (encryptedArray[i] ^ key);
        }

        return new String(returnThis);
    }

    /**
     * Returns an encrypted string used by {@link LightStringEncryption}.
     *
     * @param msg        string to encrypt.
     * @param className  name of the class the msg is in.
     * @param methodName name of the method the msg is in.
     * @param key3       random integer
     * @return an encrypted string used by {@link LightStringEncryption}.
     */
    public static String lightEncrypt(String msg, String className,
                                      String methodName, int key3) {
        char[] chars = msg.toCharArray();
        char[] returnThis = new char[chars.length];
        for (int i = 0; i < returnThis.length; i++) {
            char key2 = (char) methodName.hashCode();
            char key1 = (char) className.hashCode();
            returnThis[i] = (char) (key3 ^ key2 ^ key1 ^ chars[i]);
        }

        return new String(returnThis);
    }

    /**
     * Returns {@link String} encrypted with AES symmetrical encryption
     * algorithm to encrypt {@link String}s.
     *
     * @param msg    {@link String} to encrypt.
     * @param secret {@link String} to use as a key.
     * @return encrypted {@link String}
     */
    public static String aesEncrypt(String msg, String secret) {
        try {
            SecretKeySpec secretKey;
            byte[] key = secret.getBytes("UTF-8");
            MessageDigest sha = MessageDigest.getInstance("SHA-1");
            key = sha.digest(key);
            key = Arrays.copyOf(key, 16);
            secretKey = new SecretKeySpec(key, "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            return Base64.getEncoder().encodeToString(cipher.doFinal(msg.
                    getBytes("UTF-8")));
        } catch (Throwable t) {
            throw new IllegalStateException("Was unable to encrypt string " +
                    msg + " using " + secret);
        }
    }

    /**
     * Returns {@link String} decrypted AES encrypted {@link String}s.
     *
     * @param strToDecrypt {@link String} to decrypt.
     * @param secret       {@link String} to use as a key.
     * @return decrypted {@link String}
     */
    public static String aesDecrypt(String strToDecrypt, String secret) {
        try {
            SecretKeySpec secretKey;
            byte[] key = secret.getBytes("UTF-8");
            MessageDigest sha = MessageDigest.getInstance("SHA-1");
            key = sha.digest(key);
            key = Arrays.copyOf(key, 16);
            secretKey = new SecretKeySpec(key, "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            return new String(cipher.doFinal(Base64.getDecoder()
                    .decode(strToDecrypt)));
        } catch (Throwable t) {
            throw new IllegalStateException("Was unable to decrypt string " +
                    strToDecrypt + " using " + secret);
        }
    }

    /**
     * Returns a generated classname based on current class packages.
     *
     * @return a generated classname based on current class packages.
     */
    public static String randomClassName(Collection<String> theClassNames, int dictionary, int len) {
        List<String> classNames = new ArrayList<>(theClassNames);

        String randomClass = classNames.get(NumberUtils.
                getRandomInt(classNames.size()));
        String[] split = randomClass.split("/");
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < split.length - 1; i++) {
            sb.append(split[i]);
            sb.append("/");
        }

        sb.append(StringUtils.randomString(dictionary, len));

        return new String(sb);
    }

    /**
     * Returns a generated classname based on current class packages.
     *
     * @return a generated classname based on current class packages.
     */
    public static String randomClass(Collection<String> theClassNames) {
        List<String> classNames = new ArrayList<>(theClassNames);

        return classNames.get(NumberUtils.getRandomInt(classNames.size()));
    }

    /**
     * Returns encrypted {@link String} used by {@link NormalStringEncryption}.
     *
     * @param className  the className to get hashcode from.
     * @param methodName the methodName to get hashcode from.
     * @param key3       the extra key to ensure a different encryption each
     *                   time.
     * @param msg        the string to encrypt
     * @return encrypted {@link String} used by {@link NormalStringEncryption}.
     */
    public static String normalEncrypt(String className, String methodName,
                                       int key3, String msg) {
        char[] chars = msg.toCharArray();
        char[] returnThis = new char[chars.length];
        for (int i = 0; i < chars.length; i++) {
            returnThis[i] = (char) (key3 ^ methodName.hashCode() ^
                    className.hashCode() ^ chars[i]);
        }

        return new String(returnThis);
    }
}
