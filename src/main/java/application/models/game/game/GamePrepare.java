package application.models.game.game;

import application.models.game.field.Field;
import application.models.game.player.PlayerBot;
import application.models.game.player.PlayerGamer;
import application.services.game.GameTools;
import application.services.game.GameSocketStatusCode;
import application.views.game.statuscode1xx.StatusCode1xx;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;


public final class GamePrepare extends GameAbstract {

    private final ConcurrentHashMap<Long, PlayerGamer> gamers;
    private final CopyOnWriteArraySet<PlayerBot> bots;
    @JsonIgnore
    private final Long masterID;
    @JsonIgnore
    private Boolean isReady;

    public GamePrepare(@NotNull Field gameField, @NotNull Long gameID,
                       @NotNull Integer numberOfPlayers, @NotNull Long masterID) {

        super(gameID, gameField, numberOfPlayers);
        this.masterID = masterID;
        this.isReady = false;
        this.gamers = new ConcurrentHashMap<>(this.getNumberOfPlayer());
        this.bots = new CopyOnWriteArraySet<>();
    }

    public synchronized void addGamer(PlayerGamer gamer) {
        if (isReady) {
            return;
        }
        gamers.put(gamer.getUserID(), gamer);
        notifyPlayers(gamer, GameSocketStatusCode.CONNECT_ACTIVE);
        if (gamers.size() == getNumberOfPlayer()) {
            isReady = true;
        }
    }

    public synchronized void addBot(PlayerBot bot) {
        if (isReady) {
            return;
        }
        bots.add(bot);
        notifyPlayers(GameSocketStatusCode.ADD_BOT);
        if (gamers.size() == getNumberOfPlayer()) {
            isReady = true;
        }
    }


    public void removeGamer(Long userID) {
        gamers.get(userID).getSession().getAttributes().remove(GameTools.GAME_ID_ATTR);
        notifyPlayers(gamers.remove(userID), GameSocketStatusCode.EXIT);
        if (isReady && gamers.size() < getNumberOfPlayer()) {
            isReady = false;
        }
    }


    public synchronized void destroy() {
        notifyPlayers(GameSocketStatusCode.DESTROY);
        gamers.forEachValue(1L, gamer -> gamer.getSession()
                .getAttributes().remove(GameTools.GAME_ID_ATTR));
        gamers.clear();
    }


    // Оповещение участников о каком либо событии
    private void notifyPlayers(PlayerGamer player, GameSocketStatusCode code) {
        gamers.values().forEach(gamer -> this.sendMessageToPlayer(
                gamer, new StatusCode1xx(code, getGameID(), player)
       ));
        getHashMapOfWatchers().values().forEach(gamer -> this.sendMessageToPlayer(
                gamer, new StatusCode1xx(code, getGameID(), player)
       ));
    }

    private void notifyPlayers(GameSocketStatusCode code) {
        gamers.values().forEach(gamer -> this.sendMessageToPlayer(
                gamer, new StatusCode1xx(code, getGameID())
       ));
        getHashMapOfWatchers().values().forEach(watcher -> this.sendMessageToPlayer(
                watcher, new StatusCode1xx(code, getGameID())
       ));
    }



    public Collection<PlayerGamer> getGamers() {
        return gamers.values();
    }

    public Collection<PlayerBot> getBots() {
        return bots;
    }

    public synchronized Long getMasterID() {
        return masterID;
    }

    @JsonIgnore
    public synchronized Boolean isReady() {
        return gamers.size() >= getNumberOfPlayer();
    }
}
