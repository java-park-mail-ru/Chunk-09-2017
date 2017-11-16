package application.views.game.statuscode1xx;

import application.models.game.game.GamePrepare;
import application.models.game.player.PlayerGamer;
import application.services.game.GameSocketStatusCode;
import application.views.game.StatusCode;

public final class StatusCode1xx extends StatusCode {

    private Long gameID = null;
    private PlayerGamer player = null;
    private GamePrepare game = null;

    public StatusCode1xx(GameSocketStatusCode statusCode) {
        super(statusCode);
    }

    public StatusCode1xx(GameSocketStatusCode statusCode, Long gameID) {
        super(statusCode);
        this.gameID = gameID;
    }

    public StatusCode1xx(GameSocketStatusCode statusCode, GamePrepare game) {
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
