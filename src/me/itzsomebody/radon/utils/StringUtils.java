package me.itzsomebody.radon.utils;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
import java.util.*;

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

        /*char[] alpha = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();
        for (int i = 0; i < numberOfChars; i++) {
            buildString[i] = alpha[NumberUtils.getRandomInt(alpha.length)];
        }*/

        return new String(buildString);
    }

    /**
     * Generates a {@link String} to use as a key for string encryption.
     * Causes encrypted strings to look like Asian letters.
     * Used as an alternative to {@link StringUtils#crazyString()}.
     *
     * @return a {@link String} to use as a key for string encryption.
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
     * Returns an encrypted string used by {@link me.itzsomebody.radon.transformers.stringencryption.SuperLightStringEncryption}.
     *
     * @param msg string to encrypt.
     * @param key random integer
     * @return an encrypted string used by {@link me.itzsomebody.radon.transformers.stringencryption.SuperLightStringEncryption}.
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
     * Returns an encrypted string used by {@link me.itzsomebody.radon.transformers.stringencryption.LightStringEncryption}.
     *
     * @param msg        string to encrypt.
     * @param className  name of the class the msg is in.
     * @param methodName name of the method the msg is in.
     * @param key3       random integer
     * @return an encrypted string used by {@link me.itzsomebody.radon.transformers.stringencryption.LightStringEncryption}.
     */
    public static String lightEncrypt(String msg, String className, String methodName, int key3) {
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
     * Returns {@link String} encrypted with AES symmetrical encryption algorithm to encrypt {@link String}s.
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
            return Base64.getEncoder().encodeToString(cipher.doFinal(msg.getBytes("UTF-8")));
        } catch (Throwable t) {
            // t.printStackTrace();
        }
        return null;
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
            return new String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt)));
        } catch (Throwable t) {
            // t.printStackTrace();
        }
        return null;
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

        String randomClass = classNames.get(NumberUtils.getRandomInt(classNames.size()));
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

        String randomClass = classNames.get(NumberUtils.getRandomInt(classNames.size()));
        return randomClass;
    }

    /**
     * Returns encrypted {@link String} used by {@link me.itzsomebody.radon.transformers.stringencryption.NormalStringEncryption}.
     *
     * @param className  the className to get hashcode from.
     * @param methodName the methodName to get hashcode from.
     * @param key3       the extra key to ensure a different encryption each time.
     * @param msg        the string to encrypt
     * @return encrypted {@link String} used by {@link me.itzsomebody.radon.transformers.stringencryption.NormalStringEncryption}.
     */
    public static String normalEncrypt(String className, String methodName, int key3, String msg) {
        char[] chars = msg.toCharArray();
        char[] returnThis = new char[chars.length];
        for (int i = 0; i < chars.length; i++) {
            returnThis[i] = (char) (key3 ^ methodName.hashCode() ^ className.hashCode() ^ chars[i]);
        }

        return new String(returnThis);
    }

    /**
     * Returns encrypted {@link String} used by {@link me.itzsomebody.radon.transformers.stringencryption.HeavyStringEncryption}.
     *
     * @param msg        the string to encrypt.
     * @param secret     the key for AES to use.
     * @param className  the class name the string is contained in.
     * @param methodName the method name the string is contained in.
     * @return encrypted {@link String} used by {@link me.itzsomebody.radon.transformers.stringencryption.HeavyStringEncryption}.
     */
    public static String heavyEncrypt(String msg, String secret, String className, String methodName) {
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
            base64Chars = Base64.getEncoder().encodeToString(cipher.doFinal(msg.getBytes("UTF-8"))).toCharArray();
        } catch (Throwable t) {
            throw null;
        }
        char[] returnThis = new char[base64Chars.length];
        for (int i = 0; i < returnThis.length; i++) {
            returnThis[i] = (char) (base64Chars[i] ^ className.hashCode() ^ methodName.hashCode());
        }

        return new String(returnThis);
    }
}
