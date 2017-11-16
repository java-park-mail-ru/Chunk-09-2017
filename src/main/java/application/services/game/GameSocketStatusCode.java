package application.services.game;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum GameSocketStatusCode {

    /* Prepare status code */
    CREATE(100, "Create new game"),
    CONNECT_ACTIVE(101, "Connect to preparing game as a player"),
    CONNECT_WATCHER(102, "Connect to preparing game as a watcher"),
    EXIT(103, "Exit from preparing game"),
    STATUS(104, "Get full information about explicit game"),
    START(105, "Start preparing game"),
    SUBSCRIBE_P(106, "Subscribe to the update of the playlist"),
    UNSUBSCRIBE_P(107, "Unsubscribe from the updating of the playlist"),
    ADD_BOT(108, "Adding bot-player to multiplayer game"),
    REMOVE_BOT(109, "Removing bot-player from multiplayer game"),
    DESTROY(110, "Destroy exist preparing game"),
    FULL_STATUS(111, "Get information about all preparing games"),

    /* Playing status code */
    BEGIN(200, "Start the game"),
    STEP(201, "Game step"),
    TIMEOUT(202, "Timeout expired"),
    BLOCKED(203, "Player is blocked"),
    GAMEOVER(204, "Game had ended, check result"),
    WATCH(205, "Watching the current game"),
    REWATCH(206, "Stop watching game"),
    SUBSCRIBE_A(207, "Subscribe to the update of the active game list"),
    UNSUBSCRIBE_A(208, "Unsubscribe from the updating of the active game list"),

    /* Client Error status code */
    UNEXPECTED(300, "The requested code does not exist"),
    ALREADY_PLAY(301, "First, quite out of the previous game"),
    NOT_EXIST(302, "The requested game does not exist"),
    FORBIDDEN(303, "To perfom this action you must be a master of the game"),
    NOT_ENOUGH(304, "Not enought players (the game master is able to add bots"),
    NOT_AUTHORIZED(305, "You must be sign in"),
    FALSE(306, "Invalid game step"),
    TURN(307, "It is not your turn"),
    ATTR(308, "Missing required attributes"),
    FULL(309, "All places in the game are already occupied");





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
