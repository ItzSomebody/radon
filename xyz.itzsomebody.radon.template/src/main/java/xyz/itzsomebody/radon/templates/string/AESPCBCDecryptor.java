package xyz.itzsomebody.radon.templates.string;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;

public class AESPCBCDecryptor {
    public static int[][] sbox;
    public static int[][] invSbox;
    public static int[] rcon; // round constants
    public static int[] expandedKey; // expanded key
    public static int[][] mcTables; // lookup tables for MixColumns

    static {
        // SBox
        // See https://crypto.stackexchange.com/questions/85670/need-help-understanding-math-behind-rijndael-s-box
        // for an explanation of how 3 generates GF[2^8]
        sbox = new int[0x10][0x10];
        int p = 1;
        int q = 1;

        do {
            p = gfMult(3, p);
            q = gfMult(0xF6, q);

            int trans = q; // Affine transformation
            trans ^= ((q << 1) | (q >>> (8 - 1))) & 0xFF;
            trans ^= ((q << 2) | (q >>> (8 - 2))) & 0xFF;
            trans ^= ((q << 3) | (q >>> (8 - 3))) & 0xFF;
            trans ^= ((q << 4) | (q >>> (8 - 4))) & 0xFF;
            sbox[p / 16][p % 16] = (trans ^ 0x63) & 0xFF;
        } while (p != 1);

        sbox[0][0] = 0x63; // 0 is never invertible so set manually

        // InvSBox
        invSbox = new int[sbox.length][sbox.length];
        for (int row = 0; row < sbox.length; row++) {
            for (int col = 0; col < sbox[0].length; col++) {
                int b = sbox[row][col];
                invSbox[b >> 4][b & 0xF] = (row << 4) | col;
            }
        }

        // Round constants
        rcon = new int[10];
        for (int i = 0; i < rcon.length; i++) {
            if (i == 0) {
                rcon[i] = 1;
            } else if (rcon[i - 1] < 0x80) {
                rcon[i] = 2 * rcon[i - 1];
            } else if (rcon[i - 1] >= 0x80) {
                rcon[i] = (2 * rcon[i - 1]) ^ 0x11B;
            }
        }
        for (int i = 0; i < rcon.length; i++) {
            rcon[i] <<= 24;
        }

        // Multiplication tables for MixColumns
        mcTables = new int[0xF][];
        mcTables[0xE] = new int[0x100];
        mcTables[0xB] = new int[0x100];
        mcTables[0xD] = new int[0x100];
        mcTables[0x9] = new int[0x100];

        for (int mult = 0; mult < mcTables.length; mult++) {
            if (mcTables[mult] != null) {
                int[] table = mcTables[mult];

                for (int n = 0; n < table.length; n++) {
                    table[n] = gfMult(mult, n);
                }
            }
        }
    }

    public static int subWord(int word) {
        int b0 = (word >> (8 * 3)) & 0xFF;
        int b1 = (word >> (8 * 2)) & 0xFF;
        int b2 = (word >> 8) & 0xFF;
        int b3 = word & 0xFF;

        return (sbox[b0 >> 4][b0 & 0xF] << 8 * 3)
                | (sbox[b1 >> 4][b1 & 0xF] << 8 * 2)
                | (sbox[b2 >> 4][b2 & 0xF] << 8)
                | (sbox[b3 >> 4][b3 & 0xF]);
    }

    public static void keySchedule(int[] key) {
        // AES-128
        int R = 11;
        expandedKey = new int[4 * R];

        for (int i = 0; i < expandedKey.length; i++) {
            if (i < key.length) {
                expandedKey[i] = key[i];
            } else if (i % key.length == 0) {
                int b = (expandedKey[i - 1] << 8) | (expandedKey[i - 1] >>> (32 - 8));
                expandedKey[i] = expandedKey[i - key.length];
                expandedKey[i] ^= subWord(b);
                expandedKey[i] ^= rcon[i / key.length - 1];
            } else {
                expandedKey[i] = expandedKey[i - key.length] ^ expandedKey[i - 1];
            }
        }
    }

    public static void addRoundKey(int[][] state, int round) {
        for (int row = 0; row < state.length; row++) {
            for (int col = 0; col < state[0].length; col++) {
                int rkey = expandedKey[round * state[0].length + col];
                int xbyte = (rkey >> (8 * (state.length - 1) - 8 * row)) & 0xFF;
                state[row][col] ^= xbyte;
            }
        }
    }

    public static void invShiftRows(int[][] state) {
        for (int offset = 0; offset < state.length; offset++) {
            int[] row = state[offset];
            int[] shifted = new int[row.length];
            for (int i = 0; i < row.length; i++) {
                shifted[(i + offset) % 4] = row[i];
            }
            state[offset] = shifted;
        }
    }

    public static void invSubBytes(int[][] state) {
        for (int row = 0; row < state.length; row++) {
            for (int col = 0; col < state[row].length; col++) {
                int s = state[row][col];
                int upper = s >> 4;
                int lower = s & 0xF;
                state[row][col] = invSbox[upper][lower];
            }
        }
    }

    // Multiply in GF[2^8]
    public static int gfMult(int mult, int b) {
        int result = 0;
        for (int i = 0; i < 8; i++) {
            if ((mult & 0x1) != 0) {
                result ^= b;
            }
            mult >>= 1;
            int mod = 0;
            if ((b & 0x80) != 0) {
                mod = 0x11B;
            }
            b = (b << 1) ^ mod;
        }
        return result;
    }

    public static void invMixColumns(int[][] state) {
        for (int col = 0; col < state[0].length; col++) {
            int s1 = mcTables[0xE][state[0][col]] ^ mcTables[0xB][state[1][col]] ^ mcTables[0xD][state[2][col]] ^ mcTables[0x9][state[3][col]];
            int s2 = mcTables[0x9][state[0][col]] ^ mcTables[0xE][state[1][col]] ^ mcTables[0xB][state[2][col]] ^ mcTables[0xD][state[3][col]];
            int s3 = mcTables[0xD][state[0][col]] ^ mcTables[0x9][state[1][col]] ^ mcTables[0xE][state[2][col]] ^ mcTables[0xB][state[3][col]];
            int s4 = mcTables[0xB][state[0][col]] ^ mcTables[0xD][state[1][col]] ^ mcTables[0x9][state[2][col]] ^ mcTables[0xE][state[3][col]];

            state[0][col] = s1;
            state[1][col] = s2;
            state[2][col] = s3;
            state[3][col] = s4;
        }
    }

    public static void invCipher(int[][] state) {
        int round = 10;

        addRoundKey(state, round);

        for (round = 9; round > 0; round--) {
            invShiftRows(state);
            invSubBytes(state);
            addRoundKey(state, round);
            invMixColumns(state);
        }

        invSubBytes(state);
        invShiftRows(state);
        addRoundKey(state, round);
    }

    public static String decrypt(String input, long[] ivInts) {
        // Compute key based on callstack (yes we're doing this meme again)
        var callstack = Thread.currentThread().getStackTrace();
        int[] key = new int[]{
                callstack[1].getClassName().hashCode(),
                callstack[1].getMethodName().hashCode(),
                callstack[2].getClassName().hashCode(),
                callstack[2].getMethodName().hashCode()
        };
        keySchedule(key);

        // Convert IV to correct format
        int[] ivBytes = new int[0x10];
        for (int i = 0; i < ivInts.length; i++) {
            for (int j = 0; j < 8; j++) {
                ivBytes[i * 8 + j] = (int) ((ivInts[i] >>> (56 - j * 8)) & 0xFF);
            }
        }
        int[][] iv = create_state(ivBytes);

        // Input -> state for InvCipher
        int[] cipherText = design(Base64.getDecoder().decode(input));
        int[][][] states = new int[cipherText.length / 16][][];
        for (int i = 0; i < states.length; i++) {
            int[] subarray = Arrays.copyOfRange(cipherText, i * 16, (i + 1) * 16);
            states[i] = create_state(subarray);
        }

        // PCBC decryption alg
        int[][] previousBlock1 = copyBlock(states[0]);
        invCipher(states[0]);
        for (int row = 0; row < states[0].length; row++) {
            for (int col = 0; col < states[0][0].length; col++) {
                states[0][row][col] ^= iv[row][col];
                previousBlock1[row][col] ^= states[0][row][col];
            }
        }

        for (int i = 1; i < states.length; i++) {
            int[][] previousBlock2 = copyBlock(states[i]);
            invCipher(states[i]);
            int[][] state = states[i];
            for (int row = 0; row < state.length; row++) {
                for (int col = 0; col < state[0].length; col++) {
                    state[row][col] ^= previousBlock1[row][col];
                    previousBlock2[row][col] ^= state[row][col];
                }
            }
            previousBlock1 = previousBlock2;
        }

        // Reconstruct bytes
        byte[] processed = new byte[cipherText.length];
        for (int i = 0; i < states.length; i++) {
            int[] vector = new int[0x10];
            for (int col = 0; col < states[0].length; col++) {
                for (int row = 0; row < states[0][0].length; row++) {
                    vector[col * 4 + row] = states[i][row][col];
                }
            }
            System.arraycopy(resign(vector), 0, processed, i * 16, vector.length);
        }

        // Remove padding
        int nPadBytes = processed[processed.length - 1];
        byte[] message = new byte[processed.length - nPadBytes];
        System.arraycopy(processed, 0, message, 0, message.length);

        return new String(message, StandardCharsets.UTF_8);
    }

    public static int[][] copyBlock(int[][] block) {
        int[][] copy = new int[block.length][block[0].length];
        for (int row = 0; row < block.length; row++) {
            for (int col = 0; col < block[0].length; col++) {
                copy[row][col] = block[row][col];
            }
        }
        return copy;
    }

    public static int[] design(byte[] arr) {
        int[] designed = new int[arr.length];
        for (int i = 0; i < arr.length; i++) {
            designed[i] = arr[i] & 0xFF;
        }
        return designed;
    }

    public static byte[] resign(int[] arr) {
        byte[] signed = new byte[arr.length];
        for (int i = 0; i < arr.length; i++) {
            signed[i] = (byte) arr[i];
        }
        return signed;
    }

    public static int[][] create_state(int[] bytes) {
        int[][] state = new int[4][4];
        for (int i = 0; i < bytes.length; i++) {
            state[i % 4][i / 4] = bytes[i];
        }
        return state;
    }
}
