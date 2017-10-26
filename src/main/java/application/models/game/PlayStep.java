package application.models.game;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PlayStep {

    @JsonProperty
    private Integer x1;
    @JsonProperty
    private Integer y1;
    @JsonProperty
    private Integer x2;
    @JsonProperty
    private Integer y2;

    @JsonProperty
    private Long gameID;
    @JsonProperty
    private Integer playerID;


    public Integer getX1() {
        return x1;
    }

    public void setX1(Integer x1) {
        this.x1 = x1;
    }

    public Integer getY1() {
        return y1;
    }

    public void setY1(Integer y1) {
        this.y1 = y1;
    }

    public Integer getX2() {
        return x2;
    }

    public void setX2(Integer x2) {
        this.x2 = x2;
    }

    public Integer getY2() {
        return y2;
    }

    public void setY2(Integer y2) {
        this.y2 = y2;
    }

    public Long getGameID() {
        return gameID;
    }

    public void setGameID(Long gameID) {
        this.gameID = gameID;
    }

    public Integer getPlayerID() {
        return playerID;
    }

    public void setPlayerID(Integer playerID) {
        this.playerID = playerID;
    }
}
