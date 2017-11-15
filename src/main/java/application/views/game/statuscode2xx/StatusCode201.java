package application.views.game.statuscode2xx;

import application.models.game.field.Step;
import application.services.game.GameSocketStatusCode;
import application.views.game.StatusCode;

public final class StatusCode201 extends StatusCode {

    private final Step step;

    public StatusCode201(Step step) {
        super(GameSocketStatusCode.STEP);
        this.step = step;
    }

    public Step getStep() {
        return step;
    }
}
