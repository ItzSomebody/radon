package xyz.itzsomebody.radon.transformers.strings;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;

public class AESPCBCEncryptor {
    private static int[][] sbox; // lookup table for SBox
    private static int[][] mcTables; // lookup tables for MixColumns
    private static int[] rcon; // round constants
    private static int[] W; // expanded key

    static {
        initMultLookups();
        initSBox();
        initRcon();
    }

    private static void initSBox() {
        sbox = new int[0x10][0x10];
        int p = 1;
        int q = 1;

        // See https://crypto.stackexchange.com/questions/85670/need-help-understanding-math-behind-rijndael-s-box
        // for an explanation of how 3 generates GF[2^8]
        do {
            p = gfMult(0x3, p);
            q = gfMult(0xF6, q);

            int trans = q; // Affine transformation
            trans ^= ((q << 1) | (q >>> (8 - 1))) & 0xFF;
            trans ^= ((q << 2) | (q >>> (8 - 2))) & 0xFF;
            trans ^= ((q << 3) | (q >>> (8 - 3))) & 0xFF;
            trans ^= ((q << 4) | (q >>> (8 - 4))) & 0xFF;
            sbox[p / 16][p % 16] = (trans ^ 0x63) & 0xFF;
        } while (p != 1);

        sbox[0][0] = 0x63; // 0 is never invertible so set manually
    }

    // Multiply in GF[2^8]
    private static int gfMult(int mult, int b) {
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

    private static void initMultLookups() {
        mcTables = new int[0xF][];
        mcTables[0x1] = new int[0x100];
        mcTables[0x2] = new int[0x100];
        mcTables[0x3] = new int[0x100];

        for (int mult = 0; mult < mcTables.length; mult++) {
            if (mcTables[mult] != null) {
                int[] table = mcTables[mult];

                for (int n = 0; n < table.length; n++) {
                    table[n] = gfMult(mult, n);
                }
            }
        }
    }

    private static void initRcon() {
        rcon = new int[10];
        for (int i = 0; i < rcon.length; i++) {
            if (i == 0) {
                rcon[i] = 1;
            } else if (rcon[i - 1] < 0x80) {
                rcon[i] = 2 * rcon[i - 1];
            } else if (rcon[i - 1] >= 0x80) {
                rcon[i] = (2 * rcon[i - 1]) ^ 0x11B;
            } else {
                throw new RuntimeException("?");
            }
        }
        for (int i = 0; i < rcon.length; i++) {
            rcon[i] <<= 24;
        }
    }

    private static void cipher(int[][] state) {
        int round = 0;

        addRoundKey(state, round);

        for (round = 1; round < 10; round++) {
            subBytes(state);
            shiftRows(state);
            mixColumns(state);
            addRoundKey(state, round);
        }

        subBytes(state);
        shiftRows(state);
        addRoundKey(state, round);
    }

    private static int[] pad(int[] arr) {
        int bytesNeeded = 16 - arr.length % 16;
        int[] paddedArr = new int[arr.length + bytesNeeded];
        System.arraycopy(arr, 0, paddedArr, 0, arr.length);
        for (int i = 0; i < bytesNeeded; i++) {
            paddedArr[arr.length + i] = bytesNeeded;
        }
        return paddedArr;
    }

    private static int[] design(byte[] arr) {
        int[] designed = new int[arr.length];
        for (int i = 0; i < arr.length; i++) {
            designed[i] = arr[i] & 0xFF;
        }
        return designed;
    }

    private static byte[] sign(int[] arr) {
        byte[] signed = new byte[arr.length];
        for (int i = 0; i < arr.length; i++) {
            signed[i] = (byte) arr[i];
        }
        return signed;
    }

    public static String encrypt(String input, int[] key, long[] ivInts) {
        // Key stuff
        keySchedule(key);

        // Convert IV to correct format
        int[] ivBytes = new int[0x10];
        for (int i = 0; i < ivInts.length; i++) {
            for (int j = 0; j < 8; j++) {
                ivBytes[i * 8 + j] = (int) ((ivInts[i] >>> (56 - j * 8)) & 0xFF);
            }
        }
        int[][] iv = create_state(ivBytes);

        // Pad and create state block
        int[] paddedArr = pad(design(input.getBytes(StandardCharsets.UTF_8)));
        int[][][] states = new int[paddedArr.length / 16][][];
        for (int i = 0; i < states.length; i++) {
            int[] subarray = Arrays.copyOfRange(paddedArr, i * 16, (i + 1) * 16);
            states[i] = create_state(subarray);
        }

        // PCBC encryption algorithm
        int[][] blockCopy = copyBlock(states[0]); // plaintext here
        for (int row = 0; row < states[0].length; row++) {
            for (int col = 0; col < states[0][0].length; col++) {
                states[0][row][col] ^= iv[row][col];
            }
        }
        cipher(states[0]);
        for (int row = 0; row < states[0].length; row++) {
            for (int col = 0; col < states[0][0].length; col++) {
                blockCopy[row][col] ^= states[0][row][col]; // states[0] is ciphered here
            }
        }

        for (int i = 1; i < states.length; i++) {
            int[][] blockCopy2 = copyBlock(states[i]);
            int[][] state = states[i];
            for (int row = 0; row < state.length; row++) {
                for (int col = 0; col < state[0].length; col++) {
                    state[row][col] ^= blockCopy[row][col];
                }
            }
            cipher(states[i]);
            for (int row = 0; row < state.length; row++) {
                for (int col = 0; col < state[0].length; col++) {
                    blockCopy2[row][col] ^= state[row][col]; // state is ciphered here
                }
            }
            blockCopy = blockCopy2;
        }

        // Convert state to single-dimension array
        byte[] processed = new byte[paddedArr.length];
        for (int i = 0; i < states.length; i++) {
            int[] vector = new int[0x10];
            for (int col = 0; col < states[0].length; col++) {
                for (int row = 0; row < states[0][0].length; row++) {
                    vector[col * 4 + row] = states[i][row][col];
                }
            }
            System.arraycopy(sign(vector), 0, processed, i * 16, vector.length);
        }

        // Encode with B64 (prevents String UTF8 encoding/decoding messing the bytes up)
        // You could also just encode the bytes into a char if you want weird unprintable characters
        return Base64.getEncoder().encodeToString(processed);
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

    public static int[][] create_state(int[] bytes) {
        int[][] state = new int[4][4];
        for (int i = 0; i < bytes.length; i++) {
            state[i % 4][i / 4] = bytes[i];
        }
        return state;
    }

    public static void subBytes(int[][] state) {
        for (int row = 0; row < state.length; row++) {
            for (int col = 0; col < state[row].length; col++) {
                int s = state[row][col];
                int upper = s >> 4;
                int lower = s & 0xF;
                state[row][col] = sbox[upper][lower];
            }
        }
    }

    public static void shiftRows(int[][] state) {
        for (int offset = 0; offset < state.length; offset++) {
            int[] row = state[offset];
            int[] shifted = new int[row.length];
            for (int i = 0; i < row.length; i++) {
                shifted[i] = row[(i + offset) % 4];
            }
            state[offset] = shifted;
        }
    }

    public static void mixColumns(int[][] state) {
        for (int col = 0; col < state[0].length; col++) {
            int s1 = mcTables[0x2][state[0][col]] ^ mcTables[0x3][state[1][col]] ^ mcTables[0x1][state[2][col]] ^ mcTables[0x1][state[3][col]];
            int s2 = mcTables[0x1][state[0][col]] ^ mcTables[0x2][state[1][col]] ^ mcTables[0x3][state[2][col]] ^ mcTables[0x1][state[3][col]];
            int s3 = mcTables[0x1][state[0][col]] ^ mcTables[0x1][state[1][col]] ^ mcTables[0x2][state[2][col]] ^ mcTables[0x3][state[3][col]];
            int s4 = mcTables[0x3][state[0][col]] ^ mcTables[0x1][state[1][col]] ^ mcTables[0x1][state[2][col]] ^ mcTables[0x2][state[3][col]];

            state[0][col] = s1;
            state[1][col] = s2;
            state[2][col] = s3;
            state[3][col] = s4;
        }
    }

    public static int subWord(int word) {
        int b0 = (word >> (8 * 3)) & 0xFF;
        int b1 = (word >> (8 * 2)) & 0xFF;
        int b2 = (word >> 8) & 0xFF;
        int b3 = word & 0xFF;

        return (sbox[b0 >> 4][b0 & 0xF] << 8*3)
                | (sbox[b1 >> 4][b1 & 0xF] << 8*2)
                | (sbox[b2 >> 4][b2 & 0xF] << 8)
                | (sbox[b3 >> 4][b3 & 0xF]);
    }

    public static void keySchedule(int[] key) {
        // AES-128 has 11 round keys
        int R = 11;
        W = new int[4*R];

        for (int i = 0; i < W.length; i++) {
            if (i < key.length) {
                W[i] = key[i];
            } else if (i % key.length == 0) {
                int b = (W[i - 1] << 8) | (W[i - 1] >>> (32 - 8));
                W[i] = W[i - key.length];
                W[i] ^= subWord(b);
                W[i] ^= rcon[i / key.length - 1];
            } else {
                W[i] = W[i - key.length] ^ W[i - 1];
            }
        }
    }

    public static void addRoundKey(int[][] state, int round) {
        for (int row = 0; row < state.length; row++) {
            for (int col = 0; col < state[0].length; col++) {
                int rkey = W[round*state[0].length + col];
                int xbyte = (rkey >> (8*(state.length - 1) - 8*row)) & 0xFF;
                state[row][col] ^= xbyte;
            }
        }
    }
}
