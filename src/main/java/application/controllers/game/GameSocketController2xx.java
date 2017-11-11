package application.controllers.game;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

@Component
public class GameSocketController2xx extends GameSocketController {

	@Override
	protected void chooseAction(Long code, JsonNode jsonNode,
	                            WebSocketSession session) {

	}
}
