package application.views.game.lobby;

import application.models.game.player.PlayerGamer;
import application.services.game.GameSocketStatusCode;
import application.views.game.StatusCode;

@SuppressWarnings("unused")
public final class StatusCodePrepareAddPlayer extends StatusCode {

    private final Long userID;
    private final String username;
    private final String email;
    private final Integer usersCount;

    public StatusCodePrepareAddPlayer(PlayerGamer gamer, Integer count) {
        super(GameSocketStatusCode.ADD_PLAYER);
        userID = gamer.getUserID();
        username = gamer.getUsername();
        email = gamer.getEmail();
        usersCount = count;
    }


    public Long getUserID() {
        return userID;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public Integer getUsersCount() {
        return usersCount;
    }
}
