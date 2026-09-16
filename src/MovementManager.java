import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MovementManager {
    private int selectedIndex;
    private GUI gui;

    public MouseAdapter mouseAdapter;

    MovementManager(GUI gui) {
        this.gui = gui;
        
        mouseAdapter = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                System.out.println(((Square) e.getComponent()).index);
            }
        };

        registerSquares();
    }

    private void registerSquares() {
        for (Square square : gui.squares) {
            square.addMouseListener(mouseAdapter);
        }
    }
}
