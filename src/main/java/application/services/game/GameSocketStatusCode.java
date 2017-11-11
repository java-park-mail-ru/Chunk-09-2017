package application.services.game;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.http.HttpStatus;

public enum GameSocketStatusCode {

	/* Prepare status code */
	CREATE(100, "Create new game"),
	CONNECT_ACTIVE(101, "Connect to preparing game as a Player"),
	CONNECT_WATCHER(102, "Connect to preparing game as a Watcher"),
	EXIT(103, "Exit from preparing game"),
	START(105, "Start preparing game"),
	DESTROY(110, "Destroy exist preparing game"),
	/* Playing status code */
	BEGIN(200, "Start the game"),

	/* Client Error status code */
	NOT_AUTHORIZED(300, "You must be sign in");



	GameSocketStatusCode(int value, String reasonPhrase) {
		this.value = value;
		this.reasonPhrase = reasonPhrase;
	}

	@JsonProperty(value = "status")
	private final int value;
	@JsonProperty(value = "statusText")
	private final String reasonPhrase;

	private static final Long MAX_CODE = 500L;


	public static boolean isPreparing(final Long code) {
		return CREATE.value <= code && code < BEGIN.value;
	}

	public static boolean isPlaying(final Long code) {
		return BEGIN.value <= code && code < NOT_AUTHORIZED.value;
	}

	public static boolean isClientError(final Long code) {
		return NOT_AUTHORIZED.value <= code && code < MAX_CODE;
	}


	public int getValue() {
		return value;
	}

	public String getReasonPhrase() {
		return reasonPhrase;
	}
}
