package application.views.game.statuscode1xx;

import application.models.game.game.GamePrepare;
import application.models.game.player.PlayerAbstractActive;
import application.services.game.GameSocketStatusCode;
import application.views.game.StatusCode;

public final class StatusCode101 extends StatusCode {

    final Long gameID;
    final Integer gamersCount;
    final Integer botsCount;
    final Integer watchersCount;
    final Integer numberOfPlayers;
    final Integer maxX;
    final Integer maxY;
    final PlayerAbstractActive player;

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

}
