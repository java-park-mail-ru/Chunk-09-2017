package application.websockets;

import application.controllers.game.GameSocketHandlerLobby;
import application.controllers.game.GameSocketHandlerPlay;
import application.exceptions.game.GameException;
import application.exceptions.game.GameExceptionDestroyActive;
import application.exceptions.game.GameExceptionDestroyPrepare;
import application.services.game.GameSocketStatusCode;
import application.services.game.GameTools;
import application.services.user.UserTools;
import application.views.game.error.StatusCodeError;
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
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;


@Component
public class WebSocketGameHandler extends AbstractWebSocketHandler {

    private final GameSocketHandlerLobby gameSocketHandlerLobby;
    private final GameSocketHandlerPlay gameSocketHandlerPlay;
    private final Logger logger = LoggerFactory.getLogger(WebSocketGameHandler.class);
    private final ObjectMapper mapper;

    private final ConcurrentSkipListSet<Long> userSessions;


    WebSocketGameHandler(GameSocketHandlerLobby lobby,
                         GameSocketHandlerPlay play,
                         ObjectMapper mapper) {

        this.gameSocketHandlerLobby = lobby;
        this.gameSocketHandlerPlay = play;
        this.mapper = mapper;
        this.userSessions = new ConcurrentSkipListSet<>();
    }


    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws IOException {

        final Long userID = (Long) session.getAttributes().get(UserTools.USER_ID_ATTR);
        if (userID == null) {
            session.sendMessage(new TextMessage(mapper.writeValueAsString(
                    new StatusCodeError(GameSocketStatusCode.NOT_AUTHORIZED))));
            session.close(CloseStatus.NOT_ACCEPTABLE);
            logger.warn(GameSocketStatusCode.NOT_AUTHORIZED.toString());
            return;
        }
        if (userSessions.contains(userID)) {
            session.sendMessage(new TextMessage(mapper.writeValueAsString(
                    new StatusCodeError(GameSocketStatusCode.DOUBLE_CONNECTION))));
            session.close(CloseStatus.NOT_ACCEPTABLE);
            logger.warn("UserID #" + userID + ": "
                    + GameSocketStatusCode.DOUBLE_CONNECTION.toString());
            return;
        }
        userSessions.add(userID);
        logger.info("Succesfull connect: userID=" + userID + ", session=" + session);
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
        } catch (GameExceptionDestroyPrepare destroy) {
            gameSocketHandlerLobby.destroy(destroy.getGameID());

        } catch (GameExceptionDestroyActive destroy) {
            gameSocketHandlerPlay.destroy(destroy.getGameID());

        } catch (GameException clientError) {
            logger.info(clientError.getError());
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {

        logger.info("Disconnect: " + session);
        final Long userID = (Long) session.getAttributes().get(UserTools.USER_ID_ATTR);
        final Long gameID = (Long) session.getAttributes().get(GameTools.GAME_ID_ATTR);
        if (userID != null) {
            userSessions.remove(userID);
        }
        if (userID == null || gameID == null) {
            return;
        }
        try {
            gameSocketHandlerLobby.emergencyDiconnect(session, userID, gameID);
            gameSocketHandlerPlay.emergencyDiconnect(session, userID, gameID);

        } catch (GameExceptionDestroyPrepare destroy) {
            gameSocketHandlerLobby.destroy(destroy.getGameID());

        } catch (GameExceptionDestroyActive destroy) {
            gameSocketHandlerPlay.destroy(destroy.getGameID());
        }
    }
}