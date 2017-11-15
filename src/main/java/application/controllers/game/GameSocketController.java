package application.controllers.game;

import application.views.game.StatusCode;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;


public abstract class GameSocketController {

    public abstract void controller(Integer code, JsonNode jsonNode, WebSocketSession session);

    protected final String toJSON(ObjectMapper mapper, StatusCode statusCode) {
        try {
            return mapper.writeValueAsString(statusCode);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    protected final synchronized void sendMessage(final WebSocketSession session,
                                                  String payload) {
        try {
            session.sendMessage(new TextMessage(payload));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public abstract void emergencyDiconnect(WebSocketSession session, Long userID, Long gameID);
}
