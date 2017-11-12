package application.controllers.game;

import application.models.game.game.GamePrepare;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.concurrent.ConcurrentHashMap;

@Component
public final class GameSocketController2xx extends GameSocketController {

//	private final ConcurrentHashMap<Long, GamePrepare> activeGames;

//	GameSocketController2xx() {
//		this.activeGames = new ConcurrentHashMap<>();
//	}

	@Override
	protected void chooseAction(Integer code, JsonNode jsonNode,
	                            WebSocketSession session) {

	}

	void addGame(GamePrepare readyGame) {
		// TODO GameActive
	}
}
