import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.WindowConstants;

public class SnakeGame extends JFrame {

    private static final int CELL = 20;
    private static final int COLS = 25;
    private static final int ROWS = 25;
    private static final int DELAY_MS = 110;

    private final Random random = new Random();
    private final List<int[]> snake = new ArrayList<>();
    private final Timer timer;
    private final Board board = new Board();

    private int dx;
    private int dy;
    private int nextDx;
    private int nextDy;
    private int foodX;
    private int foodY;
    private int score;
    private boolean gameOver;

    public SnakeGame() {
        super("Snake");
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);
        add(board);
        pack();
        setLocationRelativeTo(null);

        timer = new Timer(DELAY_MS, e -> tick());
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                handleKey(e.getKeyCode());
            }
        });
        reset();
    }

    private void reset() {
        snake.clear();
        snake.add(new int[]{COLS / 2, ROWS / 2});
        snake.add(new int[]{COLS / 2 - 1, ROWS / 2});
        snake.add(new int[]{COLS / 2 - 2, ROWS / 2});
        dx = nextDx = 1;
        dy = nextDy = 0;
        score = 0;
        gameOver = false;
        placeFood();
        timer.start();
        board.repaint();
    }

    private void placeFood() {
        do {
            foodX = random.nextInt(COLS);
            foodY = random.nextInt(ROWS);
        } while (occupied(foodX, foodY));
    }

    private boolean occupied(int x, int y) {
        for (int[] p : snake) {
            if (p[0] == x && p[1] == y) {
                return true;
            }
        }
        return false;
    }

    private void handleKey(int key) {
        switch (key) {
            case KeyEvent.VK_UP, KeyEvent.VK_W -> turn(0, -1);
            case KeyEvent.VK_DOWN, KeyEvent.VK_S -> turn(0, 1);
            case KeyEvent.VK_LEFT, KeyEvent.VK_A -> turn(-1, 0);
            case KeyEvent.VK_RIGHT, KeyEvent.VK_D -> turn(1, 0);
            case KeyEvent.VK_SPACE -> {
                if (gameOver) {
                    reset();
                }
            }
            default -> { }
        }
    }

    private void turn(int newDx, int newDy) {
        if (newDx == -dx && newDy == -dy) {
            return;
        }
        nextDx = newDx;
        nextDy = newDy;
    }

    private void tick() {
        dx = nextDx;
        dy = nextDy;
        int[] head = snake.get(0);
        int nx = head[0] + dx;
        int ny = head[1] + dy;

        boolean eating = nx == foodX && ny == foodY;
        if (nx < 0 || ny < 0 || nx >= COLS || ny >= ROWS || hitsBody(nx, ny, eating)) {
            gameOver = true;
            timer.stop();
            board.repaint();
            return;
        }

        snake.add(0, new int[]{nx, ny});
        if (eating) {
            score++;
            if (snake.size() == COLS * ROWS) {
                gameOver = true;
                timer.stop();
            } else {
                placeFood();
            }
        } else {
            snake.remove(snake.size() - 1);
        }
        board.repaint();
    }

    private boolean hitsBody(int x, int y, boolean growing) {
        int limit = growing ? snake.size() : snake.size() - 1;
        for (int i = 0; i < limit; i++) {
            int[] p = snake.get(i);
            if (p[0] == x && p[1] == y) {
                return true;
            }
        }
        return false;
    }

    private class Board extends JPanel {

        Board() {
            setPreferredSize(new Dimension(COLS * CELL, ROWS * CELL));
            setBackground(Color.BLACK);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.setColor(Color.RED);
            g.fillOval(foodX * CELL, foodY * CELL, CELL, CELL);

            for (int i = 0; i < snake.size(); i++) {
                int[] p = snake.get(i);
                g.setColor(i == 0 ? new Color(120, 255, 120) : new Color(40, 180, 40));
                g.fillRect(p[0] * CELL + 1, p[1] * CELL + 1, CELL - 2, CELL - 2);
            }

            g.setColor(Color.WHITE);
            g.setFont(new Font("Segoe UI", Font.BOLD, 14));
            g.drawString("Score: " + score, 8, 18);

            if (gameOver) {
                g.setFont(new Font("Segoe UI", Font.BOLD, 32));
                g.drawString("Game Over", COLS * CELL / 2 - 85, ROWS * CELL / 2);
                g.setFont(new Font("Segoe UI", Font.PLAIN, 16));
                g.drawString("Press SPACE to restart", COLS * CELL / 2 - 80, ROWS * CELL / 2 + 28);
            }
        }
    }

    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(() -> new SnakeGame().setVisible(true));
    }
}
