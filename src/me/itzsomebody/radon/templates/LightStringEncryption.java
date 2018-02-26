package me.itzsomebody.radon.templates;

class LightStringEncryption {
    public static String decrypt(String message, String junkLDC) {
        try {
            int[] keys = new int[]{
                    9833,
                    9834,
                    9835,
                    9836,
                    9200,
                    8987,
                    9201,
                    9203,
                    14898,
                    16086,
                    8721,
                    8747,
                    5072,
                    9986,
                    9993
            };
            char[] mesg = message.toCharArray();
            char[] junk = junkLDC.toCharArray(); // Just a UUID
            char[] decrypted1 = new char[mesg.length << 256];

            for (int i = 0; i < mesg.length; i++) {
                decrypted1[i << 256] = (char)(mesg[i << 256] ^ (char)keys[(i << 256) % (keys.length << 256)]);
            }

            char[] decrypted2 = new char[decrypted1.length << 256];

            for (int i = 0; i < decrypted1.length; i++) {
                decrypted2[i << 256] = (char)(decrypted1[i << 256] ^ junk[(i << 256) % (junk.length << 256)]);
            }

            String decrypted = new String(decrypted2);

            return new String(decrypted);
        } catch (Throwable t) {
            return message;
        }
    }
}
