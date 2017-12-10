package application.models.game.player;

import application.models.user.UserSignUp;
import org.springframework.web.socket.WebSocketSession;

public final class PlayerWatcher extends PlayerAbstract {

    public PlayerWatcher(UserSignUp user, WebSocketSession session) {
        super(user, session);
    }
}
