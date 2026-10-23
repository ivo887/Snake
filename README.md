# Snake

A Snake game written in Java with Swing.

Plan page (step-by-step plan and product backlog): https://ivo887.github.io/Snake/

Contents:

1. Features
2. Controls
3. Requirements
4. Setup and running (IntelliJ IDEA, terminal, Eclipse, VS Code, NetBeans, without Maven)
5. Files created by the game
6. Common issues
7. Project structure
8. Design decisions

## 1. Features

- Change the direction of the snake with W, A, S, D or the arrow keys.
- Eat apples to grow and increase the score. Eat all the apples to fill the board and win the game.
- Boosters randomly appear and change the game speed for 5 seconds and the background colour.
  The remaining time is shown in the toolbar.
- Looping borders (the snake wraps around) or walls, selected in the menu.
- 3 board sizes: 15x15, 25x25 and 35x35.
- Pause and resume the game. The booster effect is paused as well.
- Background music and sound effects for apples, boosters and game over, each with a mute key.
- Save the game with a name and load it from the menu. The saves are ordered by the newest first,
  with their board size, score and date, can be deleted, and corrupted or missing files are
  handled not to crash the game.
- Settings and a high score for each board size are saved between the launches.

## 2. Controls

| Key             | Action                                   |
|-----------------|------------------------------------------|
| W / Up arrow    | Move up                                  |
| A / Left arrow  | Move left                                |
| S / Down arrow  | Move down                                |
| D / Right arrow | Move right                               |
| SPACE           | New game                                 |
| P               | Pause / resume                           |
| M               | Toggle music                             |
| N               | Toggle sound effects                     |
| ESC             | Return to menu (asks to save if in-game) |

In the menu:

- Enter: create a new game.
- Double-click a save: load it.
- Select a save and press Delete: delete it.

## 3. Requirements

- **JDK 17 or newer.** The JDK (Java Development Kit) is required, not only the JRE.
  Check with `java -version` and `javac -version`.
- **Maven 3.6.3 or newer.** Already included in IntelliJ IDEA, Eclipse, NetBeans and the VS Code
  Java extensions, so it only has to be installed for the terminal (option B).
  Check with `mvn -version`.
- **Internet connection for the first build.** Maven downloads Gson, JUnit and the Maven plugins
  once and caches them in `~/.m2/repository`. Without internet, use option F.
- **Speakers or headphones (optional).** The game runs without sound if there is no audio device.

### 3.1 External libraries

The libraries are declared in `pom.xml`, so they don't need to be installed separately.

- **Gson 2.11.0**: save files and settings (JSON).
  Downloaded by Maven from Maven Central. If it can't be downloaded, see section 6.1 or use
  option F.
- **JUnit Jupiter 5.11.4**: unit tests only.
  Downloaded by Maven from Maven Central. If it is missing, only the tests fail to compile;
  the game still runs.
- **jaco-mp3-player 0.9.3**: music and sound effects.
  Not on Maven Central, so Maven can never download it. It is included in the project as
  `lib/jaco-mp3-player-0.9.3.jar` and linked in `pom.xml` with `<scope>system</scope>`.
  If it is missing, re-clone the repository or re-extract the ZIP. The jar is self-contained
  (the MP3 decoder is inside it), so it does not need any other libraries.

## 4. Setup and running

### 4.1 Before you start

1. Install JDK 17 or newer, for example Eclipse Temurin (https://adoptium.net/).
   On Windows, select "Set JAVA_HOME" and "Add to PATH" in the installer, then open a NEW
   terminal and check that `java -version` prints 17 or newer.
2. Get the project with git (`git clone https://github.com/ivo887/Snake.git`), or extract the
   ZIP file.
3. Check that the folder structure is preserved. The project folder, the one you open in the IDE,
   must look like this:

```
Snake/                              <- open this folder (it contains pom.xml)
+-- pom.xml
+-- lib/jaco-mp3-player-0.9.3.jar   <- required to compile
+-- SoundTrack/*.mp3                <- required for sound
+-- src/main/java/snake/Main.java   <- main class: snake.Main
```

If the ZIP extracted into `Snake/Snake/`, open the inner folder, the one that contains `pom.xml`.

Two rules for every IDE:

- **Open or import the project as a Maven project** (from `pom.xml`). Otherwise, the IDE does not
  know about Gson and JUnit, and you get `package com.google.gson does not exist`.
- **Run with the project folder as the working directory.** The game looks for `SoundTrack/`,
  `saves/` and `settings.json` relative to it. All the IDEs below use the project folder by
  default. If it is changed, the game runs without sound and creates new saves in another place.

Choose one of the options A-F below.

### 4.2 Option A: IntelliJ IDEA (Community or Ultimate)

1. File > Open, then select the project folder (the one with `pom.xml`).
   If asked, choose "Open as Project" and "Trust Project".
2. IntelliJ IDEA detects `pom.xml` and imports the project with Maven. If the imports stay red,
   open the Maven tool window on the right and click "Reload All Maven Projects".
   If there is no Maven tool window, right-click `pom.xml` and choose "Add as Maven Project".
3. File > Project Structure > Project, and set the SDK to a JDK 17 or newer.
   The project was made with `openjdk-23`, which may not exist on your computer, so pick any
   JDK 17+ you have, or use "Add SDK > Download JDK". Set the language level to "SDK default"
   or 17.
4. Open `src/main/java/snake/Main.java` and click the green Run arrow next to `main`.
5. To run the tests, right-click `src/test/java` and choose "Run 'All Tests'".

### 4.3 Option B: terminal (Maven)

1. Install Maven. Download it from https://maven.apache.org/download.cgi, extract it, and add
   its `bin` folder to PATH. Or use a package manager:
   `winget install Apache.Maven` (Windows), `brew install maven` (macOS),
   `sudo apt install maven` (Linux).
2. In the project folder, run `mvn compile exec:java`. The first time, it downloads the
   dependencies and may take a while; afterwards, the menu opens.
3. Run the tests with `mvn test`. All tests should pass (`BUILD SUCCESS`), and the reports are
   in `target/surefire-reports/`.

Note: `mvn package` builds a jar without Gson and the MP3 library inside, so
`java -jar target/Snake-1.0-SNAPSHOT.jar` does NOT work. Use `mvn compile exec:java` or option F.

### 4.4 Option C: Eclipse (IDE for Java Developers, 2022-03 or newer)

1. File > Import > Maven > Existing Maven Projects, then Browse to the project folder, make sure
   `pom.xml` is checked, and click Finish. Do not use "Existing Projects into Workspace" or
   "Projects from Folder", because they don't read `pom.xml`.
2. Wait until the dependencies are downloaded (progress in the bottom right). If there are red
   errors afterwards, right-click the project > Maven > Update Project..., check
   "Force Update of Snapshots/Releases", and click OK.
3. Check the JDK: Window > Preferences > Java > Installed JREs must contain a JDK 17 or newer
   (click Add... if not), and Java > Compiler must have compliance level 17 or higher.
4. Right-click `src/main/java/snake/Main.java` > Run As > Java Application.
5. To run the tests, right-click `src/test/java` > Run As > JUnit Test.
   Eclipse selects JUnit 5 automatically.

### 4.5 Option D: Visual Studio Code

1. Install the "Extension Pack for Java" (by Microsoft). It includes Maven support, so Maven
   does not need to be installed separately.
2. File > Open Folder..., then select the project folder (the one with `pom.xml`). Wait until the
   status bar shows that the Java projects are loaded. If asked about the build tool, choose Maven.
3. If no JDK 17+ is found, run "Java: Configure Java Runtime" from the command palette
   (Ctrl+Shift+P, or Cmd+Shift+P on macOS) and install or select one.
4. Open `src/main/java/snake/Main.java` and click "Run" above `main`.
5. To run the tests, open the Testing view (flask icon) and click "Run Tests".

If the imports stay red, run "Java: Clean Java Language Server Workspace" from the command
palette and reload the window.

### 4.6 Option E: Apache NetBeans (17 or newer)

1. File > Open Project..., then select the project folder. NetBeans recognises Maven projects by
   `pom.xml`.
2. Right-click the project > Build with Dependencies. Check that NetBeans uses a JDK 17+
   (Tools > Java Platforms).
3. Right-click `Main.java` > Run File, or use Run Project and select `snake.Main` as the main class.
4. To run the tests, right-click the project > Test.

### 4.7 Option F: without Maven (only javac and java)

Use this when Maven cannot download the libraries (no internet, blocked network, etc.).

1. Download `gson-2.11.0.jar` from Maven Central:
   https://repo1.maven.org/maven2/com/google/code/gson/gson/2.11.0/gson-2.11.0.jar
   and put it in `lib/` next to the MP3 jar. If the project was ever built with Maven, the jar is
   already in `~/.m2/repository/com/google/code/gson/gson/2.11.0/`.
2. In the project folder, compile and run with the commands below. The classpath separator is
   `;` on Windows and `:` on macOS and Linux.

Windows (PowerShell or cmd):

```
javac -d target/manual -cp "lib/jaco-mp3-player-0.9.3.jar;lib/gson-2.11.0.jar" --source-path src/main/java src/main/java/snake/Main.java
java -cp "target/manual;lib/jaco-mp3-player-0.9.3.jar;lib/gson-2.11.0.jar" snake.Main
```

macOS and Linux:

```
javac -d target/manual -cp "lib/jaco-mp3-player-0.9.3.jar:lib/gson-2.11.0.jar" --source-path src/main/java src/main/java/snake/Main.java
java -cp "target/manual:lib/jaco-mp3-player-0.9.3.jar:lib/gson-2.11.0.jar" snake.Main
```

`--source-path` makes `javac` also compile all the classes that `Main` uses. The tests are not
run this way, because they need JUnit; use one of the options A-E for the tests.

## 5. Files created by the game

- `settings.json`: menu settings (borders, board size, music, effects) and the high score per
  board size. To reset them, delete the file.
- `saves/*.json`: saved games. Delete them in the menu.

Both are created in the working directory and are ignored by git.

## 6. Common issues

Each issue lists the error message or symptom, the cause and the fix.

### 6.1 Library problems

1. `package com.google.gson does not exist` or `Cannot resolve symbol 'Gson'`
   - Cause: the project was opened as a plain folder, not as a Maven project, so the IDE does
     not know about the dependencies.
   - Fix: IntelliJ IDEA: right-click `pom.xml` > "Add as Maven Project", or
     "Reload All Maven Projects". Eclipse: import again with "Existing Maven Projects" (4.4).
     VS Code: "Java: Clean Java Language Server Workspace" (4.5).

2. `Could not resolve dependencies`, `Cannot resolve com.google.code.gson:gson:2.11.0`, or a
   JUnit artifact is not found
   - Cause: Maven could not download the library because of no internet, a proxy or firewall,
     offline mode, or an earlier failed download that Maven remembers.
   - Fix: check the internet connection and turn off offline mode (IntelliJ IDEA: Maven tool
     window > "Toggle Offline Mode"; terminal: don't use `-o`). Then force a new download with
     `mvn -U clean compile` or "Reload All Maven Projects". If it still fails, delete the folder
     `~/.m2/repository/com/google/code/gson` (or `~/.m2/repository/org/junit`) and try again.
     As a last resort, use option F.

3. `Could not find artifact jaco.mp3:jaco-mp3-player:jar:0.9.3`,
   `package jaco.mp3.player does not exist`, or `systemPath ... refers to a non-existing file`
   - Cause: `lib/jaco-mp3-player-0.9.3.jar` is missing, or the wrong folder was opened. This
     library is not on Maven Central, so Maven cannot download it.
   - Fix: make sure the opened folder contains both `pom.xml` and `lib/`, and that the jar
     exists. If it doesn't, re-clone the repository or re-extract the ZIP. Do not rename or
     move the jar.

4. Warning: `'dependencies.dependency.scope' for jaco.mp3:jaco-mp3-player:jar should not be
   'system'` or `'systemPath' ... should not point at files within the project directory`
   - Cause: this is how Maven reports a local jar in `lib/`.
   - Fix: none needed, it is safe to ignore and the build still works. Do not delete the jar,
     because the game needs it to compile.

5. `No plugin found for prefix 'exec'` or `Plugin org.codehaus.mojo:exec-maven-plugin ... could
   not be resolved`
   - Cause: the command was run outside the project folder, or the plugin could not be
     downloaded.
   - Fix: run `mvn compile exec:java` in the folder with `pom.xml`, with an internet connection.

6. `NoClassDefFoundError: com/google/gson/Gson` or `NoClassDefFoundError:
   jaco/mp3/player/MP3Player` when starting
   - Cause: the game was started without the libraries on the classpath (for example with
     `java -jar` or a hand-made run configuration).
   - Fix: start it with the IDE's Run button on `Main.java`, with `mvn compile exec:java`, or
     with the full `-cp` from option F.

7. Tests: `package org.junit.jupiter.api does not exist`, or "No tests found"
   - Cause: the IDE is not using the Maven dependencies, or it offered to add JUnit 4.
   - Fix: do not accept quick fixes like "Add JUnit 4 to classpath". Reload the Maven project
     (see issue 1), then run the tests again. `mvn test` always uses JUnit 5.

### 6.2 Java version problems

1. `release version 17 not supported`, `invalid target release: 17` or
   `invalid source release: 17`
   - Cause: Maven or the IDE uses a JDK older than 17.
   - Fix: terminal: set `JAVA_HOME` to a JDK 17+ and open a new terminal (`mvn -version` shows
     the JDK in use). IDE: set the project SDK/JRE as in the setup steps.

2. `UnsupportedClassVersionError ... class file version 61.0` (or higher)
   - Cause: the game was compiled with a newer JDK but started with an older Java.
   - Fix: run it with the same JDK 17+ that compiled it.

3. IntelliJ IDEA: "Project JDK is not defined", or `openjdk-23` shown in red
   - Cause: the project remembers the JDK name from the computer it was made on.
   - Fix: File > Project Structure > Project > SDK, and pick any JDK 17+ (step 3 of 4.2).

4. `javac` is not recognised, but `java` works
   - Cause: only a JRE is installed, or the JDK's `bin` folder is not in PATH.
   - Fix: install a JDK (see 4.1) and open a new terminal.

### 6.3 Other problems

1. `mvn` is not recognised
   - Fix: Maven is not in PATH. Install it (4.3), or use an IDE (options A, C, D, E), which
     includes Maven.

2. The game runs but has no sound
   - Fix: make sure the working directory is the project folder, so that the game can find
     `SoundTrack/`. Check that music and effects are enabled in the menu (or press M/N in-game)
     and that an audio output device is available.

3. `HeadlessException`
   - Cause: the game was started without a display (for example over SSH, in WSL without a
     GUI, or in a container).
   - Fix: run it on a desktop.

4. Saves or high scores "disappear"
   - Cause: the game was started from a different working directory, so it created a new
     `settings.json` and `saves/` there.
   - Fix: run it from the project folder.

5. An old save is shown as "unreadable"
   - Cause: the file is probably corrupted or was modified.
   - Fix: delete it in the menu.

## 7. Project structure

The code is split into packages by responsibility.

- `snake`
  - `Main`: entry point; sets the look and feel and opens the menu.
  - `AppContext`: holds the shared services (settings, saves, sounds) injected into windows.
- `snake.model`
  - `GameState`: all game logic: movement, eating, boosters, collisions, winning.
  - `Snake`: the snake's body and direction.
  - `Point`: an immutable position on the board (a `record`).
  - `Direction`: directions and their offsets (an `enum`).
  - `BoosterEffect`: fast / slow effect with its tick delay in milliseconds.
  - `GameEvent`: describes what happens on each game tick (apple, booster, etc.).
  - `BoardSize`: board sizes offered in the menu.
- `snake.ui`
  - `MenuFrame`: menu: new game, load game, settings.
  - `GameFrame`: game window: toolbar, board, keys, save dialog.
  - `BoardPanel`: draws the board and the start / pause / game over screens.
  - `GameController`: updates the game state on a `javax.swing.Timer`, handles pause and game
    over, plays sounds.
  - `Theme`: colours and fonts.
- `snake.persistence`
  - `SaveManager`: lists, saves, loads and deletes JSON save files.
  - `Settings`: menu settings and high scores per board size, saved to `settings.json`.
  - `SaveInfo`, `InvalidSaveException`: save list item, error while loading.
- `snake.audio`
  - `SoundManager`: plays music and effects on separate `MP3Player`s.
  - `Sound`: describes a sound and its MP3 file.

## 8. Design decisions

- **Model-view-controller separation.** `GameState` knows nothing of the UI and only implements
  the rules; `BoardPanel` only draws the board and does not receive input; `GameController`
  connects them and updates the game on a timer. `GameState.tick()` returns a `GameEvent`, which
  allows the controller to choose what sound to play without coupling to audio.
- **Encapsulation.** Fields are private by default. The snake's body is a list of `Point`s, an
  immutable `record`, and the `Snake.body()` method returns a defensive copy, so that other
  classes cannot modify the game state.
- **Dependency injection.** Instead of having the `GameFrame` create its own settings, saves or
  sounds, they are created once in `AppContext` and passed to the frame when it's built. This
  allows `Settings` and `SaveManager` to be faked in tests.
- **Enums with behaviours.** `Direction.isOpposite()`, `BoosterEffect.tickDelayMs()` and
  `Sound.fileName()` implement logic specific to each value, which reduces "fragile duplication"
  and replaces `switch` statements and constants.
- **Effects last for a number of ticks.** A fast boost or slow down lasts for 50 or 16 ticks,
  respectively, rather than a wall time of 5 seconds. This makes the effect pause when the game
  is paused and survive saving/loading.
