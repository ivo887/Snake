package snake.audio;

import jaco.mp3.player.MP3Player;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import snake.persistence.Settings;

public class SoundManager {
    private final Map<Sound, MP3Player> players = new HashMap<>();
    private final Settings settings;

    public SoundManager(File folder, Settings settings) {
        this.settings = settings;
        for (Sound sound : Sound.values()) {
            File file = new File(folder, sound.fileName());
            if (file.exists()) {
                players.put(sound, new MP3Player(file));
            }
        }
        if (players.containsKey(Sound.MUSIC)) {
            players.get(Sound.MUSIC).setRepeat(true);
        }
    }

    public void play(Sound effect) {
        if (settings.isEffectsOn() && players.containsKey(effect)) {
            players.get(effect).play();
        }
    }

    public void playMusic() {
        if (settings.isMusicOn() && players.containsKey(Sound.MUSIC)) {
            players.get(Sound.MUSIC).play();
        }
    }

    public void pauseMusic() {
        if (players.containsKey(Sound.MUSIC)) {
            players.get(Sound.MUSIC).pause();
        }
    }

    public void stopMusic() {
        if (players.containsKey(Sound.MUSIC)) {
            players.get(Sound.MUSIC).stop();
        }
    }
}
