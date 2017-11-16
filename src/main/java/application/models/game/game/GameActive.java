package application.models.game.game;

import application.models.game.field.Step;
import application.models.game.player.PlayerAbstractActive;
import application.models.game.player.PlayerBot;
import application.models.game.player.PlayerGamer;
import application.services.game.GameSocketStatusCode;
import application.services.game.GameTools;
import application.views.game.StatusCode;
import application.views.game.statuscode2xx.StatusCode200;
import application.views.game.statuscode2xx.StatusCode201;
import application.views.game.statuscode2xx.StatusCode204;
import application.views.game.statuscode2xx.StatusCode2xx;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

public final class GameActive extends GameAbstract {

    private Integer currentPlayerID;
    private final ConcurrentHashMap<Integer /*playerID*/, PlayerAbstractActive> gamers;
    private Boolean gameOver;

    public GameActive(GamePrepare prepared) {
        super(prepared.getGameID(), prepared.getField(),
                prepared.getNumberOfPlayers(), prepared.getHashMapOfWatchers());

        this.gamers = new ConcurrentHashMap<>(getNumberOfPlayers());

        Integer playerIdIncrement = GameTools.PLAYER_1;
        for (PlayerGamer gamer : prepared.getGamers()) {
            gamer.setPlayerID(playerIdIncrement);
            gamers.put(playerIdIncrement++, gamer);
        }
        for (PlayerBot bot : prepared.getBots()) {
            bot.setPlayerID(playerIdIncrement);
            gamers.put(playerIdIncrement++, bot);
        }

        this.getField().initialize(getNumberOfPlayers());
        this.currentPlayerID = GameTools.PLAYER_1;
        this.gameOver = false;

        notifyPlayers(new StatusCode200(this));
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
            currentPlayerID = (currentPlayerID + 1) % getNumberOfPlayers();
            if (!getField().isBlocked(currentPlayerID)) {
                break;
            }
            notifyPlayers(GameSocketStatusCode.BLOCKED, gamers.get(currentPlayerID));
        }

        if (gamers.get(currentPlayerID) instanceof PlayerBot) {

            final PlayerBot bot = (PlayerBot) gamers.get(currentPlayerID);
            this.makeStep(bot.generateStep(getField()));
        }
        return true;
    }

    public synchronized void playerOff(Long userID) {
        gamers.values().forEach(gamer -> {
            if (gamer.getUserID().equals(userID)) {
                gamer.switchOff();
                notifyPlayers(GameSocketStatusCode.PLAYER_OFF, gamer);
            }
        });
    }

    public synchronized void playerOff(PlayerGamer player) {
        player.switchOff();
        notifyPlayers(GameSocketStatusCode.PLAYER_OFF, player);
    }

    // Оповещения игроков и наблюдателей
    private synchronized void notifyPlayers(GameSocketStatusCode code,
                                            PlayerAbstractActive player) {
        // player blocked
        gamers.values().forEach(gamer -> {
            if (gamer.getUserID() != null) {
                if (gamer.getOnline() && gamer.getSession().isOpen()) {
                    this.sendMessageToPlayer(gamer, new StatusCode2xx(code, player));
                } else {
                    this.playerOff((PlayerGamer) gamer);
                }
            }
        });
        getHashMapOfWatchers().values().forEach(watcher -> {
            if (watcher.getSession().isOpen()) {
                this.sendMessageToPlayer(watcher, new StatusCode2xx(code, player));
            } else {
                getHashMapOfWatchers().remove(watcher.getUserID());
            }
        });
    }

    private synchronized void notifyPlayers(Step step) {
        // make step
        gamers.values().forEach(gamer -> {
            if (gamer.getUserID() != null) {
                if (gamer.getOnline() && gamer.getSession().isOpen()) {
                    this.sendMessageToPlayer(gamer, new StatusCode201(step));
                } else {
                    this.playerOff((PlayerGamer) gamer);

                }
            }
        });
        getHashMapOfWatchers().values().forEach(watcher -> {
            if (watcher.getSession().isOpen()) {
                this.sendMessageToPlayer(watcher, new StatusCode201(step));
            } else {
                getHashMapOfWatchers().remove(watcher.getUserID());
            }
        });
    }

    private synchronized void notifyPlayers() {
        // game end
        gamers.values().forEach(gamer -> {
            if (gamer.getUserID() != null) {
                if (gamer.getOnline() && gamer.getSession().isOpen()) {
                    this.sendMessageToPlayer(gamer, new StatusCode204(getField()));
                } else {
                    this.playerOff((PlayerGamer) gamer);
                }
            }
        });
        getHashMapOfWatchers().values().forEach(watcher -> {
            if (watcher.getSession().isOpen()) {
                this.sendMessageToPlayer(watcher, new StatusCode204(getField()));
            }
        });
    }

    private synchronized void notifyPlayers(StatusCode statusCode) {
        // game end
        gamers.values().forEach(gamer -> {
            if (gamer.getUserID() != null) {
                if (gamer.getOnline() && gamer.getSession().isOpen()) {
                    this.sendMessageToPlayer(gamer, statusCode);
                } else {
                    this.playerOff((PlayerGamer) gamer);
                }
            }
        });
        getHashMapOfWatchers().values().forEach(watcher -> {
            if (watcher.getSession().isOpen()) {
                this.sendMessageToPlayer(watcher, statusCode);
            }
        });
    }

    private void end() {
        notifyPlayers();

        getHashMapOfWatchers().clear();

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
