package snake;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import snake.ui.MenuFrame;

public final class Main {
    private Main() {
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (ReflectiveOperationException | UnsupportedLookAndFeelException e) {
                System.err.println("Using the default look and feel: " + e.getMessage());
            }
            new MenuFrame(AppContext.createDefault()).setVisible(true);
        });
    }
}
