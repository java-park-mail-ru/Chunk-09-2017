package application.models.game.game;

import application.models.game.player.PlayerAbstractActive;
import application.models.game.player.PlayerBot;
import application.models.game.player.PlayerGamer;

import java.util.concurrent.ConcurrentHashMap;

public final class GameActive extends GameAbstract {

	private Long currentPlayerId;
	private Boolean gameOver; // todo to think is it needed?
	private final ConcurrentHashMap<Long /*playerID*/, PlayerAbstractActive> gamers;

	public GameActive(GamePrepare prepared) {
		super(prepared.gameID, prepared.gameField,
			  prepared.numberOfPlayer, prepared.watchers);

		class MyInt { long i = 0; }
		final MyInt playerIncrement = new MyInt();

		this.gamers = new ConcurrentHashMap<>(prepared.gamers.size());
		prepared.gamers.forEachValue(1L, gamer -> {
			if (gamer.getUserID() != null) {
				gamers.put(
						playerIncrement.i, new PlayerGamer((int)playerIncrement.i, gamer)
				);
			} else {
				gamers.put(
						playerIncrement.i, new PlayerBot((int)playerIncrement.i)
				);
			}
			++playerIncrement.i;
		});


		this.currentPlayerId = 1L; // TODO Заменить на константы
		this.gameOver = false;

		// TODO - проблема - в Watcher'ах тоже есть Bot'ы как их распознать, interface???
	}
}
