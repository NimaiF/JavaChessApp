public class Game {
    public static final int whitePawn = 0;
    public static final int whiteKnight = 1;
    public static final int whiteBishop = 2;
    public static final int whiteRook = 3;
    public static final int whiteQueen = 4;
    public static final int whiteKing = 5;
    public static final int blackPawn = 6;
    public static final int blackKnight = 7;
    public static final int blackBishop = 8;
    public static final int blackRook = 9;
    public static final int blackQueen = 10;
    public static final int blackKing = 11;
    public static final int empty = 12;
    
    public boolean whiteToMove;
    public byte castlingRights = 0b0000;
    public int enPassantIndex;
    public int halfMoves;
    public int fullMoves;
    public final int[] board = new int[64];

    Game(String FEN) {
        String[] fenData = FEN.split(" ");
        if (fenData[1] == "w") {
            this.whiteToMove = true;
        } else {
            this.whiteToMove = false;
        }

        if (fenData[2].contains("K")) {
            this.castlingRights |= 0b1000;
        }
        if (fenData[2].contains("Q")) {
            this.castlingRights |= 0b0100;
        }
        if (fenData[2].contains("k")) {
            this.castlingRights |= 0b0010;
        }
        if (fenData[2].contains("q")) {
            this.castlingRights |= 0b0001;
        }

        if (fenData[3] != "-") {
            this.enPassantIndex = (fenData[3].charAt(0) - 'a') + (fenData[3].charAt(1) - '1') * 8;
        }

        this.halfMoves = Integer.parseInt(fenData[4]);
        this.fullMoves = Integer.parseInt(fenData[5]);

        String[] boardFEN = fenData[0].split("/");
        int row = 0;
        int col = 0;
        for (String fenRow : boardFEN) {
            
        }
    }
}
