package application.models.game.game;

import application.exceptions.game.GameExceptionDestroy;
import application.models.game.field.Field;
import application.models.game.player.PlayerBot;
import application.models.game.player.PlayerGamer;
import application.services.game.GameTools;
import application.services.game.GameSocketStatusCode;
import application.views.game.StatusCode;
import application.views.game.lobby.StatusCodePrepareAddBot;
import application.views.game.lobby.StatusCodePrepareAddPlayer;
import application.views.game.StatusCodeSendID;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.validation.constraints.NotNull;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;


public final class GamePrepare extends GameAbstract {

    private final ConcurrentHashMap<Long /*userID*/, PlayerGamer> gamers;
    private final ConcurrentHashMap<Long /*botID*/, PlayerBot> bots;
    private Long masterID;
    @JsonIgnore
    private final AtomicLong generatorBotID = new AtomicLong();

    public GamePrepare(@NotNull Field gameField, @NotNull Long gameID,
                       @NotNull Integer numberOfPlayers, @NotNull Long masterID) {

        super(gameID, gameField, numberOfPlayers);
        this.masterID = masterID;
        this.gamers = new ConcurrentHashMap<>(this.getNumberOfPlayers());
        this.bots = new ConcurrentHashMap<>();
    }

    public synchronized void addGamer(PlayerGamer gamer) {

        notifyPlayers(new StatusCodePrepareAddPlayer(gamer, gamers.size() + 1));
        if (isReady()) {
            return;
        }
        gamers.put(gamer.getUserID(), gamer);
        gamer.getSession().getAttributes().put(GameTools.GAME_ID_ATTR, getGameID());
    }

    public void addBot(PlayerBot bot) {

        bot.setBotID(generatorBotID.incrementAndGet());
        if (isReady()) {
            return;
        }
        bots.put(bot.getBotID(), bot);
        notifyPlayers(new StatusCodePrepareAddBot(bot, bots.size()));
    }

    public void removeGamer(Long userID) {
        final PlayerGamer removeGamer = gamers.remove(userID);
        if (removeGamer == null) {
            return;
        }
        notifyPlayers(new StatusCodeSendID(GameSocketStatusCode.REMOVE_PLAYER, userID));

        if (removeGamer.getSession().isOpen()) {
            removeGamer.getSession().getAttributes().remove(GameTools.GAME_ID_ATTR);
            this.sendMessageToPlayer(removeGamer,
                    new StatusCodeSendID(GameSocketStatusCode.REMOVE_PLAYER, userID));
        }

        // Если удаленный игрок оказался master'ом
        synchronized (this) {
            if (userID.equals(masterID)) {
                final Optional<PlayerGamer> newMaster = gamers.values().stream().findFirst();
                if (!newMaster.isPresent()) {
                    throw new GameExceptionDestroy(getGameID());
                }
                masterID = newMaster.get().getUserID();
                notifyPlayers(new StatusCodeSendID(
                        GameSocketStatusCode.CHANGE_MASTER, masterID));
            }
        }
    }

    public void removeBot(Long botID) {
        if (bots.remove(botID) == null) {
            return;
        }
        notifyPlayers(new StatusCodeSendID(GameSocketStatusCode.KICK_BOT, botID));
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
    }

    public ConcurrentHashMap<Long, PlayerGamer> getGamers() {
        return gamers;
    }

    public ConcurrentHashMap<Long, PlayerBot> getBots() {
        return bots;
    }

    public synchronized Long getMasterID() {
        return masterID;
    }

    public synchronized Boolean isReady() {
        return gamers.size() + bots.size() >= getNumberOfPlayers();
    }

    public boolean isEmpty() {
        return gamers.isEmpty();
    }

}
