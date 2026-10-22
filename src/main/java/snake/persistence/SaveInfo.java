package snake.persistence;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import snake.model.GameState;

// game is null when the file could not be read
public record SaveInfo(String name, File file, GameState game) {
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
