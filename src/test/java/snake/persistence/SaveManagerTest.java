package snake.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.nio.file.Files;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import snake.model.GameState;

class SaveManagerTest {
    @TempDir
    File folder;

    @Test
    void listIsEmptyBeforeTheFirstSave() {
        SaveManager saves = new SaveManager(new File(folder, "saves"));
        assertTrue(saves.list().isEmpty());
    }

    @Test
    void saveAndLoadRestoresTheGame() throws Exception {
        SaveManager saves = new SaveManager(folder);
        GameState game = new GameState(12, 8, false);
        game.tick();

        saves.save("my game", game);
        SaveInfo save = saves.list().get(0);
        GameState loaded = saves.load(save);

        assertEquals("my game", save.name());
        assertEquals(12, loaded.cols());
        assertFalse(loaded.isLooping());
        assertEquals(game.snake().body(), loaded.snake().body());
        assertEquals(game.apple(), loaded.apple());
        assertEquals(game.score(), loaded.score());
    }

    @Test
    void loadedGameCanBePlayed() throws Exception {
        SaveManager saves = new SaveManager(folder);
        saves.save("a", new GameState(10, 10, true));
        GameState loaded = saves.load(saves.list().get(0));
        loaded.tick();
        assertEquals(3, loaded.snake().length());
    }

    @Test
    void corruptedSaveIsRejected() throws Exception {
        SaveManager saves = new SaveManager(folder);
        Files.writeString(new File(folder, "broken.json").toPath(), "not json at all");
        SaveInfo save = saves.list().get(0);
        assertThrows(InvalidSaveException.class, () -> saves.load(save));
    }

    @Test
    void saveOutsideTheBoardIsRejected() throws Exception {
        SaveManager saves = new SaveManager(folder);
        String json = "{\"cols\": 10, \"rows\": 10, \"snake\": {\"body\": [{\"x\": 50, \"y\": 50}]}}";
        Files.writeString(new File(folder, "broken.json").toPath(), json);
        SaveInfo save = saves.list().get(0);
        assertThrows(InvalidSaveException.class, () -> saves.load(save));
    }

    @Test
    void listShowsBoardSizeAndScore() throws Exception {
        SaveManager saves = new SaveManager(folder);
        saves.save("big", new GameState(35, 35, true));
        Files.writeString(new File(folder, "broken.json").toPath(), "not json at all");

        for (SaveInfo save : saves.list()) {
            if (save.name().equals("big")) {
                assertTrue(save.toString().contains("35x35, score 0"));
            } else {
                assertNull(save.game());
                assertTrue(save.toString().contains("unreadable"));
            }
        }
    }

    @Test
    void deleteRemovesTheSave() throws Exception {
        SaveManager saves = new SaveManager(folder);
        saves.save("gone", new GameState(10, 10, true));
        assertTrue(saves.exists("gone"));
        saves.delete(saves.list().get(0));
        assertTrue(saves.list().isEmpty());
    }
}
