package snake.model;

public enum BoosterEffect {
    FAST(100),
    SLOW(300);

    private final int tickDelayMs;

    BoosterEffect(int tickDelayMs) {
        this.tickDelayMs = tickDelayMs;
    }

    public int tickDelayMs() {
        return tickDelayMs;
    }
}
