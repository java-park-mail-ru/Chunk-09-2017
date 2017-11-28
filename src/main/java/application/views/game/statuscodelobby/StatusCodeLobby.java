package application.views.game.statuscodelobby;

import application.models.game.game.GamePrepare;
import application.models.game.player.PlayerGamer;
import application.services.game.GameSocketStatusCode;
import application.views.game.StatusCode;

public final class StatusCodeLobby extends StatusCode {

    private Long gameID = null;
    private PlayerGamer player = null;
    private GamePrepare game = null;

    public StatusCodeLobby(GameSocketStatusCode statusCode) {
        super(statusCode);
    }

    public StatusCodeLobby(GameSocketStatusCode statusCode, Long gameID) {
        super(statusCode);
        this.gameID = gameID;
    }

    public StatusCodeLobby(GameSocketStatusCode statusCode, GamePrepare game) {
        super(statusCode);
        this.game = game;
        this.gameID = game.getGameID();
    }


    public Long getGameID() {
        return gameID;
    }

    public PlayerGamer getPlayer() {
        return player;
    }

    public GamePrepare getGame() {
        return game;
    }
}
