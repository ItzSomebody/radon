package me.itzsomebody.radon.utils;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
import java.util.*;

import me.itzsomebody.radon.transformers.stringencryption.*;
import me.itzsomebody.radon.transformers.misc.*;

/**
 * Utils for operating, and generating {@link String}s.
 *
 * @author ItzSomebody
 */
public class StringUtils {
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
     * Generates and returns a gigantic string.
     *
     * @return a gigantic string.
     */
    public static String bigLDC() {
        StringBuilder sb = new StringBuilder();
        while (sb.length() < 65536 - 1) {
            sb.append("[");
        }
        return new String(sb);
    }

    /**
     * Returns a generated classname based on current class packages.
     *
     * @return a generated classname based on current class packages.
     */
    public static String randomClassName(Collection<String> theClassNames) {
        List<String> classNames = new ArrayList<>();
        classNames.addAll(theClassNames);

        String randomClass = classNames.get(NumberUtils.
                getRandomInt(classNames.size()));
        String[] split = randomClass.split("/");
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < split.length - 1; i++) {
            sb.append(split[i]);
            sb.append("/");
        }

        sb.append(crazyString());

        return new String(sb);
    }

    /**
     * Returns a generated classname based on current class packages.
     *
     * @return a generated classname based on current class packages.
     */
    public static String randomClass(Collection<String> theClassNames) {
        List<String> classNames = new ArrayList<>();
        classNames.addAll(theClassNames);

        String randomClass = classNames.get(NumberUtils
                .getRandomInt(classNames.size()));
        return randomClass;
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
     * Returns encrypted {@link String} used by {@link HeavyStringEncryption}.
     *
     * @param msg        the string to encrypt.
     * @param secret     the key for AES to use.
     * @param className  the class name the string is contained in.
     * @param methodName the method name the string is contained in.
     * @return encrypted {@link String} used by {@link HeavyStringEncryption}.
     */
    public static String heavyEncrypt(String msg, String secret,
                                      String className, String methodName) {
        char[] base64Chars;
        try {
            SecretKeySpec secretKey;
            byte[] key = secret.getBytes("UTF-8");
            MessageDigest sha = MessageDigest.getInstance("SHA-1");
            key = sha.digest(key);
            key = Arrays.copyOf(key, 16);
            secretKey = new SecretKeySpec(key, "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            base64Chars = Base64.getEncoder().encodeToString(cipher.
                    doFinal(msg.getBytes("UTF-8"))).toCharArray();
        } catch (Throwable t) {
            throw new IllegalStateException("Was unable to encrypt string " +
                    msg + " using " + secret);
        }
        char[] returnThis = new char[base64Chars.length];
        for (int i = 0; i < returnThis.length; i++) {
            returnThis[i] = (char) (base64Chars[i] ^ className.hashCode() ^
                    methodName.hashCode());
        }

        return new String(returnThis);
    }

    /**
     * String encryption used by {@link PrimitivesObfuscation}.
     *
     * @param msg  {@link String} representation of the primitive type.
     * @param key1 hashCode of class name the decryption method is in.
     * @param key2 hashCode of method name of the decryption method.
     * @param key3 randomized integer.
     * @return encrypted string which contains primitive type information used
     * by {@link PrimitivesObfuscation}.
     */
    public static String primitiveEncrypt(String msg, int key1, int key2, int key3) {
        char[] chars = msg.toCharArray();
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < chars.length; i++) {
            switch (i % 8) {
                case 0:
                    sb.append((char) ((chars[i] ^ (key3 ^ key1))));
                    break;
                case 1:
                    sb.append((char) (chars[i] ^ (key2 ^ key1)));
                    break;
                case 2:
                    sb.append((char) ((chars[i] ^ (key2 & key1))));
                    break;
                case 3:
                    sb.append((char) ((chars[i] ^ (key3 & key1))));
                    break;
                case 4:
                    sb.append((char) ((chars[i] ^ (key3 & key2))));
                    break;
                case 5:
                    sb.append((char) (chars[i] ^ key1));
                    break;
                case 6:
                    sb.append((char) (chars[i] ^ key2));
                    break;
                case 7:
                    sb.append((char) (chars[i] ^ (key3 ^ key2)));
                    break;
            }
        }

        return sb.toString();
    }
}
