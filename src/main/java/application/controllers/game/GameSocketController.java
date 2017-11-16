package application.controllers.game;

import application.services.game.GameTools;
import application.views.game.StatusCode;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;


public abstract class GameSocketController {

    public abstract void controller(Integer code, JsonNode jsonNode, WebSocketSession session);

    protected final String toJSON(ObjectMapper mapper, StatusCode statusCode) {
        try {
            return mapper.writeValueAsString(statusCode);
        } catch (IOException e) {
            gameLogger.error(e.getMessage(), e.getCause());
            return null;
        }
    }

    protected final synchronized void sendMessage(final WebSocketSession session,
                                                  String payload) {
        try {
            session.sendMessage(new TextMessage(payload));
        } catch (IOException e) {
            gameLogger.error(e.getMessage(), e.getCause());
        }
    }

    public abstract void emergencyDiconnect(WebSocketSession session, Long userID, Long gameID);

    private final Logger gameLogger = LoggerFactory.getLogger(GameTools.LOGGER_NAME);

    protected final Logger getGameLogger() {
        return gameLogger;
    }
}
