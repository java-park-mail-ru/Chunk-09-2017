package application.views.game;

import application.models.game.game.GamePrepare;
import application.models.game.player.PlayerGamer;
import application.models.game.player.PlayerWatcher;
import application.services.game.GameSocketStatusCode;

import java.util.List;

public final class StatusCode1xx extends StatusCode{

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

	public StatusCode1xx(GameSocketStatusCode statusCode,
	                     Long gameID, PlayerGamer player) {
		super(statusCode);
		this.gameID = gameID;
		this.player = player;
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
