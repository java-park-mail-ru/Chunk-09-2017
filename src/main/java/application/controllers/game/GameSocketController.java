package application.controllers.game;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;


public abstract class GameSocketController {

	private final ObjectMapper mapper = new ObjectMapper()
			.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

	public final void controller(Long code, JsonNode jsonNode, WebSocketSession session) {

		new Thread( () -> chooseAction(code, jsonNode, session) ).run();
	}

	protected final synchronized void sendMessage(WebSocketSession session) {
		try {
			session.sendMessage(new TextMessage("asdasd"));
		} catch (IOException e) {
			// и как отрабатывать?
		}
	}

	protected abstract void chooseAction(Long code, JsonNode jsonNode,
	                                     WebSocketSession session);
}
