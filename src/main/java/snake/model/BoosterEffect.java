package snake.model;

public enum BoosterEffect {
    FAST("Speed up", 100),
    SLOW("Slow down", 300);

    private final String label;
    private final int tickDelayMs;

    BoosterEffect(String label, int tickDelayMs) {
        this.label = label;
        this.tickDelayMs = tickDelayMs;
    }

    public String label() {
        return label;
    }

    public int tickDelayMs() {
        return tickDelayMs;
    }
}
