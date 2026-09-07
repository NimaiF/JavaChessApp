import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.imageio.ImageIO;
import java.io.IOException;

public class Square extends JLabel {
    public static final int SIZE = App.SIZE / 8;
    public static final ImageIcon[] pieceIcons = new ImageIcon[12];

    public int index;
    public int pieceType;

    static {
        String[] pieceNames = {"whitePawn", "whiteKnight", "whiteBishop", "whiteRook", "whiteQueen", "whiteKing", "blackPawn", "blackKnight", "blackBishop", "blackRook", "blackQueen", "blackKing"};

        try {
            for (int i = 0; i < 12; i++) {
                pieceIcons[i] = new ImageIcon(ImageIO.read(Square.class.getResource("/imgs/" + pieceNames[i] + ".png")).getScaledInstance(SIZE, SIZE, Image.SCALE_SMOOTH));
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    Square(int index, int pieceType) {
        this.setSize(new Dimension(SIZE, SIZE));
        this.setPiece(pieceType);
        this.moveTo(index);
        this.pieceType = pieceType;
    }

    public void setPiece(int pieceType) {
        this.setIcon(pieceIcons[pieceType]);
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
}
