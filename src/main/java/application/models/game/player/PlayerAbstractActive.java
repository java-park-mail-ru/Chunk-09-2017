package application.models.game.player;

import application.models.user.UserSignUp;
import org.springframework.web.socket.WebSocketSession;

public abstract class PlayerAbstractActive extends PlayerAbstract {

    private Integer playerID;
    private Boolean online;


    public PlayerAbstractActive(UserSignUp user, WebSocketSession session) {
        // Конструктор для реального игрока
        super(user, session);
        this.online = true;
    }

    public PlayerAbstractActive(String username) {
        // Конструктор для бота
        super(username);
        this.online = true;
    }

    public void switchOn() {
        this.online = true;
    }

    public void switchOff() {
        this.online = false;
    }

    public Integer getPlayerID() {
        return playerID;
    }

    public Boolean getOnline() {
        return online;
    }

    public void setPlayerID(Integer playerID) {
        this.playerID = playerID;
    }

    public void setOnline(Boolean online) {
        this.online = online;
    }
}
