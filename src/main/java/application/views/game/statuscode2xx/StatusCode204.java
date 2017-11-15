package application.views.game.statuscode2xx;

import application.models.game.field.Field;
import application.services.game.GameSocketStatusCode;
import application.views.game.StatusCode;

public final class StatusCode204 extends StatusCode {

    private final Field field;
    private final Long gameID;


    public StatusCode204(Field field) {
        super(GameSocketStatusCode.GAMEOVER);
        this.field = field;
        this.gameID = null;
    }

    public StatusCode204(Long gameID) {
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
