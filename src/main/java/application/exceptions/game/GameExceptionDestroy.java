package application.exceptions.game;


public final class GameExceptionDestroy extends RuntimeException {

    private final Long gameID;

    public GameExceptionDestroy(Long gameID) {
        this.gameID = gameID;
    }

    public Long getGameID() {
        return gameID;
    }
}
