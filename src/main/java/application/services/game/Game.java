package application.services.game;

import application.models.game.GamePrepare;
import application.models.game.Player;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

public class Game {

    @JsonProperty
    final Integer[][] field;
    @JsonProperty
    final ArrayList<Player> players;
    @JsonProperty
    Short currentPlayerID;
    final Integer width;
    final Integer height;

    public Game(Integer width, Integer height, ArrayList<Player> players) {

        this.width = width;
        this.height = height;
        this.field = new Integer[height][width];
        this.players = players;
        this.currentPlayerID = 1;
    }

    public Game(GamePrepare gamePrepare) {

        this.width = gamePrepare.getWidth();
        this.height = gamePrepare.getHeight();
        this.field = new Integer[height][width];
        this.players = gamePrepare.getPlayers();
        this.currentPlayerID = 1;
    }

    public void initilizeSingleField() {
        for (int h = 0; h < height; ++h) {
            for (int w = 0; w < width; ++w) {
                field[h][w] = GameUtils.EMPTY_CELL;
            }
        }
        field[0][0] = field[height-1][0] = GameUtils.PLAYER_1;
        field[0][width - 1] = field[height - 1][width - 1] = GameUtils.PLAYER_2;
    }
}
