package application.views.game.statuscodegame;

import application.models.game.field.Field;
import application.services.game.GameSocketStatusCode;
import application.views.game.StatusCode;

public final class StatusCodeGameover extends StatusCode {

    private final Field field;
    private final Long gameID;


    public StatusCodeGameover(Field field) {
        super(GameSocketStatusCode.GAMEOVER);
        this.field = field;
        this.gameID = null;
    }

    public StatusCodeGameover(Long gameID) {
        super(GameSocketStatusCode.GAMEOVER);
        this.gameID = gameID;
        this.field = null;
    }


    public Field getField() {
        return field;
    }

    public Long getGameID() {
        return gameID;
    }
}
