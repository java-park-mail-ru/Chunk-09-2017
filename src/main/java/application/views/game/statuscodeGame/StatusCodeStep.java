package application.views.game.statuscodeGame;

import application.models.game.field.Step;
import application.services.game.GameSocketStatusCode;
import application.views.game.StatusCode;

public final class StatusCodeStep extends StatusCode {

    private final Step step;

    public StatusCodeStep(Step step) {
        super(GameSocketStatusCode.STEP);
        this.step = step;
    }

    public Step getStep() {
        return step;
    }
}
