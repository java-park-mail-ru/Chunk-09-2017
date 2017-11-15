package application.models.game.game;

import application.models.game.field.Step;
import application.models.game.player.PlayerAbstractActive;
import application.models.game.player.PlayerBot;
import application.models.game.player.PlayerGamer;
import application.services.game.GameTools;
import application.views.game.statuscode2xx.StatusCode201;
import application.views.game.statuscode2xx.StatusCode203;
import application.views.game.statuscode2xx.StatusCode204;

import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

public final class GameActive extends GameAbstract {

    private Integer currentPlayerID;
    private final ConcurrentHashMap<Integer /*playerID*/, PlayerAbstractActive> gamers;
    private Boolean gameOver;

    public GameActive(GamePrepare prepared) {
        super(prepared.getGameID(), prepared.getField(),
                prepared.getNumberOfPlayer(), prepared.getHashMapOfWatchers());

        this.gamers = new ConcurrentHashMap<>(getNumberOfPlayer());

        final Iterator<PlayerGamer> iteratorGamer = prepared.getGamers().iterator();
        for (int i = 1; iteratorGamer.hasNext(); ++i) {
            final PlayerGamer gamer = iteratorGamer.next();
            gamer.setPlayerID(i);
            gamers.put(i, gamer);
        }

        final Iterator<PlayerBot> iteratorBot = prepared.getBots().iterator();
        for (int i = 0; iteratorBot.hasNext(); ++i) {
            final PlayerBot bot = iteratorBot.next();
            bot.setPlayerID(i);
            gamers.put(i, bot);
        }

        this.getField().initialize(getNumberOfPlayer());
        this.currentPlayerID = GameTools.PLAYER_1;
        this.gameOver = false;
    }

    public synchronized Boolean makeStep(Step step) {

        if (getField().getPlayerInPoint(step.getSrc()).equals(currentPlayerID)) {
            return false;
        }

        if (!getField().makeStep(step)) {
            return false;
        }

        notifyPlayers(step);

        if (getField().isGameOver()) {
            end();
            this.gameOver = true;
            return false;
        }

        while (true) {
            currentPlayerID = (currentPlayerID + 1) % getNumberOfPlayer();
            if (!getField().isBlocked(currentPlayerID)) {
                break;
            }

            try {
                Thread.sleep(GameTools.TIME_BETWEEN_BLOCKED);
            } catch (InterruptedException e) {
                e.printStackTrace();
                return false;
            }
            notifyPlayers(gamers.get(currentPlayerID));
        }

        if (gamers.get(currentPlayerID) instanceof PlayerBot) {
            try {
                Thread.sleep(GameTools.TIME_BEFORE_BOTS_STEP);
            } catch (InterruptedException e) {
                e.printStackTrace();
                return false;
            }
            final PlayerBot bot = (PlayerBot) gamers.get(currentPlayerID);
            this.makeStep(bot.generateStep(getField()));
        }
        return true;
    }

//    public synchronized void playerOff()
    // TODO check on session.isOpen();

    // Опопвестить пользователей о совершенном ходе
    private synchronized void notifyPlayers(Step step) {
        // make step
        gamers.values().forEach(gamer -> {
            if (gamer.getUserID() != null) {
                this.sendMessageToPlayer(gamer,
                        new StatusCode201(step));
            }
        });
        getHashMapOfWatchers().values().forEach(watcher -> {
            this.sendMessageToPlayer(watcher,
                    new StatusCode201(step));
        });
    }

    private synchronized void notifyPlayers(PlayerAbstractActive blockedPlayer) {
        // player blocked
        gamers.values().forEach(gamer -> {
            if (gamer.getUserID() != null) {
                this.sendMessageToPlayer(gamer,
                        new StatusCode203(blockedPlayer));
            }
        });
        getHashMapOfWatchers().values().forEach(watcher ->
                this.sendMessageToPlayer(watcher, new StatusCode203(blockedPlayer)));
    }

    private synchronized void notifyPlayers() {
        // game end
        gamers.values().forEach(gamer -> {
            if (gamer.getUserID() != null) {
                this.sendMessageToPlayer(gamer, new StatusCode204(getField()));
            }
        });
        getHashMapOfWatchers().values().forEach(watcher ->
                this.sendMessageToPlayer(watcher, new StatusCode204(getField())));
    }

    private void end() {
        notifyPlayers();
        gamers.values().forEach(gamer -> {
            if (gamer instanceof PlayerGamer) {
                gamer.getSession().getAttributes().remove(GameTools.GAME_ID_ATTR);
            }
        });
        gamers.clear();
    }


    public boolean getGameOver() {
        return gameOver;
    }

    public Long getCurrentUserID() {
        return gamers.get(currentPlayerID).getUserID();
    }

    public Integer getCurrentPlayerID() {
        return currentPlayerID;
    }

    public Collection<PlayerAbstractActive> getGamers() {
        return gamers.values();
    }
}
