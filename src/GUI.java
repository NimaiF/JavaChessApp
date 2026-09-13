import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
    
public class GUI extends JPanel {
    private Image background;
    private int[] board;

    public final Square[] squares = new Square[64];

    GUI(int[] board) {
        this.board = board;
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
        this.updateSquares();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(background, 0, 0, this);
    }

    public void updateSquares() {
        for (int index = 0; index < 64; index++) {
            squares[index].setPiece(this.board[index]);
        }
    }
}
