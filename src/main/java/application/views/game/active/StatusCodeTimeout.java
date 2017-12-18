package application.views.game.active;

import application.services.game.GameSocketStatusCode;
import application.views.game.StatusCode;


public final class StatusCodeTimeout extends StatusCode {

    private final Integer playerID;

    public StatusCodeTimeout(GameSocketStatusCode statusCode, Integer playerID) {
        super(statusCode);
        this.playerID = playerID;
    }

    public Integer getPlayerID() {
        return playerID;
    }
}
