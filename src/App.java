import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.*;

public class App {

    public static final int SIZE = 800;
    public static final boolean playerWhite = true;

    public static void main(final String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new App().display();
            }
        });
    }

    private void display() {
        JFrame frame = new JFrame("Chess");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);

        GUI gui = new GUI();
        frame.add(gui);
        frame.pack();
        frame.setVisible(true);   

        engineBattle(gui);
    }

    private void engineBattle(GUI gui) {
        new Thread(new Runnable() {
            public void run() {
                Game game = new Game("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
                Engine engine = new Engine(game, 500000000L);
                gui.updateSquaresNow(game.board);
                while (true) {
                    int move = engine.iterativeDeepening();
                    if (move == 0) {
                        break;
                    }
                    game.makeMove(move);
                    gui.updateSquaresNow(game.board);
                }
            }
        }).start();
    }
}
