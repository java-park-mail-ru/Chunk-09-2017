package application.websockets;

import application.controllers.game.GameSocketController1xx;
import application.controllers.game.GameSocketController2xx;
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

    private final GameSocketController1xx gameSocketController1xx;
    private final GameSocketController2xx gameSocketController2xx;
    private final ObjectMapper mapper = new ObjectMapper();
    private static final Logger LOGGER = LoggerFactory.getLogger("GameDefense");
    //  почему не работает?
    //  @Autowired
    //  private Logger LOGGER;

    WebSocketGameHandler(GameSocketController1xx controller1xx,
                         GameSocketController2xx controller2xx) {

        this.gameSocketController1xx = controller1xx;
        this.gameSocketController2xx = controller2xx;
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
            LOGGER.warn(GameSocketStatusCode.NOT_AUTHORIZED.toString());
        } else {
            session.sendMessage(new TextMessage(
                    mapper.writeValueAsString(new StatusCode112(userID))
            ));
            LOGGER.info("Succesfull connect: userID=" + userID + ", session=" + session);
        }
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {

        final JsonNode jsonNode = mapper.readTree(message.getPayload());
        final Integer code = jsonNode.get("code").asInt();

        if (GameSocketStatusCode.isPreparing(code)) {
            gameSocketController1xx.controller(code, jsonNode, session);
            return;
        }
        if (GameSocketStatusCode.isPlaying(code)) {
            gameSocketController2xx.controller(code, jsonNode, session);
            return;
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {

        LOGGER.info("Disconnect: " + session);
        final Long userID = (Long) session.getAttributes().get(UserTools.USER_ID_ATTR);
        final Long gameID = (Long) session.getAttributes().get(GameTools.GAME_ID_ATTR);
        if (userID == null || gameID == null) {
            return;
        }
        gameSocketController1xx.emergencyDiconnect(session, userID, gameID);
        gameSocketController2xx.emergencyDiconnect(session, userID, gameID);
    }
}
