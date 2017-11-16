package application.views.game.statuscode2xx;

import application.models.game.player.PlayerAbstractActive;
import application.services.game.GameSocketStatusCode;
import application.views.game.StatusCode;

public final class StatusCode2xx extends StatusCode {

    private final PlayerAbstractActive player;

    public StatusCode2xx(GameSocketStatusCode statusCode, PlayerAbstractActive player) {
        super(statusCode);
        this.player = player;
    }

    public PlayerAbstractActive getPlayer() {
        return player;
    }
}
