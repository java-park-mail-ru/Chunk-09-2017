package application.controllers.game;

import application.views.game.StatusCode;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;


public abstract class GameSocketController {

	public final void controller(Integer code, JsonNode jsonNode,
	                             WebSocketSession session) {

		new Thread( () -> chooseAction(code, jsonNode, session) ).run();
	}

	protected final synchronized void sendMessage(final WebSocketSession session,
	                                              String payload) {
		try {
			session.sendMessage(new TextMessage(payload));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	protected static synchronized void setAttribute(final WebSocketSession session,
	                                                final String attributeName,
	                                                final Object value) {
		session.getAttributes().put(attributeName, value);
	}

	protected final String toJSON(ObjectMapper mapper, StatusCode statusCode) {
		try {
			return mapper.writeValueAsString(statusCode);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	protected abstract void chooseAction(Integer code, JsonNode jsonNode,
	                                     final WebSocketSession session);

	private final ObjectMapper mapper = new ObjectMapper()
			.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
}
