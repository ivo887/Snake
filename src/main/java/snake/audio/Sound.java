package snake.audio;

public enum Sound {
    MUSIC("Snake Game Soundtrack-Main.mp3"),
    APPLE("Snake Game Soundtrack-apple.mp3"),
    BOOSTER("Snake Game Soundtrack-booster.mp3"),
    GAME_OVER("Snake Game Soundtrack-gameover.mp3");

    private final String fileName;

    Sound(String fileName) {
        this.fileName = fileName;
    }

    public String fileName() {
        return fileName;
    }
}
