package snake.ui;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import snake.AppContext;
import snake.model.BoardSize;
import snake.model.GameState;
import snake.persistence.InvalidSaveException;
import snake.persistence.SaveInfo;
import snake.persistence.Settings;

public class MenuFrame extends JFrame {
    private final AppContext app;
    private final JList<SaveInfo> saveList = new JList<>();
    private final JLabel highScore = new JLabel("", SwingConstants.CENTER);
    private final JButton loadGame = new JButton("Load Game");
    private final JButton deleteSave = new JButton("Delete Save");

    public MenuFrame(AppContext app) {
        super("Snake - Menu");
        this.app = app;
        Settings settings = app.settings();
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        JLabel title = new JLabel("Snake", SwingConstants.CENTER);
        title.setFont(new Font(Font.SANS_SERIF, Font.BOLD | Font.ITALIC, 36));
        highScore.setFont(Theme.TEXT);
        showHighScore();
        JPanel header = new JPanel(new GridLayout(2, 1));
        header.setOpaque(false);
        header.add(title);
        header.add(highScore);

        saveList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    loadSelected();
                }
            }
        });
        saveList.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_DELETE) {
                    deleteSelected();
                }
            }
        });
        saveList.addListSelectionListener(e -> updateButtons());
        JScrollPane saves = new JScrollPane(saveList);
        saves.setBorder(BorderFactory.createTitledBorder("Saved games (double-click to load, Delete to remove)"));

        JCheckBox looping = new JCheckBox("Looping borders", settings.isLooping());
        JCheckBox music = new JCheckBox("Music", settings.isMusicOn());
        JCheckBox effects = new JCheckBox("Sound effects", settings.isEffectsOn());
        looping.setOpaque(false);
        music.setOpaque(false);
        effects.setOpaque(false);
        looping.addActionListener(e -> settings.setLooping(looping.isSelected()));
        music.addActionListener(e -> settings.setMusicOn(music.isSelected()));
        effects.addActionListener(e -> settings.setEffectsOn(effects.isSelected()));

        JComboBox<BoardSize> size = new JComboBox<>(BoardSize.values());
        size.setSelectedItem(settings.getBoardSize());
        size.addActionListener(e -> {
            settings.setBoardSize((BoardSize) size.getSelectedItem());
            showHighScore();
        });
        JPanel sizeRow = new JPanel(new BorderLayout(6, 0));
        sizeRow.setOpaque(false);
        sizeRow.add(new JLabel("Board:"), BorderLayout.WEST);
        sizeRow.add(size, BorderLayout.CENTER);

        JPanel options = new JPanel(new GridLayout(2, 2, 10, 4));
        options.setOpaque(false);
        options.add(looping);
        options.add(music);
        options.add(effects);
        options.add(sizeRow);

        JButton newGame = new JButton("New Game");
        JButton quit = new JButton("Quit");
        newGame.addActionListener(e -> startNewGame());
        loadGame.addActionListener(e -> loadSelected());
        deleteSave.addActionListener(e -> deleteSelected());
        quit.addActionListener(e -> System.exit(0));
        JPanel buttons = new JPanel(new GridLayout(1, 4, 10, 0));
        buttons.setOpaque(false);
        buttons.add(newGame);
        buttons.add(loadGame);
        buttons.add(deleteSave);
        buttons.add(quit);

        JPanel bottom = new JPanel(new BorderLayout(0, 12));
        bottom.setOpaque(false);
        bottom.add(options, BorderLayout.NORTH);
        bottom.add(buttons, BorderLayout.SOUTH);

        JPanel root = new JPanel(new BorderLayout(0, 12));
        root.setBackground(Theme.MENU_BACKGROUND);
        root.setBorder(BorderFactory.createEmptyBorder(15, 20, 20, 20));
        root.add(header, BorderLayout.NORTH);
        root.add(saves, BorderLayout.CENTER);
        root.add(bottom, BorderLayout.SOUTH);
        setContentPane(root);
        getRootPane().setDefaultButton(newGame);

        setSize(520, 460);
        setLocationRelativeTo(null);
        refreshSaves();
    }

    private void startNewGame() {
        Settings settings = app.settings();
        int tiles = settings.getBoardSize().tiles();
        openGame(new GameState(tiles, tiles, settings.isLooping()), GameController.Phase.READY);
    }

    private void loadSelected() {
        SaveInfo save = saveList.getSelectedValue();
        if (save == null || save.game() == null) {
            return;
        }
        try {
            openGame(app.saves().load(save), GameController.Phase.PAUSED);
        } catch (InvalidSaveException e) {
            JOptionPane.showMessageDialog(this, "Could not load \"" + save.name() + "\". " + e.getMessage());
            refreshSaves();
        }
    }

    private void deleteSelected() {
        SaveInfo save = saveList.getSelectedValue();
        if (save == null) {
            return;
        }
        int choice = JOptionPane.showConfirmDialog(this, "Delete the save \"" + save.name() + "\"?",
                "Delete Save", JOptionPane.YES_NO_OPTION);
        if (choice != JOptionPane.YES_OPTION) {
            return;
        }
        if (!app.saves().delete(save)) {
            JOptionPane.showMessageDialog(this, "Could not delete the save.");
        }
        refreshSaves();
    }

    private void openGame(GameState state, GameController.Phase startPhase) {
        new GameFrame(app, state, startPhase).setVisible(true);
        dispose();
    }

    private void refreshSaves() {
        saveList.setListData(app.saves().list().toArray(new SaveInfo[0]));
        updateButtons();
    }

    private void updateButtons() {
        SaveInfo save = saveList.getSelectedValue();
        loadGame.setEnabled(save != null && save.game() != null);
        deleteSave.setEnabled(save != null);
    }

    private void showHighScore() {
        BoardSize size = app.settings().getBoardSize();
        highScore.setText("High score on " + size + ": " + app.settings().getHighScore(size.tiles()));
    }
}
