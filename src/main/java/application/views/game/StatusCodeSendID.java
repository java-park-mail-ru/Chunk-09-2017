package application.views.game;

import application.services.game.GameSocketStatusCode;


public final class StatusCodeSendID extends StatusCode {

    private final Long id;

    public StatusCodeSendID(GameSocketStatusCode statusCode, Long id) {
        super(statusCode);
        this.id = id;
    }

    public Long getUserID() {
        return id;
    }
}
