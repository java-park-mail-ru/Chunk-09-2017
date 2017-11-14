package application.models.game.player;

import application.models.user.UserSignUp;
import org.springframework.web.socket.WebSocketSession;

public abstract class PlayerAbstractActive extends PlayerAbstract {

	private final Integer playerID;
	private Boolean online;

	public PlayerAbstractActive(Integer playerID, PlayerWatcher user) {
		// Конструктор для реального игрока
		super(user);
		this.playerID = playerID;
		this.online = true;
	}

	public PlayerAbstractActive(Integer playerID, String username) {
		// Конструктор для бота
		super(username);
		this.playerID = playerID;
		this.online = true;
	}
}
