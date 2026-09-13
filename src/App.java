import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class App {

    public static final int SIZE = 800;
    public static final boolean playerWhite = false;

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
        GUI gui = new GUI(game.board);
        MovementManager movementManager = new MovementManager();
        movementManager.registerSquares(gui.squares);

        frame.add(gui);
        frame.pack();
        frame.setVisible(true);   
    }
}
