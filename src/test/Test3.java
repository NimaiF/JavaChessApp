package test;
import java.util.*;

public class Test3 {
    public static void main(String[] args) {
        Object[][] arr = new Object[2][3];
        int[] arr2 = {1, 2};
        arr[0][0] = arr2.clone();
        int[] arr3 = ((int[]) arr[0][0]).clone();
        arr3[0] = 5;
        System.out.println(Arrays.toString((int[]) arr[0][0]));
    }
}
