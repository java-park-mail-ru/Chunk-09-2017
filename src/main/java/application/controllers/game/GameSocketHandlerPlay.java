package application.controllers.game;

import application.exceptions.game.GameException;
import application.models.game.field.Step;
import application.models.game.game.GameActive;
import application.models.game.game.GamePrepare;
import application.models.game.player.PlayerWatcher;
import application.models.user.UserSignUp;
import application.services.game.GameSocketStatusCode;
import application.services.game.GameTools;
import application.services.user.UserService;
import application.services.user.UserTools;
import application.views.game.active.StatusCodeGameover;
import application.views.game.error.StatusCodeError;
import application.views.game.error.StatusCodeErrorAttr;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.concurrent.*;

@Component
public final class GameSocketHandlerPlay extends GameSocketHandler {

    private final ConcurrentHashMap<Long, GameActive> activeGames;
    private final CopyOnWriteArraySet<WebSocketSession> subscribers;
    private ScheduledExecutorService executor;

    private final UserService userService;

    GameSocketHandlerPlay(UserService userService, ObjectMapper mapper,
                          ScheduledExecutorService executor) {
        super(mapper);
        this.activeGames = new ConcurrentHashMap<>();
        this.subscribers = new CopyOnWriteArraySet<>();
        this.executor = executor;
        this.userService = userService;

        this.executor.scheduleWithFixedDelay(
                this::destroyFinishedGames,
                GameTools.TIME_BETWEEN_CHECKS_MIN,
                GameTools.TIME_BETWEEN_CHECKS_MIN,
                TimeUnit.MINUTES
        );
    }

    @Override
    public void handler(Integer code, JsonNode jsonNode,
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
            dewatch(session, jsonNode);
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
        if (code.equals(GameSocketStatusCode.LEAVE.getValue())) {
            leave(session);
            return;
        }

        final String payload = toJSON(
                new StatusCodeError(GameSocketStatusCode.UNEXPECTED));
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
        activeGames.put(readyGame.getGameID(), new GameActive(readyGame, executor));

        // Оповещение подписчиков
        final String payload = this.toJSON(
                new StatusCodeGameover(readyGame.getGameID()));
        this.notifySubscribers(payload);

        getGameLogger().info("Game #" + readyGame.getGameID() + " is started");
    }

    private void step(WebSocketSession session, JsonNode jsonNode) {

        final Long userID = (Long) session.getAttributes().get(UserTools.USER_ID_ATTR);
        final Long gameID = (Long) session.getAttributes().get(GameTools.GAME_ID_ATTR);

        final String payload;

        // Проверка 300 (авторизация)
        if (gameID == null || userID == null) {
            payload = this.toJSON(
                    new StatusCodeError(GameSocketStatusCode.NOT_AUTHORIZED));
            this.sendMessage(session, payload);
            return;
        }

        final GameActive game = activeGames.get(gameID);

        // Проверка 307 (чей ход)
        if (!game.getCurrentUserID().equals(userID)) {
            payload = this.toJSON(
                    new StatusCodeError(GameSocketStatusCode.TURN));
            this.sendMessage(session, payload);
            return;
        }

        // Вытащить аттрибуты
        final Step step;
        try {
            step = getMapper().readValue(
                    jsonNode.get(GameTools.STEP_ATTR).toString(), Step.class);
        } catch (IOException ignore) {
            sendMessage(session, toJSON(new StatusCodeErrorAttr(GameTools.STEP_ATTR)));
            return;
        }
        if (!jsonNode.hasNonNull(GameTools.STEP_ID_ATTR)) {
            payload = toJSON(new StatusCodeErrorAttr(GameTools.STEP_ID_ATTR));
            sendMessage(session, payload);
            throw new GameException(payload);
        }
        final Long stepID = jsonNode.get(GameTools.STEP_ID_ATTR).asLong();

        // Совершить ход
        if (!game.makeStep(step, stepID)) {
            payload = this.toJSON(new StatusCodeError(GameSocketStatusCode.FALSE));
            this.sendMessage(session, payload);
        }
    }

    private void watch(WebSocketSession session, JsonNode jsonNode) {

        unsubscribe(session);

        final Long userID = (Long) session.getAttributes().get(UserTools.USER_ID_ATTR);
        final Long prevGameID = (Long) session.getAttributes().get(GameTools.GAME_ID_ATTR);

        if (userID == null) {
            sendMessage(session, toJSON(
                    new StatusCodeError(GameSocketStatusCode.NOT_AUTHORIZED)));
            return;
        }

        if (prevGameID != null) {
            sendMessage(session, toJSON(
                    new StatusCodeError(GameSocketStatusCode.ALREADY_PLAY, prevGameID)));
            return;
        }

        if (!jsonNode.hasNonNull(GameTools.GAME_ID_ATTR)) {
            sendMessage(session, toJSON(
                    new StatusCodeErrorAttr(GameTools.GAME_ID_ATTR)));
            return;
        }

        final Long gameID = jsonNode.get(GameTools.GAME_ID_ATTR).asLong();
        final GameActive game = activeGames.get(gameID);

        if (game == null) {
            sendMessage(session, toJSON(
                    new StatusCodeError(GameSocketStatusCode.NOT_EXIST, gameID)));
            return;
        }

        final UserSignUp user = userService.getUserById(userID);
        final PlayerWatcher watcher = new PlayerWatcher(user, session);

        game.addWatcher(watcher);
    }

    private void dewatch(WebSocketSession session, JsonNode jsonNode) {

        final String payload;

        if (!jsonNode.hasNonNull(GameTools.GAME_ID_ATTR)) {
            payload = toJSON(new StatusCodeErrorAttr(GameTools.GAME_ID_ATTR));
            sendMessage(session, payload);
            throw new GameException(payload);
        }

        final Long gameID = jsonNode.get(GameTools.GAME_ID_ATTR).asLong();
        final GameActive game = activeGames.get(gameID);

        if (game == null) {
            payload = toJSON(new StatusCodeError(GameSocketStatusCode.NOT_EXIST, gameID));
            sendMessage(session, payload);
            throw new GameException(payload);
        }

        final Long userID = (Long) session.getAttributes().get(UserTools.USER_ID_ATTR);
        game.removeWatcher(userID);
    }

    private void leave(WebSocketSession session) {

        final Long userID = (Long) session.getAttributes().get(UserTools.USER_ID_ATTR);
        final Long gameID = (Long) session.getAttributes().get(GameTools.GAME_ID_ATTR);

        final String payload;

        if (gameID == null || userID == null) {
            payload = this.toJSON(
                    new StatusCodeError(GameSocketStatusCode.NOT_AUTHORIZED));
            this.sendMessage(session, payload);
            return;
        }

        final GameActive game = activeGames.get(gameID);

        if (game == null) {
            payload = this.toJSON(
                    new StatusCodeError(GameSocketStatusCode.NOT_EXIST));
            this.sendMessage(session, payload);
            return;
        }

        game.playerOff(userID);
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

    public void destroy(Long gameID) {

        final GameActive destroyingGame = activeGames.get(gameID);
        if (destroyingGame == null) {
            return;
        }

        if (destroyingGame.getGameOver()) {
            this.notifySubscribers(this.toJSON(
                    new StatusCodeGameover(destroyingGame.getGameID())));
            activeGames.remove(gameID);
            getGameLogger().info("Game #" + gameID + " is ended");
        }
    }


    private void destroyFinishedGames() {

        for (GameActive game : activeGames.values()) {
            if (game.getGameOver()) {
                final Long gameID = game.getGameID();
                this.notifySubscribers(this.toJSON(new StatusCodeGameover(gameID)));

                activeGames.remove(gameID);
                getGameLogger().info("Game #" + gameID + " is ended");
            }
        }
    }
}