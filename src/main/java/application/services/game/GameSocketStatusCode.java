package application.services.game;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.http.HttpStatus;

public enum GameSocketStatusCode {

	/* Prepare status code */
	CREATE(100, "Create new game"),
	CONNECT_ACTIVE(101, "Connect to preparing game as a Player"),
	CONNECT_WATCHER(102, "Connect to preparing game as a Watcher"),
	EXIT(103, "Exit from preparing game"),
	STATUS(104, "Get the current game"),
	START(105, "Start preparing game"),
	SUBSCRIBE(106, "Subscribe to the update of the playlist"),
	UNSUBSCRIBE(107, "Unsubscribe from the updating of the playlist"),
	DESTROY(110, "Destroy exist preparing game"),
	/* Playing status code */
	BEGIN(200, "Start the game"),

	/* Client Error status code */
	NOT_AUTHORIZED(300, "You must be sign in"),
	ALREADY_PLAY(301, "First, quite out of the previous game");



	GameSocketStatusCode(int value, String reasonPhrase) {
		this.value = value;
		this.reasonPhrase = reasonPhrase;
	}

	@JsonProperty(value = "status")
	private final int value;
	@JsonProperty(value = "statusText")
	private final String reasonPhrase;

	private static final Long MAX_CODE = 500L;


	public static boolean isPreparing(final Integer code) {
		return CREATE.value <= code && code < BEGIN.value;
	}

	public static boolean isPlaying(final Integer code) {
		return BEGIN.value <= code && code < NOT_AUTHORIZED.value;
	}

	public static boolean isClientError(final Integer code) {
		return NOT_AUTHORIZED.value <= code && code < MAX_CODE;
	}


	public int getValue() {
		return value;
	}

	public String getReasonPhrase() {
		return reasonPhrase;
	}
}
