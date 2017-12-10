package application.views.game;

import application.services.game.GameSocketStatusCode;

@SuppressWarnings("unused")
public abstract class StatusCode {

    private final Integer code;
    private final String reason;

    public StatusCode(GameSocketStatusCode statusCode) {
        this.code = statusCode.getValue();
        this.reason = statusCode.getReasonPhrase();
    }

    public Integer getCode() {
        return code;
    }

    public String getReason() {
        return reason;
    }
}
