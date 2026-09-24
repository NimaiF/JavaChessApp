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

        Game game = new Game("8/2p5/3p4/KP5r/1R3p1k/8/4P1P1/8 w - - 0 1");
        GUI gui = new GUI();
        MovementManager movementManager = new MovementManager(gui);
        Engine engine = new Engine(game);

        engine.perftTest(6);
        gui.updateSquares(game.board);

        frame.add(gui);
        frame.pack();
        frame.setVisible(true);   
    }
}
