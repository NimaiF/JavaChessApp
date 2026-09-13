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

        public void displayPieces(int[] board) {
            for (int index = 0; index < 64; index++) {
                if (board[index] == Game.empty) {
                    continue;
                }

                Square piece = new Square(index, board[index]);
                this.add(piece);
            }
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
        Game game = new Game("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
        gui.displayPieces(game.board);

        frame.add(gui);
        frame.pack();
        frame.setVisible(true);
    }
}
