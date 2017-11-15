package application.views.game.statuscode3xx;

import application.services.game.GameSocketStatusCode;
import application.views.game.StatusCode;

public final class StatusCode3xx extends StatusCode {

    private final Long gameID;

    public StatusCode3xx(GameSocketStatusCode statusCode) {
        super(statusCode);
        this.gameID = null;
    }

    public StatusCode3xx(GameSocketStatusCode statusCode, Long gameID) {
        super(statusCode);
        this.gameID = gameID;
    }

    public Long getGameID() {
        return gameID;
    }
}
