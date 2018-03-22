package me.itzsomebody.radon.templates;

class SuperLightStringEncryption {
    public static String decrypt(String encrypted, int key) {
        char[] encryptedArray = encrypted.toCharArray();
        char[] returnThis = new char[encryptedArray.length];

        for (int i = 0; i < returnThis.length; i++) {
            returnThis[i] = (char) (encryptedArray[i] ^ key);
        }

        return new String(returnThis);
    }
}
