package application.views.game.statuscode1xx;

import application.services.game.GameSocketStatusCode;
import application.views.game.StatusCode;

public final class StatusCode112 extends StatusCode {

    private Long userID = null;

    public StatusCode112(Long userID) {
        super(GameSocketStatusCode.WHOAMI);
        this.userID = userID;
    }

    public Long getUserID() {
        return userID;
    }
}
