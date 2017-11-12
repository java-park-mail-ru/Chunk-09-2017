package application.models.game.game;

import application.models.game.player.PlayerAbstract;
import application.models.game.player.PlayerWatcher;
import application.views.game.StatusCode;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.socket.TextMessage;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;


public abstract class GameAbstract {

	protected final ConcurrentHashMap<Long, PlayerWatcher> watchers;
	protected final Long gameID;
	protected final Integer numberOfPlayer;
	@JsonIgnore
	protected final ObjectMapper mapper = new ObjectMapper();

	protected GameAbstract(@NotNull Long gameID, @NotNull Integer numberOfPlayers) {
		this.gameID = gameID;
		this.watchers = new ConcurrentHashMap<>();
		this.numberOfPlayer = numberOfPlayers;
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


	public Long getGameID() {
		return gameID;
	}

	public Integer getWatchers() {
		return watchers.size();
	}

	public Integer getNumberOfPlayer() {
		return numberOfPlayer;
	}

	public ConcurrentHashMap<Long, PlayerWatcher> getHashMapOfWatchers() {
		return watchers;
	}
}
