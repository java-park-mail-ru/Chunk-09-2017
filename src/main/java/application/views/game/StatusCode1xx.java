package application.views.game;

import application.models.game.Player.PlayerWatcher;
import application.services.game.GameSocketStatusCode;

public final class StatusCode1xx extends StatusCode{

	private final Long gameID;
	private final PlayerWatcher player;

	public StatusCode1xx(GameSocketStatusCode statusCode, Long gameID) {
		super(statusCode);
		this.gameID = gameID;
		this.player = null;
	}

	public StatusCode1xx(GameSocketStatusCode statusCode,
	                     Long gameID, PlayerWatcher player) {
		super(statusCode);
		this.gameID = gameID;
		this.player = player;
	}


	public Long getGameID() {
		return gameID;
	}

	public PlayerWatcher getPlayer() {
		return player;
	}
}
