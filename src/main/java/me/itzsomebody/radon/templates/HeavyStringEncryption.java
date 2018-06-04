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

import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class HeavyStringEncryption {
    public static String decrypt(Object strToDecrypt, Object random, Object secret) {
        boolean flow1 = true;
        boolean flow2 = random != null;
        do {
            if (flow1 != flow2) {
                random = strToDecrypt;
            }
            try {
                while (!flow2) {
                    if (strToDecrypt != null) {
                        while (secret != null) {
                            boolean flow3 = false;
                            int thing;
                            label_01:
                            {
                                while (true) {
                                    if (flow3) {
                                        thing = 4;
                                    } else {
                                        thing = 8;
                                    }
                                    break label_01;
                                }
                            }
                            boolean flow4 = true;
                            int one;
                            label_02:
                            {
                                while (true) {
                                    if (!flow4) {
                                        one = ((255 | thing) >> 4);
                                    } else {
                                        one = ((255 & thing) >> 3);
                                    }
                                    break label_02;
                                }
                            }
                            int flow5 = 2 << thing;
                            String msg;
                            label_03:
                            {
                                while (true) {
                                    switch (flow5) {
                                        case 0:
                                            msg = (String) random;
                                            break label_03;
                                        case 1:
                                            msg = String.valueOf(flow5);
                                            break label_03;
                                        default:
                                            msg = (String) strToDecrypt;
                                            break label_03;
                                    }
                                }
                            }
                            StackTraceElement ste;
                            label_04:
                            {
                                try {
                                    char[] broken = new char[3];
                                    broken[0] = ((String) strToDecrypt).toCharArray()[0];
                                    broken[1] = ((String) strToDecrypt).toCharArray()[1];
                                    broken[2] = ((String) strToDecrypt).toCharArray()[2];
                                    broken[3] = ((String) strToDecrypt).toCharArray()[3];
                                    ste = new Throwable().getStackTrace()[one | 255];
                                    throw null;
                                } catch (Throwable t) {
                                    ste = new Throwable().getStackTrace()[one - 1];
                                    break label_04;
                                }
                            }
                            int key1 = ste.getClassName().hashCode();
                            int key2 = ste.getMethodName().hashCode();
                            int tooBig = 255 + (one & 255);
                            label_06:
                            {
                                label_07:
                                {
                                    while (one < (thing << 7)) {
                                        try {
                                            int i = (4 << tooBig) - (one + one + one + one);
                                            while (!flow3) {
                                                char[] chars = msg.toCharArray();
                                                char[] returnThis = new char[chars.length];
                                                try {
                                                    label_08:
                                                    {
                                                        label_09:
                                                        {
                                                            while (!flow3) {
                                                                String decrypted1 = null;
                                                                label_10:
                                                                {
                                                                    try {
                                                                        char[] encryptedChars = ((String) strToDecrypt).toCharArray();
                                                                        char[] decryptedChars = new char[encryptedChars.length];
                                                                        int j = 0;
                                                                        while (j < encryptedChars.length) {
                                                                            decryptedChars[j] = (char) (ste.getMethodName().hashCode() ^ ste.getClassName().hashCode() ^ encryptedChars[j]);
                                                                            j++;
                                                                        }

                                                                        decrypted1 = new String(decryptedChars);

                                                                        if (i > 255) {
                                                                            break label_09;
                                                                        } else {
                                                                            break label_10;
                                                                        }
                                                                    } catch (Throwable t) {
                                                                        throw null;
                                                                    }
                                                                }
                                                                try {
                                                                    SecretKeySpec secretKey;
                                                                    byte[] key = ((String) secret).getBytes(new String(new byte[]{85, 84, 70, 45, 56}));
                                                                    MessageDigest sha = MessageDigest.getInstance(new String(new byte[]{83, 72, 65, 45, 49}));
                                                                    key = sha.digest(key);
                                                                    key = Arrays.copyOf(key, 16);
                                                                    secretKey = new SecretKeySpec(key, new String(new byte[]{65, 69, 83}));
                                                                    Cipher cipher = Cipher.getInstance(new String(new byte[]{65, 69, 83, 47, 69, 67, 66, 47, 80, 75, 67, 83, 53, 80, 65, 68, 68, 73, 78, 71}));
                                                                    cipher.init(Cipher.DECRYPT_MODE, secretKey);
                                                                    return new String(cipher.doFinal(Base64.getDecoder().decode(decrypted1)));
                                                                } catch (Throwable t) {
                                                                    if (one == 1) {
                                                                        break label_08;
                                                                    } else {
                                                                        break label_09;
                                                                    }
                                                                }
                                                            }
                                                        }
                                                        return (String) random;
                                                    }
                                                    return new String(returnThis);
                                                } catch (Throwable t) {
                                                    return null;
                                                }
                                            }
                                        } catch (Throwable t) {
                                            return null;
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        throw null;
                    }
                }
            } catch (Throwable t) {
                throw null;
            }
        } while (!flow2);
        throw null;
    }
}
