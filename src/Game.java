import java.io.*;
import java.util.Arrays;

public class Game {
    public static final long eighthRank = 0b1111111100000000000000000000000000000000000000000000000000000000L;
    public static final long firstRank = 0b0000000000000000000000000000000000000000000000000000000011111111L;
    public static final long seventhRank = 0b0000000011111111000000000000000000000000000000000000000000000000L;
    public static final long secondRank = 0b0000000000000000000000000000000000000000000000001111111100000000L;

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
    public static final int[] promotions = {knightPromotion, bishopPromotion, rookPromotion, queenPromotion};
    public static final int[] promotionCaptures = {knightPromotionCapture, bishopPromotionCapture, rookPromotionCapture, queenPromotionCapture};

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
    public int[] board = new int[64];
    public long[] bitboards = new long[12];
    public final Object[][] moveStack = new Object[128][8];
    public int stackIndex = 0;

    public int whiteKingIndex;
    public int blackKingIndex;

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
            if (square == whiteKing) {
                whiteKingIndex = i;
            }
            else if (square == blackKing) {
                blackKingIndex = i;
            }
        }
    }

    public static int encodeMove(int from, int to, int type) {
        return (from << 10) | (to << 4) | type;
    }

    public static int[] decodeMove(int move) {
        int[] moveArray = {move >>> 10, (move >>> 4) & 0b111111, move & 0b1111};
        return moveArray;
    }

    public void makeMove(int move) {
        Object[] stack = moveStack[stackIndex];
        stack[0] = halfMoves;
        stack[1] = fullMoves;
        stack[2] = castlingRights;
        stack[3] = enPassantIndex;
        stack[4] = bitboards.clone();
        stack[5] = board.clone();
        stack[6] = whiteKingIndex;
        stack[7] = blackKingIndex;
        stackIndex++;

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
                    board[3] = whiteRook;
                    bitboards[whiteRook] ^= (1L << 0) | (1L << 3);
                }

                whiteKingIndex = to;
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

            if ((type & 0b1000) != 0) {
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

            if ((type & 0b0100) != 0) {
                if (captured != empty) {
                    bitboards[captured] ^= 1L << to;
                }
                else {
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
                    board[61] = blackRook;
                    bitboards[blackRook] ^= (1L << 63) | (1L << 61);
                }
                else if (type == queenCastle) {
                    board[56] = empty;
                    board[59] = blackRook;
                    bitboards[blackRook] ^= (1L << 56) | (1L << 59);
                }

                blackKingIndex = to;
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

            if ((type & 0b1000) != 0) {
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

            if ((type & 0b0100) != 0) {
                if (captured != empty) {
                    bitboards[captured] ^= 1L << to;
                }
                else {
                    int index = enPassantIndex + 8;
                    board[index] = empty;
                    bitboards[whitePawn] ^= 1L << index;
                }
            }
        }
        
        if (type != doublePawnPush) {
            enPassantIndex = -1;
        }

        whiteToMove = !whiteToMove;
    }

    public void undoMove() {
        stackIndex--;
        Object[] stack = moveStack[stackIndex];
        halfMoves = (int) stack[0];
        fullMoves = (int) stack[1];
        castlingRights = (byte) stack[2];
        enPassantIndex = (int) stack[3];
        bitboards = ((long[]) stack[4]).clone();
        board = ((int[]) stack[5]).clone();
        whiteKingIndex = (int) stack[6];
        blackKingIndex = (int) stack[7];

        whiteToMove = !whiteToMove;
    }

    public int generateMoves(int[] moves) {
        int index = 0;

        if (whiteToMove) {
            long enemyBlockers = bitboards[blackPawn] | bitboards[blackKnight] | bitboards[blackBishop] | bitboards[blackRook] | bitboards[blackQueen] | bitboards[blackKing];
            long friendlyBlockers = bitboards[whitePawn] | bitboards[whiteKnight] | bitboards[whiteBishop] | bitboards[whiteRook] | bitboards[whiteQueen] | bitboards[whiteKing];
            long blockers = enemyBlockers | friendlyBlockers;
            long piecesBitboard = friendlyBlockers;

            while (piecesBitboard != 0) {
                int i = Long.numberOfTrailingZeros(piecesBitboard);
                piecesBitboard &= piecesBitboard - 1;
                int piece = board[i];

                if (piece == whitePawn) {
                    if (((1L << (i + 8)) & blockers) == 0) {
                        if (((1L << (i + 8)) & eighthRank) != 0) {
                            for (int promotion : promotions) {
                                moves[index] = encodeMove(i, i + 8, promotion);
                                index++;
                            }
                        }
                        else {
                            moves[index] = encodeMove(i, i + 8, quietMove);
                            index++;
                        }

                        if (((1L << (i + 16)) & blockers) == 0 && ((1L << i) & secondRank) != 0) {
                            moves[index] = encodeMove(i, i + 16, doublePawnPush);
                            index++;
                        }
                    }

                    long captures = whitePawnBitboards[i] & enemyBlockers;
                    while (captures != 0) {
                        int j = Long.numberOfTrailingZeros(captures);
                        captures &= captures - 1;
                        if (((1L << j) & eighthRank) != 0) {
                            for (int promotion : promotionCaptures) {
                                moves[index] = encodeMove(i, j, promotion);
                                index++;
                            }
                        }
                        else {
                            moves[index] = encodeMove(i, j, capture);
                            index++;
                        }
                    }
                    if ((whitePawnBitboards[i] & (1L << enPassantIndex)) != 0) {
                        moves[index] = encodeMove(i, enPassantIndex, enPassant);
                        index++;
                    }
                }
                else {
                    long attacks = 0;
                    switch (piece) {
                        case whiteKnight:
                            attacks = knightBitboards[i] & ~friendlyBlockers;
                            break;
                        case whiteBishop:
                            attacks = bishopBitboards[i][(int) (((blockers & bishopMasks[i]) * bishopMagics[i]) >>> (64 - bishopBits[i]))] & ~friendlyBlockers;
                            break;
                        case whiteRook:
                            attacks = rookBitboards[i][(int) (((blockers & rookMasks[i]) * rookMagics[i]) >>> (64 - rookBits[i]))] & ~friendlyBlockers;
                            break;
                        case whiteQueen:
                            attacks = (bishopBitboards[i][(int) (((blockers & bishopMasks[i]) * bishopMagics[i]) >>> (64 - bishopBits[i]))] | rookBitboards[i][(int) (((blockers & rookMasks[i]) * rookMagics[i]) >>> (64 - rookBits[i]))]) & ~friendlyBlockers;
                            break;
                        case whiteKing:
                            attacks = kingBitboards[i] & ~friendlyBlockers;
                            break;
                    }

                    while (attacks != 0) {
                        int j = Long.numberOfTrailingZeros(attacks);
                        attacks &= attacks - 1;

                        if (((1L << j) & enemyBlockers) != 0) {
                            moves[index] = encodeMove(i, j, capture);
                        }
                        else {
                            moves[index] = encodeMove(i, j, quietMove);
                        }
                        index++;
                    }

                    if (piece == whiteKing && !isAttacked(i, blockers, true)) {
                        if ((castlingRights & 0b1000) != 0 && (0b0000000000000000000000000000000000000000000000000000000001100000L & blockers) == 0) {
                            if (!isAttacked(5, blockers, true) && !isAttacked(6, blockers, true)) {
                                moves[index] = encodeMove(i, 6, kingCastle);
                                index++;
                            }
                        }
                        if ((castlingRights & 0b0100) != 0 && (0b0000000000000000000000000000000000000000000000000000000000001100L & blockers) == 0) {
                            if (!isAttacked(2, blockers, true) && !isAttacked(3, blockers, true)) {
                                moves[index] = encodeMove(i, 2, queenCastle);
                                index++;
                            }
                        }
                    }
                }
            }
        }
        else {
            long friendlyBlockers = bitboards[blackPawn] | bitboards[blackKnight] | bitboards[blackBishop] | bitboards[blackRook] | bitboards[blackQueen] | bitboards[blackKing];
            long enemyBlockers = bitboards[whitePawn] | bitboards[whiteKnight] | bitboards[whiteBishop] | bitboards[whiteRook] | bitboards[whiteQueen] | bitboards[whiteKing];
            long blockers = enemyBlockers | friendlyBlockers;
            long piecesBitboard = friendlyBlockers;

            while (piecesBitboard != 0) {
                int i = Long.numberOfTrailingZeros(piecesBitboard);
                piecesBitboard &= piecesBitboard - 1;
                int piece = board[i];

                if (piece == blackPawn) {
                    if (((1L << (i - 8)) & blockers) == 0) {
                        if (((1L << (i - 8)) & firstRank) != 0) {
                            for (int promotion : promotions) {
                                moves[index] = encodeMove(i, i - 8, promotion);
                                index++;
                            }
                        }
                        else {
                            moves[index] = encodeMove(i, i - 8, quietMove);
                            index++;
                        }

                        if (((1L << (i - 16)) & blockers) == 0 && ((1L << i) & seventhRank) != 0) {
                            moves[index] = encodeMove(i, i - 16, doublePawnPush);
                            index++;
                        }
                    }

                    long captures = blackPawnBitboards[i] & enemyBlockers;
                    while (captures != 0) {
                        int j = Long.numberOfTrailingZeros(captures);
                        captures &= captures - 1;
                        if (((1L << j) & firstRank) != 0) {
                            for (int promotion : promotionCaptures) {
                                moves[index] = encodeMove(i, j, promotion);
                                index++;
                            }
                        }
                        else {
                            moves[index] = encodeMove(i, j, capture);
                            index++;
                        }
                    }
                    if ((blackPawnBitboards[i] & (1L << enPassantIndex)) != 0) {
                        moves[index] = encodeMove(i, enPassantIndex, enPassant);
                        index++;
                    }
                }
                else {
                    long attacks = 0;
                    switch (piece) {
                        case blackKnight:
                            attacks = knightBitboards[i] & ~friendlyBlockers;
                            break;
                        case blackBishop:
                            attacks = bishopBitboards[i][(int) (((blockers & bishopMasks[i]) * bishopMagics[i]) >>> (64 - bishopBits[i]))] & ~friendlyBlockers;
                            break;
                        case blackRook:
                            attacks = rookBitboards[i][(int) (((blockers & rookMasks[i]) * rookMagics[i]) >>> (64 - rookBits[i]))] & ~friendlyBlockers;
                            break;
                        case blackQueen:
                            attacks = (bishopBitboards[i][(int) (((blockers & bishopMasks[i]) * bishopMagics[i]) >>> (64 - bishopBits[i]))] | rookBitboards[i][(int) (((blockers & rookMasks[i]) * rookMagics[i]) >>> (64 - rookBits[i]))]) & ~friendlyBlockers;
                            break;
                        case blackKing:
                            attacks = kingBitboards[i] & ~friendlyBlockers;
                            break;
                    }

                    while (attacks != 0) {
                        int j = Long.numberOfTrailingZeros(attacks);
                        attacks &= attacks - 1;

                        if (((1L << j) & enemyBlockers) != 0) {
                            moves[index] = encodeMove(i, j, capture);
                        }
                        else {
                            moves[index] = encodeMove(i, j, quietMove);
                        }
                        index++;
                    }

                    if (piece == blackKing && !isAttacked(i, blockers, false)) {
                        if ((castlingRights & 0b0010) != 0 && (0b0110000000000000000000000000000000000000000000000000000000000000L & blockers) == 0) {
                            if (!isAttacked(61, blockers, false) && !isAttacked(62, blockers, false)) {
                                moves[index] = encodeMove(i, 62, kingCastle);
                                index++;
                            }
                        }
                        if ((castlingRights & 0b0001) != 0 && (0b0000110000000000000000000000000000000000000000000000000000000000L & blockers) == 0) {
                            if (!isAttacked(58, blockers, false) && !isAttacked(59, blockers, false)) {
                                moves[index] = encodeMove(i, 58, queenCastle);
                                index++;
                            }
                        }
                    }
                }
            }
        }

        return index;
    }

    public boolean isAttacked(int i, long blockers, boolean white) {
        long bishopAttacks = bishopBitboards[i][(int) (((blockers & bishopMasks[i]) * bishopMagics[i]) >>> (64 - bishopBits[i]))];
        long rookAttacks = rookBitboards[i][(int) (((blockers & rookMasks[i]) * rookMagics[i]) >>> (64 - rookBits[i]))];

        if (white) {
            if ((bishopAttacks & (bitboards[blackBishop] | bitboards[blackQueen])) != 0 || (rookAttacks & (bitboards[blackRook] | bitboards[blackQueen])) != 0) {
                return true;
            }
            else if ((knightBitboards[i] & bitboards[blackKnight]) != 0) {
                return true;
            }
            else if ((whitePawnBitboards[i] & bitboards[blackPawn]) != 0) {
                return true;
            }
            else if ((kingBitboards[i] & bitboards[blackKing]) != 0) {
                return true;
            }
            else {
                return false;
            }
        }
        else {
            if ((bishopAttacks & (bitboards[whiteBishop] | bitboards[whiteQueen])) != 0 || (rookAttacks & (bitboards[whiteRook] | bitboards[whiteQueen])) != 0) {
                return true;
            }
            else if ((knightBitboards[i] & bitboards[whiteKnight]) != 0) {
                return true;
            }
            else if ((blackPawnBitboards[i] & bitboards[whitePawn]) != 0) {
                return true;
            }
            else if ((kingBitboards[i] & bitboards[whiteKing]) != 0) {
                return true;
            }
            else {
                return false;
            }
        }
    }

    public long getBlockers() {
        long blockers = 0;
        for (int i = 0; i < 12; i++) {
            blockers |= bitboards[i];
        }
        return blockers;
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
}
