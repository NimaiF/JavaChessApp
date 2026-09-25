import java.util.Arrays;

public class Engine {
    public Game game;
    public String[] squareNames = {"a1", "b1", "c1", "d1", "e1", "f1", "g1", "h1",
                                  "a2", "b2", "c2", "d2", "e2", "f2", "g2", "h2",
                                  "a3", "b3", "c3", "d3", "e3", "f3", "g3", "h3",
                                  "a4", "b4", "c4", "d4", "e4", "f4", "g4", "h4",
                                  "a5", "b5", "c5", "d5", "e5", "f5", "g5", "h5",
                                  "a6", "b6", "c6", "d6", "e6", "f6", "g6", "h6",
                                  "a7", "b7", "c7", "d7", "e7", "f7", "g7", "h7",
                                  "a8", "b8", "c8", "d8", "e8", "f8", "g8", "h8"};
    public int[] pieceValues = {100, 300, 300, 500, 900, 0, -100, -300, -300, -500, -900, 0};
    public long moveTime;

    Engine(Game game, long moveTime) {
        this.game = game;
        this.moveTime = moveTime;
    }

    public void perftTest(int depth) {
        long start = System.nanoTime();

        long nodes = perft(depth);

        long end = System.nanoTime();

        System.out.println("Nodes:" + nodes);
        System.out.println("Time:" + (end - start) / 1000000000);
    }

    public long perft(int depth) {
        long nodes = 0;
        int[] moves = new int[218];

        if (depth == 0) {
            return 1L;
        }

        int nMoves = game.generateMoves(moves);
        for (int i = 0; i < nMoves; i++) {
            int move = moves[i];
            game.makeMove(move);

            if (!game.whiteToMove) {
                if (game.isAttacked(game.whiteKingIndex, game.getBlockers(), true)) {
                    game.undoMove();
                    continue;
                }
            }
            else {
                if (game.isAttacked(game.blackKingIndex, game.getBlockers(), false)) {
                    game.undoMove();
                    continue;
                }
            }

            nodes += perft(depth - 1);
            game.undoMove();
        }

        return nodes;
    }

    public int eval() {
        int value = 0;
        for (int i = 0; i < 64; i++) {
            int piece = game.board[i];
            if (piece != Game.empty) {
                value += pieceValues[game.board[i]];
            }
        }

        return game.whiteToMove ? value : -value;
    }

    public int negaMax(int depth, long startTime) {
        if (System.nanoTime() - startTime > moveTime) {
            return 0;
        }
        if (depth == 0) {
            return eval();
        }
        
        int max = Integer.MIN_VALUE;

        int[] moves = new int[218];
        int nMoves = game.generateMoves(moves);
        for (int i = 0; i < nMoves; i++) {
            int move = moves[i];
            game.makeMove(move);

            if (!game.whiteToMove) {
                if (game.isAttacked(game.whiteKingIndex, game.getBlockers(), true)) {
                    game.undoMove();
                    continue;
                }
            }
            else {
                if (game.isAttacked(game.blackKingIndex, game.getBlockers(), false)) {
                    game.undoMove();
                    continue;
                }
            }

            int score = -negaMax(depth - 1, startTime);
            if (score > max) {
                max = score;
            }
            game.undoMove();
        }

        return max;
    }

    public int rootNegaMax(int depth, long startTime) {        
        int max = Integer.MIN_VALUE;

        int[] moves = new int[218];
        int bestMove = moves[0];

        int nMoves = game.generateMoves(moves);
        for (int i = 0; i < nMoves; i++) {
            int move = moves[i];
            game.makeMove(move);

            if (!game.whiteToMove) {
                if (game.isAttacked(game.whiteKingIndex, game.getBlockers(), true)) {
                    game.undoMove();
                    continue;
                }
            }
            else {
                if (game.isAttacked(game.blackKingIndex, game.getBlockers(), false)) {
                    game.undoMove();
                    continue;
                }
            }

            int score = -negaMax(depth - 1, startTime);
            if (score > max) {
                max = score;
                bestMove = move;
            }
            game.undoMove();

            if (System.nanoTime() - startTime > moveTime) {
                return 0;
            }
        }

        return bestMove;
    }

    public int iterativeDeepening() {
        long start = System.nanoTime();
        int depth = 1;
        int bestMove = 0;
        do {
            System.out.println(depth);
            int move = rootNegaMax(depth, start);
            depth++;
            if (move != 0) {
                bestMove = move;
            }
        } while (System.nanoTime() - start < moveTime);

        return bestMove;
    }
}
