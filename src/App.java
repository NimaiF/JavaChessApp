import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class App {

    public static final int SIZE = 800;
    public static final boolean playerWhite = true;

    private class GUI extends JPanel {
        private Image background;

        public GUI() {
            this.setPreferredSize(new Dimension(SIZE, SIZE));
            this.setLayout(null);
            try {
                background = ImageIO.read(getClass().getResource("/imgs/chessboard.png"))
                        .getScaledInstance(SIZE, SIZE, Image.SCALE_SMOOTH);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(background, 0, 0, this);
        }
    }

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
        Square piece = new Square(0, Game.whitePawn);
        gui.add(piece);

        frame.add(gui);
        frame.pack();
        frame.setVisible(true);
    }
}
