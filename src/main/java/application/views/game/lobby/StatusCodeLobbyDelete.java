package application.views.game.lobby;

import application.services.game.GameSocketStatusCode;
import application.views.game.StatusCode;


public final class StatusCodeLobbyDelete extends StatusCode {

    private final Long gameID;

    public StatusCodeLobbyDelete(Long gameID) {
        super(GameSocketStatusCode.DELETE_GAME);
        this.gameID = gameID;
    }


    public Long getGameID() {
        return gameID;
    }
}
