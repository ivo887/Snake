package snake.persistence;

import com.google.gson.Gson;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import snake.model.GameState;

public class SaveManager {
    private static final Gson GSON = new Gson();

    private final File folder;

    public SaveManager(File folder) {
        this.folder = folder;
    }

    public List<SaveInfo> list() {
        List<SaveInfo> saves = new ArrayList<>();
        File[] files = folder.listFiles();
        if (files == null) {
            return saves;
        }
        for (File file : files) {
            String fileName = file.getName();
            if (fileName.endsWith(".json")) {
                GameState game;
                try {
                    game = read(file);
                } catch (InvalidSaveException e) {
                    game = null;
                }
                saves.add(new SaveInfo(fileName.substring(0, fileName.length() - 5), file, game));
            }
        }
        saves.sort((a, b) -> Long.compare(b.file().lastModified(), a.file().lastModified()));
        return saves;
    }

    public boolean exists(String name) {
        return fileFor(name).exists();
    }

    public void save(String name, GameState state) throws IOException {
        folder.mkdirs();
        Files.writeString(fileFor(name).toPath(), GSON.toJson(state));
    }

    public GameState load(SaveInfo save) throws InvalidSaveException {
        return read(save.file());
    }

    public boolean delete(SaveInfo save) {
        return save.file().delete();
    }

    private static GameState read(File file) throws InvalidSaveException {
        GameState state;
        try {
            state = GSON.fromJson(Files.readString(file.toPath()), GameState.class);
        } catch (IOException | RuntimeException e) {
            throw new InvalidSaveException("The save file could not be read.");
        }
        if (state == null || !state.isValid()) {
            throw new InvalidSaveException("The save file is corrupted.");
        }
        return state;
    }

    private File fileFor(String name) {
        return new File(folder, name + ".json");
    }
}
