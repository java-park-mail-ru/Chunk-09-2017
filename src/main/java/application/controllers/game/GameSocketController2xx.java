package application.controllers.game;

import application.models.game.field.Step;
import application.models.game.game.GameActive;
import application.models.game.game.GamePrepare;
import application.models.game.player.PlayerWatcher;
import application.models.user.UserSignUp;
import application.services.game.GameSocketStatusCode;
import application.services.game.GameTools;
import application.services.user.UserService;
import application.services.user.UserTools;
import application.views.game.StatusCode;
import application.views.game.statuscode1xx.StatusCode1xx;
import application.views.game.statuscode2xx.StatusCode204;
import application.views.game.statuscode3xx.StatusCode3xx;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import javax.validation.constraints.NotNull;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

@Component
public final class GameSocketController2xx extends GameSocketController {

    private final ConcurrentHashMap<Long, GameActive> activeGames;
    private final CopyOnWriteArraySet<WebSocketSession> subscribers;

    final UserService userService;

    GameSocketController2xx(UserService userService) {
        this.activeGames = new ConcurrentHashMap<>();
        this.subscribers = new CopyOnWriteArraySet<>();
        this.userService = userService;
    }

    @Override
    public void controller(Integer code, JsonNode jsonNode,
                                WebSocketSession session) {

        if (code.equals(GameSocketStatusCode.STEP.getValue())) {
            step(session, jsonNode);
            return;
        }
        if (code.equals(GameSocketStatusCode.WATCH.getValue())) {
            watch(session, jsonNode);
            return;
        }
        if (code.equals(GameSocketStatusCode.REWATCH.getValue())) {
            rewatch(session, jsonNode);
            return;
        }
        if (code.equals(GameSocketStatusCode.SUBSCRIBE_A.getValue())) {
            subscribe(session);
            return;
        }
        if (code.equals(GameSocketStatusCode.UNSUBSCRIBE_A.getValue())) {
            unsubscribe(session);
            return;
        }

        final ObjectMapper mapper = new ObjectMapper();
        final String payload = toJSON(mapper,
                new StatusCode3xx(GameSocketStatusCode.UNEXPECTED));
        this.sendMessage(session, payload);
    }

    @Override
    public void emergencyDiconnect(WebSocketSession session, Long userID, Long gameID) {

        final GameActive game = activeGames.get(gameID);
        if (game != null) {
            game.playerOff(userID);
        }
    }

    void addGame(GamePrepare readyGame) {
        activeGames.put(readyGame.getGameID(), new GameActive(readyGame));
        final String payload = this.toJSON(new ObjectMapper(),
                new StatusCode204(readyGame.getGameID()));
        this.notifySubscribers(payload);
    }

    private void step(WebSocketSession session, JsonNode jsonNode) {

        final Long userID = (Long) session.getAttributes().get(UserTools.USER_ID_ATTR);
        final Long gameID = (Long) session.getAttributes().get(GameTools.GAME_ID_ATTR);

        final ObjectMapper mapper = new ObjectMapper();
        final String payload;

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
            if (!game.getGameOver()) {
                payload = this.toJSON(mapper, new StatusCode3xx(
                        GameSocketStatusCode.FALSE));
                this.sendMessage(session, payload);
            } else {
                payload = this.toJSON(mapper, new StatusCode204(game.getGameID()));
                this.notifySubscribers(payload);
                activeGames.remove(gameID);
            }
        }
    }

    private void watch(WebSocketSession session, JsonNode jsonNode) {

        unsubscribe(session);

        final Long userID = (Long) session.getAttributes().get(UserTools.USER_ID_ATTR);
        final Long prevGameID = (Long) session.getAttributes().get(GameTools.GAME_ID_ATTR);

        final ObjectMapper mapper = new ObjectMapper();
        final String payload;

        if (userID == null) {
            payload = this.toJSON(mapper, new StatusCode3xx(
                    GameSocketStatusCode.NOT_AUTHORIZED));
            this.sendMessage(session, payload);
            return;
        }

        if (prevGameID != null) {
            payload = this.toJSON(mapper, new StatusCode3xx(
                    GameSocketStatusCode.ALREADY_PLAY, prevGameID));
            this.sendMessage(session, payload);
            return;
        }

        if (!jsonNode.hasNonNull(GameTools.GAME_ID_ATTR)) {
            payload = this.toJSON(mapper, new StatusCode3xx(
                    GameSocketStatusCode.ATTR));
            this.sendMessage(session, payload);
            return;
        }

        final Long gameID = jsonNode.get(GameTools.GAME_ID_ATTR).asLong();
        final GameActive game = activeGames.get(gameID);

        if (game == null) {
            payload = this.toJSON(mapper, new StatusCode3xx(
                    GameSocketStatusCode.NOT_EXIST, gameID));
            this.sendMessage(session, payload);
            return;
        }

        final UserSignUp user = userService.getUserById(userID);
        final PlayerWatcher watcher = new PlayerWatcher(user, session);

        game.addWatcher(watcher);
    }

    private void rewatch(WebSocketSession session, JsonNode jsonNode) {

        final Long userID = (Long) session.getAttributes().get(UserTools.USER_ID_ATTR);
        final ObjectMapper mapper = new ObjectMapper();
        final String payload;

        if (userID == null) {
            payload = this.toJSON(mapper, new StatusCode3xx(
                    GameSocketStatusCode.NOT_AUTHORIZED));
            this.sendMessage(session, payload);
            return;
        }

        if (!jsonNode.hasNonNull(GameTools.GAME_ID_ATTR)) {
            payload = this.toJSON(mapper, new StatusCode3xx(
                    GameSocketStatusCode.ATTR));
            this.sendMessage(session, payload);
            return;
        }

        final Long gameID = jsonNode.get(GameTools.GAME_ID_ATTR).asLong();
        final GameActive game = activeGames.get(gameID);

        if (game == null) {
            payload = this.toJSON(mapper, new StatusCode3xx(
                    GameSocketStatusCode.NOT_EXIST, gameID));
            this.sendMessage(session, payload);
            return;
        }

        game.removeWatcher(userID);
    }

    private void subscribe(@NotNull WebSocketSession session) {
        subscribers.add(session);
    }

    private void unsubscribe(WebSocketSession session) {
        subscribers.remove(session);
    }

    private synchronized void notifySubscribers(final String payload) {
        subscribers.forEach(websession -> {
            if (websession.isOpen()) {
                this.sendMessage(websession, payload);
            } else {
                this.unsubscribe(websession);
            }
        });
    }
}
