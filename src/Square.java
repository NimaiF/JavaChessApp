import javax.swing.*;
import java.awt.*;

import javax.imageio.ImageIO;
import java.io.IOException;

public class Square extends JLabel {
    public static final int SIZE = App.SIZE / 8;
    public static final ImageIcon[] pieceIcons = new ImageIcon[13];
    public static ImageIcon selected;
    public int pieceType;

    public int index;

    static {
        String[] pieceNames = {"whitePawn", "whiteKnight", "whiteBishop", "whiteRook", "whiteQueen", "whiteKing", "blackPawn", "blackKnight", "blackBishop", "blackRook", "blackQueen", "blackKing"};

        try {
            for (int i = 0; i < 12; i++) {
                pieceIcons[i] = new ImageIcon(ImageIO.read(Square.class.getResource("/imgs/" + pieceNames[i] + ".png")).getScaledInstance(SIZE, SIZE, Image.SCALE_SMOOTH));
            }
            selected = new ImageIcon(ImageIO.read(Square.class.getResource("/imgs/Selected.png")).getScaledInstance(SIZE, SIZE, Image.SCALE_SMOOTH));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    Square(int index) {
        this.setSize(new Dimension(SIZE, SIZE));
        this.moveTo(index);
    }

    public void setPiece(int pieceType) {
        this.setIcon(pieceIcons[pieceType]);
        this.pieceType = pieceType;
    }

    public void moveTo(int index) {
        this.index = index;
        if (App.playerWhite) {
            index = 63 - index;
        }
        int x = (7 - (index % 8)) * SIZE;
        int y = (index / 8) * SIZE;
        this.setLocation(x, y);
    }

    public void setSelected() {
        this.setIcon(selected);
    }
}
