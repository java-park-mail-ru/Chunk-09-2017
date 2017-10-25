package application.services.game;

import application.models.game.GamePrepare;
import application.models.game.Player;
import application.models.user.UserModel;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class GameService {

    private final HashMap<Long, GamePrepare> prepareGames = new HashMap<>();
    private final HashMap<Long, Game> readyGames = new HashMap<>();
    private AtomicLong gameIdSequence = new AtomicLong();

    public Long createGame(GamePrepare gamePrepare) {
        if (gamePrepare.getMaxPlayers() < 2) {
            return null;
        }
        final Long gameID = gameIdSequence.get();
        prepareGames.put(gameID, gamePrepare);
        return gameID;
    }

    public Integer addPlayer(Long gameID, UserModel user) {
        final Integer playerID = prepareGames.get(gameID).addPlayer(user);
        if (prepareGames.get(gameID).isReady() && readyGames.get(gameID) == null) {
            startGame(gameID, prepareGames.get(gameID));
        }
        return playerID;
    }

    public Integer addBot(Long gameID) {
        final Integer playerID = prepareGames.get(gameID).addBot();
        if (prepareGames.get(gameID).isReady() && readyGames.get(gameID) == null) {
            startGame(gameID, prepareGames.get(gameID));
        }
        return playerID;
    }

    public boolean getPrepareGameStatus(Long gameID) {
        return prepareGames.get(gameID).isReady();
    }



    public ArrayList<Player> getPrepareGamePlayers(Long gameID) {
        return prepareGames.get(gameID).getPlayers();
    }

    public Game getReadyGame(Long gameID) {
        return readyGames.get(gameID);
    }

    private void startGame(Long gameID, GamePrepare gamePrepared) {
        readyGames.put(gameID, new Game(gamePrepared));
    }
}
