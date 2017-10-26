package application.views.game;

import application.models.game.Player;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

public class GameCreated {

    @JsonProperty
    ArrayList<Player> players;
    @JsonProperty
    Integer[][] field;

    public GameCreated(ArrayList<Player> players, Integer[][] field) {
        this.players = players;
        this.field = field;
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public void setPlayers(ArrayList<Player> players) {
        this.players = players;
    }

    public Integer[][] getField() {
        return field;
    }

    public void setField(Integer[][] field) {
        this.field = field;
    }
}
