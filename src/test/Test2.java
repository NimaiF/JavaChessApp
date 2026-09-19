package test;
import java.io.*;
import java.util.Arrays;

public class Test2 {
    public static void main(final String[] args) {
        try {
            InputStream str = Test2.class.getResourceAsStream("../Bitboards/WhitePawnBitboards");
            ObjectInputStream ois = new ObjectInputStream(str);
            long[] arr = (long[]) ois.readObject();
            showBitboard(arr[9]);
            ois.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void showBitboard(long bitboard) {
        String board = String.format("%64s", Long.toBinaryString(bitboard)).replace(" ", "0");
        for (int i = 0; i < 64; i++) {
            if ((i % 8) == 0) {
                System.out.println();
            }
            System.out.print(board.charAt(i));
        }
        System.out.println();
    }
}