package snake.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JPanel;
import snake.model.GameState;
import snake.model.Point;

public class BoardPanel extends JPanel {
    private static final int TILE = Theme.TILE;

    private final GameController controller;

    public BoardPanel(GameController controller) {
        this.controller = controller;
        GameState state = controller.state();
        setPreferredSize(new Dimension(state.cols() * TILE, state.rows() * TILE));
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D g = (Graphics2D) graphics;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        GameState state = controller.state();

        drawBoard(g, state);
        if (controller.phase() == GameController.Phase.READY) {
            drawMessage(g, "Snake", "Press SPACE to start", "Arrow keys or W A S D to steer");
            return;
        }
        drawGame(g, state);
        if (controller.phase() == GameController.Phase.PAUSED) {
            drawMessage(g, "Paused", "Press P to resume", "ESC: menu");
        } else if (controller.phase() == GameController.Phase.OVER) {
            String title = state.isWon() ? "You win!" : "Game Over";
            String score = "Final score: " + state.score();
            if (controller.isNewHighScore()) {
                score += "  - new high score!";
            }
            drawMessage(g, title, score, "SPACE: new game   ESC: menu");
        }
    }

    private void drawBoard(Graphics g, GameState state) {
        g.setColor(Theme.boardColor(state.effect()));
        g.fillRect(0, 0, getWidth(), getHeight());

        int width = state.cols() * TILE;
        int height = state.rows() * TILE;
        g.setColor(Theme.GRID);
        for (int x = 0; x <= state.cols(); x++) {
            g.drawLine(x * TILE, 0, x * TILE, height);
        }
        for (int y = 0; y <= state.rows(); y++) {
            g.drawLine(0, y * TILE, width, y * TILE);
        }
        if (!state.isLooping()) {
            g.setColor(Color.LIGHT_GRAY);
            g.drawRect(0, 0, width - 1, height - 1);
            g.drawRect(1, 1, width - 3, height - 3);
        }
    }

    private void drawGame(Graphics g, GameState state) {
        Point apple = state.apple();
        if (apple != null) {
            g.setColor(Theme.APPLE);
            g.fillOval(apple.x() * TILE + 2, apple.y() * TILE + 2, TILE - 4, TILE - 4);
        }
        Point booster = state.booster();
        if (booster != null) {
            g.setColor(Theme.BOOSTER);
            g.fillRect(booster.x() * TILE + 4, booster.y() * TILE + 4, TILE - 8, TILE - 8);
        }
        boolean head = true;
        for (Point p : state.snake().body()) {
            g.setColor(head ? Theme.SNAKE_HEAD : Theme.SNAKE_BODY);
            g.fillRoundRect(p.x() * TILE + 1, p.y() * TILE + 1, TILE - 2, TILE - 2, 8, 8);
            head = false;
        }
    }

    private void drawMessage(Graphics g, String title, String line1, String line2) {
        g.setColor(Theme.OVERLAY);
        g.fillRect(0, 0, getWidth(), getHeight());
        g.setColor(Color.WHITE);
        drawCentered(g, title, Theme.TITLE, -10);
        drawCentered(g, line1, Theme.TEXT, 22);
        drawCentered(g, line2, Theme.TEXT, 46);
    }

    private void drawCentered(Graphics g, String text, Font font, int dy) {
        g.setFont(font);
        int width = g.getFontMetrics().stringWidth(text);
        g.drawString(text, (getWidth() - width) / 2, getHeight() / 2 + dy);
    }
}
