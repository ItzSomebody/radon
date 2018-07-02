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
import me.itzsomebody.radon.transformers.stringencryption.HeavyStringEncryption;
import me.itzsomebody.radon.transformers.stringencryption.LightStringEncryption;
import me.itzsomebody.radon.transformers.stringencryption.NormalStringEncryption;
import me.itzsomebody.radon.transformers.stringencryption.SuperLightStringEncryption;

/**
 * Utils for operating, and generating {@link String}s.
 *
 * @author ItzSomebody
 */
public class StringUtils {
    /**
     * Returns the proper string generation type given a dictionary type to use.
     *
     * @param dictionary an integer indicating which pre-defined string
     *                   generation type to use.
     * @return the proper string generation type given a dictionary type to use.
     */
    public static String randomString(int dictionary) {
        switch (dictionary) {
            case 0:
                return crazyString();
            case 1:
                return crazyKey();
            case 2:
                return alphaNumString();
            default:
                throw new IllegalArgumentException("Illegal dictionary type " + dictionary);
        }
    }

    /**
     * Generates and returns a pseudo-random alpha-numeric string.
     *
     * @return a pseudo-random alpha-numeric string.
     */
    public static String alphaNumString() {
        int numberOfChars = 4;
        char[] alphaNum = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789".toCharArray();
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < numberOfChars; i++) {
            sb.append(alphaNum[NumberUtils.getRandomInt(alphaNum.length)]);
        }

        return sb.toString();
    }

    /**
     * Generates a {@link String} with a length of 10 consisting of spaces.
     * Stole this idea from NeonObf and Smoke.
     *
     * @return a built {@link String} consisting of spaces.
     */
    public static String crazyString() {
        int numberOfChars = 10; // Just so I can do a quick switch.
        char[] buildString = new char[numberOfChars];

        for (int i = 0; i < numberOfChars; i++) {
            switch (NumberUtils.getRandomInt(16)) {
                case 0:
                    buildString[i] = '\u2000';
                    break;
                case 1:
                    buildString[i] = '\u2001';
                    break;
                case 2:
                    buildString[i] = '\u2002';
                    break;
                case 3:
                    buildString[i] = '\u2003';
                    break;
                case 4:
                    buildString[i] = '\u2004';
                    break;
                case 5:
                    buildString[i] = '\u2005';
                    break;
                case 6:
                    buildString[i] = '\u2006';
                    break;
                case 7:
                    buildString[i] = '\u2007';
                    break;
                case 8:
                    buildString[i] = '\u2008';
                    break;
                case 9:
                    buildString[i] = '\u2009';
                    break;
                case 10:
                    buildString[i] = '\u200A';
                    break;
                case 11:
                    buildString[i] = '\u200B';
                    break;
                case 12:
                    buildString[i] = '\u200C';
                    break;
                case 13:
                    buildString[i] = '\u200D';
                    break;
                case 14:
                    buildString[i] = '\u200E';
                    break;
                case 15:
                    buildString[i] = '\u200F';
                    break;
            }
        }

        return new String(buildString);
    }

    /**
     * Alternative generator to the method above.
     *
     * @return a {@link String} consisting of characters the JVM doesn't
     * recognize.
     */
    public static String crazyKey() {
        int numberOfChars = 10; // Just so I can do a quick switch.
        char[] buildString = new char[numberOfChars];

        for (int i = 0; i < numberOfChars; i++) {
            switch (NumberUtils.getRandomInt(8)) {
                case 0:
                    buildString[i] = '\ua6ac';
                    break;
                case 1:
                    buildString[i] = '\ua6ea';
                    break;
                case 2:
                    buildString[i] = '\ua6ba';
                    break;
                case 3:
                    buildString[i] = '\ua6a3';
                    break;
                case 4:
                    buildString[i] = '\ua6a4';
                    break;
                case 5:
                    buildString[i] = '\ua6b5';
                    break;
                case 6:
                    buildString[i] = '\ua6b0';
                    break;
                case 7:
                    buildString[i] = '\ua6a8';
                    break;
            }
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
    public static String randomClassName(Collection<String> theClassNames, int dictionary) {
        List<String> classNames = new ArrayList<>(theClassNames);

        String randomClass = classNames.get(NumberUtils.
                getRandomInt(classNames.size()));
        String[] split = randomClass.split("/");
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < split.length - 1; i++) {
            sb.append(split[i]);
            sb.append("/");
        }

        sb.append(StringUtils.randomString(dictionary));

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

    /**
     * Encrypts string for use in {@link HeavyStringEncryption}.
     *
     * @param msg  {@link String} to encrypt.
     * @param key1 decryptor's containing class's name's hashcode.
     * @param key2 hashcode of <clinit>.
     * @param key3 hashcode of the string's containing class's name's hashcode.
     * @param key4 hashcode of the string's containing method's name's hashcode.
     * @param key5 random integer to ensure randomization.
     * @return {@link String} as the encrypted variant of the inputted string.
     */
    public static String heavyEncrypt(String msg, int key1, int key2, int key3, int key4, int key5) {
        char[] chars = msg.toCharArray();

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < chars.length; i++) {
            switch (i % 8) {
                case 0:
                    sb.append((char) (chars[i] ^ key3 ^ key5));
                    break;
                case 1:
                    sb.append((char) (chars[i] ^ key2 ^ key4));
                    break;
                case 2:
                    sb.append((char) (chars[i] ^ key1 ^ key3));
                    break;
                case 3:
                    sb.append((char) (chars[i] ^ key5 ^ key2));
                    break;
                case 4:
                    sb.append((char) (chars[i] ^ key4 ^ key1));
                    break;
                case 5:
                    sb.append((char) (chars[i] ^ key3 ^ key2));
                    break;
                case 6:
                    sb.append((char) (chars[i] ^ key4 ^ key3));
                    break;
                case 7:
                    sb.append((char) (chars[i] ^ key5 ^ key4));
                    break;
            }
        }

        return sb.toString();
    }
}
