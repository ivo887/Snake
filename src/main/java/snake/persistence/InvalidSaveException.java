package snake.persistence;

import java.io.IOException;

public class InvalidSaveException extends IOException {
    public InvalidSaveException(String message) {
        super(message);
    }
}
