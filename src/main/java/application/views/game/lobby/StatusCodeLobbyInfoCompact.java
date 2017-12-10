package application.views.game.lobby;

import application.models.game.game.GamePrepare;
import application.services.game.GameSocketStatusCode;
import application.views.game.information.GameInformationCompact;
import application.views.game.StatusCode;


public final class StatusCodeLobbyInfoCompact extends StatusCode {

    private final GameInformationCompact game;

    public StatusCodeLobbyInfoCompact(GameSocketStatusCode statusCode, GamePrepare game) {
        super(statusCode);
        this.game = new GameInformationCompact(game);
    }


    public GameInformationCompact getGame() {
        return game;
    }
}
