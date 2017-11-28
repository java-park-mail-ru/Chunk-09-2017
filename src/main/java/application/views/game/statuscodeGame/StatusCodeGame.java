package application.views.game.statuscodeGame;

import application.models.game.player.PlayerAbstractActive;
import application.services.game.GameSocketStatusCode;
import application.views.game.StatusCode;

public final class StatusCodeGame extends StatusCode {

    private final PlayerAbstractActive player;

    public StatusCodeGame(GameSocketStatusCode statusCode, PlayerAbstractActive player) {
        super(statusCode);
        this.player = player;
    }

    public PlayerAbstractActive getPlayer() {
        return player;
    }
}
