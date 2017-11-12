package application.models.game.Player;

import application.models.user.UserSignUp;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.web.socket.WebSocketSession;

public abstract class PlayerAbstract {

	private final Long userID;
	private final String username;
	private final String email;
	@JsonIgnore
	private final WebSocketSession session;

	public PlayerAbstract(UserSignUp user, WebSocketSession session) {
		this.userID = user.getId();
		this.username = user.getUsername();
		this.email = user.getEmail();
		this.session = session;
	}



	public Long getUserID() {
		return userID;
	}

	public String getUsername() {
		return username;
	}

	public String getEmail() {
		return email;
	}

	public WebSocketSession getSession() {
		return session;
	}
}
