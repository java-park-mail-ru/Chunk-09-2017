package application.controllers.game;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.concurrent.atomic.AtomicLong;


public abstract class GameSocketController {

	public final void controller(Long code, JsonNode jsonNode) {
		final GameThread gameThread = new GameThread(code, jsonNode);
		gameThread.start();
	}

	protected abstract void chooseAction(Long code, JsonNode jsonNode);

	private AtomicLong generatorID = new AtomicLong();

	private final class GameThread extends Thread {

		private final Long code;
		private final JsonNode jsonNode;

		GameThread(Long code, JsonNode jsonNode) {
			this.code = code;
			this.jsonNode = jsonNode;
		}

		@Override
		public void run() {
			chooseAction(code, jsonNode);
			System.out.println("Поток №" + generatorID.getAndIncrement() + " запущен");
		}
	}
}
