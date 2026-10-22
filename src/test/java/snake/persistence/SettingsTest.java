package snake.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import snake.model.BoardSize;

class SettingsTest {
    @TempDir
    File folder;

    @Test
    void missingFileGivesDefaults() {
        Settings settings = Settings.load(new File(folder, "settings.json"));
        assertTrue(settings.isLooping());
        assertTrue(settings.isMusicOn());
        assertEquals(BoardSize.MEDIUM, settings.getBoardSize());
        assertEquals(0, settings.getHighScore());
    }

    @Test
    void changesAreSaved() {
        File file = new File(folder, "settings.json");
        Settings settings = Settings.load(file);
        settings.setLooping(false);
        settings.setBoardSize(BoardSize.LARGE);
        settings.recordScore(12);

        Settings reloaded = Settings.load(file);
        assertFalse(reloaded.isLooping());
        assertEquals(BoardSize.LARGE, reloaded.getBoardSize());
        assertEquals(12, reloaded.getHighScore());
    }

    @Test
    void onlyHigherScoresReplaceTheHighScore() {
        Settings settings = new Settings();
        assertTrue(settings.recordScore(5));
        assertFalse(settings.recordScore(3));
        assertEquals(5, settings.getHighScore());
    }
}
