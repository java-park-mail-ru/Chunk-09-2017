package application.views.game.statuscode2xx;

import application.models.game.player.PlayerAbstractActive;
import application.services.game.GameSocketStatusCode;
import application.views.game.StatusCode;

public final class StatusCode203 extends StatusCode {

    private final PlayerAbstractActive blocked;

    public StatusCode203(PlayerAbstractActive blocked) {
        super(GameSocketStatusCode.BLOCKED);
        this.blocked = blocked;
    }

    public PlayerAbstractActive getBlocked() {
        return blocked;
    }
}
