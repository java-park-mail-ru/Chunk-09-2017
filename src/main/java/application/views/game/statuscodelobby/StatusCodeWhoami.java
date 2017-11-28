package application.views.game.statuscodelobby;

import application.services.game.GameSocketStatusCode;
import application.views.game.StatusCode;

public final class StatusCodeWhoami extends StatusCode {

    private final Long userID;
    private final Long gameID;

    public StatusCodeWhoami(Long userID, Long gameID) {
        super(GameSocketStatusCode.WHOAMI);
        this.userID = userID;
        this.gameID = gameID;
    }

    public Long getUserID() {
        return userID;
    }

    public Long getGameID() {
        return gameID;
    }
}
