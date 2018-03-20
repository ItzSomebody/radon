package me.itzsomebody.radon.templates;

class NormalStringEncryption {
    public static String decrypt(Object encryptedString, Object useless, int key3) {
        boolean flow01 = true;
        boolean flow02 = useless != null;
        do {
            if (flow01 == flow02) {
                encryptedString = useless;
            }
            try {
                while (!flow02) {
                    if (encryptedString != null) {
                        while (!flow02) {
                            try {
                                while (!flow02) {
                                    if (useless == null) {
                                        try {
                                            while (!flow02) {
                                                if (key3 != 0) {
                                                    while (!flow02) {
                                                        boolean flow1 = false;
                                                        int thing;
                                                        label_01:
                                                        {
                                                            while (true) {
                                                                if (flow1) {
                                                                    thing = 4;
                                                                } else {
                                                                    thing = 8;
                                                                }
                                                                break label_01;
                                                            }
                                                        }
                                                        boolean flow2 = true;
                                                        int one;
                                                        label_02:
                                                        {
                                                            while (true) {
                                                                if (!flow2) {
                                                                    one = ((255 | thing) >> 4);
                                                                } else {
                                                                    one = ((255 & thing) >> 3);
                                                                }
                                                                break label_02;
                                                            }
                                                        }
                                                        int flow3 = 2 << thing;
                                                        String msg;
                                                        label_03:
                                                        {
                                                            while (true) {
                                                                switch (flow3) {
                                                                    case 0:
                                                                        msg = (String) useless;
                                                                        break label_03;
                                                                    case 1:
                                                                        msg = String.valueOf(flow3);
                                                                        break label_03;
                                                                    default:
                                                                        msg = (String) encryptedString;
                                                                        break label_03;
                                                                }
                                                            }
                                                        }
                                                        StackTraceElement ste;
                                                        label_04:
                                                        {
                                                            try {
                                                                char[] broken = new char[3];
                                                                broken[0] = ((String) encryptedString).toCharArray()[0];
                                                                broken[1] = ((String) encryptedString).toCharArray()[1];
                                                                broken[2] = ((String) encryptedString).toCharArray()[2];
                                                                broken[3] = ((String) encryptedString).toCharArray()[3];
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
                                                                        while (!flow02) {
                                                                            char[] chars = msg.toCharArray();
                                                                            char[] returnThis = new char[chars.length];
                                                                            try {
                                                                                label_08:
                                                                                {
                                                                                    label_09:
                                                                                    {
                                                                                        while (!flow02) {
                                                                                            label_10:
                                                                                            {
                                                                                                while (!flow02) {
                                                                                                    if (i < msg.toCharArray().length) {
                                                                                                        returnThis[i] = (char) (chars[i] ^ key1 ^ key2 ^ (int) key3);
                                                                                                        i++;
                                                                                                    } else {
                                                                                                        break label_10;
                                                                                                    }
                                                                                                }
                                                                                            }
                                                                                            if (one == one) {
                                                                                                break label_08;
                                                                                            } else {
                                                                                                break label_09;
                                                                                            }
                                                                                        }
                                                                                    }
                                                                                    return (String) useless;
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
                                                        return null;
                                                    }
                                                } else {
                                                    throw null;
                                                }
                                            }
                                        } catch (Throwable t) {
                                            throw t;
                                        }
                                    } else {
                                        throw null;
                                    }
                                }
                            } catch (Throwable t) {
                                throw t;
                            }
                        }
                    } else {
                        throw null;
                    }
                }
            } catch (Throwable t) {
                throw t;
            }
        } while (!flow02);
        throw null;
    }
}
