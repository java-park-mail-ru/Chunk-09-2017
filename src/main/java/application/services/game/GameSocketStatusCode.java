package application.services.game;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum GameSocketStatusCode {

    /* Prepare status code */
    SUBSCRIBE(100, "Subscribe to the update of the lobby"),
    UNSUBSCRIBE(101, "Unsubscribe from the updating of the lobby"),
    FULL_STATUS(102, "Get full information about all preparing games"),
    WHOAMI(103, "Returns your userID, gameID"),
    CREATE_GAME(110, "You have created a new active"),
    CONNECT_GAME(111, "Information about the active which is connected"),
    UPDATE_GAME(120, "Changes in lobby active"),
    NEW_GAME(121, "New active in lobby was created"),
    DELETE_GAME(122, "Remove active from lobby"),
    ADD_PLAYER(130, "A new real player joined"),
    ADD_BOT(131, "A new bot player joined"),
    REMOVE_PLAYER(132, "The player left active"),
    KICK_BOT(133, "The bot player was kicked out from the active"),
    KICK_PLAYER(134, "The real player was kicked out from the active"),
    START_GAME(135, "The preparing active was started"),
    CHANGE_MASTER(136, "The master of the game have changed"),

    /* Playing status code */
    BEGIN(200, "Start the active"),
    STEP(201, "Game step"),
    TIMEOUT(202, "Timeout expired"),
    BLOCKED(203, "Player is blocked"),
    GAMEOVER(204, "Game had ended, check result"),
    WATCH(205, "Watching the current active"),
    REWATCH(206, "Stop watching active"),
    SUBSCRIBE_A(207, "Subscribe to the update of the active active list"),
    UNSUBSCRIBE_A(208, "Unsubscribe from the updating of the active active list"),
    PLAYER_OFF(209, "Player is offline"),
    RECONNECT(210, "Reconnect to active"),
    LEAVE(210, "Leave from active active"),

    /* Client Error status code */
    UNEXPECTED(300, "The requested code does not exist"),
    ALREADY_PLAY(301, "First, quite out of the previous active"),
    NOT_EXIST(302, "The requested active does not exist"),
    FORBIDDEN(303, "To perfom this action you must be a master of the active"),
    NOT_ENOUGH(304, "Not enought players (the active master is able to add bots"),
    NOT_AUTHORIZED(305, "You must be sign in"),
    FALSE(306, "Invalid active step"),
    TURN(307, "It is not your turn"),
    ATTR(308, "Missing required attributes"),
    FULL(309, "All places in the active are already occupied");


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
        return SUBSCRIBE.value <= code && code < BEGIN.value;
    }

    public static boolean isPlaying(final Integer code) {
        return BEGIN.value <= code && code < NOT_AUTHORIZED.value;
    }

    public static boolean isClientError(final Integer code) {
        return NOT_AUTHORIZED.value <= code && code < MAX_CODE;
    }

    @Override
    public String toString() {
        return "code=" + value + ", reason: " + reasonPhrase;
    }

    public int getValue() {
        return value;
    }

    public String getReasonPhrase() {
        return reasonPhrase;
    }
}
