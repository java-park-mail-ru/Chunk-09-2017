package application.views.game;

import application.services.game.GameSocketStatusCode;
import com.fasterxml.jackson.annotation.JsonProperty;

public class StatusCode {

	@JsonProperty(value = "code")
	private Integer value;
	@JsonProperty(value = "reason")
	private String reasonPhrase;


	public StatusCode(GameSocketStatusCode socketStatusCode) {
		this.value = socketStatusCode.getValue();
		this.reasonPhrase = socketStatusCode.getReasonPhrase();
	}

	public Integer getValue() {
		return value;
	}

	public String getReasonPhrase() {
		return reasonPhrase;
	}
}
