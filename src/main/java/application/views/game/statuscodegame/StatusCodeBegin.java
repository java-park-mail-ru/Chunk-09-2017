package application.views.game.statuscodegame;

import application.models.game.game.GameActive;
import application.services.game.GameSocketStatusCode;
import application.views.game.StatusCode;

public final class StatusCodeBegin extends StatusCode {

    private final GameActive game;

    public StatusCodeBegin(GameActive game) {
        super(GameSocketStatusCode.BEGIN);
        this.game = game;
    }

    public GameActive getGame() {
        return game;
    }
}
