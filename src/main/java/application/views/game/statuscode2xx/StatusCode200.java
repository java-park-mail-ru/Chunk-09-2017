package application.views.game.statuscode2xx;

import application.services.game.GameSocketStatusCode;
import application.views.game.StatusCode;

public final class StatusCode200 extends StatusCode {

    private final Long gameID;

    public StatusCode200(Long gameID) {
        super(GameSocketStatusCode.BEGIN);
        this.gameID = gameID;
    }

    public Long getGameID() {
        return gameID;
    }
}
