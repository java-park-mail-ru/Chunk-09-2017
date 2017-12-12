package application.controllers.game;

import application.exceptions.game.GameException;
import application.exceptions.game.GameExceptionDestroy;
import application.models.game.field.Field;
import application.models.game.game.GamePrepare;
import application.models.game.player.PlayerBot;
import application.models.game.player.PlayerGamer;
import application.services.game.GameTools;
import application.services.game.GameSocketStatusCode;
import application.services.user.UserService;
import application.services.user.UserTools;
import application.views.game.StatusCodeSendID;
import application.views.game.lobby.*;
import application.views.game.error.StatusCodeErrorAttr;
import application.views.game.active.StatusCodeWhoami;
import application.views.game.error.StatusCodeError;
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

        if (code.equals(GameSocketStatusCode.SUBSCRIBE.getValue())) {
            subscribe(session);
            return;
        }
        if (code.equals(GameSocketStatusCode.UNSUBSCRIBE.getValue())) {
            unsubscribe(session);
            return;
        }
        if (code.equals(GameSocketStatusCode.FULL_STATUS.getValue())) {
            fullStatus(session);
            return;
        }
        if (code.equals(GameSocketStatusCode.CREATE_GAME.getValue())) {
            createNewGame(session, jsonNode);
            return;
        }
        if (code.equals(GameSocketStatusCode.CONNECT_GAME.getValue())) {
            connectToGame(session, jsonNode);
            return;
        }

        if (code.equals(GameSocketStatusCode.ADD_BOT.getValue())) {
            addBot(session, jsonNode);
            return;
        }
        if (code.equals(GameSocketStatusCode.REMOVE_PLAYER.getValue())) {
            exitPlayer(session);
            return;
        }
        if (code.equals(GameSocketStatusCode.KICK_BOT.getValue())) {
            kickBot(session, jsonNode);
            return;
        }

        if (code.equals(GameSocketStatusCode.KICK_PLAYER.getValue())) {
            kickPlayer(session, jsonNode);
            return;
        }
        if (code.equals(GameSocketStatusCode.START_GAME.getValue())) {
            startGame(session);
            return;
        }
        if (code.equals(GameSocketStatusCode.WHOAMI.getValue())) {
            whoami(session);
            return;
        }

        // Запрашиваемый код не найден
        sendMessage(session, toJSON(new StatusCodeError(GameSocketStatusCode.UNEXPECTED)));
    }

    @Override
    public void emergencyDiconnect(WebSocketSession session, Long userID, Long gameID) {
        final GamePrepare game = preparingGames.get(gameID);
        if (game != null) {
            this.exitPlayer(session);
        } else {
            this.unsubscribe(session);
        }
    }

    public void createNewGame(final WebSocketSession session, JsonNode jsonNode) {

        unsubscribe(session);

        // Уже играет
        Long newGameID = (Long) session.getAttributes().get(GameTools.GAME_ID_ATTR);
        if (newGameID != null) {
            sendMessage(session, toJSON(
                    new StatusCodeError(GameSocketStatusCode.ALREADY_PLAY, newGameID)));
            return;
        }

        // Необходимые атрибуты
        checkAttribute(session, jsonNode, GameTools.NUMBER_OF_PLAYERS);
        checkAttribute(session, jsonNode, GameTools.MAX_X_ATTR);
        checkAttribute(session, jsonNode, GameTools.MAX_Y_ATTR);


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
        sendMessage(session, toJSON(
                new StatusCodeLobbyInfoVerbose(GameSocketStatusCode.CREATE_GAME, newGame)));
        preparingGames.put(newGameID, newGame);


        // Оповестить подписчиков
        this.notifySubscribers(toJSON(
                new StatusCodeLobbyInfoCompact(GameSocketStatusCode.NEW_GAME, newGame)));
        getGameLogger().info("Create prepare Game #" + newGameID);
    }

    private void connectToGame(final WebSocketSession session, JsonNode jsonNode) {

        unsubscribe(session);

        // Проверка 301
        Long gameID = (Long) session.getAttributes().get("gameID");
        if (gameID != null) {
            sendMessage(session, toJSON(
                    new StatusCodeError(GameSocketStatusCode.ALREADY_PLAY, gameID)));
            return;
        }

        checkAttribute(session, jsonNode, GameTools.GAME_ID_ATTR);

        gameID = jsonNode.get(GameTools.GAME_ID_ATTR).asLong();
        final GamePrepare game = preparingGames.get(gameID);

        if (game == null) {
            sendMessage(session, toJSON(
                    new StatusCodeError(GameSocketStatusCode.NOT_EXIST, gameID)));
            return;
        }

        final Long userID = (Long) session.getAttributes().get(UserTools.USER_ID_ATTR);
        final PlayerGamer gamer = new PlayerGamer(userService.getUserById(userID), session);

        // Проверка 309 (мест нет)
        if (game.isReady()) {
            sendMessage(session, toJSON(
                    new StatusCodeError(GameSocketStatusCode.FULL)));
            return;
        }

        game.addGamer(gamer);

        // Передать игроку полную инфу об игре
        sendMessage(session, toJSON(
                new StatusCodeLobbyInfoVerbose(GameSocketStatusCode.CONNECT_GAME, game)));
        // Оповестить подписчиков
        this.notifySubscribers(toJSON(
                new StatusCodeLobbyInfoCompact(GameSocketStatusCode.UPDATE_GAME, game)));
    }

    private void exitPlayer(WebSocketSession session) {

        final GamePrepare game = getGameBySession(session);
        final Long userID = (Long) session.getAttributes().get(UserTools.USER_ID_ATTR);

        try {
            game.removeGamer(userID);
        } catch (GameExceptionDestroy destroy) {
            destroy(destroy.getGameID());
        }
        notifySubscribers(toJSON(
                new StatusCodeLobbyInfoCompact(GameSocketStatusCode.UPDATE_GAME, game)));
    }

    private void kickPlayer(WebSocketSession session, JsonNode jsonNode) {

        checkAttribute(session, jsonNode, GameTools.KICK_USER_ATTR);
        final Long kickID = jsonNode.get(GameTools.KICK_USER_ATTR).asLong();
        final Long userID = (Long) session.getAttributes().get(UserTools.USER_ID_ATTR);

        final GamePrepare game = getGameBySession(session);
        if (!userID.equals(game.getMasterID())) {
            sendMessage(session, toJSON(new StatusCodeError(GameSocketStatusCode.FORBIDDEN)));
            return;
        }
        try {
            game.removeGamer(kickID);
        } catch (GameExceptionDestroy destroy) {
            destroy(destroy.getGameID());
        }
        notifySubscribers(toJSON(
                new StatusCodeLobbyInfoCompact(GameSocketStatusCode.UPDATE_GAME, game)));
    }

    private void kickBot(WebSocketSession session, JsonNode jsonNode) {

        checkAttribute(session, jsonNode, GameTools.KICK_BOT_ATTR);
        final Long kickID = jsonNode.get(GameTools.KICK_BOT_ATTR).asLong();
        final Long userID = (Long) session.getAttributes().get(UserTools.USER_ID_ATTR);

        final GamePrepare game = getGameBySession(session);
        if (!userID.equals(game.getMasterID())) {
            sendMessage(session, toJSON(new StatusCodeError(GameSocketStatusCode.FORBIDDEN)));
            return;
        }

        game.removeBot(kickID);
        notifySubscribers(toJSON(
                new StatusCodeLobbyInfoCompact(GameSocketStatusCode.UPDATE_GAME, game)));
    }

    private void fullStatus(WebSocketSession session) {

        final String paylod = toJSON(new StatusCodeLobbyInfoFull(preparingGames.values()));
        this.sendMessage(session, paylod);
    }

    private void startGame(WebSocketSession session) {

        final GamePrepare game = this.getGameBySession(session);
        final Long userID = (Long) session.getAttributes().get(UserTools.USER_ID_ATTR);

        // Проверка 303 (хозяин игры)
        if (!game.getMasterID().equals(userID)) {
            sendMessage(session, toJSON(
                    new StatusCodeError(GameSocketStatusCode.FORBIDDEN)));
            return;
        }

        // Проверка 304 (хватает ли игроков)
        if (!game.isReady()) {
            sendMessage(session, toJSON(
                    new StatusCodeError(GameSocketStatusCode.NOT_ENOUGH)));
            return;
        }

        playController.addGame(preparingGames.remove(game.getGameID()));
        notifySubscribers(toJSON(
                new StatusCodeSendID(GameSocketStatusCode.DELETE_GAME, game.getGameID())));
    }

    public void destroy(Long gameID) {

        if (!preparingGames.get(gameID).isEmpty()) {
            return;
        }
        preparingGames.remove(gameID);
        notifySubscribers(toJSON(new StatusCodeLobbyDelete(gameID)));
    }

    private void addBot(WebSocketSession session, JsonNode jsonNode) {

        checkAttribute(session, jsonNode, GameTools.BOT_LEVEL_ATTR);
        final GamePrepare game = getGameBySession(session);
        final Long userID = (Long) session.getAttributes().get(UserTools.USER_ID_ATTR);

        // Проверка (хозяин игры)
        if (!game.getMasterID().equals(userID)) {
            sendMessage(session, toJSON(
                    new StatusCodeError(GameSocketStatusCode.FORBIDDEN, game.getGameID())));
            return;
        }
        // Проверка (свободные места)
        if (game.isReady()) {
            sendMessage(session, toJSON(new StatusCodeError(GameSocketStatusCode.FULL)));
            return;
        }

        final Integer level = jsonNode.get(GameTools.BOT_LEVEL_ATTR).asInt();
        final PlayerBot bot = new PlayerBot(level);
        game.addBot(bot);

        // Оповестить подписчиков
        this.notifySubscribers(toJSON(
                new StatusCodeLobbyInfoCompact(GameSocketStatusCode.UPDATE_GAME, game)));
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

        final String payload;

        final Long gameID = (Long) session.getAttributes().get(GameTools.GAME_ID_ATTR);
        if (gameID == null) {
            payload = toJSON(new StatusCodeError(GameSocketStatusCode.NOT_MEMBER));
            sendMessage(session, payload);
            throw new GameException(payload);
        }

        final GamePrepare game = preparingGames.get(gameID);
        if (game == null) {
            payload = toJSON(new StatusCodeError(GameSocketStatusCode.NOT_EXIST, gameID));
            throw new GameException(payload);
        }

        return game;
    }

    private void checkAttribute(WebSocketSession session, JsonNode jsonNode, String requiredAttr) {
        if (!jsonNode.hasNonNull(requiredAttr)) {
            final String payload = toJSON(new StatusCodeErrorAttr(requiredAttr));
            sendMessage(session, payload);
            throw new GameException(payload);
        }
    }
}