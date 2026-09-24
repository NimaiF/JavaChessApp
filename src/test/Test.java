package test;
import java.io.*;
import java.util.Arrays;

public class Test {
    public static void main(final String[] args) {
        int[][] arr = new int[2][2];
        int[] subarr = arr[0];
        subarr[0] = 123;
        System.out.println(arr[0][0]);
    }
}
