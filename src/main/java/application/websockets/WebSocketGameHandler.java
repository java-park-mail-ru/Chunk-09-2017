package application.websockets;

import application.controllers.game.GameSocketHandler;
import application.controllers.game.GameSocketHandlerLobby;
import application.controllers.game.GameSocketHandlerPlay;
import application.services.game.GameSocketStatusCode;
import application.services.game.GameTools;
import application.services.user.UserTools;
import application.views.game.statuscode1xx.StatusCode112;
import application.views.game.statuscode3xx.StatusCode3xx;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
    private final ObjectMapper mapper = new ObjectMapper();
//    private final Logger logger = LoggerFactory.getLogger(WebSocketGameHandler.class);
    @Autowired
    private Logger logger;


    WebSocketGameHandler(GameSocketHandlerLobby lobby,
                         GameSocketHandlerPlay play) {

        this.gameSocketHandlerLobby = lobby;
        this.gameSocketHandlerPlay = play;
    }


    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws IOException {

        final Long userID = (Long) session.getAttributes().get(UserTools.USER_ID_ATTR);
        if (userID == null) {
            session.sendMessage(new TextMessage(
                    mapper.writeValueAsString(
                            new StatusCode3xx(GameSocketStatusCode.NOT_AUTHORIZED)
                    )
            ));
            session.close(CloseStatus.NOT_ACCEPTABLE);
            logger.warn(GameSocketStatusCode.NOT_AUTHORIZED.toString());
        } else {
            session.sendMessage(new TextMessage(
                    mapper.writeValueAsString(new StatusCode112(userID))
            ));
            logger.info("Succesfull connect: userID=" + userID + ", session=" + session);
        }
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {

        final JsonNode jsonNode = mapper.readTree(message.getPayload());
        final Integer code = jsonNode.get("code").asInt();

        if (GameSocketStatusCode.isPreparing(code)) {
            gameSocketHandlerLobby.controller(code, jsonNode, session);
            return;
        }
        if (GameSocketStatusCode.isPlaying(code)) {
            gameSocketHandlerPlay.controller(code, jsonNode, session);
            return;
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
