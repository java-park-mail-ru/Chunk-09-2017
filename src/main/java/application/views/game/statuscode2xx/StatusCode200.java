package application.views.game.statuscode2xx;

import application.models.game.game.GameActive;
import application.services.game.GameSocketStatusCode;
import application.views.game.StatusCode;

public final class StatusCode200 extends StatusCode {

    private final GameActive game;

    public StatusCode200(GameActive game) {
        super(GameSocketStatusCode.BEGIN);
        this.game = game;
    }

    public GameActive getGame() {
        return game;
    }
}
