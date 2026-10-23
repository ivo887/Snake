package snake.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameState {
    public static final int NORMAL_DELAY_MS = 150;
    public static final int FAST_DELAY_MS = 100;
    public static final int SLOW_DELAY_MS = 300;
    public static final int EFFECT_DURATION_MS = 5000;

    private int cols;
    private int rows;
    private boolean looping;
    private Snake snake;
    private Point apple;
    private Point booster;
    private int score;
    private boolean over;
    private boolean won;
    private BoosterEffect effect;
    private int effectTicksLeft;
    private transient Random random = new Random();

    // for Gson
    private GameState() {
    }

    public GameState(int cols, int rows, boolean looping) {
        this.cols = cols;
        this.rows = rows;
        this.looping = looping;
        reset();
    }

    public void reset() {
        snake = new Snake(new Point(cols / 2, rows / 2), Direction.RIGHT, 3);
        score = 0;
        over = false;
        won = false;
        effect = null;
        effectTicksLeft = 0;
        apple = null;
        booster = null;
        apple = randomFreeCell();
        booster = randomFreeCell();
    }

    public void turn(Direction direction) {
        if (!over) {
            snake.turn(direction);
        }
    }

    public GameEvent tick() {
        if (over) {
            return GameEvent.NONE;
        }
        if (effect != null) {
            effectTicksLeft--;
            if (effectTicksLeft <= 0) {
                effect = null;
            }
        }

        Point next = Snake.step(snake.head(), snake.applyTurn());
        if (looping) {
            next = new Point((next.x() + cols) % cols, (next.y() + rows) % rows);
        }
        boolean eating = next.equals(apple);
        if (!isInside(next) || snake.wouldCollide(next, eating)) {
            over = true;
            return GameEvent.CRASH;
        }
        snake.moveTo(next, eating);

        if (eating) {
            score++;
            apple = randomFreeCell();
            if (apple == null) {
                over = true;
                won = true;
                return GameEvent.WIN;
            }
            if (booster == null) {
                booster = randomFreeCell();
            }
            return GameEvent.APPLE;
        }
        if (next.equals(booster)) {
            booster = randomFreeCell();
            startEffect(random.nextBoolean() ? BoosterEffect.FAST : BoosterEffect.SLOW);
            return GameEvent.BOOSTER;
        }
        return GameEvent.NONE;
    }

    public int tickDelayMs() {
        if (effect == BoosterEffect.FAST) {
            return FAST_DELAY_MS;
        }
        if (effect == BoosterEffect.SLOW) {
            return SLOW_DELAY_MS;
        }
        return NORMAL_DELAY_MS;
    }

    // checks a game loaded from a file
    public boolean isValid() {
        if (cols < 4 || rows < 1 || cols > 100 || rows > 100) {
            return false;
        }
        if (snake == null || !snake.isValid(cols, rows)) {
            return false;
        }
        if (apple == null) {
            return won;
        }
        return isInside(apple) && (booster == null || isInside(booster)) && score >= 0;
    }

    public boolean isInside(Point cell) {
        return cell.x() >= 0 && cell.y() >= 0 && cell.x() < cols && cell.y() < rows;
    }

    public int cols() {
        return cols;
    }

    public int rows() {
        return rows;
    }

    public boolean isLooping() {
        return looping;
    }

    public Snake snake() {
        return snake;
    }

    public Point apple() {
        return apple;
    }

    public Point booster() {
        return booster;
    }

    public int score() {
        return score;
    }

    public boolean isOver() {
        return over;
    }

    public boolean isWon() {
        return won;
    }

    public BoosterEffect effect() {
        return effect;
    }

    public int effectSecondsLeft() {
        if (effect == null) {
            return 0;
        }
        return effectTicksLeft * tickDelayMs() / 1000 + 1;
    }

    void startEffect(BoosterEffect newEffect) {
        effect = newEffect;
        effectTicksLeft = EFFECT_DURATION_MS / tickDelayMs();
    }

    void setApple(Point apple) {
        this.apple = apple;
    }

    void setBooster(Point booster) {
        this.booster = booster;
    }

    void setSnake(Snake snake) {
        this.snake = snake;
    }

    private Point randomFreeCell() {
        List<Point> free = new ArrayList<>();
        for (int x = 0; x < cols; x++) {
            for (int y = 0; y < rows; y++) {
                Point cell = new Point(x, y);
                if (!snake.occupies(cell) && !cell.equals(apple) && !cell.equals(booster)) {
                    free.add(cell);
                }
            }
        }
        if (free.isEmpty()) {
            return null;
        }
        return free.get(random.nextInt(free.size()));
    }
}
