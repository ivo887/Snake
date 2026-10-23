package snake.model;

import java.util.ArrayList;
import java.util.List;

public class Snake {
    private List<Point> body = new ArrayList<>();
    private Direction direction;
    private Direction nextDirection;

    // for Gson
    private Snake() {
    }

    public Snake(Point head, Direction direction, int length) {
        this.direction = direction;
        this.nextDirection = direction;
        Point cell = head;
        for (int i = 0; i < length; i++) {
            body.add(cell);
            cell = step(cell, opposite(direction));
        }
    }

    // the cell next to "from" in the given direction
    static Point step(Point from, Direction direction) {
        int x = from.x();
        int y = from.y();
        switch (direction) {
            case UP -> y--;
            case DOWN -> y++;
            case LEFT -> x--;
            case RIGHT -> x++;
            default -> { }
        }
        return new Point(x, y);
    }

    static Direction opposite(Direction direction) {
        return switch (direction) {
            case UP -> Direction.DOWN;
            case DOWN -> Direction.UP;
            case LEFT -> Direction.RIGHT;
            case RIGHT -> Direction.LEFT;
        };
    }

    public Point head() {
        return body.get(0);
    }

    public Direction direction() {
        return direction;
    }

    public int length() {
        return body.size();
    }

    public List<Point> body() {
        return List.copyOf(body);
    }

    public boolean occupies(Point cell) {
        return body.contains(cell);
    }

    // only one turn per tick
    public boolean turn(Direction newDirection) {
        if (nextDirection != direction || newDirection == opposite(direction)) {
            return false;
        }
        nextDirection = newDirection;
        return true;
    }

    Direction applyTurn() {
        direction = nextDirection;
        return direction;
    }

    // tail moves away unless growing
    public boolean wouldCollide(Point target, boolean growing) {
        int count = growing ? body.size() : body.size() - 1;
        for (int i = 0; i < count; i++) {
            if (body.get(i).equals(target)) {
                return true;
            }
        }
        return false;
    }

    void moveTo(Point newHead, boolean grow) {
        body.add(0, newHead);
        if (!grow) {
            body.remove(body.size() - 1);
        }
    }

    boolean isValid(int cols, int rows) {
        if (body == null || body.isEmpty() || direction == null || nextDirection == null) {
            return false;
        }
        for (Point p : body) {
            if (p == null || p.x() < 0 || p.y() < 0 || p.x() >= cols || p.y() >= rows) {
                return false;
            }
        }
        return true;
    }
}
