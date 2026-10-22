package snake.model;

public enum BoardSize {
    SMALL("Small", 15),
    MEDIUM("Medium", 25),
    LARGE("Large", 35);

    private final String label;
    private final int tiles;

    BoardSize(String label, int tiles) {
        this.label = label;
        this.tiles = tiles;
    }

    public int tiles() {
        return tiles;
    }

    @Override
    public String toString() {
        return label + " (" + tiles + "x" + tiles + ")";
    }
}
