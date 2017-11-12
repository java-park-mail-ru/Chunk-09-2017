package application.models.game.Game;

import application.models.game.Player.PlayerAbstract;
import application.models.game.Player.PlayerWatcher;
import application.views.game.StatusCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.socket.TextMessage;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;


public abstract class GameAbstract {

	protected final Long gameID;
	protected final ConcurrentHashMap<Long, PlayerWatcher> watchers;

	protected final ObjectMapper mapper = new ObjectMapper();

	protected GameAbstract(@NotNull Long gameID) {
		this.gameID = gameID;
		this.watchers = new ConcurrentHashMap<>();
	}

	public void addWatcher(PlayerWatcher watcher) {
		watchers.put(watcher.getUserID(), watcher);
	}

	public void removeWatcher(Long userID) {
		watchers.remove(userID);
	}

	protected synchronized void sendMessageToPlayer(PlayerAbstract player,
	                                                StatusCode statusCode) {
		try {
			player.getSession().sendMessage(
					new TextMessage(mapper.writeValueAsString(statusCode)));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
