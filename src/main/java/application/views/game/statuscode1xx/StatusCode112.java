package application.views.game.statuscode1xx;

import application.services.game.GameSocketStatusCode;
import application.views.game.StatusCode;

public final class StatusCode112 extends StatusCode {

    private final Long userID;
    private final Long gameID;

    public StatusCode112(Long userID, Long gameID) {
        super(GameSocketStatusCode.WHOAMI);
        this.userID = userID;
        this.gameID = gameID;
    }

    public Long getUserID() {
        return userID;
    }
}
