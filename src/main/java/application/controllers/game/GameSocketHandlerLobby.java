package application.controllers.game;

import application.models.game.field.Field;
import application.models.game.game.GamePrepare;
import application.models.game.player.PlayerBot;
import application.models.game.player.PlayerGamer;
import application.models.game.player.PlayerWatcher;
import application.services.game.GameTools;
import application.services.game.GameSocketStatusCode;
import application.services.user.UserService;
import application.services.user.UserTools;
import application.views.game.statuscode1xx.StatusCode1xx;
import application.views.game.statuscode1xx.StatusCode111;
import application.views.game.statuscode3xx.StatusCode3xx;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import javax.validation.constraints.NotNull;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicLong;


@Component
public final class GameSocketHandlerLobby extends GameSocketHandler {

    private final GameSocketHandlerPlay playController;

    private final CopyOnWriteArraySet<WebSocketSession> subscribers;
    private final ConcurrentHashMap<Long, GamePrepare> preparingGames;
    private final AtomicLong generatorGameID;
    private final UserService userService;


    public GameSocketHandlerLobby(GameSocketHandlerPlay playController,
                                  UserService userService, ObjectMapper mapper) {
        super(mapper);
        this.playController = playController;
        this.subscribers = new CopyOnWriteArraySet<>();
        this.preparingGames = new ConcurrentHashMap<>();
        this.generatorGameID = new AtomicLong();
        this.userService = userService;
    }

    @Override
    public void controller(Integer code, JsonNode jsonNode,
                                final WebSocketSession session) {

        if (code.equals(GameSocketStatusCode.CREATE.getValue())) {
            create(session, jsonNode);
            return;
        }
        if (code.equals(GameSocketStatusCode.CONNECT_ACTIVE.getValue())) {
            connectActive(session, jsonNode);
            return;
        }
        if (code.equals(GameSocketStatusCode.CONNECT_WATCHER.getValue())) {
            connectWatcher(session, jsonNode);
            return;
        }
        if (code.equals(GameSocketStatusCode.EXIT.getValue())) {
            exit(session);
            return;
        }
        if (code.equals(GameSocketStatusCode.STATUS.getValue())) {
            status(session, jsonNode);
            return;
        }
        if (code.equals(GameSocketStatusCode.START.getValue())) {
            start(session);
            return;
        }
        // todo remove bot

        if (code.equals(GameSocketStatusCode.SUBSCRIBE_P.getValue())) {
            subscribe(session);
            return;
        }
        if (code.equals(GameSocketStatusCode.UNSUBSCRIBE_P.getValue())) {
            unsubscribe(session);
            return;
        }
        if (code.equals(GameSocketStatusCode.ADD_BOT.getValue())) {
            addBot(session, jsonNode);
            return;
        }

        if (code.equals(GameSocketStatusCode.DESTROY.getValue())) {
            destroy(session);
            return;
        }
        if (code.equals(GameSocketStatusCode.FULL_STATUS.getValue())) {
            fullStatus(session);
            return;
        }

        // Запрашиваемый код не найден
        final String payload = toJSON(
                new StatusCode3xx(GameSocketStatusCode.UNEXPECTED));
        this.sendMessage(session, payload);
    }

    @Override
    public void emergencyDiconnect(WebSocketSession session, Long userID, Long gameID) {
        final GamePrepare game = preparingGames.get(gameID);
        final String payload;
        if (game != null) {
            this.exit(session);
        } else {
            this.unsubscribe(session);
        }
    }

    public void create(final WebSocketSession session, JsonNode jsonNode) {

        unsubscribe(session);
        final String payload;

        // Проверка 301
        Long newGameID = (Long) session.getAttributes().get(GameTools.GAME_ID_ATTR);
        if (newGameID != null) {
            payload = this.toJSON(new StatusCode3xx(
                    GameSocketStatusCode.ALREADY_PLAY, newGameID)
           );
            this.sendMessage(session, payload);
            return;
        }

        final Long masterID = (Long) session.getAttributes().get(UserTools.USER_ID_ATTR);
        final PlayerGamer master = new PlayerGamer(userService.getUserById(masterID), session);


        // Проверка 308 (наличие необходимых атрибутов)
        if (!jsonNode.hasNonNull(GameTools.NUMBER_OF_PLAYERS)
                || !jsonNode.hasNonNull(GameTools.MAX_X_ATTR)
                || !jsonNode.hasNonNull(GameTools.MAX_Y_ATTR)) {

            payload = this.toJSON(new StatusCode3xx(
                    GameSocketStatusCode.ATTR)
           );
            this.sendMessage(session, payload);
            return;
        }

        newGameID = generatorGameID.getAndIncrement();
        final Integer numberOfPlayers = jsonNode.get(GameTools.NUMBER_OF_PLAYERS).asInt();
        final Field field = new Field(
                jsonNode.get(GameTools.MAX_X_ATTR).asInt(),
                jsonNode.get(GameTools.MAX_Y_ATTR).asInt()
       );
        final GamePrepare newGame = new GamePrepare(field, newGameID, numberOfPlayers, masterID);

        newGame.addGamer(master);
        preparingGames.put(newGameID, newGame);

        // Оповестить подписчиков
        payload = this.toJSON(new StatusCode1xx(
                GameSocketStatusCode.SUBSCRIBE_P, newGame
       ));
        this.notifySubscribers(payload);

        getGameLogger().info("Create prepare Game #" + newGameID);
    }

    private void connectActive(final WebSocketSession session, JsonNode jsonNode) {

        final String payload;

        unsubscribe(session);

        // Проверка 301
        Long gameID = (Long) session.getAttributes().get("gameID");
        if (gameID != null) {
            payload = this.toJSON(new StatusCode3xx(
                    GameSocketStatusCode.ALREADY_PLAY, gameID)
           );
            this.sendMessage(session, payload);
            return;
        }

        if (!jsonNode.hasNonNull(GameTools.GAME_ID_ATTR)) {
            payload = this.toJSON(new StatusCode3xx(GameSocketStatusCode.ATTR));
            this.sendMessage(session, payload);
            return;
        }
        gameID = jsonNode.get(GameTools.GAME_ID_ATTR).asLong();
        final GamePrepare game = preparingGames.get(gameID);

        if (game == null) {
            payload = this.toJSON(new StatusCode3xx(
                    GameSocketStatusCode.NOT_EXIST, gameID));
            this.sendMessage(session, payload);
            return;
        }

        final Long userID = (Long) session.getAttributes().get(UserTools.USER_ID_ATTR);
        final PlayerGamer gamer = new PlayerGamer(userService.getUserById(userID), session);

        // Проверка 309 (мест нет)
        if (game.isReady()) {
            payload = this.toJSON(new StatusCode3xx(GameSocketStatusCode.FULL));
            this.sendMessage(session, payload);
            return;
        }
        game.addGamer(gamer);

        // Оповестить подписчиков
        payload = this.toJSON(new StatusCode1xx(
                GameSocketStatusCode.SUBSCRIBE_P, game
       ));
        this.notifySubscribers(payload);
    }

    private void connectWatcher(WebSocketSession session, JsonNode jsonNode) {

        unsubscribe(session);

        final String payload;

        if (!jsonNode.hasNonNull(GameTools.GAME_ID_ATTR)) {
            payload = this.toJSON(new StatusCode3xx(GameSocketStatusCode.ATTR));
            this.notifySubscribers(payload);
            return;
        }

        final Long gameID = jsonNode.get("gameID").asLong();
        final GamePrepare game = preparingGames.get(gameID);

        if (game == null) {
            payload = this.toJSON(
                    new StatusCode3xx(GameSocketStatusCode.NOT_EXIST, gameID));
            this.notifySubscribers(payload);
            return;
        }

        final Long watcherID = (Long) session.getAttributes().get(UserTools.USER_ID_ATTR);
        final PlayerWatcher watcher = new PlayerWatcher(userService.getUserById(watcherID), session);

        game.addWatcher(watcher);

        // Оповестить подписчиков
        payload = this.toJSON(
                new StatusCode1xx(GameSocketStatusCode.SUBSCRIBE_P, game));
        this.notifySubscribers(payload);
    }

    private void exit(WebSocketSession session) {
        final Long userID = (Long) session.getAttributes().get(UserTools.USER_ID_ATTR);
        final Long gameID = (Long) session.getAttributes().get(GameTools.GAME_ID_ATTR);
        if (gameID == null || userID == null) {
            return;
        }

        final String payload;

        final GamePrepare game = preparingGames.get(gameID);

        if (game == null) {
            payload = this.toJSON(new StatusCode3xx(GameSocketStatusCode.ATTR));
            this.sendMessage(session, payload);
            return;
        }

        if (userID.equals(game.getMasterID())) {
            this.destroy(session);
            return;
        }

        game.removeGamer(userID);

        payload = this.toJSON(new StatusCode1xx(GameSocketStatusCode.EXIT, game));
        notifySubscribers(payload);
    }

    private void status(WebSocketSession session, JsonNode jsonNode) {

        final String payload;

        if (!jsonNode.hasNonNull(GameTools.GAME_ID_ATTR)) {
            payload = this.toJSON(new StatusCode3xx(GameSocketStatusCode.ATTR));
            this.sendMessage(session, payload);
            return;
        }

        final Long gameID = jsonNode.get(GameTools.GAME_ID_ATTR).asLong();
        final GamePrepare game = preparingGames.get(gameID);

        if (game == null) {
            payload = this.toJSON(new StatusCode3xx(
                    GameSocketStatusCode.NOT_EXIST, gameID));
            this.sendMessage(session, payload);
            return;
        }

        payload = this.toJSON(new StatusCode1xx(GameSocketStatusCode.STATUS, game));
        this.sendMessage(session, payload);
    }

    private void fullStatus(WebSocketSession session) {

        final String paylod = this.toJSON(
                new StatusCode111(
                        GameSocketStatusCode.FULL_STATUS, preparingGames.values()
                )
        );
        this.sendMessage(session, paylod);
    }

    private void start(WebSocketSession session) {

        final Long userID = (Long) session.getAttributes().get(UserTools.USER_ID_ATTR);
        final Long gameID = (Long) session.getAttributes().get(GameTools.GAME_ID_ATTR);

        final String payload;

        // Проверка 300 (авторизация)
        if (gameID == null || userID == null) {
            payload = this.toJSON(
                    new StatusCode3xx(GameSocketStatusCode.NOT_AUTHORIZED));
            this.sendMessage(session, payload);
            return;
        }

        final GamePrepare game = preparingGames.get(gameID);

        // Проверка 303 (хозяин игры)
        if (!game.getMasterID().equals(userID)) {
            payload = this.toJSON(
                    new StatusCode3xx(GameSocketStatusCode.FORBIDDEN));
            this.sendMessage(session, payload);
            return;
        }

        // Проверка 304 (хватает ли игроков)
        if (!game.isReady()) {
            payload = this.toJSON(
                    new StatusCode3xx(GameSocketStatusCode.NOT_ENOUGH));
            this.sendMessage(session, payload);
            return;
        }

        playController.addGame(preparingGames.remove(gameID));

        payload = this.toJSON(new StatusCode1xx(GameSocketStatusCode.START, gameID));
        notifySubscribers(payload);
    }

    private void destroy(WebSocketSession session) {

        final Long userID = (Long) session.getAttributes().get(UserTools.USER_ID_ATTR);
        final Long gameID = (Long) session.getAttributes().get(GameTools.GAME_ID_ATTR);
        final String payload;

        // Проверка 300 (авторизация)
        if (gameID == null || userID == null) {
            payload = this.toJSON(
                    new StatusCode3xx(GameSocketStatusCode.NOT_AUTHORIZED));
            this.sendMessage(session, payload);
            return;
        }

        final GamePrepare game = preparingGames.get(gameID);

        if (game == null) {
            payload = this.toJSON(
                    new StatusCode3xx(GameSocketStatusCode.NOT_EXIST, gameID)
            );
            this.sendMessage(session, payload);
            return;
        }

        // Проверка 303 (хозяин игры)
        if (!game.getMasterID().equals(userID)) {
            payload = this.toJSON(
                    new StatusCode3xx(GameSocketStatusCode.FORBIDDEN, gameID)
            );
            this.sendMessage(session, payload);
            return;
        }

        game.destroy();
        preparingGames.remove(gameID);
        payload = this.toJSON(
                new StatusCode3xx(GameSocketStatusCode.DESTROY, gameID)
        );
        notifySubscribers(payload);
    }

    private void addBot(WebSocketSession session, JsonNode jsonNode) {

        final Long userID = (Long) session.getAttributes().get(UserTools.USER_ID_ATTR);
        final Long gameID = (Long) session.getAttributes().get(GameTools.GAME_ID_ATTR);
        final String payload;

        // Проверка 300 (авторизация)
        if (gameID == null || userID == null) {
            payload = this.toJSON(
                    new StatusCode3xx(GameSocketStatusCode.NOT_AUTHORIZED));
            this.sendMessage(session, payload);
            return;
        }

        final GamePrepare game = preparingGames.get(gameID);

        if (game == null) {
            payload = this.toJSON(
                    new StatusCode3xx(GameSocketStatusCode.NOT_EXIST, gameID));
            this.sendMessage(session, payload);
            return;
        }

        // Проверка 303 (хозяин игры)
        if (!game.getMasterID().equals(userID)) {
            payload = this.toJSON(
                    new StatusCode3xx(GameSocketStatusCode.FORBIDDEN));
            this.sendMessage(session, payload);
            return;
        }

        if (!jsonNode.hasNonNull(GameTools.BOT_LEVEL_ATTR)) {
            payload = this.toJSON(new StatusCode3xx(GameSocketStatusCode.ATTR));
            this.sendMessage(session, payload);
            return;
        }

        final Integer level = jsonNode.get(GameTools.BOT_LEVEL_ATTR).asInt();
        final PlayerBot bot = new PlayerBot(level);

        // Проверка 309 (мест нет)
        if (game.isReady()) {
            payload = this.toJSON(new StatusCode3xx(GameSocketStatusCode.FULL));
            this.sendMessage(session, payload);
            return;
        }
        game.addBot(bot);

        // Оповестить подписчиков
        payload = this.toJSON(new StatusCode1xx(
                GameSocketStatusCode.SUBSCRIBE_P, game
        ));
        this.notifySubscribers(payload);
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
