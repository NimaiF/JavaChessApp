import java.io.*;

public class BitboardGenerator {
    public static void main(String[] args) {
        generateKnightBitboards();
    }

    private static void generateKnightBitboards() {
        long[] bitboards = new long[64];
        int[] offsets0x88 = {31, 33, 18, 14, -14, -18, -33, -31};
        int[] offsets = {15, 17, 10, 6, -6, -10, -17, -15};
        for (int i = 0; i < 64; i++) {
            long bitboard = 0;
            int index = (i / 8) * 16 + (i % 8);
            for (int offset = 0; offset < 8; offset++) {
                if (((index + offsets0x88[offset]) & 0x88) == 0) {
                    bitboard |= 1L << (i + offsets[offset]);
                }
            }
            bitboards[i] = bitboard;
        }
        try {
            File dir = new File("./Bitboards/KnightBitboards");
            FileOutputStream fos = new FileOutputStream(dir);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(bitboards);
            oos.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}