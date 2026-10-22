package snake.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import snake.AppContext;
import snake.model.BoosterEffect;
import snake.model.Direction;
import snake.model.GameState;

public class GameFrame extends JFrame {
    private final AppContext app;
    private final GameController controller;
    private final BoardPanel board;
    private final JButton saveButton = new JButton("Save");
    private final JButton pauseButton = new JButton("Pause");
    private final JButton menuButton = new JButton("Menu");
    private final JLabel scoreLabel = new JLabel();
    private final JLabel effectLabel = new JLabel();
    private final JLabel soundLabel = new JLabel();

    public GameFrame(AppContext app, GameState state, GameController.Phase startPhase) {
        super("Snake");
        this.app = app;
        controller = new GameController(state, startPhase, app.settings(), app.sounds(), this::refresh);
        board = new BoardPanel(controller);

        saveButton.setFocusable(false);
        pauseButton.setFocusable(false);
        menuButton.setFocusable(false);
        saveButton.addActionListener(e -> saveGame());
        pauseButton.addActionListener(e -> controller.togglePause());
        menuButton.addActionListener(e -> returnToMenu());
        scoreLabel.setFont(Theme.HUD);
        effectLabel.setFont(Theme.HUD);

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        toolbar.add(saveButton);
        toolbar.add(pauseButton);
        toolbar.add(menuButton);
        toolbar.add(scoreLabel);
        toolbar.add(effectLabel);

        JPanel boardHolder = new JPanel(new GridBagLayout());
        boardHolder.setBackground(Theme.BOARD);
        boardHolder.add(board);

        JPanel hints = new JPanel(new GridLayout(2, 1));
        hints.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        hints.add(new JLabel("WASD/arrows: move   P: pause   M: music   N: effects   ESC: menu"));
        hints.add(soundLabel);

        add(toolbar, BorderLayout.NORTH);
        add(boardHolder, BorderLayout.CENTER);
        add(hints, BorderLayout.SOUTH);

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                handleKey(e.getKeyCode());
            }
        });
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                returnToMenu();
            }
        });
        setResizable(false);
        pack();
        setLocationRelativeTo(null);
        refresh();
    }

    private void handleKey(int key) {
        switch (key) {
            case KeyEvent.VK_W, KeyEvent.VK_UP -> controller.turn(Direction.UP);
            case KeyEvent.VK_S, KeyEvent.VK_DOWN -> controller.turn(Direction.DOWN);
            case KeyEvent.VK_A, KeyEvent.VK_LEFT -> controller.turn(Direction.LEFT);
            case KeyEvent.VK_D, KeyEvent.VK_RIGHT -> controller.turn(Direction.RIGHT);
            case KeyEvent.VK_SPACE -> controller.startNewGame();
            case KeyEvent.VK_P -> controller.togglePause();
            case KeyEvent.VK_M -> controller.toggleMusic();
            case KeyEvent.VK_N -> controller.toggleEffects();
            case KeyEvent.VK_ESCAPE -> returnToMenu();
            default -> { }
        }
    }

    private void refresh() {
        GameState state = controller.state();
        int high = Math.max(state.score(), app.settings().getHighScore(state.cols()));
        scoreLabel.setText("Score: " + state.score() + "    High: " + high);

        BoosterEffect effect = state.effect();
        if (effect != null) {
            effectLabel.setText(effect.label() + " " + state.effectSecondsLeft() + "s");
            effectLabel.setForeground(Theme.boardColor(effect));
        } else {
            effectLabel.setText("");
        }

        String music = app.settings().isMusicOn() ? "on" : "off";
        String effects = app.settings().isEffectsOn() ? "on" : "off";
        soundLabel.setText("Music " + music + "  |  Effects " + effects);

        if (controller.phase() == GameController.Phase.PAUSED) {
            pauseButton.setText("Resume");
        } else {
            pauseButton.setText("Pause");
        }
        pauseButton.setEnabled(controller.canSave());
        saveButton.setEnabled(controller.canSave());
        board.repaint();
    }

    private boolean saveGame() {
        if (!controller.canSave()) {
            return false;
        }
        controller.pause();
        String name = JOptionPane.showInputDialog(this, "Save name:");
        if (name == null || name.isBlank()) {
            return false;
        }
        if (app.saves().exists(name)) {
            int choice = JOptionPane.showConfirmDialog(this, "This save already exists. Replace it?", "Save Game",
                    JOptionPane.YES_NO_OPTION);
            if (choice != JOptionPane.YES_OPTION) {
                return false;
            }
        }
        try {
            app.saves().save(name, controller.state());
            JOptionPane.showMessageDialog(this, "Game saved.");
            return true;
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Could not save the game: " + e.getMessage());
            return false;
        }
    }

    private void returnToMenu() {
        if (controller.canSave()) {
            controller.pause();
            int choice = JOptionPane.showConfirmDialog(this, "Save the game before going to the menu?", "Menu",
                    JOptionPane.YES_NO_CANCEL_OPTION);
            if (choice == JOptionPane.CANCEL_OPTION || choice == JOptionPane.CLOSED_OPTION) {
                return;
            }
            if (choice == JOptionPane.YES_OPTION && !saveGame()) {
                return;
            }
        }
        controller.stop();
        dispose();
        new MenuFrame(app).setVisible(true);
    }
}
