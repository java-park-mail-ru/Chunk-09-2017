package application.views.game.lobby;

import application.models.game.game.GamePrepare;
import application.services.game.GameSocketStatusCode;
import application.views.game.information.GameInformationCompact;
import application.views.game.StatusCode;

import java.util.ArrayList;
import java.util.Collection;


@SuppressWarnings("unused")
public final class StatusCodeLobbyInfoFull extends StatusCode {

    private final ArrayList<GameInformationCompact> games;

    public StatusCodeLobbyInfoFull(Collection<GamePrepare> games) {
        super(GameSocketStatusCode.FULL_STATUS);

        this.games = new ArrayList<>(games.size());
        for (GamePrepare game : games) {
            this.games.add(new GameInformationCompact(game));
        }
    }

    public ArrayList<GameInformationCompact> getGames() {
        return games;
    }
}
