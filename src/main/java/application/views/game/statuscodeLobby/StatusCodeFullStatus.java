package application.views.game.statuscodeLobby;

import application.models.game.game.GamePrepare;
import application.services.game.GameSocketStatusCode;
import application.views.game.StatusCode;

import java.util.Collection;

public final class StatusCodeFullStatus extends StatusCode {

    private final Collection<GamePrepare> games;

    public StatusCodeFullStatus(GameSocketStatusCode statusCode,
                                Collection<GamePrepare> games) {
        super(statusCode);
        this.games = games;
    }

    public Collection<GamePrepare> getGames() {
        return games;
    }
}
