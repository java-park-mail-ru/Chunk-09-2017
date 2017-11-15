package application.models.game.player;


import application.models.user.UserSignUp;
import org.springframework.web.socket.WebSocketSession;

public final class PlayerGamer extends PlayerAbstractActive {

	public PlayerGamer(UserSignUp user, WebSocketSession session) {
		super(user, session);
	}
}
