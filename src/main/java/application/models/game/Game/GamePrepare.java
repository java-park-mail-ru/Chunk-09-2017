package application.models.game.Game;

//import application.models.game.Player.PlayerActive;
import application.models.game.Player.PlayerWatcher;
import application.services.game.GameSocketStatusCode;
import application.views.game.StatusCode1xx;

import javax.validation.constraints.NotNull;
import java.util.concurrent.ConcurrentHashMap;


public final class GamePrepare extends GameAbstract {

	private final ConcurrentHashMap<Long, PlayerWatcher> gamers;
	private final Long masterID;

	public GamePrepare(@NotNull Long gameID, @NotNull Long masterID) {
		super(gameID);
		this.masterID = masterID;
		this.gamers = new ConcurrentHashMap<>();
	}

	public void addGamer(PlayerWatcher gamer) {
		notifyPlayers(gamer, GameSocketStatusCode.CONNECT_ACTIVE);
		gamers.put(gamer.getUserID(), gamer);
	}

	public void removeGamer(Long userID) {
		notifyPlayers(gamers.remove(userID), GameSocketStatusCode.EXIT);
	}

	// Оповещение участников о входе/выходе PlayerActive (101/103)
	private void notifyPlayers(PlayerWatcher player, GameSocketStatusCode code) {
		gamers.values().forEach(gamer -> this.sendMessageToPlayer(
				gamer,
				new StatusCode1xx(code, gameID, player)
		));
		watchers.values().forEach(watcher -> this.sendMessageToPlayer(
				watcher,
				new StatusCode1xx(code, gameID, player)
		));
	}
}
