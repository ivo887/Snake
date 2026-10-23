package snake.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class SnakeTest {
    private Snake newSnake() {
        return new Snake(new Point(5, 5), Direction.RIGHT, 3);
    }

    @Test
    void bodyTrailsBehindTheHead() {
        Snake snake = newSnake();
        assertEquals(new Point(5, 5), snake.head());
        assertTrue(snake.occupies(new Point(4, 5)));
        assertTrue(snake.occupies(new Point(3, 5)));
        assertEquals(3, snake.length());
    }

    @Test
    void cannotReverse() {
        Snake snake = newSnake();
        assertFalse(snake.turn(Direction.LEFT));
        assertEquals(Direction.RIGHT, snake.applyTurn());
    }

    @Test
    void onlyOneTurnPerTick() {
        Snake snake = newSnake();
        assertTrue(snake.turn(Direction.UP));
        assertFalse(snake.turn(Direction.LEFT));
        assertEquals(Direction.UP, snake.applyTurn());
    }

    @Test
    void oppositeDirections() {
        assertEquals(Direction.DOWN, Snake.opposite(Direction.UP));
        assertEquals(Direction.RIGHT, Snake.opposite(Direction.LEFT));
    }

    @Test
    void stepMovesOneCell() {
        assertEquals(new Point(5, 4), Snake.step(new Point(5, 5), Direction.UP));
        assertEquals(new Point(6, 5), Snake.step(new Point(5, 5), Direction.RIGHT));
    }

    @Test
    void movingKeepsTheLength() {
        Snake snake = newSnake();
        snake.moveTo(new Point(6, 5), false);
        assertEquals(3, snake.length());
        assertEquals(new Point(6, 5), snake.head());
    }

    @Test
    void growingAddsOneSegment() {
        Snake snake = newSnake();
        snake.moveTo(new Point(6, 5), true);
        assertEquals(4, snake.length());
    }

    @Test
    void headCanMoveIntoTheTailCell() {
        assertFalse(newSnake().wouldCollide(new Point(3, 5), false));
    }

    @Test
    void headCannotMoveIntoTheBody() {
        assertTrue(newSnake().wouldCollide(new Point(4, 5), false));
    }
}
