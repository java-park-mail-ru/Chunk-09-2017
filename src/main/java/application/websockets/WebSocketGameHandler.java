package application.websockets;

import application.controllers.game.GameSocketHandlerLobby;
import application.controllers.game.GameSocketHandlerPlay;
import application.exceptions.game.GameException;
import application.services.game.GameSocketStatusCode;
import application.services.game.GameTools;
import application.services.user.UserTools;
import application.views.game.statuscode1xx.StatusCode112;
import application.views.game.statuscode3xx.StatusCode3xx;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import java.io.IOException;


@Component
public class WebSocketGameHandler extends AbstractWebSocketHandler {

    private final GameSocketHandlerLobby gameSocketHandlerLobby;
    private final GameSocketHandlerPlay gameSocketHandlerPlay;
    private final Logger logger = LoggerFactory.getLogger(WebSocketGameHandler.class);
    private final ObjectMapper mapper;


    WebSocketGameHandler(GameSocketHandlerLobby lobby,
                         GameSocketHandlerPlay play,
                         ObjectMapper mapper) {

        this.gameSocketHandlerLobby = lobby;
        this.gameSocketHandlerPlay = play;
        this.mapper = mapper;
    }


    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws IOException {

        final Long userID = (Long) session.getAttributes().get(UserTools.USER_ID_ATTR);
        if (userID == null) {
            session.sendMessage(new TextMessage(mapper.writeValueAsString(
                            new StatusCode3xx(GameSocketStatusCode.NOT_AUTHORIZED))));
            session.close(CloseStatus.NOT_ACCEPTABLE);
            logger.warn(GameSocketStatusCode.NOT_AUTHORIZED.toString());
        } else {
            session.sendMessage(new TextMessage(
                    mapper.writeValueAsString(new StatusCode112(userID, null))
            ));
            logger.info("Succesfull connect: userID=" + userID + ", session=" + session);
        }
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {

        final JsonNode jsonNode = mapper.readTree(message.getPayload());
        final Integer code = jsonNode.get("code").asInt();

        try {
            if (GameSocketStatusCode.isPreparing(code)) {
                gameSocketHandlerLobby.handler(code, jsonNode, session);
                return;
            }
            if (GameSocketStatusCode.isPlaying(code)) {
                gameSocketHandlerPlay.handler(code, jsonNode, session);
                return;
            }
        } catch (GameException e) {
            synchronized (e.getSession()) {
                e.getSession().sendMessage(new TextMessage(e.getPayload()));
            }
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {

        logger.info("Disconnect: " + session);
        final Long userID = (Long) session.getAttributes().get(UserTools.USER_ID_ATTR);
        final Long gameID = (Long) session.getAttributes().get(GameTools.GAME_ID_ATTR);
        if (userID == null || gameID == null) {
            return;
        }
        gameSocketHandlerLobby.emergencyDiconnect(session, userID, gameID);
        gameSocketHandlerPlay.emergencyDiconnect(session, userID, gameID);
    }
}
