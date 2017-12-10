package application.views.game.information;

import application.models.game.game.GamePrepare;


@SuppressWarnings("unused")
public abstract class GameInformation {

    private final Long gameID;
    private final Integer numberOfPlayers;
    private final Integer maxX;
    private final Integer maxY;

    public GameInformation(GamePrepare game) {
        gameID = game.getGameID();
        numberOfPlayers = game.getNumberOfPlayers();
        maxX = game.getField().getMaxX();
        maxY = game.getField().getMaxY();
    }


    public Long getGameID() {
        return gameID;
    }

    public Integer getNumberOfPlayers() {
        return numberOfPlayers;
    }

    public Integer getMaxX() {
        return maxX;
    }

    public Integer getMaxY() {
        return maxY;
    }
}
