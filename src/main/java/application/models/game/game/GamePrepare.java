package application.models.game.game;

import application.models.game.field.Field;
import application.models.game.player.PlayerBot;
import application.models.game.player.PlayerGamer;
import application.services.game.GameTools;
import application.services.game.GameSocketStatusCode;
import application.views.game.StatusCode;
import application.views.game.statuscode1xx.StatusCode101;
import application.views.game.statuscode1xx.StatusCode1xx;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;


public final class GamePrepare extends GameAbstract {

    private final ConcurrentHashMap<Long, PlayerGamer> gamers;
    private final CopyOnWriteArraySet<PlayerBot> bots;
    private final Long masterID;
    @JsonIgnore
    private Boolean isReady;

    public GamePrepare(@NotNull Field gameField, @NotNull Long gameID,
                       @NotNull Integer numberOfPlayers, @NotNull Long masterID) {

        super(gameID, gameField, numberOfPlayers);
        this.masterID = masterID;
        this.isReady = false;
        this.gamers = new ConcurrentHashMap<>(this.getNumberOfPlayers());
        this.bots = new CopyOnWriteArraySet<>();
    }

    public synchronized void addGamer(PlayerGamer gamer) {
        if (isReady) {
            return;
        }
        gamers.put(gamer.getUserID(), gamer);
        notifyPlayers(new StatusCode101(
                GameSocketStatusCode.CONNECT_ACTIVE, this, gamer));
        if (gamers.size() + bots.size() == getNumberOfPlayers()) {
            isReady = true;
        }
        gamer.getSession().getAttributes().put(GameTools.GAME_ID_ATTR, getGameID());
    }

    public synchronized void addBot(PlayerBot bot) {
        if (isReady) {
            return;
        }
        bots.add(bot);
        notifyPlayers(new StatusCode101(
                GameSocketStatusCode.CONNECT_ACTIVE, this, bot));
        if (gamers.size() + bots.size() == getNumberOfPlayers()) {
            isReady = true;
        }
    }

    public void removeGamer(Long userID) {
        gamers.get(userID).getSession().getAttributes().remove(GameTools.GAME_ID_ATTR);
        notifyPlayers(new StatusCode101(
                GameSocketStatusCode.EXIT, this, gamers.remove(userID)));
        if (isReady && gamers.size() < getNumberOfPlayers()) {
            isReady = false;
        }
    }

    public synchronized void destroy() {
        notifyPlayers(new StatusCode1xx(GameSocketStatusCode.DESTROY, getGameID()));
        gamers.values().forEach(gamer -> gamer.getSession()
                .getAttributes().remove(GameTools.GAME_ID_ATTR));
        gamers.clear();
    }

    @Override
    synchronized void notifyPlayers(StatusCode code) {
        gamers.values().forEach(gamer -> {
            if (gamer.getSession().isOpen()) {
                this.sendMessageToPlayer(gamer, code);
            } else {
                this.removeGamer(gamer.getUserID());
            }
        });
        getHashMapOfWatchers().values().forEach(watcher -> {
            if (watcher.getSession().isOpen()) {
                this.sendMessageToPlayer(watcher, code);
            } else {
                getHashMapOfWatchers().remove(watcher.getUserID());
            }
        });
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
        return gamers.size() + bots.size() >= getNumberOfPlayers();
    }
}
