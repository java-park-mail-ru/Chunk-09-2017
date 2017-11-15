package application.views.game;

import application.models.game.player.PlayerAbstractActive;
import application.services.game.GameSocketStatusCode;

public final class StatusCode203 extends StatusCode {

	PlayerAbstractActive blocked;

	public StatusCode203(PlayerAbstractActive blocked) {
		super(GameSocketStatusCode.BLOCKED);
		this.blocked = blocked;
	}

	public PlayerAbstractActive getBlocked() {
		return blocked;
	}
}
