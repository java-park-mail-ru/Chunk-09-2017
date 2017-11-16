package application.views.game.statuscode1xx;

import application.models.game.game.GamePrepare;
import application.models.game.player.PlayerAbstractActive;
import application.services.game.GameSocketStatusCode;
import application.views.game.StatusCode;

public final class StatusCode101 extends StatusCode {

    private final Long gameID;
    private final Integer gamersCount;
    private final Integer botsCount;
    private final Integer watchersCount;
    private final Integer numberOfPlayers;
    private final Integer maxX;
    private final Integer maxY;
    private final PlayerAbstractActive player;

    public StatusCode101(GameSocketStatusCode statusCode,
                         GamePrepare game, PlayerAbstractActive player) {
        super(statusCode);
        this.gameID = game.getGameID();
        this.gamersCount = game.getGamers().size();
        this.botsCount = game.getBots().size();
        this.watchersCount = game.getWatchers();
        this.numberOfPlayers = game.getNumberOfPlayers();
        this.maxX = game.getField().getMaxX();
        this.maxY = game.getField().getMaxY();
        this.player = player;
    }


    public Long getGameID() {
        return gameID;
    }

    public Integer getGamersCount() {
        return gamersCount;
    }

    public Integer getBotsCount() {
        return botsCount;
    }

    public Integer getWatchersCount() {
        return watchersCount;
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

    public PlayerAbstractActive getPlayer() {
        return player;
    }
}
