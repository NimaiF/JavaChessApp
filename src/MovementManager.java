import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MovementManager {
    private int selectedIndex;

    public MouseAdapter mouseAdapter;

    MovementManager() {
        mouseAdapter = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                System.out.println(((Square) e.getComponent()).index);
            }
        };
    }

    public void registerSquares(Square[] squares) {
        for (Square square : squares) {
            square.addMouseListener(mouseAdapter);
        }
    }
}
