package io.allteran.game;

public class GameSessionNotFoundException extends RuntimeException {
    public GameSessionNotFoundException(String message) {
        super(message);
    }
}
