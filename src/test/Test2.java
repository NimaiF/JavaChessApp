package test;
import java.io.*;
import java.util.Arrays;

public class Test2 {
    public static void main(final String[] args) {
        try {
            FileInputStream str = new FileInputStream("./Bitboards/KnightBitboards");
            ObjectInputStream ois = new ObjectInputStream(str);
            long[] arr = (long[]) ois.readObject();
            System.out.println(Arrays.toString(arr));
            System.out.println(Long.toBinaryString(arr[63]));
            ois.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}