package snake.ui;

import javax.swing.Timer;
import snake.audio.Sound;
import snake.audio.SoundManager;
import snake.model.Direction;
import snake.model.GameEvent;
import snake.model.GameState;
import snake.persistence.Settings;

public class GameController {
    public enum Phase {
        READY,
        RUNNING,
        PAUSED,
        OVER
    }

    private final GameState state;
    private final Settings settings;
    private final SoundManager sounds;
    private final Runnable onChange;
    private final Timer timer;
    private Phase phase;
    private boolean newHighScore;

    // startPhase is READY for a new game, PAUSED for a loaded one
    public GameController(GameState state, Phase startPhase, Settings settings, SoundManager sounds,
                          Runnable onChange) {
        if (startPhase != Phase.READY && startPhase != Phase.PAUSED) {
            throw new IllegalArgumentException("A game can only start READY or PAUSED, not " + startPhase);
        }
        this.state = state;
        this.phase = startPhase;
        this.settings = settings;
        this.sounds = sounds;
        this.onChange = onChange;
        this.timer = new Timer(state.tickDelayMs(), e -> tick());
    }

    public void startNewGame() {
        if (phase == Phase.RUNNING || phase == Phase.PAUSED) {
            return;
        }
        state.reset();
        newHighScore = false;
        phase = Phase.RUNNING;
        timer.setDelay(state.tickDelayMs());
        timer.start();
        sounds.stopMusic();
        sounds.playMusic();
        onChange.run();
    }

    public void togglePause() {
        if (phase == Phase.RUNNING) {
            pause();
        } else if (phase == Phase.PAUSED) {
            phase = Phase.RUNNING;
            timer.setDelay(state.tickDelayMs());
            timer.start();
            sounds.playMusic();
            onChange.run();
        }
    }

    public void pause() {
        if (phase == Phase.RUNNING) {
            phase = Phase.PAUSED;
            timer.stop();
            sounds.pauseMusic();
            onChange.run();
        }
    }

    public void turn(Direction direction) {
        if (phase == Phase.RUNNING) {
            state.turn(direction);
        }
    }

    public void toggleMusic() {
        settings.setMusicOn(!settings.isMusicOn());
        if (settings.isMusicOn() && phase == Phase.RUNNING) {
            sounds.playMusic();
        } else {
            sounds.pauseMusic();
        }
        onChange.run();
    }

    public void toggleEffects() {
        settings.setEffectsOn(!settings.isEffectsOn());
        onChange.run();
    }

    public boolean canSave() {
        return phase == Phase.RUNNING || phase == Phase.PAUSED;
    }

    public void stop() {
        timer.stop();
        sounds.stopMusic();
    }

    public GameState state() {
        return state;
    }

    public Phase phase() {
        return phase;
    }

    public boolean isNewHighScore() {
        return newHighScore;
    }

    private void tick() {
        GameEvent event = state.tick();
        if (event == GameEvent.APPLE) {
            sounds.play(Sound.APPLE);
        } else if (event == GameEvent.BOOSTER) {
            sounds.play(Sound.BOOSTER);
        } else if (event == GameEvent.CRASH || event == GameEvent.WIN) {
            phase = Phase.OVER;
            timer.stop();
            sounds.stopMusic();
            sounds.play(Sound.GAME_OVER);
            newHighScore = settings.recordScore(state.cols(), state.score());
        }
        timer.setDelay(state.tickDelayMs());
        onChange.run();
    }
}
