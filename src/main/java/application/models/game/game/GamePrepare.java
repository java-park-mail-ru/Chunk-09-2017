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

    final ConcurrentHashMap<Long, PlayerGamer> gamers;
    final CopyOnWriteArraySet<PlayerBot> bots;
    @JsonIgnore
    private final Long masterID;
    @JsonIgnore
    private Boolean isReady;

    public GamePrepare(@NotNull Field gameField, @NotNull Long gameID,
                       @NotNull Integer numberOfPlayer, @NotNull Long masterID) {

        super(gameID, gameField, numberOfPlayer);
        this.masterID = masterID;
        this.isReady = false;
        this.gamers = new ConcurrentHashMap<>(this.numberOfPlayer);
        this.bots = new CopyOnWriteArraySet<>();
    }

    public synchronized void addGamer(PlayerGamer gamer) {
        if (isReady) {
            return;
        }
        gamers.put(gamer.getUserID(), gamer);
        notifyPlayers(gamer, GameSocketStatusCode.CONNECT_ACTIVE);
        if (gamers.size() == numberOfPlayer) {
            isReady = true;
        }
    }

    public synchronized void addBot(PlayerBot bot) {
        if (isReady) {
            return;
        }
        bots.add(bot);
        notifyPlayers(GameSocketStatusCode.ADD_BOT);
        if (gamers.size() == numberOfPlayer) {
            isReady = true;
        }
    }


    public void removeGamer(Long userID) {
        gamers.get(userID).getSession().getAttributes().remove(GameTools.GAME_ID_ATTR);
        notifyPlayers(gamers.remove(userID), GameSocketStatusCode.EXIT);
        if (isReady && gamers.size() < numberOfPlayer) {
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
                gamer, new StatusCode1xx(code, gameID, player)
       ));
        watchers.values().forEach(gamer -> this.sendMessageToPlayer(
                gamer, new StatusCode1xx(code, gameID, player)
       ));
    }

    private void notifyPlayers(GameSocketStatusCode code) {
        gamers.values().forEach(gamer -> this.sendMessageToPlayer(
                gamer, new StatusCode1xx(code, gameID)
       ));
        watchers.values().forEach(watcher -> this.sendMessageToPlayer(
                watcher, new StatusCode1xx(code, gameID)
       ));
    }



    public Collection<PlayerGamer> getGamers() {
        return gamers.values();
    }

    public Integer getBots() {
        return bots.size();
    }

    public synchronized Long getMasterID() {
        return masterID;
    }

    @JsonIgnore
    public synchronized Boolean isReady() {
        return gamers.size() >= numberOfPlayer;
    }
}
