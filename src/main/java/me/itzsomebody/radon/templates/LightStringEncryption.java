package me.itzsomebody.radon.templates;

class LightStringEncryption {
    public static String decrypt(Object encrypted, int key3) {
        char[] returnThis = new char[((String) encrypted).length()];
        char[] encryptedChars = ((String) encrypted).toCharArray();
        StackTraceElement[] ste = new Throwable().getStackTrace();
        int i = 0;
        while (i < returnThis.length) {
            char key1 = (char) ste[0].getClassName().hashCode();
            char key2 = (char) ste[0].getMethodName().hashCode();
            returnThis[i] = (char) (encryptedChars[i] ^ key1 ^ key2 ^ key3);

            i++;
        }

        return new String(returnThis);
    }
}
