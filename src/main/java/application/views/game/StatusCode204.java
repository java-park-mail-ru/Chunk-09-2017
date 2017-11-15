package application.views.game;

import application.models.game.field.Field;
import application.services.game.GameSocketStatusCode;

public final class StatusCode204 extends StatusCode {

	final Field field;
	final Long gameID;


	public StatusCode204(Field field) {
		super(GameSocketStatusCode.GAMEOVER);
		this.field = field;
		this.gameID = null;

	}

	public StatusCode204(Long gameID) {
		super(GameSocketStatusCode.GAMEOVER);
		this.gameID = gameID;
		this.field = null;
	}


	public Field getField() {
		return field;
	}

	public Long getGameID() {
		return gameID;
	}
}
