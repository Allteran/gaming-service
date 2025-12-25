package io.allteran.game;

public class GameNotAllowed extends RuntimeException {
    public GameNotAllowed(String message) {
        super(message);
    }
}
