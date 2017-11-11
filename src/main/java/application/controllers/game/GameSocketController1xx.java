package application.controllers.game;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

@Component
public class GameSocketController1xx extends GameSocketController {

	@Override
	protected void chooseAction(Long code, JsonNode jsonNode,
	                                         WebSocketSession session) {

		this.sendMessage(session);
	}
}
