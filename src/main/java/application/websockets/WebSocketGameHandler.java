package application.websockets;

import application.controllers.game.GameSocketController;
import application.controllers.game.GameSocketController1xx;
import application.controllers.game.GameSocketController2xx;
import application.services.game.GameSocketStatusCode;
import application.services.user.UserServiceTools;
import application.views.game.StatusCode;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import java.io.IOException;


@Component
public class WebSocketGameHandler extends AbstractWebSocketHandler {

	private final GameSocketController gameSocketController1xx;
	private final GameSocketController gameSocketController2xx;
	private final ObjectMapper mapper;


	WebSocketGameHandler(GameSocketController1xx controller1xx,
	                     GameSocketController2xx controller2xx) {

		this.gameSocketController1xx = controller1xx;
		this.gameSocketController2xx = controller2xx;
		this.mapper = new ObjectMapper();
		this.mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}


	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws IOException {
		final Long userID = (Long) session.getAttributes().get(UserServiceTools.USER_ID);
		if (userID == null) {
			session.sendMessage(new TextMessage(
					mapper.writeValueAsString(new StatusCode(GameSocketStatusCode.NOT_AUTHORIZED))
			));
			session.close(CloseStatus.NOT_ACCEPTABLE);
		}
	}

	@Override
	public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {

		final JsonNode jsonNode = mapper.readTree(message.getPayload());
		final Long code = jsonNode.get("code").asLong();

		if (GameSocketStatusCode.isPreparing(code)) {
			gameSocketController1xx.controller(code, jsonNode);
			return;
		}
		if (GameSocketStatusCode.isPlaying(code)) {
			gameSocketController2xx.controller(code, jsonNode);
		}
	}
}
