package snake;

import java.io.File;
import snake.audio.SoundManager;
import snake.persistence.SaveManager;
import snake.persistence.Settings;

public record AppContext(Settings settings, SaveManager saves, SoundManager sounds) {
    public static AppContext createDefault() {
        Settings settings = Settings.load(new File("settings.json"));
        return new AppContext(settings, new SaveManager(new File("saves")),
                new SoundManager(new File("SoundTrack"), settings));
    }
}
