package snake.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class GameStateTest {
    // snake at (5,5) heading right, apple in a corner, no booster
    private GameState newGame(boolean looping) {
        GameState state = new GameState(10, 10, looping);
        state.setApple(new Point(0, 0));
        state.setBooster(null);
        return state;
    }

    @Test
    void movesInTheCurrentDirection() {
        GameState state = newGame(true);
        assertEquals(GameEvent.NONE, state.tick());
        assertEquals(new Point(6, 5), state.snake().head());
    }

    @Test
    void turnsOnTheNextTick() {
        GameState state = newGame(true);
        state.turn(Direction.UP);
        state.tick();
        assertEquals(new Point(5, 4), state.snake().head());
    }

    @Test
    void wrapsAroundWhenLooping() {
        GameState state = newGame(true);
        for (int i = 0; i < 5; i++) {
            state.tick();
        }
        assertEquals(new Point(0, 5), state.snake().head());
    }

    @Test
    void crashesIntoTheWallWhenNotLooping() {
        GameState state = newGame(false);
        for (int i = 0; i < 4; i++) {
            assertEquals(GameEvent.NONE, state.tick());
        }
        assertEquals(GameEvent.CRASH, state.tick());
        assertTrue(state.isOver());
    }

    @Test
    void crashesIntoItself() {
        GameState state = newGame(true);
        state.setSnake(new Snake(new Point(5, 5), Direction.RIGHT, 5));
        state.turn(Direction.DOWN);
        state.tick();
        state.turn(Direction.LEFT);
        state.tick();
        state.turn(Direction.UP);
        assertEquals(GameEvent.CRASH, state.tick());
    }

    @Test
    void eatingAnAppleGrowsTheSnakeAndScores() {
        GameState state = newGame(true);
        state.setApple(new Point(6, 5));
        assertEquals(GameEvent.APPLE, state.tick());
        assertEquals(4, state.snake().length());
        assertEquals(1, state.score());
        assertNotEquals(new Point(6, 5), state.apple());
    }

    @Test
    void applesAndBoostersNeverSpawnOnTheSnake() {
        for (int i = 0; i < 100; i++) {
            GameState state = new GameState(6, 6, true);
            assertFalse(state.snake().occupies(state.apple()));
            assertFalse(state.snake().occupies(state.booster()));
            assertNotEquals(state.apple(), state.booster());
        }
    }

    @Test
    void collectingABoosterStartsAnEffect() {
        GameState state = newGame(true);
        state.setBooster(new Point(6, 5));
        assertEquals(GameEvent.BOOSTER, state.tick());
        assertNotNull(state.effect());
        assertNotEquals(GameState.NORMAL_DELAY_MS, state.tickDelayMs());
    }

    @Test
    void effectEndsAfterFiveSeconds() {
        GameState state = newGame(true);
        state.startEffect(BoosterEffect.FAST);
        for (int i = 0; i < 49; i++) {
            state.tick();
        }
        assertEquals(BoosterEffect.FAST, state.effect());
        state.tick();
        assertNull(state.effect());
        assertEquals(GameState.NORMAL_DELAY_MS, state.tickDelayMs());
    }

    @Test
    void fillingTheBoardWinsTheGame() {
        GameState state = new GameState(4, 1, false);
        assertEquals(GameEvent.WIN, state.tick());
        assertTrue(state.isWon());
    }

    @Test
    void resetStartsAFreshGame() {
        GameState state = newGame(true);
        state.setApple(new Point(6, 5));
        state.tick();
        state.reset();
        assertEquals(0, state.score());
        assertEquals(3, state.snake().length());
        assertFalse(state.isOver());
    }
}
