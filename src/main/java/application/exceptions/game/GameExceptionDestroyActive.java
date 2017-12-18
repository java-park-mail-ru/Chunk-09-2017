package application.exceptions.game;

public final class GameExceptionDestroyActive extends RuntimeException {

    private final Long gameID;

    public GameExceptionDestroyActive(Long gameID) {
        this.gameID = gameID;
    }

    public Long getGameID() {
        return gameID;
    }
}
