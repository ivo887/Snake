package snake.model;

public record Point(int x, int y) {
    public Point step(Direction direction) {
        return new Point(x + direction.dx(), y + direction.dy());
    }
}
