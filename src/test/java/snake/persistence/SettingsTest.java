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
        assertEquals(0, settings.getHighScore(25));
    }

    @Test
    void changesAreSaved() {
        File file = new File(folder, "settings.json");
        Settings settings = Settings.load(file);
        settings.setLooping(false);
        settings.setBoardSize(BoardSize.LARGE);
        settings.recordScore(35, 12);

        Settings reloaded = Settings.load(file);
        assertFalse(reloaded.isLooping());
        assertEquals(BoardSize.LARGE, reloaded.getBoardSize());
        assertEquals(12, reloaded.getHighScore(35));
    }

    @Test
    void onlyHigherScoresReplaceTheHighScore() {
        Settings settings = new Settings();
        assertTrue(settings.recordScore(25, 5));
        assertFalse(settings.recordScore(25, 3));
        assertEquals(5, settings.getHighScore(25));
    }

    @Test
    void eachBoardSizeHasItsOwnHighScore() {
        Settings settings = new Settings();
        settings.recordScore(15, 20);
        assertTrue(settings.recordScore(35, 4));
        assertEquals(20, settings.getHighScore(15));
        assertEquals(4, settings.getHighScore(35));
        assertEquals(0, settings.getHighScore(25));
    }
}
