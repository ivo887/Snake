package snake.persistence;

import com.google.gson.Gson;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import snake.model.BoardSize;

public class Settings {
    private static final Gson GSON = new Gson();

    private boolean looping = true;
    private boolean musicOn = true;
    private boolean effectsOn = true;
    private BoardSize boardSize = BoardSize.MEDIUM;
    private int highScore;
    private transient File file;

    public static Settings load(File file) {
        Settings settings = null;
        try {
            settings = GSON.fromJson(Files.readString(file.toPath()), Settings.class);
        } catch (IOException | RuntimeException e) {
            // missing or broken file: use defaults
        }
        if (settings == null) {
            settings = new Settings();
        }
        if (settings.boardSize == null) {
            settings.boardSize = BoardSize.MEDIUM;
        }
        settings.file = file;
        return settings;
    }

    public boolean isLooping() {
        return looping;
    }

    public void setLooping(boolean looping) {
        this.looping = looping;
        save();
    }

    public boolean isMusicOn() {
        return musicOn;
    }

    public void setMusicOn(boolean musicOn) {
        this.musicOn = musicOn;
        save();
    }

    public boolean isEffectsOn() {
        return effectsOn;
    }

    public void setEffectsOn(boolean effectsOn) {
        this.effectsOn = effectsOn;
        save();
    }

    public BoardSize getBoardSize() {
        return boardSize;
    }

    public void setBoardSize(BoardSize boardSize) {
        this.boardSize = boardSize;
        save();
    }

    public int getHighScore() {
        return highScore;
    }

    public boolean recordScore(int score) {
        if (score <= highScore) {
            return false;
        }
        highScore = score;
        save();
        return true;
    }

    private void save() {
        if (file == null) {
            return;
        }
        try {
            Files.writeString(file.toPath(), GSON.toJson(this));
        } catch (IOException e) {
            System.err.println("Could not save settings: " + e.getMessage());
        }
    }
}
