package application.views.game;

import application.models.game.field.Step;
import application.services.game.GameSocketStatusCode;

public final class StatusCode201 extends StatusCode{

	Step step;

	public StatusCode201(Step step) {
		super(GameSocketStatusCode.STEP);
		this.step = step;
	}

	public Step getStep() {
		return step;
	}
}
