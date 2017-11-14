package application.models.game.player;


import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public final class PlayerBot extends PlayerAbstractActive {

	static AtomicLong generatorBotName = new AtomicLong();
	final Short level;

	// TODO подумать как реализовать Бота в PlayerWatch;

	public PlayerBot(String username, Integer playerID, Short level) {
		super(playerID, username);
		this.level = level;
	}

	public PlayerBot(Integer playerID) {
		// TODO какой-то конструтор надо удалить слишком много костылей
		super(playerID,"Bot_" + generatorBotName.getAndIncrement());
		this.level = 1;
	}
}
