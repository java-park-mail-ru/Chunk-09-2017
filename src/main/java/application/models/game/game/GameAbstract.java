package application.models.game.game;

import application.models.game.field.Field;
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

	protected final Long gameID;
	protected final Field field;
	protected final Integer numberOfPlayer;
	protected final ConcurrentHashMap<Long /*userID*/, PlayerWatcher> watchers;
	@JsonIgnore
	protected final ObjectMapper mapper = new ObjectMapper();


	protected GameAbstract(@NotNull Long gameID, @NotNull Field gameField,
	                       @NotNull Integer numberOfPlayer) {

		this.gameID = gameID;
		this.field = gameField;
		this.numberOfPlayer = numberOfPlayer;
		this.watchers = new ConcurrentHashMap<>();
	}

	public GameAbstract(Long gameID, Field gameField, Integer numberOfPlayer,
	                    ConcurrentHashMap<Long, PlayerWatcher> watchers) {
		this.gameID = gameID;
		this.field = gameField;
		this.numberOfPlayer = numberOfPlayer;
		this.watchers = watchers;
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

	@JsonIgnore
	public ConcurrentHashMap<Long, PlayerWatcher> getHashMapOfWatchers() {
		return watchers;
	}
}
