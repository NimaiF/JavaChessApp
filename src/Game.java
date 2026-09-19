import java.io.*;

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

    public static final int quietMove = 0;
    public static final int doublePawnPush = 1;
    public static final int kingCastle = 2;
    public static final int queenCastle = 3;
    public static final int capture = 4;
    public static final int enPassant = 5;
    public static final int knightPromotion = 8;
    public static final int bishopPromotion = 9;
    public static final int rookPromotion = 10;
    public static final int queenPromotion = 11;
    public static final int knightPromotionCapture = 12;
    public static final int bishopPromotionCapture = 13;
    public static final int rookPromotionCapture = 14;
    public static final int queenPromotionCapture = 15;

    public static final int whiteKingsideRook = 7;
    public static final int whiteQueensideRook = 0;
    public static final int blackKingsideRook = 63;
    public static final int blackQueensideRook = 56;

    public static long[] whitePawnBitboards;
    public static long[] blackPawnBitboards;
    public static long[] knightBitboards;
    public static long[][] bishopBitboards;
    public static long[][] rookBitboards;
    public static long[] kingBitboards;
    public static long[] bishopMasks;
    public static long[] rookMasks;
    public static long[] bishopMagics;
    public static long[] rookMagics;
    public static int[] bishopBits;
    public static int[] rookBits;
    
    // -------------------------------

    public boolean whiteToMove;
    public byte castlingRights = 0b0000;
    public int enPassantIndex;
    public int halfMoves;
    public int fullMoves;
    public final int[] board = new int[64];
    public final long[] bitboards = new long[12];

    static {
        try {
            InputStream str = Game.class.getResourceAsStream("/Bitboards/KnightBitboards");
            ObjectInputStream ois = new ObjectInputStream(str);
            knightBitboards = (long[]) ois.readObject();
            ois.close();

            str = Game.class.getResourceAsStream("/Bitboards/WhitePawnBitboards");
            ois = new ObjectInputStream(str);
            whitePawnBitboards = (long[]) ois.readObject();
            ois.close();

            str = Game.class.getResourceAsStream("/Bitboards/BlackPawnBitboards");
            ois = new ObjectInputStream(str);
            blackPawnBitboards = (long[]) ois.readObject();
            ois.close();

            str = Game.class.getResourceAsStream("/Bitboards/BishopBitboards");
            ois = new ObjectInputStream(str);
            bishopBitboards = (long[][]) ois.readObject();
            ois.close();

            str = Game.class.getResourceAsStream("/Bitboards/RookBitboards");
            ois = new ObjectInputStream(str);
            rookBitboards = (long[][]) ois.readObject();
            ois.close();

            str = Game.class.getResourceAsStream("/Bitboards/KingBitboards");
            ois = new ObjectInputStream(str);
            kingBitboards = (long[]) ois.readObject();
            ois.close();

            str = Game.class.getResourceAsStream("/Bitboards/BishopMasks");
            ois = new ObjectInputStream(str);
            bishopMasks = (long[]) ois.readObject();
            ois.close();

            str = Game.class.getResourceAsStream("/Bitboards/RookMasks");
            ois = new ObjectInputStream(str);
            rookMasks = (long[]) ois.readObject();
            ois.close();

            str = Game.class.getResourceAsStream("/Bitboards/BishopMagics");
            ois = new ObjectInputStream(str);
            bishopMagics = (long[]) ois.readObject();
            ois.close();

            str = Game.class.getResourceAsStream("/Bitboards/RookMagics");
            ois = new ObjectInputStream(str);
            rookMagics = (long[]) ois.readObject();
            ois.close();

            str = Game.class.getResourceAsStream("/Bitboards/BishopBits");
            ois = new ObjectInputStream(str);
            bishopBits = (int[]) ois.readObject();
            ois.close();

            str = Game.class.getResourceAsStream("/Bitboards/RookBits");
            ois = new ObjectInputStream(str);
            rookBits = (int[]) ois.readObject();
            ois.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    Game(String FEN) {
        String[] fenData = FEN.split(" ");
        if (fenData[1].equals("w")) {
            whiteToMove = true;
        } else {
            whiteToMove = false;
        }

        if (fenData[2].contains("K")) {
            castlingRights |= 0b1000;
        }
        if (fenData[2].contains("Q")) {
            castlingRights |= 0b0100;
        }
        if (fenData[2].contains("k")) {
            castlingRights |= 0b0010;
        }
        if (fenData[2].contains("q")) {
            castlingRights |= 0b0001;
        }

        if (!fenData[3].equals("-")) {
            enPassantIndex = (fenData[3].charAt(0) - 'a') + (fenData[3].charAt(1) - '1') * 8;
        }
        else {
            enPassantIndex = -1;
        }

        this.halfMoves = Integer.parseInt(fenData[4]);
        this.fullMoves = Integer.parseInt(fenData[5]);

        String[] boardFEN = fenData[0].split("/");
        int index = 56;
        for (String fenRow : boardFEN) {
            for (String fenCol : fenRow.split("")) {
                if (fenCol.matches("[1-8]")) {
                    int end = index + Integer.parseInt(fenCol);
                    while (index < end) {
                        board[index] = empty;
                        index++;
                    }
                }
                else {
                    switch (fenCol) {
                        case "P":
                            board[index] = whitePawn;
                            break;
                        case "N":
                            board[index] = whiteKnight;
                            break;
                        case "B":
                            board[index] = whiteBishop;
                            break;
                        case "R":
                            board[index] = whiteRook;
                            break;
                        case "Q":
                            board[index] = whiteQueen;
                            break;
                        case "K":
                            board[index] = whiteKing;
                            break;
                        case "p":
                            board[index] = blackPawn;
                            break;
                        case "n":
                            board[index] = blackKnight;
                            break;
                        case "b":
                            board[index] = blackBishop;
                            break;
                        case "r":
                            board[index] = blackRook;
                            break;
                        case "q":
                            board[index] = blackQueen;
                            break;
                        case "k":
                            board[index] = blackKing;
                            break;
                    }
                    index++;
                }
            }
            index -= 16;
        }

        for (int i = 0; i < 64; i++) {
            int square = board[i];
            if (square != Game.empty) {
                bitboards[square] |= 1L << i;
            }
        }
    }

    public static int encodeMove(int from, int to, int type) {
        return (from << 10) | (to << 4) | type;
    }

    public static int[] decodeMove(int move) {
        int[] moveArray = {move >>> 10, move >>> 4 & 0b111111, move & 0b1111};
        return moveArray;
    }

    public void makeMove(int move) {
        halfMoves += 1;
        int[] decodedMove = decodeMove(move);
        int from = decodedMove[0];
        int to = decodedMove[1];
        int type = decodedMove[2];

        int moved = board[from];
        int captured = board[to];

        board[from] = empty;
        bitboards[moved] ^= 1L << from;

        if (whiteToMove) {
            if (moved == whiteKing) {
                castlingRights &= 0b0011;

                if (type == kingCastle) {
                    board[7] = empty;
                    board[5] = whiteRook;
                    bitboards[whiteRook] ^= (1L << 7) | (1L << 5);
                }
                else if (type == queenCastle) {
                    board[0] = empty;
                    board[2] = whiteRook;
                    bitboards[whiteRook] ^= (1L << 0) | (1L << 2);
                }
            }
            else if (moved == whiteRook) {
                if (from == whiteKingsideRook) {
                    castlingRights &= 0b0111;
                }
                else if (from == whiteQueensideRook) {
                    castlingRights &= 0b1011;
                }
            }
            else if (type == doublePawnPush) {
                enPassantIndex = to - 8;
            }

            if (captured == blackRook) {
                if (to == blackKingsideRook) {
                    castlingRights &= 0b1101;
                }
                else if (to == blackQueensideRook) {
                    castlingRights &= 0b1110;
                }
            }

            if ((type & 0b1000) == 1) {
                switch (type & 0b0011) {
                    case 0b00:
                        board[to] = whiteKnight;
                        bitboards[whiteKnight] |= 1L << to;
                        break;
                    case 0b01:
                        board[to] = whiteBishop;
                        bitboards[whiteBishop] |= 1L << to;
                        break;
                    case 0b10:
                        board[to] = whiteRook;
                        bitboards[whiteRook] |= 1L << to;
                        break;
                    case 0b11:
                        board[to] = whiteQueen;
                        bitboards[whiteQueen] |= 1L << to;
                        break;
                }
            }
            else {
                board[to] = moved;
                bitboards[moved] |= 1L << to;
            }

            if ((type & 0b0100) == 1) {
                bitboards[captured] ^= 1L << to;

                if (type == enPassant) {
                    int index = enPassantIndex - 8;
                    board[index] = empty;
                    bitboards[blackPawn] ^= 1L << index;
                }
            }
        }


        else {
            fullMoves += 1;

            if (moved == blackKing) {
                castlingRights &= 0b1100;

                if (type == kingCastle) {
                    board[63] = empty;
                    board[61] = whiteRook;
                    bitboards[whiteRook] ^= (1L << 63) | (1L << 61);
                }
                else if (type == queenCastle) {
                    board[56] = empty;
                    board[58] = whiteRook;
                    bitboards[whiteRook] ^= (1L << 56) | (1L << 58);
                }
            }
            else if (moved == blackRook) {
                if (from == blackKingsideRook) {
                    castlingRights &= 0b1101;
                }
                else if (from == blackQueensideRook) {
                    castlingRights &= 0b1110;
                }
            }
            else if (type == doublePawnPush) {
                enPassantIndex = to + 8;
            }

            if (captured == whiteRook) {
                if (to == whiteKingsideRook) {
                    castlingRights &= 0b0111;
                }
                else if (to == whiteQueensideRook) {
                    castlingRights &= 0b1011;
                }
            }

            if ((type & 0b1000) == 1) {
                switch (type & 0b0011) {
                    case 0b00:
                        board[to] = blackKnight;
                        bitboards[blackKnight] |= 1L << to;
                        break;
                    case 0b01:
                        board[to] = blackBishop;
                        bitboards[blackBishop] |= 1L << to;
                        break;
                    case 0b10:
                        board[to] = blackRook;
                        bitboards[blackRook] |= 1L << to;
                        break;
                    case 0b11:
                        board[to] = blackQueen;
                        bitboards[blackQueen] |= 1L << to;
                        break;
                }
            }
            else {
                board[to] = moved;
                bitboards[moved] |= 1L << to;
            }

            if ((type & 0b0100) == 1) {
                bitboards[captured] ^= 1L << to;

                if (type == enPassant) {
                    int index = enPassantIndex + 8;
                    board[index] = empty;
                    bitboards[blackPawn] ^= 1L << index;
                }
            }
        }
        
        if (type != doublePawnPush) {
            enPassantIndex = -1;
        }

        whiteToMove = !whiteToMove;
    }
}
