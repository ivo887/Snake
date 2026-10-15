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
import jaco.mp3.player.MP3Player;
import java.io.File;


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
    private static int highScore;
    private boolean gameOver;
    private boolean started;
    private boolean paused;
    MP3Player musicplayer = new MP3Player();
    MP3Player appleplayer = new MP3Player();
    MP3Player gameoverplayer = new MP3Player();
    MP3Player boosterplayer = new MP3Player();



    public SnakeGame() {
        super("Snake");
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);
        add(board);
        pack();
        setLocationRelativeTo(null);

        musicplayer.addToPlayList(new File("SoundTrack/Snake Game Soundtrack-Main.mp3"));

        musicplayer.setRepeat(true);

        musicplayer.play();
        appleplayer.addToPlayList(new File("SoundTrack/Snake Game Soundtrack-apple.mp3"));
        gameoverplayer.addToPlayList(new File("SoundTrack/Snake Game Soundtrack-gameover.mp3"));
        boosterplayer.addToPlayList(new File("SoundTrack/Snake Game Soundtrack-booster.mp3"));



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
        paused = false;
        placeFood();
        board.repaint();
    }

    private void start() {
        reset();
        started = true;
        timer.start();
    }

    private void endGame() {
        musicplayer.pause();
        gameOver = true;
        highScore = Math.max(highScore, score);
        gameoverplayer.play();
        timer.stop();
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
        if (key == KeyEvent.VK_ESCAPE) {
            timer.stop();
            new Menu().setVisible(true);
            dispose();
            musicplayer.pause();
            return;
        }
        if (!started || gameOver) {
            if (key == KeyEvent.VK_SPACE) {
                start();
            }
            return;
        }
        if (key == KeyEvent.VK_P) {
            paused = !paused;
            if (paused) {
                timer.stop();
                musicplayer.pause();
            } else {
                timer.start();
                musicplayer.play();
            }
            board.repaint();
            return;
        }
        if (paused) {
            return;
        }
        switch (key) {
            case KeyEvent.VK_UP, KeyEvent.VK_W -> turn(0, -1);
            case KeyEvent.VK_DOWN, KeyEvent.VK_S -> turn(0, 1);
            case KeyEvent.VK_LEFT, KeyEvent.VK_A -> turn(-1, 0);
            case KeyEvent.VK_RIGHT, KeyEvent.VK_D -> turn(1, 0);
            default -> { }
        }
    }

    private void turn(int newDx, int newDy) {
        if (nextDx != dx || nextDy != dy || (newDx == -dx && newDy == -dy)) {
            return;
        }
        nextDx = newDx;
        nextDy = newDy;
    }

    private void tick() {
        dx = nextDx;
        dy = nextDy;
        int[] head = snake.get(0);
        int nx = (head[0] + dx + COLS) % COLS;
        int ny = (head[1] + dy + ROWS) % ROWS;

        boolean eating = nx == foodX && ny == foodY;
        if (checkCollision(nx, ny, eating)) {
            endGame();
            return;
        }

        snake.add(0, new int[]{nx, ny});
        if (eating) {
            score++;
            appleplayer.play();
            if (snake.size() == COLS * ROWS) {
                endGame();
            } else {
                placeFood();
            }
        } else {
            snake.remove(snake.size() - 1);
        }
        board.repaint();
    }

    private boolean checkCollision(int x, int y, boolean growing) {
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
            g.setColor(new Color(30, 30, 30));
            for (int i = 0; i <= COLS; i++) {
                g.drawLine(i * CELL, 0, i * CELL, ROWS * CELL);
            }
            for (int i = 0; i <= ROWS; i++) {
                g.drawLine(0, i * CELL, COLS * CELL, i * CELL);
            }

            if (!started) {
                g.setColor(Color.WHITE);
                g.setFont(new Font("Segoe UI", Font.BOLD, 32));
                g.drawString("Snake", COLS * CELL / 2 - 45, ROWS * CELL / 2);
                g.setFont(new Font("Segoe UI", Font.PLAIN, 16));
                g.drawString("Press SPACE to start", COLS * CELL / 2 - 75, ROWS * CELL / 2 + 28);
                return;
            }

            g.setColor(Color.RED);
            g.fillOval(foodX * CELL, foodY * CELL, CELL, CELL);

            for (int i = 0; i < snake.size(); i++) {
                int[] p = snake.get(i);
                g.setColor(i == 0 ? new Color(120, 255, 120) : new Color(40, 180, 40));
                g.fillRect(p[0] * CELL + 1, p[1] * CELL + 1, CELL - 2, CELL - 2);
            }

            g.setColor(Color.WHITE);
            g.setFont(new Font("Segoe UI", Font.BOLD, 14));
            g.drawString("Score: " + score + "   High: " + highScore, 8, 18);

            if (paused) {
                g.setFont(new Font("Segoe UI", Font.BOLD, 32));
                g.drawString("Paused", COLS * CELL / 2 - 55, ROWS * CELL / 2);
            }

            if (gameOver) {
                g.setFont(new Font("Segoe UI", Font.BOLD, 32));
                g.drawString("Game Over", COLS * CELL / 2 - 85, ROWS * CELL / 2);
                g.setFont(new Font("Segoe UI", Font.PLAIN, 16));
                g.drawString("Final score: " + score, COLS * CELL / 2 - 45, ROWS * CELL / 2 + 28);
                g.drawString("SPACE: new game   ESC: menu", COLS * CELL / 2 - 105, ROWS * CELL / 2 + 52);
            }
        }
    }

    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(() -> new SnakeGame().setVisible(true));
    }
}
