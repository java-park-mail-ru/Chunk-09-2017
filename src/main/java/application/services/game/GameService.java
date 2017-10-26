package application.services.game;

import application.models.game.*;
import application.models.user.UserModel;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class GameService {

    private final HashMap<Long, preGame> prepareGames = new HashMap<>();
    private final HashMap<Long, Game> readyGames = new HashMap<>();
    private AtomicLong gameIdSequence = new AtomicLong();

    public Long createGame(preGame preGame) {
        if (preGame.getMaxPlayers() < 2) {
            return null;
        }
        final Long gameID = gameIdSequence.get();
        prepareGames.put(gameID, preGame);
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

    public Field play(Long userID, PlayStep playStep) {

        final Game game = readyGames.get(playStep.getGameID());
        if ( game == null ) {
            // TODO throw exception
            return null;
        }

        if ( game.getCurrentPlayerID().equals(playStep.getPlayerID()) ) {
            // TODO throw exception
            return null;
        }

        if ( !game.getCurrentUserID().equals(userID) ) {
            // TODO throw читер
            return null;
        }

        return game.play(playStep.getX1(), playStep.getY1(), playStep.getX2(), playStep.getY2());
    }

    public ArrayList<Player> getPrepareGamePlayers(Long gameID) {
        return prepareGames.get(gameID).getPlayers();
    }

    public Game getReadyGame(Long gameID) {
        return readyGames.get(gameID);
    }

    private void startGame(Long gameID, preGame preGamePrepared) {
        readyGames.put(gameID, new Game(preGamePrepared));
    }
}
