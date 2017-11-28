package application.controllers.game;

import application.exceptions.game.GameException;
import application.models.game.field.Field;
import application.models.game.game.GamePrepare;
import application.models.game.player.PlayerBot;
import application.models.game.player.PlayerGamer;
import application.models.game.player.PlayerWatcher;
import application.services.game.GameTools;
import application.services.game.GameSocketStatusCode;
import application.services.user.UserService;
import application.services.user.UserTools;
import application.views.game.statuscodelobby.StatusCodeLobby;
import application.views.game.statuscodelobby.StatusCodeFullStatus;
import application.views.game.statuscodelobby.StatusCodeWhoami;
import application.views.game.statuscodeerror.StatusCodeError;
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
    public void handler(Integer code, JsonNode jsonNode,
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
        if (code.equals(GameSocketStatusCode.WHOAMI.getValue())) {
            whoami(session);
            return;
        }

        // Запрашиваемый код не найден
        throw new GameException(session, toJSON(
                new StatusCodeError(GameSocketStatusCode.UNEXPECTED)));
    }

    @Override
    public void emergencyDiconnect(WebSocketSession session, Long userID, Long gameID) {
        final GamePrepare game = preparingGames.get(gameID);
        if (game != null) {
            this.exit(session);
        } else {
            this.unsubscribe(session);
        }
    }

    public void create(final WebSocketSession session, JsonNode jsonNode) {

        unsubscribe(session);

        // Проверка 301
        Long newGameID = (Long) session.getAttributes().get(GameTools.GAME_ID_ATTR);
        if (newGameID != null) {
            throw new GameException(session, toJSON(
                    new StatusCodeError(GameSocketStatusCode.ALREADY_PLAY, newGameID)
            ));
        }

        // Проверка 308 (наличие необходимых атрибутов)
        if (!jsonNode.hasNonNull(GameTools.NUMBER_OF_PLAYERS)
                || !jsonNode.hasNonNull(GameTools.MAX_X_ATTR)
                || !jsonNode.hasNonNull(GameTools.MAX_Y_ATTR)) {

            throw new GameException(session, toJSON(
                    new StatusCodeError(GameSocketStatusCode.ATTR)
            ));
        }

        final Long masterID = (Long) session.getAttributes().get(UserTools.USER_ID_ATTR);
        final PlayerGamer master = new PlayerGamer(userService.getUserById(masterID), session);



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
        final String payload = this.toJSON(
                new StatusCodeLobby(GameSocketStatusCode.SUBSCRIBE_P, newGame));
        this.notifySubscribers(payload);

        getGameLogger().info("Create prepare Game #" + newGameID);
    }

    private void connectActive(final WebSocketSession session, JsonNode jsonNode) {

        unsubscribe(session);

        // Проверка 301
        Long gameID = (Long) session.getAttributes().get("gameID");
        if (gameID != null) {
            throw new GameException(session, toJSON(
                    new StatusCodeError(GameSocketStatusCode.ALREADY_PLAY, gameID)
            ));
        }

        if (!jsonNode.hasNonNull(GameTools.GAME_ID_ATTR)) {
            throw new GameException(session, toJSON(
                    new StatusCodeError(GameSocketStatusCode.ATTR)
            ));
        }

        gameID = jsonNode.get(GameTools.GAME_ID_ATTR).asLong();
        final GamePrepare game = preparingGames.get(gameID);

        if (game == null) {
            throw new GameException(session, toJSON(
                    new StatusCodeError(GameSocketStatusCode.NOT_EXIST, gameID)
            ));
        }

        final Long userID = (Long) session.getAttributes().get(UserTools.USER_ID_ATTR);
        final PlayerGamer gamer = new PlayerGamer(userService.getUserById(userID), session);

        // Проверка 309 (мест нет)
        if (game.isReady()) {
            throw new GameException(session, toJSON(
                    new StatusCodeError(GameSocketStatusCode.FULL)
            ));
        }
        game.addGamer(gamer);

        // Оповестить подписчиков
        final String payload = this.toJSON(
                new StatusCodeLobby(GameSocketStatusCode.SUBSCRIBE_P, game));
        this.notifySubscribers(payload);
    }

    private void connectWatcher(WebSocketSession session, JsonNode jsonNode) {

        unsubscribe(session);

        if (!jsonNode.hasNonNull(GameTools.GAME_ID_ATTR)) {
            throw new GameException(session, toJSON(
                    new StatusCodeError(GameSocketStatusCode.ATTR)
            ));
        }

        final Long gameID = jsonNode.get("gameID").asLong();
        final GamePrepare game = preparingGames.get(gameID);

        if (game == null) {
            throw new GameException(session, toJSON(
                    new StatusCodeError(GameSocketStatusCode.NOT_EXIST, gameID)
            ));
        }

        final Long watcherID = (Long) session.getAttributes().get(UserTools.USER_ID_ATTR);
        final PlayerWatcher watcher = new PlayerWatcher(
                userService.getUserById(watcherID), session);

        game.addWatcher(watcher);

        // Оповестить подписчиков
        final String payload = this.toJSON(
                new StatusCodeLobby(GameSocketStatusCode.SUBSCRIBE_P, game));
        this.notifySubscribers(payload);
    }

    private void exit(WebSocketSession session) {

        final GamePrepare game = getGameBySession(session);
        final Long userID = (Long) session.getAttributes().get(UserTools.USER_ID_ATTR);

        if (userID.equals(game.getMasterID())) {
            this.destroy(session);
            return;
        }

        game.removeGamer(userID);

        final String payload = toJSON(new StatusCodeLobby(GameSocketStatusCode.EXIT, game));
        notifySubscribers(payload);
    }

    private void status(WebSocketSession session, JsonNode jsonNode) {

        if (!jsonNode.hasNonNull(GameTools.GAME_ID_ATTR)) {
            throw new GameException(session, toJSON(
                    new StatusCodeError(GameSocketStatusCode.ATTR)
            ));
        }

        final Long gameID = jsonNode.get(GameTools.GAME_ID_ATTR).asLong();
        final GamePrepare game = preparingGames.get(gameID);

        if (game == null) {
            throw new GameException(session, toJSON(
                    new StatusCodeError(GameSocketStatusCode.NOT_EXIST, gameID)
            ));
        }

        final String payload = this.toJSON(
                new StatusCodeLobby(GameSocketStatusCode.STATUS, game));
        this.sendMessage(session, payload);
    }

    private void fullStatus(WebSocketSession session) {

        final String paylod = toJSON(new StatusCodeFullStatus(
                GameSocketStatusCode.FULL_STATUS, preparingGames.values()));
        this.sendMessage(session, paylod);
    }

    private void start(WebSocketSession session) {

        final GamePrepare game = this.getGameBySession(session);
        final Long userID = (Long) session.getAttributes().get(UserTools.USER_ID_ATTR);

        // Проверка 303 (хозяин игры)
        if (!game.getMasterID().equals(userID)) {
            throw new GameException(session, toJSON(
                    new StatusCodeError(GameSocketStatusCode.FORBIDDEN)
            ));
        }

        // Проверка 304 (хватает ли игроков)
        if (!game.isReady()) {
            throw new GameException(session, toJSON(
                    new StatusCodeError(GameSocketStatusCode.NOT_ENOUGH)
            ));
        }

        playController.addGame(preparingGames.remove(game.getGameID()));

        final String payload = toJSON(
                new StatusCodeLobby(GameSocketStatusCode.START, game.getGameID()));
        notifySubscribers(payload);
    }

    private void destroy(WebSocketSession session) {

        final GamePrepare game = this.getGameBySession(session);
        final Long userID = (Long) session.getAttributes().get(UserTools.USER_ID_ATTR);

        // Проверка (хозяин игры)
        if (!game.getMasterID().equals(userID)) {
            throw new GameException(session, toJSON(
                    new StatusCodeError(GameSocketStatusCode.FORBIDDEN, game.getGameID())
            ));
        }

        game.destroy();
        preparingGames.remove(game.getGameID());
        final String payload = this.toJSON(
                new StatusCodeError(GameSocketStatusCode.DESTROY, game.getGameID())
        );
        notifySubscribers(payload);
    }

    private void addBot(WebSocketSession session, JsonNode jsonNode) {

        final GamePrepare game = getGameBySession(session);
        final Long userID = (Long) session.getAttributes().get(UserTools.USER_ID_ATTR);

        // Проверка (хозяин игры)
        if (!game.getMasterID().equals(userID)) {
            throw new GameException(session, toJSON(
                    new StatusCodeError(GameSocketStatusCode.FORBIDDEN, game.getGameID())
            ));
        }

        // Проверка (наличие аттрибутов)
        if (!jsonNode.hasNonNull(GameTools.BOT_LEVEL_ATTR)) {
            throw new GameException(session, toJSON(
                    new StatusCodeError(GameSocketStatusCode.ATTR)
            ));
        }

        // Проверка (свободные места)
        if (game.isReady()) {
            throw new GameException(session, toJSON(
                    new StatusCodeError(GameSocketStatusCode.FULL)
            ));
        }

        final Integer level = jsonNode.get(GameTools.BOT_LEVEL_ATTR).asInt();
        final PlayerBot bot = new PlayerBot(level);
        game.addBot(bot);

        // Оповестить подписчиков
        final String payload = toJSON(
                new StatusCodeLobby(GameSocketStatusCode.SUBSCRIBE_P, game));
        this.notifySubscribers(payload);
    }

    private void whoami(WebSocketSession session) {

        final Long userID = (Long) session.getAttributes().get(UserTools.USER_ID_ATTR);
        final Long gameID = (Long) session.getAttributes().get(GameTools.GAME_ID_ATTR);

        this.sendMessage(session, this.toJSON(new StatusCodeWhoami(userID, gameID)));
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

    private GamePrepare getGameBySession(WebSocketSession session) {

        final Long gameID = (Long) session.getAttributes().get(GameTools.GAME_ID_ATTR);
        if (gameID == null) {
            throw new GameException(session, toJSON(
                    new StatusCodeError(GameSocketStatusCode.NOT_AUTHORIZED)));
        }

        final GamePrepare game = preparingGames.get(gameID);
        if (game == null) {
            throw new GameException(session, toJSON(
                    new StatusCodeError(GameSocketStatusCode.NOT_EXIST, gameID)));
        }

        return game;
    }
}