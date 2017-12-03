package application.exceptions.game;


public final class GameException extends RuntimeException {

    private final String error;

    public GameException(String error) {
        this.error = error;
    }

    public String getError() {
        return error;
    }
}