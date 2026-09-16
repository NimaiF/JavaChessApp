package test;
import java.io.*;

public class Test {
    public static void main(final String[] args) {
        int[] arr = {1, 2, 3};
        try {
            FileOutputStream str = new FileOutputStream("test.txt");
            ObjectOutputStream oos = new ObjectOutputStream(str);
            oos.writeObject(arr);
            oos.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        
    }
}
