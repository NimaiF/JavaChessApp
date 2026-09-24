import java.io.*;
import java.util.*;

public class BitboardGenerator {
    public static void main(String[] args) {
        generateWhitePawnBitboards();
        generateBlackPawnBitboards();
    }

    private static void generateJumpingBitboards(int[] offsets0x88, int[] offsets, String filename) {
        long[] bitboards = new long[64];

        for (int i = 0; i < 64; i++) {
            long bitboard = 0;
            int index = (i / 8) * 16 + (i % 8);
            for (int offset = 0; offset < offsets.length; offset++) {
                if (((index + offsets0x88[offset]) & 0x88) == 0) {
                    bitboard |= 1L << (i + offsets[offset]);
                }
            }
            bitboards[i] = bitboard;
        }
        writeToFile(bitboards, filename);
    }

    private static void generateWhitePawnBitboards() {
        int[] offsets0x88 = {15, 17};
        int[] offsets = {7, 9};
        generateJumpingBitboards(offsets0x88, offsets, "WhitePawnBitboards");
    }

    private static void generateBlackPawnBitboards() {
        int[] offsets0x88 = {-15, -17};
        int[] offsets = {-7, -9};
        generateJumpingBitboards(offsets0x88, offsets, "BlackPawnBitboards");
    }

    private static void generateKnightBitboards() {
        int[] offsets0x88 = {31, 33, 18, 14, -14, -18, -33, -31};
        int[] offsets = {15, 17, 10, 6, -6, -10, -17, -15};
        generateJumpingBitboards(offsets0x88, offsets, "KnightBitboards");
    }

    private static void generateKingBitboards() {
        int[] offsets0x88 = {16, 15, 17, 1, -1, -16, -15, -17};
        int[] offsets = {8, 7, 9, 1, -1, -8, -7, -9};
        generateJumpingBitboards(offsets0x88, offsets, "KingBitboards");
    }

    private static void writeToFile(long[] bitboards, String filename) {
        try {
            File dir = new File("./Bitboards/" + filename);
            FileOutputStream fos = new FileOutputStream(dir);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(bitboards);
            oos.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void writeToFile(int[] bitboards, String filename) {
        try {
            File dir = new File("./Bitboards/" + filename);
            FileOutputStream fos = new FileOutputStream(dir);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(bitboards);
            oos.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void writeToFile(long[][] bitboards, String filename) {
        try {
            File dir = new File("./Bitboards/" + filename);
            FileOutputStream fos = new FileOutputStream(dir);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(bitboards);
            oos.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void generateSlidingBitboards(int[] offsets0x88, int[] offsets, String lookupName, String magicName, String maskName, String bitName) {
        long[][] lookupTable = new long[64][];
        Map<Integer, Map<Long, Long>> bitboards = new HashMap<>();
        long[] masks = new long[64];
        int[] bitCounts = new int[64];

        for (int i = 0; i < 64; i++) {
            bitboards.put(i, new HashMap<Long, Long>());
            long mask = 0;
            int index = (i / 8) * 16 + (i % 8);
            for (int offset = 0; offset < 4; offset++) {
                int end0x88 = index + offsets0x88[offset];
                int end = i + offsets[offset];
                while (((end0x88 + offsets0x88[offset]) & 0x88) == 0) {
                    mask |= 1L << end;
                    end += offsets[offset];
                    end0x88 += offsets0x88[offset];
                }
            }
            masks[i] = mask;
        }

        for (int i = 0; i < 64; i++) {
            long mask = masks[i];
            int numBits = Long.bitCount(mask);
            bitCounts[i] = numBits;
            int[] squareIndices = new int[numBits];
            int index = 0;
            while (mask != 0) {
                int lsb = getLSB(mask);
                squareIndices[index] = lsb;
                index++;
                mask ^= (1L << lsb);
            }
            int num = (1 << numBits);
            for (int n = 0; n < num; n++) {
                long occupancy = 0;
                for (int j = 0; j < numBits; j++) {
                    long bit = getBitAt(n, j);
                    occupancy |= (bit << squareIndices[j]);
                }

                long attacks = 0;
                int index0x88 = (i / 8) * 16 + (i % 8);
                for (int offset = 0; offset < 4; offset++) {
                    int end0x88 = index0x88 + offsets0x88[offset];
                    int end = i + offsets[offset];
                    while (true) {
                        if ((end0x88 & 0x88) != 0) {
                            break;
                        }
                        if (((1L << end) & occupancy) != 0) {
                            attacks |= 1L << end;
                            break;
                        }
                        else {
                            attacks |= 1L << end;
                            end += offsets[offset];
                            end0x88 += offsets0x88[offset];
                        }
                    }
                }
                bitboards.get(i).put(occupancy, attacks);
            }
        }
        Random random = new Random();
        long[] magics = new long[64];
        for (int i = 0; i < 64; i++) {
            lookupTable[i] = new long[1 << bitCounts[i]];
            Map<Long, Long> attackMap = bitboards.get(i);
            boolean valid = false;
            while (!valid) {
                boolean[] used = new boolean[1 << bitCounts[i]];
                long magic = random.nextLong() & random.nextLong() & random.nextLong();
                valid = true;
                for (long occupancy : attackMap.keySet()) {
                    int index = (int) ((occupancy * magic) >>> (64 - bitCounts[i]));
                    if (used[index]) {
                        valid = false;
                        break;
                    }
                    used[index] = true;
                }
                if (valid) {
                    magics[i] = magic;
                }
            }
            for (long occupancy : attackMap.keySet()) {
                long attack = attackMap.get(occupancy);
                int index = (int) ((occupancy * magics[i]) >>> (64 - bitCounts[i]));
                lookupTable[i][index] = attack;
            }
        }
        writeToFile(lookupTable, lookupName);
        writeToFile(magics, magicName);
        writeToFile(masks, maskName);
        writeToFile(bitCounts, bitName);
    }

    private static void generateRookBitboards() {
        int[] offsets0x88 = {16, -16, 1, -1};
        int[] offsets = {8, -8, 1, -1};
        generateSlidingBitboards(offsets0x88, offsets, "RookBitboards", "RookMagics", "RookMasks","RookBits");
    }

    private static void generateBishopBitboards() {
        int[] offsets0x88 = {17, 15, -15, -17};
        int[] offsets = {9, 7, -7, -9};
        generateSlidingBitboards(offsets0x88, offsets, "BishopBitboards", "BishopMagics", "BishopMasks", "BishopBits");
    }

    public static void showBitboard(long bitboard) {
        String board = String.format("%64s", Long.toBinaryString(bitboard)).replace(" ", "0");
        for (int i = 0; i < 64; i++) {
            if ((i % 8) == 0) {
                System.out.println();
            }
            System.out.print(board.charAt(i));
        }
        System.out.println();
    }

    private static int getLSB(long bitboard) {
        return (bitboard == 0) ? (-1) : (Long.numberOfTrailingZeros(bitboard));
    }

    private static long getBitAt(int n, int index) {
        return (n >>> index) & 1;
    }
}
