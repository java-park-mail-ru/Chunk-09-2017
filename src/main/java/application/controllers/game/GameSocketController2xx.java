package application.controllers.game;

import application.models.game.field.Step;
import application.models.game.game.GameActive;
import application.models.game.game.GamePrepare;
import application.services.game.GameSocketStatusCode;
import application.services.game.GameTools;
import application.services.user.UserTools;
import application.views.game.statuscode3xx.StatusCode3xx;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.concurrent.ConcurrentHashMap;

@Component
public final class GameSocketController2xx extends GameSocketController {

    private final ConcurrentHashMap<Long, GameActive> activeGames;

    GameSocketController2xx() {
        this.activeGames = new ConcurrentHashMap<>();
    }

    @Override
    protected void chooseAction(Integer code, JsonNode jsonNode,
                                WebSocketSession session) {

        if (code.equals(GameSocketStatusCode.STEP.getValue())) {
            step(session, jsonNode);
            return;
        }

        final ObjectMapper mapper = new ObjectMapper();
        final String payload = toJSON(mapper,
                new StatusCode3xx(GameSocketStatusCode.UNEXPECTED));
        this.sendMessage(session, payload);
    }

    @Override
    public void emergencyDiconnect(WebSocketSession session, Long userID, Long gameID) {
//        final GameActive game = activeGames.get(gameID);
//        if (game != null) {
//            activeGames.
//        }
        // TODO gamerOff
    }

    void addGame(GamePrepare readyGame) {
        activeGames.put(readyGame.getGameID(), new GameActive(readyGame));
    }

    private void step(WebSocketSession session, JsonNode jsonNode) {

        final Long userID = (Long) session.getAttributes().get(UserTools.USER_ID_ATTR);
        final Long gameID = (Long) session.getAttributes().get(GameTools.GAME_ID_ATTR);

        final ObjectMapper mapper = new ObjectMapper();
        String payload;

        // Проверка 300 (авторизация)
        if (gameID == null || userID == null) {
            payload = this.toJSON(
                    mapper, new StatusCode3xx(GameSocketStatusCode.NOT_AUTHORIZED));
            this.sendMessage(session, payload);
            return;
        }

        final GameActive game = activeGames.get(gameID);

        // Проверка 307 (чей ход)
        if (!game.getCurrentUserID().equals(userID)) {
            payload = this.toJSON(
                    mapper, new StatusCode3xx(GameSocketStatusCode.TURN));
            this.sendMessage(session, payload);
            return;
        }

        // Вытащить аттрибуты
        final Step step;
        try {
            step = mapper.treeToValue(jsonNode.get(GameTools.STEP_ATTR), Step.class);
        } catch (JsonProcessingException e) {
            payload = this.toJSON(
                    mapper, new StatusCode3xx(GameSocketStatusCode.ATTR));
            this.sendMessage(session, payload);
            return;
        }

        // Совершить ход
        if (game.makeStep(step)) {
            if (game.getGameOver()) {
                payload = this.toJSON(
                        mapper, new StatusCode3xx(GameSocketStatusCode.FALSE));
                this.sendMessage(session, payload);
            }
            // TODO notify watchers
            activeGames.remove(gameID);
        }
    }
}
