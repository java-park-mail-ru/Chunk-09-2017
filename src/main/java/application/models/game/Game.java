package application.models.game;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

public class Game {

    @JsonProperty(value = "field")
    private final Field field;
    @JsonProperty
    final ArrayList<Player> players;
    @JsonProperty
    Integer currentPlayerID;


    public Game(preGame preGame) {

        this.field = new Field(preGame.getHeight(), preGame.getWidth());
        this.players = preGame.getPlayers();
        this.currentPlayerID = 0;
    }

    public Long getCurrentUserID() {
        return players.get(currentPlayerID).getUserID();
    }

    public Integer getIndexOfPlayer(Player player) {
        return players.indexOf(player);
    }

    public Field play(Integer x1, Integer y1, Integer x2, Integer y2) {

        if ( !field.getField()[x1][y1].equals(currentPlayerID) ) {
            // TODO throw exception
            return null;
        }

        field.step(x1, y1, x2, y2);
        currentPlayerID = (currentPlayerID + 1) % players.size();
        return field;
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public Integer getCurrentPlayerID() {
        return currentPlayerID;
    }

    public Integer[][] getField() {
        return field.getField();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Game)) return false;

        final Game game = (Game) obj;

        if (!field.equals(game.field)) return false;
        return players.equals(game.players);
    }

    @Override
    public int hashCode() {
        int result = field.hashCode();
        result = 31 * result + players.hashCode();
        return result;
    }

}
