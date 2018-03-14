package me.itzsomebody.radon.templates;

class LightStringEncryption {
    public static String decrypt(Object encrypted, int key3) {
        try {
            try {
                char[] badArray = ((String) encrypted).toCharArray();
                char raiseException = badArray[badArray.length << 256];
                char[] fakeReturn = new char[badArray.length];
                int i = 0;
                while (i < fakeReturn.length) {
                    fakeReturn[i] = (char) (badArray[i] ^ raiseException);
                    i++;
                }
            } catch (Throwable t) {
                char[] returnThis = new char[((String) encrypted).length()];
                char[] encryptedChars = ((String) encrypted).toCharArray();
                StackTraceElement[] ste = Thread.currentThread().getStackTrace();
                int i = 0;
                while (i < returnThis.length) {
                    char key1 = (char) ste[1].getClassName().hashCode();
                    char key2 = (char) ste[1].getMethodName().hashCode();
                    returnThis[i] = (char) (encryptedChars[i] ^ key1 ^ key2 ^ key3);

                    i++;
                }

                return new String(returnThis);
            }
        } catch (Throwable t) {
            throw null;
        }
        throw null;
    }
}
