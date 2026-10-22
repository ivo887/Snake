# Snake

A Snake game written in Java with Swing.

**Plan page:** https://ivo887.github.io/Snake/ (step-by-step plan and product backlog)

## Features

- Change the direction of the snake with W, A, S, D or the arrow keys.
- Eat apples to grow and increase the score. Eat all the apples to fill the board and win the game.
- Boosters randomly appear and change the game speed for 5 seconds and the background colour.
  The remaining time is shown in the toolbar.
- Looping borders (the snake wraps around) or walls, selected in the menu.
- 3 board sizes: 15x15, 25x25 and 35x35.
- Pause and resume the game. The booster effect is paused as well.
- Background music and sound effects for apples, boosters and game over, each with a mute key.
- Save the game with a name and load it from the menu. The saves are ordered by the newest first, with their board size, score and date,
  can be deleted, and corrupted or missing files are handled not to crash the game.
- Settings and a high score for each board size are saved between the launches.

## Controls

| Key | Action |
|-----|--------|
| W / Up arrow | Move up |
| A / Left arrow | Move left |
| S / Down arrow | Move down |
| D / Right arrow | Move right |
| SPACE | New game |
| P | Pause / resume |
| M | Toggle music |
| N | Toggle sound effects |
| ESC | Return to menu (asks to save if in-game) |

In the menu, press Enter to create a new game, double-click a save to load it, or select a save and press Delete to delete it.

## Requirements

| What | Version | Notes |
|------|---------|-------|
| JDK | 17 or newer | JDK (Java Development Kit) is required, not JRE. Check with `java -version` |
| Maven | 3.6 or newer | Optional if you use IntelliJ IDEA, which includes Maven. Check with `mvn -version` |
| Internet connection | First build only | Maven downloads Gson and JUnit on the first build, then it caches them |
| Speakers/headphones | Optional | The game runs without sound if not available |

The libraries are described in `pom.xml`, so they don't need to be installed separately:

| What | It's used for | Where it's from |
|------|---------------|-----------------|
| Gson 2.11.0 | Save files and settings (JSON) | Downloaded by Maven |
| JUnit 5.11.4 | Unit tests | Downloaded by Maven |
| jaco-mp3-player 0.9.3 | Music and sound effects | Included in `lib/` |

## Setup

Get JDK 17 or newer, for example, [Eclipse Temurin](https://adoptium.net/), and install it. On Windows, make sure to select
"Set JAVA_HOME" and "Add to PATH" in the installer, then open a new terminal and type `java -version` to make sure it outputs
JDK 17 or newer.

Get the project with git: `git clone https://github.com/ivo887/Snake.git`, or extract the downloaded ZIP file.
Make sure to preserve the folder structure: the game requires `lib/` and `SoundTrack/` to be in the same directory as `pom.xml`.

Choose between building the project with IntelliJ IDEA (easiest) or with Maven in a terminal.

### A. IntelliJ IDEA

1. **File → Open** → select the folder with the project (not the Snake subfolder). If prompted, select **Open as Project** and **Trust Project**.
2. IntelliJ IDEA detects `pom.xml` and imports it as a Maven project. If the libraries are not detected, open the **Maven** tool window on the right and click **Reload All Maven Projects**.
3. **File → Project Structure → Project** and set the **SDK** to a JDK 17 or newer (see **Add SDK → Download JDK** if nothing is listed).
4. Open `src/main/java/snake/Main.java` and click the green ▶ next to `main`.
5. To run the tests, right-click `src/test/java/` and select **Run 'All Tests'**.

The working directory for the run configurations should be the project folder (not a subfolder), or the game will not be able to find `SoundTrack/` and will run without sound.

### B. Terminal (Maven)

1. Download Maven from [maven.apache.org](https://maven.apache.org/download.cgi), extract it somewhere, and add it to PATH. Alternatively, install with a package manager: `winget install Apache.Maven`, `brew install maven`, `sudo apt install maven`.
2. In the project folder, run `mvn compile exec:java`. The first time, it downloads the dependencies and may take a while, but afterwards, the menu should open.
3. Run the tests with `mvn test`. All tests should pass (`BUILD SUCCESS`), and the reports will be in `target/surefire-reports/`.

## Files created by the game

| Location | Contents | Notes |
|----------|----------|-------|
| `settings.json` | Menu settings (borders, board size, music, effects) and the high score per board size. | Delete the file |
| `saves/*.json` | Saved games. | Delete the files in the menu |

## Common issues

| Problem | Solution |
|---------|----------|
| `mvn` is not recognised | Maven is not in your PATH, so install it (see step 1 for B), or use IntelliJ IDEA (A). |
| `release version 17 not supported` or `invalid target release: 17` | Maven uses an old JDK, so set `JAVA_HOME` to a JDK 17 or newer, or see notes for B. |
| `package jaco.mp3.player does not exist` | `lib/jaco-mp3-player-0.9.3.jar` is missing, so re-clone the repository or restore this file. |
| The game runs but has no sound | Run it from the project folder so that it can find `SoundTrack/` (see notes for A). Make sure that music and effects are enabled in the menu, or press M/N in-game. |
| Warning: `'systemPath' ... should not point at files within the project directory` | Safe to ignore. It's how Maven handles the MP3 library `lib/jaco-mp3-player-0.9.3.jar`. The build still works, and the warning disappears after you delete the file. |
| An old save is shown as "unreadable" | The file is probably corrupted or modified, so delete it in the menu. |

## Project structure

The code is split into packages by responsibility. Arrows show which packages import which; the model has no dependencies, so that the game logic can be tested without a window.

```
snake.ui  ───►  snake.model  ◄───  snake.persistence
   │                                     ▲
   ├──────────►  snake.audio  ───────────┘ (reads Settings)
   └─────────────────────────────────────┘
```

| Package | Class | Responsibility |
|---------|-------|----------------|
| `snake` | `Main` | Entry point; sets the look and feel and opens the menu |
| | `AppContext` | Holds the shared services (settings, saves, sounds) injected into windows |
| `snake.model` | `GameState` | All game logic: movement, eating, boosters, collisions, winning |
| | `Snake` | Snake's body and direction |
| | `Point` | An immutable position on the board (a `record`) |
| | `Direction` | Directions and offsets (an `enum`) |
| | `BoosterEffect` | Fast / slow effect with a speed multiplier |
| | `GameEvent` | Describes what happens on each game tick (apple, booster, etc) |
| | `BoardSize` | Board sizes offered in the menu |
| `snake.ui` | `MenuFrame` | Menu: new game, load game, settings |
| | `GameFrame` | Game window: toolbar, board, keys, save dialog |
| | `BoardPanel` | Draws the board and the start/pause/game over screens |
| | `GameController` | Updates the game state on a `javax.swing.Timer`, handles pause/game over, plays sounds |
| | `Theme` | Colours and fonts |
| `snake.persistence` | `SaveManager` | Lists, saves, loads and deletes JSON save files |
| | `Settings` | Menu settings and high scores per board size, saved to `settings.json` |
| | `SaveInfo`, `InvalidSaveException` | Save list item, error while loading |
| `snake.audio` | `SoundManager` | Plays music and effects on separate `MP3Player`s |
| | `Sound` | Describes a sound and its MP3 file |

## Design decisions

- **Model-view-controller separation.** `GameState` knows nothing of the UI and only implements the rules; `BoardPanel` only draws the board and does not receive input; `GameController` connects them and updates the game on a timer. `GameState.tick()` returns a `GameEvent`, which allows the controller to choose what sound to play without coupling to audio.
- **Encapsulation.** Fields are private by default. The snake's body is a list of `Point`s, an immutable `record`, and the `Snake.body()` method returns a defensive copy, so that other classes cannot modify the game state.
- **Dependency injection.** Instead of having the `GameFrame` create its own settings, saves or sounds, they are created once in `AppContext` and passed to the frame when it's built. This allows `Settings` and `SaveManager` to be faked in tests.
- **Enums with behaviours.** `Direction.isOpposite()`, `BoosterEffect.tickDelayMs()` and `Sound.fileName()` implement logic specific to each value, which reduces "fragile duplication" and replaces `switch` statements and constants.
- **Effects last for a number of ticks.** A fast boost or slow down lasts for 50 or 16 ticks, respectively, rather than a wall time of 5 seconds. This makes the effect pause when the game is paused and survive saving/loading.

## Independent topics

### 1. Saving game states to files

The game saves the state and settings to files in JSON format with the [Gson](https://github.com/google/gson) library. See the [Gson guide](https://github.com/google/gson/blob/main/UserGuide.md) for more information.

- `GSON.toJson(state)` serializes a `GameState` into a JSON string, and `GSON.fromJson(text, GameState.class)` deserialize it back. Gson uses the private fields directly.
- Fields marked `transient` (the random number generator, the actual settings file location) are not saved.
- `GameState` and `Snake` have a private empty constructor so that Gson can instantiate them.
- After deserializing, `GameState.isValid()` checks that the snake, apple and booster are within the bounds. If not, it throws an exception, which the menu catches to show an error rather than crashing.
- Save names are sanitized to prevent writing to an arbitrary place by typing `../some_name`.

Sample save file:

```json
{
  "cols": 25,
  "rows": 25,
  "looping": true,
  "snake": {
    "body": [
      {"x": 14, "y": 12},
      {"x": 13, "y": 12},
      {"x": 12, "y": 12}
    ],
    "direction": "RIGHT",
    "nextDirection": "RIGHT"
  },
  "apple": {"x": 3, "y": 20},
  "booster": {"x": 18, "y": 4},
  "score": 0,
  "over": false,
  "won": false,
  "effectTicksLeft": 0
}
```

### 2. Unit tests

The game logic and persistence layer are covered with unit tests in `src/test/java`. `SnakeTest` only tests the moves, growing, turning and collisions; the actual rendering is handled in `GameController`. Running the tests with `mvn test` should show no failures.

Testing approach:

- `SnakeTest` tests the movement and collisions with `Snake.move(Direction)`
- `GameStateTest` tests wrapping around the board, collisions, eating, boosters and winning; all package-private setters allow setting up the state
- `SaveManagerTest`, `SettingsTest` use `@TempDir` from JUnit 5 to write to a temporary folder, not the real `saves/`
- `assertThrows(IllegalStateException.class, ...)` checks that the menu correctly rejects a corrupted save file

### Other features: MP3 audio playback

Music and effects are played from MP3 files with the jaco-mp3-player library. Each sound has its own `MP3Player`, so that effects do not interfere with music, and missing files are simply skipped.
