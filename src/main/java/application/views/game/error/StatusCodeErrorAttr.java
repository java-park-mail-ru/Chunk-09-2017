package application.views.game.error;

import application.services.game.GameSocketStatusCode;
import application.views.game.StatusCode;


@SuppressWarnings("unused")
public final class StatusCodeErrorAttr extends StatusCode {

    private final String missingAttr;

    public StatusCodeErrorAttr(String missingAttr) {
        super(GameSocketStatusCode.ATTR);
        this.missingAttr = missingAttr;
    }


    public String getMissingAttr() {
        return missingAttr;
    }
}
