package application.exceptions.game;


public final class GameException extends RuntimeException {

    public GameException(String error) {
        super(error);
    }
}