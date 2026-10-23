package snake.persistence;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import snake.model.GameState;

public class SaveInfo {
    private final String name;
    private final File file;
    private final GameState game; // null when the file could not be read

    public SaveInfo(String name, File file, GameState game) {
        this.name = name;
        this.file = file;
        this.game = game;
    }

    public String name() {
        return name;
    }

    public File file() {
        return file;
    }

    public GameState game() {
        return game;
    }

    @Override
    public String toString() {
        String date = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date(file.lastModified()));
        String details = "unreadable";
        if (game != null) {
            details = game.cols() + "x" + game.rows() + ", score " + game.score();
        }
        return name + "   " + details + "   (" + date + ")";
    }
}
