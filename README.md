# Snake

A Snake game written in Java with Swing.

**Plan page:** https://ivo887.github.io/Snake/ (step-by-step plan and product backlog)

## Features

- Steer the snake with W, A, S, D or the arrow keys.
- Apples make the snake grow and raise the score. Filling the whole board wins the game.
- Boosters randomly speed the game up or slow it down for 5 seconds and change the background colour.
  The remaining time is shown in the toolbar.
- Looping borders (the snake wraps around) or solid walls, chosen in the menu.
- Three board sizes: 15x15, 25x25 and 35x35.
- Pause and resume. A booster effect is frozen while paused.
- Background music and sound effects for apples, boosters and game over, each with its own mute key.
- Save a game under a name and load it later from the menu. Saves are listed newest first with their board size, score and date,
  can be deleted, and corrupted or missing files show an error instead of crashing.
- Settings and a separate high score for each board size are remembered between runs.

## Controls

| Key | Action |
|-----|--------|
| W / Up arrow | Up |
| A / Left arrow | Left |
| S / Down arrow | Down |
| D / Right arrow | Right |
| SPACE | Start a new game |
| P | Pause / resume |
| M | Music on / off |
| N | Sound effects on / off |
| ESC | Back to the menu (offers to save first) |

In the menu, press Enter to start a new game, double-click a save to load it, or select a save and press
Delete to remove it.

## Requirements

| What | Version | Notes |
|------|---------|-------|
| JDK | 17 or newer | A full JDK, not just a JRE. Check with `java -version`. |
| Maven | 3.6 or newer | Optional if you use IntelliJ IDEA, which has Maven built in. Check with `mvn -version`. |
| Internet connection | first build only | Maven downloads Gson and JUnit once and then caches them. |
| Speakers / headphones | optional | The game runs without sound if no audio device is available. |

The libraries are set up by `pom.xml`, so you do not have to install them yourself:

| Library | Used for | Where it comes from |
|---------|----------|---------------------|
| Gson 2.11.0 | Save files and settings (JSON) | Downloaded by Maven |
| JUnit 5.11.4 | Unit tests | Downloaded by Maven |
| jaco-mp3-player 0.9.3 | Music and sound effects | Included in `lib/` (not on Maven Central) |

## Setup

1. **Install a JDK (17 or newer).** For example [Eclipse Temurin](https://adoptium.net/). On Windows, tick
   "Set JAVA_HOME" and "Add to PATH" in the installer. Open a new terminal and check that `java -version`
   prints 17 or higher.
2. **Get the project.** Clone it with `git clone https://github.com/ivo887/Snake.git`, or unzip the submitted ZIP.
   Keep the folder layout as it is: the game needs `lib/` and `SoundTrack/` next to `pom.xml`.
3. **Choose how to build it:** IntelliJ IDEA (easiest, no extra installs) or Maven from a terminal.

### Option A: IntelliJ IDEA

1. **File → Open** and pick the project folder (the one with `pom.xml`). If asked, choose **Open as Project**
   and **Trust Project**.
2. IntelliJ sees `pom.xml` and imports it as a Maven project. If the libraries do not load, open the **Maven**
   tool window (right side) and click **Reload All Maven Projects**.
3. **File → Project Structure → Project** and set **SDK** to a JDK 17 or newer (use **Add SDK → Download JDK**
   if none is listed).
4. Open `src/main/java/snake/Main.java` and click the green ▶ next to `main`.
5. To run the tests, right-click `src/test/java` and choose **Run 'All Tests'**.

The run configuration's working directory must be the project folder (IntelliJ's default). Otherwise the game
cannot find `SoundTrack/` and runs without sound.

### Option B: Maven in a terminal

1. Install Maven: download it from [maven.apache.org](https://maven.apache.org/download.cgi), unzip it, and add its
   `bin` folder to your `PATH`. Package managers also work: `winget install Apache.Maven`, `brew install maven`,
   `sudo apt install maven`.
2. Open a terminal **in the project folder** and run:

```
mvn compile exec:java
```

The first run downloads the dependencies and can take a minute. After that the menu window opens.

Run the tests with:

```
mvn test
```

All tests should pass (`BUILD SUCCESS`). Results are written to `target/surefire-reports/`.

## Files the game creates

The game writes these in the folder it was started from. Both are ignored by git.

| File | Contents | To reset |
|------|----------|----------|
| `settings.json` | Menu options (borders, board size, music, effects) and the high score per board size | Delete the file |
| `saves/*.json` | One file per saved game | Delete the save in the menu, or delete the file |

## Troubleshooting

| Problem | Fix |
|---------|-----|
| `mvn` is not recognized | Maven is not on your `PATH`. Install it (Option B, step 1) and open a new terminal, or use IntelliJ (Option A). |
| `release version 17 not supported` or `invalid target release: 17` | Maven is using an older JDK. Point `JAVA_HOME` at a JDK 17+ and check with `mvn -version`. |
| `package jaco.mp3.player does not exist` | `lib/jaco-mp3-player-0.9.3.jar` is missing. Restore the `lib/` folder. |
| The game runs but there is no sound | Start it from the project folder so that `SoundTrack/` can be found. Check that music and effects are ticked in the menu (or press M / N in the game). |
| Warning: `'systemPath' ... should not point at files within the project directory` | Harmless. This is how Maven reports the MP3 library in `lib/`; the build still succeeds. |
| An old save shows as "unreadable" | The file is broken or was edited by hand. Delete it from the menu. |

## Project structure

The code is split into packages by responsibility. The arrows show which package uses which; the model depends on
nothing else, so the game rules can be tested without a window.

```
snake.ui  ───►  snake.model  ◄───  snake.persistence
   │                                     ▲
   ├──────────►  snake.audio  ───────────┘ (reads Settings)
   └─────────────────────────────────────┘
```

| Package | Class | Responsibility |
|---------|-------|----------------|
| `snake` | `Main` | Entry point; sets the look and feel and opens the menu |
| | `AppContext` | Holds the shared services (settings, saves, sounds) handed to each window |
| `snake.model` | `GameState` | All game rules: moving, eating, boosters, collisions, winning |
| | `Snake` | The body and direction |
| | `Point` | Immutable board position (a `record`) |
| | `Direction` | The four directions and their offsets (an `enum`) |
| | `BoosterEffect` | Fast / slow effect with its speed |
| | `GameEvent` | What happened on a tick (apple, booster, crash, win) |
| | `BoardSize` | The board sizes offered in the menu |
| `snake.ui` | `MenuFrame` | Main menu: new game, saved games list, settings |
| | `GameFrame` | Game window: toolbar, board, keys, save dialog |
| | `BoardPanel` | Draws the board and the start / pause / game-over screens |
| | `GameController` | Runs the game timer, pause and game over, and plays sounds for events |
| | `Theme` | Shared colours and fonts |
| `snake.persistence` | `SaveManager` | Lists, saves, loads and deletes JSON save files |
| | `Settings` | Options and high scores per board size, saved to `settings.json` |
| | `SaveInfo`, `InvalidSaveException` | Save list entry, load error |
| `snake.audio` | `SoundManager` | Plays music and effects, each on its own MP3 player |
| | `Sound` | The sounds and their MP3 files |

### Design decisions

- **Model, view, controller.** `GameState` knows the rules but nothing about Swing; `BoardPanel` only draws;
  `GameController` connects the two with a `javax.swing.Timer`. `GameState.tick()` returns a `GameEvent`, so the
  controller decides what sound to play without the model knowing about audio.
- **Encapsulation.** Fields are private. Positions are an immutable `Point` record rather than `int[]`, and
  `Snake.body()` returns a read-only copy, so other classes cannot corrupt the game state.
- **Dependency injection instead of globals.** Settings, saves and sounds are created once in `AppContext` and
  passed to each window. Tests create their own `Settings` and `SaveManager` in a temporary folder.
- **Enums with behaviour.** `Direction.isOpposite()`, `BoosterEffect.tickDelayMs()` and `Sound.fileName()` keep data and
  the logic that uses it together and replace `switch` statements and magic numbers.
- **Effects measured in ticks.** A booster lasts a number of ticks (50 fast, 16 slow) instead of a separate wall-clock
  timer, so pausing freezes it and it survives a save and load.

## Independent topics

### 1. Saving to files with JSON (Gson)

Games and settings are stored as JSON files using the [Gson](https://github.com/google/gson) library.

- `GSON.toJson(state)` turns a `GameState` into a JSON string, and `GSON.fromJson(text, GameState.class)` turns it
  back. Gson reads and writes the private fields directly.
- Fields marked `transient` (the random generator, the settings file path) are not saved.
- `GameState` and `Snake` have an empty private constructor so Gson can create them.
- After loading, `GameState.isValid()` checks that the snake, apple and booster are inside the board. A broken or
  edited file shows an error in the menu instead of crashing the game.
- Save names are cleaned so a name like `../x` cannot write outside the `saves/` folder.

Example save file (formatted here for readability):

```json
{
  "cols": 25,
  "rows": 25,
  "looping": true,
  "snake": {
    "body": [{"x": 14, "y": 12}, {"x": 13, "y": 12}, {"x": 12, "y": 12}],
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

### 2. Unit testing with JUnit 5

The game rules and the saving code are tested with JUnit 5 in `src/test/java` (run with `mvn test`).
The windows are not tested; keeping the rules out of the Swing classes is what makes them easy to test.

- `SnakeTest` checks moving, growing, turning and collisions.
- `GameStateTest` checks wrapping, crashing, eating apples, boosters and winning. Package-private setters let a test
  put the apple or booster exactly where it needs them.
- `SaveManagerTest` and `SettingsTest` use `@TempDir`, a temporary folder from JUnit, so the tests never touch the
  real `saves/` folder.
- `assertThrows` checks that a broken save file is rejected.

### Also used: MP3 audio

Music and effects are played with the `jaco-mp3-player` library from `lib/`. Every sound has its own `MP3Player`
so an effect does not stop the music, and missing sound files are skipped so the game still runs.
