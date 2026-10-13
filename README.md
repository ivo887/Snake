# Snake

A Snake game written in Java with Swing.

**Plan page:** https://ivo887.github.io/Snake/ (step-by-step plan for finishing the game, with switches for the optional features)

## Features

Working now:

- Move the snake with W, A, S, D
- Apples that grow the snake and raise the score
- Boosters that randomly speed up or slow down the game and flash the background for 5 seconds
- Start, Pause and Finish buttons

Planned (see the plan page for the full list):

- Game over on collision with the snake or a wall
- Resume after pause
- Looping borders (with a setting to turn them off)
- Game music and interaction sounds
- Board builder with obstacles and fixed apple and booster positions
- Save game and choose a save to load
- JUnit tests

## Controls

| Key | Action |
|-----|--------|
| W | Up |
| A | Left |
| S | Down |
| D | Right |

## Requirements

- Java 17 or newer (JDK)

## Run

The source file is in the `snake` package, so compile and run from the folder that contains the `snake` directory:

```
javac snake/SnakeGame.java
java snake.SnakeGame
```

Or open the project in an IDE such as IntelliJ IDEA or Eclipse and run `SnakeGame`.

## Project structure

| Class | Purpose |
|-------|---------|
| `SnakeGame` | Window and `main` method |
| `GamePanel` | Game loop, drawing, buttons, score |
| `Snake` | Body, movement, direction, collision checks |
| `Food` | Apple spawning and drawing |
| `PowerUp` | Booster spawning, drawing and random effect |

## Roadmap

The backlog and the step-by-step plan are on the plan page: https://ivo887.github.io/Snake/
