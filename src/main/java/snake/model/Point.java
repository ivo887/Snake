package snake.model;

public class Point {
    private int x;
    private int y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int x() {
        return x;
    }

    public int y() {
        return y;
    }

    // needed so List.contains and assertEquals compare by position
    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Point)) {
            return false;
        }
        Point p = (Point) other;
        return x == p.x && y == p.y;
    }

    @Override
    public int hashCode() {
        return 31 * x + y;
    }
}
