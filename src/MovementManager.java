import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MovementManager {
    private int selectedIndex = -1;
    private Square selectedSquare;
    private int[] legalMoves;
    private GUI gui;
    private Game game;
    private Engine engine;
    private boolean done = true;

    private MouseAdapter mouseAdapter;

    private Runnable renderSquares = new Runnable() {
        public void run() {
            gui.updateSquares(game.board);
        }
    };


    MovementManager(GUI gui, Game game, Engine engine) {
        this.gui = gui;
        this.game = game;
        this.engine = engine;
        
        mouseAdapter = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleClick(e);
            }
        };

        registerSquares();
        selectedSquare = new Square(5);
        selectedSquare.setVisible(false);
        selectedSquare.setSelected();
        gui.add(selectedSquare);
    }

    private void registerSquares() {
        for (Square square : gui.squares) {
            square.addMouseListener(mouseAdapter);
        }
    }

    private boolean isMovable(Square square) {
        if (App.playerWhite) {
            if (square.pieceType >= Game.whitePawn && square.pieceType <= Game.whiteKing) {
                return true;
            }
            else {
                return false;
            }
        }
        else {
            if (square.pieceType >= Game.blackPawn && square.pieceType <= Game.blackKing) {
                return true;
            }
            else {
                return false;
            }
        }
    }

    private void getLegalMoves(int index) {
        int[] moves = new int[218];
        legalMoves = new int[218];
        int nMoves = game.generateMoves(moves);
        int legalMovesIndex = 0;

        for (int i = 0; i < nMoves; i++) {
            int move = moves[i];
            int[] moveInfo = Game.decodeMove(move);
            if (moveInfo[0] != index) {
                continue;
            }

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

            legalMoves[legalMovesIndex] = move;
            legalMovesIndex++;

            game.undoMove();
        }
    }

    private void handleClick(MouseEvent e) {
        if (!done) {
            return;
        }
        new Thread(new Runnable() {
            public void run() {
                done = false;
                Square clicked = (Square) e.getComponent();
                if (game.whiteToMove == App.playerWhite) {
                    if (isMovable(clicked)) {
                        selectedSquare.moveTo(clicked.index);
                        selectedSquare.setVisible(true);
                        selectedIndex = clicked.index;
                        getLegalMoves(selectedIndex);
                    }
                    else {
                        if (selectedIndex != -1) {
                            for (int move : legalMoves) {
                                if (move == 0) {
                                    break;
                                }
                                int[] moveInfo = Game.decodeMove(move);
                                if (moveInfo[1] == clicked.index) {
                                    game.makeMove(move);
                                    execute(renderSquares);
                                    selectedSquare.setVisible(false);
                                    int engineMove = engine.iterativeDeepening();
                                    game.makeMove(engineMove);
                                    execute(renderSquares);
                                    break;
                                }
                            }
                        }
                        selectedSquare.setVisible(false);
                        selectedIndex = -1;
                    }
                }
                else {
                    selectedSquare.setVisible(false);
                    selectedIndex = -1;
                }
                done = true;
            }
        }).start();
    }

    private void execute(Runnable runnable) {
        try { 
            SwingUtilities.invokeAndWait(runnable);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }       
}
