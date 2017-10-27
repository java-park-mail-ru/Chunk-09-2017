package application.models.game;

import application.services.game.GameServiceTools;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

public class Game {

    @JsonProperty(value = "field")
    private final Field field;
    @JsonProperty
    private final ArrayList<Player> players;
    @JsonProperty
    private Integer currentPlayerID;
    @JsonProperty
    private Boolean gameOver = false;


    public Game(preGame preGame) {

        this.field = new Field(preGame.getHeight(), preGame.getWidth());
        this.players = preGame.getPlayers();
        this.currentPlayerID = 0;
        // TODO make initilize
        field.simpleInitializeField();
    }

    @JsonIgnore
    public Long getCurrentUserID() {
        return players.get(currentPlayerID).getUserID();
    }

    @JsonIgnore
    public Integer getIndexOfPlayer(Player player) {
        return players.indexOf(player);
    }

    public void play(Integer x1, Integer y1, Integer x2, Integer y2) {

        if ( !field.getField()[x1][y1].equals(currentPlayerID) ) {
            // TODO throw exception
            return;
        }

        this.gameOver = field.step(x1, y1, x2, y2);
        currentPlayerID = (currentPlayerID + 1) % players.size();
    }

    public void playByCurrentBot() {

        if ( this.getCurrentUserID() != null ) {
            return;
        }
        // TODO отдельный модуль ИИ
        for (int i = 0; i < field.getMaxX(); ++i) {
            for (int j = 0; j < field.getMaxY(); ++j) {
                if ( field.getField()[i][j].equals(this.currentPlayerID) ) {
                    for (int ii = i - 1; ii <= i + 1; ++ii) {
                        if ( ii < 0 || ii >= field.getMaxX()) {
                            continue;
                        }
                        for (int jj = j - 1; jj < j + 1; ++jj) {
                            if ( jj < 0 || jj >= field.getMaxY()) {
                                continue;
                            }
                            if (field.getField()[ii][jj].equals(GameServiceTools.EMPTY_CELL)) {
                                this.gameOver = field.step(i, j, ii, jj);
                                currentPlayerID = (currentPlayerID + 1) % players.size();
                                return;
                            }
                        }
                    }
                }
            }
        }
        currentPlayerID = (currentPlayerID + 1) % players.size();
        this.playByCurrentBot();
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
