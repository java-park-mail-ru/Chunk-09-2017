package application.views.game.statuscodeerror;

import application.services.game.GameSocketStatusCode;
import application.views.game.StatusCode;

public final class StatusCodeError extends StatusCode {

    private final Long gameID;

    public StatusCodeError(GameSocketStatusCode statusCode) {
        super(statusCode);
        this.gameID = null;
    }

    public StatusCodeError(GameSocketStatusCode statusCode, Long gameID) {
        super(statusCode);
        this.gameID = gameID;
    }

    public Long getGameID() {
        return gameID;
    }
}
