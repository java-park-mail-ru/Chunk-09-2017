package application.models.game;

import application.models.user.UserModel;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Player {

    @JsonProperty
    protected Integer playerID;
    @JsonProperty
    protected String username;
    Long userID;

    protected Player() { }

    protected Player(Integer playerID, String username, Long userID) {
        this.playerID = playerID;
        this.username = username;
        this.userID = userID;
    }

    public Player(UserModel userModel) {
        this.userID = userModel.getId();
        this.username = userModel.getUsername();
    }

    public Long getUserID() {
        return userID;
    }

    public void setUserID(Long userID) {
        this.userID = userID;
    }

    public Integer getPlayerID() {
        return playerID;
    }

    public void setPlayerID(Integer playerID) {
        this.playerID = playerID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
