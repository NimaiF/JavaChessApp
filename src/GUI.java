import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
    
public class GUI extends JPanel {
    private Image background;

    public final Square[] squares = new Square[64];

    GUI() {
        this.setPreferredSize(new Dimension(App.SIZE, App.SIZE));
        this.setLayout(null);
        try {
            background = ImageIO.read(getClass().getResource("/imgs/chessboard.png"))
                    .getScaledInstance(App.SIZE, App.SIZE, Image.SCALE_SMOOTH);
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (int index = 0; index < 64; index++) {
            Square square = new Square(index);
            this.add(square);
            squares[index] = square;
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(background, 0, 0, this);
    }

    public void updateSquares(int[] board) {
        for (int index = 0; index < 64; index++) {
            squares[index].setPiece(board[index]);
        }
    }

    public void updateSquaresNow(int[] board) {
        try {
            SwingUtilities.invokeAndWait(new Runnable() {
                public void run() {
                    updateSquares(board);
                }
            });
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
