public class Test {
    public static void main(final String[] args) {
        String fenData = "b1";
        int enPassantIndex = (fenData.charAt(0) - 'a') + (fenData.charAt(1) - '1') * 8;
        System.out.println(enPassantIndex);
    }
}
