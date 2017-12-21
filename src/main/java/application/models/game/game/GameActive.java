package application.models.game.game;

import application.models.game.field.Step;
import application.models.game.player.PlayerAbstractActive;
import application.models.game.player.PlayerBot;
import application.models.game.player.PlayerGamer;
import application.models.game.player.PlayerWatcher;
import application.services.game.BotLogic;
import application.services.game.GameSocketStatusCode;
import application.services.game.GameTools;
import application.views.game.StatusCode;
import application.views.game.active.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;


public final class GameActive extends GameAbstract {

    private Integer currentPlayerID;
    private volatile long stepCount;
    private final ScheduledExecutorService executor;
    private ScheduledFuture future;

    private final ConcurrentHashMap<Integer /*playerID*/, PlayerAbstractActive> gamers;
    private final ConcurrentHashMap<Long /*userID*/, PlayerWatcher> watchers;

    private AtomicBoolean gameOver = new AtomicBoolean(false);

    public GameActive(GamePrepare prepared, ScheduledExecutorService executor) {
        super(prepared.getGameID(), prepared.getField(), prepared.getNumberOfPlayers());

        this.executor = executor;
        this.watchers = new ConcurrentHashMap<>();
        this.gamers = new ConcurrentHashMap<>(getNumberOfPlayers());

        Integer playerIdIncrement = GameTools.PLAYER_1;
        for (PlayerGamer gamer : prepared.getGamers().values()) {
            gamer.setPlayerID(playerIdIncrement);
            gamers.put(playerIdIncrement++, gamer);
        }
        for (PlayerBot bot : prepared.getBots().values()) {
            bot.setPlayerID(playerIdIncrement);
            gamers.put(playerIdIncrement++, bot);
        }

        this.getField().initialize(getNumberOfPlayers());
        this.currentPlayerID = GameTools.PLAYER_1;

        notifyPlayers(new StatusCodeBegin(this));

        stepCount = 0L;
        future = executor.schedule(new Task(stepCount),
                2 * GameTools.ROUND_TIME_SEC, TimeUnit.SECONDS);
    }

    public synchronized Boolean makeStep(Step step, Long stepID) {

        if (!stepID.equals(stepCount)) {
            return false;
        }
        if (!getField().getPlayerInPoint(step.getSrc()).equals(currentPlayerID)) {
            return false;
        }
        if (!getField().makeStep(step)) {
            return false;
        }

        future.cancel(false);
        if (THREAD_LOCAL.get() != null && THREAD_LOCAL.get() == stepCount) {
            notifyPlayers(new StatusCodeTimeout(
                    GameSocketStatusCode.TIMEOUT, currentPlayerID));
        }
        notifyPlayers(new StatusCodeStep(step));
        ++stepCount;

        if (getField().isGameOver()) {
            gameOver.set(true);
            end();
            return true;
        }

        while (true) {
            currentPlayerID = (currentPlayerID + 1) % (getNumberOfPlayers() + 1);
            if (currentPlayerID == 0) {
                currentPlayerID = GameTools.PLAYER_1;
            }
            // Проверяем не заблокирован ли следующий игрок
            if (!getField().isBlocked(currentPlayerID)) {
                break;
            }
            notifyPlayers(new StatusCodeGame(
                    GameSocketStatusCode.BLOCKED, gamers.get(currentPlayerID)));
        }

        // Если игрок бот, то делаем ход ботом
        if (gamers.get(currentPlayerID) instanceof PlayerBot) {
            final PlayerBot bot = (PlayerBot) gamers.get(currentPlayerID);
            makeStep(bot.generateStep(getField()), stepCount);
            future.cancel(false);
            if (gameOver.get()) {
                return true;
            }
        }

        // Если игрок оффлайн, то делаем за него рандомный ход
        if (!gamers.get(currentPlayerID).getOnline()) {
            makeStep(BotLogic.lowLogic(getField(), currentPlayerID), stepCount);
            future.cancel(false);
            if (gameOver.get()) {
                return true;
            }
        }

        future = executor.schedule(new Task(stepCount),
                GameTools.ROUND_TIME_SEC, TimeUnit.SECONDS);
        return true;
    }

    public synchronized void playerOff(Long userID) {
        gamers.values().forEach(gamer -> {
            if (gamer.getUserID() != null) {
                if (gamer.getUserID().equals(userID)) {
                    notifyPlayers(new StatusCodeGame(
                            GameSocketStatusCode.PLAYER_OFF, gamer));
                    gamer.switchOff();
                }
            }
        });
        if (userID.equals(getCurrentUserID())) {
            makeStep(BotLogic.lowLogic(getField(), currentPlayerID), stepCount);
        }
    }

    public synchronized void playerOff(PlayerGamer player) {
        player.switchOff();
        new StatusCodeGame(GameSocketStatusCode.PLAYER_OFF, player);
    }

    public void addWatcher(PlayerWatcher watcher) {
        watchers.put(watcher.getUserID(), watcher);
    }

    public void removeWatcher(Long userID) {
        watchers.remove(userID);
    }

    @Override
    void notifyPlayers(StatusCode statusCode) {
        gamers.values().forEach(gamer -> {
            if (gamer.getUserID() != null) {
                if (gamer.getOnline() && gamer.getSession().isOpen()) {
                    this.sendMessageToPlayer(gamer, statusCode);
                } else {
                    this.playerOff((PlayerGamer) gamer);
                }
            }
        });
        watchers.values().forEach(watcher -> {
            if (watcher.getSession().isOpen()) {
                this.sendMessageToPlayer(watcher, statusCode);
            } else {
                watchers.remove(watcher.getUserID());
            }
        });
    }

    private void end() {

        notifyPlayers(new StatusCodeGameover(getField()));
        watchers.clear();

        gamers.values().forEach(gamer -> {
            if (gamer instanceof PlayerGamer) {
                gamer.getSession().getAttributes().remove(GameTools.GAME_ID_ATTR);
            }
        });
        gamers.clear();

        getObserver().afterGameOver(getGameID());
    }

    @JsonIgnore
    public boolean getGameOver() {
        return gameOver.get();
    }

    @JsonIgnore
    public Long getCurrentUserID() {
        return gamers.get(currentPlayerID).getUserID();
    }

    public Integer getCurrentPlayerID() {
        return currentPlayerID;
    }

    public ConcurrentHashMap<Integer, PlayerAbstractActive> getGamers() {
        return gamers;
    }

    public ConcurrentHashMap<Long, PlayerWatcher> getWatchers() {
        return watchers;
    }

    private static final ThreadLocal<Long> THREAD_LOCAL = new ThreadLocal<>();

    private final class Task extends Thread {

        private final Long stepID;

        Task(Long taskStepID) {
            this.stepID = taskStepID;
        }

        @Override
        public void run() {
            THREAD_LOCAL.set(stepID);
            makeStep(BotLogic.lowLogic(getField(), currentPlayerID), THREAD_LOCAL.get());
        }
    }
}
