package application.models.game.game;

import application.models.game.player.PlayerWatcher;
import application.services.game.GameTools;
import application.services.game.GameSocketStatusCode;
import application.views.game.StatusCode1xx;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;


public final class GamePrepare extends GameAbstract {

	private final ConcurrentHashMap<Long, PlayerWatcher> gamers;
	@JsonIgnore
	private final Long masterID;
	@JsonIgnore
	private Boolean isReady;

	public GamePrepare(@NotNull Long gameID,
	                   @NotNull Long masterID,
	                   @NotNull Integer numberOfPlayers) {
		super(gameID, numberOfPlayers);
		this.masterID = masterID;
		this.isReady = false;
		this.gamers = new ConcurrentHashMap<>(this.numberOfPlayer);
	}

	public synchronized void addGamer(PlayerWatcher gamer) {
		if ( isReady ) {
			return;
		}
		gamers.put(gamer.getUserID(), gamer);
		notifyPlayers(gamer, GameSocketStatusCode.CONNECT_ACTIVE);
		if ( gamers.size() == numberOfPlayer ) {
			isReady = true;
		}
	}

	public void removeGamer(Long userID) {
		gamers.get(userID).getSession().getAttributes().remove(GameTools.GAME_ID_ATTR);
		notifyPlayers(gamers.remove(userID), GameSocketStatusCode.EXIT);
		if ( isReady && gamers.size() < numberOfPlayer ) {
			isReady = false;
		}
	}

	public synchronized void destroy() {
		notifyPlayers(GameSocketStatusCode.DESTROY);
		gamers.forEachValue(1L, gamer -> gamer.getSession()
				.getAttributes().remove(GameTools.GAME_ID_ATTR));
		gamers.clear();
	}

	// Оповещение участников о каком либо событии
	// TODO поиграться с количеством потоков в forEachValue
	private void notifyPlayers(PlayerWatcher player, GameSocketStatusCode code) {
		gamers.forEachValue(1L, gamer -> this.sendMessageToPlayer(
				gamer,
				new StatusCode1xx(code, gameID, player)
		));
		watchers.forEachValue(1L, watcher -> this.sendMessageToPlayer(
				watcher,
				new StatusCode1xx(code, gameID, player)
		));
	}

	private void notifyPlayers(GameSocketStatusCode code) {
		gamers.forEachValue(1L, gamer -> this.sendMessageToPlayer(
				gamer,
				new StatusCode1xx(code)
		));
		watchers.forEachValue(1L, watcher -> this.sendMessageToPlayer(
				watcher,
				new StatusCode1xx(code)
		));
	}



	public Collection<PlayerWatcher> getGamers() {
		return gamers.values();
	}

	public synchronized Long getMasterID() {
		return masterID;
	}

	public synchronized Boolean isReady() {
		return gamers.size() == numberOfPlayer;
	}
}
