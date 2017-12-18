package application.exceptions.game;


public final class GameExceptionDestroyPrepare extends RuntimeException {

    private final Long gameID;

    public GameExceptionDestroyPrepare(Long gameID) {
        this.gameID = gameID;
    }

    public Long getGameID() {
        return gameID;
    }
}
