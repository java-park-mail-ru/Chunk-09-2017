package application.views.game.lobby;

import application.models.game.game.GamePrepare;
import application.services.game.GameSocketStatusCode;
import application.views.game.information.GameInformationVerbose;
import application.views.game.StatusCode;


public final class StatusCodeLobbyInfoVerbose extends StatusCode {

    private final GameInformationVerbose game;

    public StatusCodeLobbyInfoVerbose(GameSocketStatusCode statusCode, GamePrepare game) {
        super(statusCode);
        this.game = new GameInformationVerbose(game);
    }

    public GameInformationVerbose getGame() {
        return game;
    }
}
