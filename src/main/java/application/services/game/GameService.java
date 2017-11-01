package application.services.game;

import application.models.game.*;
import application.models.user.UserSignUp;
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
        final Long gameID = gameIdSequence.getAndIncrement();
        prepareGames.put(gameID, preGame);
        return gameID;
    }

    public Integer addPlayer(Long gameID, UserSignUp user) {
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

    public Game play(Long userID, Snapshot snapshot) {

        final Game game = readyGames.get(snapshot.getGameID());
        if ( game == null ) {
            // TODO throw exception
            return null;
        }

        if ( !game.getCurrentPlayerID().equals(snapshot.getPlayerID()) ) {
            // TODO throw exception
            return null;
        }

        if ( !game.getCurrentUserID().equals(userID) ) {
            // TODO throw читер
            return null;
        }

        game.play(snapshot.getX1(), snapshot.getY1(), snapshot.getX2(), snapshot.getY2());
        return game;
    }

    public Game waitingAnotherPlayer(Long userID, Snapshot snapshot) {

        // TODO validator for 3 'if'
        final Game game = readyGames.get(snapshot.getGameID());
        if ( game == null ) {
            // TODO throw exception
            return null;
        }

        if ( !game.getPlayers().get(snapshot.getPlayerID()).getUserID().equals(userID) ) {
            // TODO throw читер
            return null;
        }

        if ( !game.getCurrentPlayerID().equals(snapshot.getCurrentPlayerID()) ) {
            return game;
        } else {
            if (game.getCurrentUserID() == null) {
                game.playByCurrentBot();
                return game;
            } else {
                //      TODO for realPlayers
                return null;
            }
        }
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
