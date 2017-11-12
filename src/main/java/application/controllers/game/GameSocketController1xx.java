package application.controllers.game;

import application.models.game.Game.GamePrepare;
import application.models.game.Player.PlayerWatcher;
import application.services.game.GameSocketStatusCode;
import application.services.user.UserService;
import application.services.user.UserServiceTools;
import application.views.game.StatusCode1xx;
import application.views.game.StatusCode3xx;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;


@Component
public final class GameSocketController1xx extends GameSocketController {

	private final HashSet<WebSocketSession> subscribers;
	private final ConcurrentHashMap<Long, GamePrepare> preparingGames;
	private final AtomicLong generatorGameID;
	private final UserService userService;


	public GameSocketController1xx(UserService userService) {

		this.subscribers = new HashSet<>();
		this.preparingGames = new ConcurrentHashMap<>();
		this.generatorGameID = new AtomicLong();
		this.userService = userService;
	}

	@Override
	protected void chooseAction(Integer code, JsonNode jsonNode,
	                                         final WebSocketSession session) {

		if (code.equals(GameSocketStatusCode.CREATE.getValue())) {
			create(session, jsonNode);
			return;
		}
		if (code.equals(GameSocketStatusCode.CONNECT_ACTIVE.getValue())) {
			connectActive(session, jsonNode);
			return;
		}
		if (code.equals(GameSocketStatusCode.CONNECT_WATCHER.getValue())) {
			connectWatcher(session, jsonNode);
			return;
		}
		if (code.equals(GameSocketStatusCode.EXIT.getValue())) {
			exit(session);
			return;
		}
		if (code.equals(GameSocketStatusCode.STATUS.getValue())) {
			status(session);
			return;
		}
		if (code.equals(GameSocketStatusCode.START.getValue())) {
			start(session);
			return;
		}
		if (code.equals(GameSocketStatusCode.SUBSCRIBE.getValue())) {
			subscribe(session);
			return;
		}
		if (code.equals(GameSocketStatusCode.UNSUBSCRIBE.getValue())) {
			unsubscribe(session);
			return;
		}
		if (code.equals(GameSocketStatusCode.DESTROY.getValue())) {
			destroy(session);
			return;
		}
	}


	public void create(final WebSocketSession session, JsonNode jsonNode) {

		final ObjectMapper mapper = new ObjectMapper();
		final String payload;

		// Проверка 301
		Long newGameID = (Long) session.getAttributes().get("gameID");
		if (newGameID != null) {
			payload = this.toJSON(mapper, new StatusCode3xx(
					GameSocketStatusCode.ALREADY_PLAY, newGameID)
			);
			this.sendMessage(session, payload);
			return;
		}

		final Long masterID = (Long) session.getAttributes().get(UserServiceTools.USER_ID);
		final PlayerWatcher master = new PlayerWatcher(userService.getUserById(masterID), session);

		newGameID = generatorGameID.getAndIncrement();
		final GamePrepare newGame = new GamePrepare(newGameID, masterID);
		setAttribute(session, "gameID", newGameID);

		newGame.addGamer(master);
		preparingGames.put(newGameID, newGame);

		unsubscribe(session);

		// Ответ 101
		payload = this.toJSON(mapper, new StatusCode1xx(
				GameSocketStatusCode.CONNECT_ACTIVE, newGameID)
		);
		this.sendMessage(session, payload);
	}
	
	private void connectActive(final WebSocketSession session, JsonNode jsonNode) {

		final ObjectMapper mapper = new ObjectMapper();


		// Проверка 301
		Long gameID = (Long) session.getAttributes().get("gameID");
		if (gameID != null) {
			final String payload = this.toJSON(mapper, new StatusCode3xx(
					GameSocketStatusCode.ALREADY_PLAY, gameID)
			);
			this.sendMessage(session, payload);
			return;
		}

		gameID = jsonNode.get("gameID").asLong();
		final GamePrepare game = preparingGames.get(gameID);
		setAttribute(session, "gameID", gameID);

		unsubscribe(session);

		final Long gamerID = (Long) session.getAttributes().get(UserServiceTools.USER_ID);
		final PlayerWatcher gamer = new PlayerWatcher(userService.getUserById(gamerID), session);

		game.addGamer(gamer);
	}

	private void connectWatcher(WebSocketSession session, JsonNode jsonNode) {

		final ObjectMapper mapper = new ObjectMapper();

		// Проверка 301
		Long gameID = (Long) session.getAttributes().get("gameID");
		if (gameID != null) {
			final String payload = this.toJSON(mapper, new StatusCode3xx(
					GameSocketStatusCode.ALREADY_PLAY, gameID)
			);
			this.sendMessage(session, payload);
			return;
		}

		gameID = jsonNode.get("gameID").asLong();
		final GamePrepare game = preparingGames.get(gameID);
		setAttribute(session, "gameID", gameID);

		unsubscribe(session);

		final Long watcherID = (Long) session.getAttributes().get(UserServiceTools.USER_ID);
		final PlayerWatcher watcher = new PlayerWatcher(userService.getUserById(watcherID), session);

		game.addWatcher(watcher);
	}

	private void exit(WebSocketSession session) {

	}

	private void status(WebSocketSession session) {

	}

	private void start(WebSocketSession session) {

	}

	private void destroy(WebSocketSession session) {

	}

	public synchronized void subscribe(@NotNull WebSocketSession session) {
		subscribers.add(session);
	}

	public synchronized void unsubscribe(WebSocketSession session) {
		subscribers.remove(session);
	}

}
