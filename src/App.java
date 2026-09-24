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

        Game game = new Game("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
        GUI gui = new GUI();
        MovementManager movementManager = new MovementManager(gui);
        Engine engine = new Engine(game);

        engine.perftTest(7);
        gui.updateSquares(game.board);

        frame.add(gui);
        frame.pack();
        frame.setVisible(true);   

        // Nodes:164075551
        // Time:22.133065978
    }
}
