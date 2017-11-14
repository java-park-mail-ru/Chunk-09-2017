package application.models.game.player;

import application.models.user.UserSignUp;
import org.springframework.web.socket.WebSocketSession;

public final class PlayerGamer extends PlayerAbstractActive {

	public PlayerGamer(Integer playerID, PlayerWatcher user) {
		super(playerID, user);
	}
}
