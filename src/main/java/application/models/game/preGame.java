package application.models.game;

import application.models.game.Player;
import application.models.user.UserModel;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sun.jmx.remote.internal.ArrayQueue;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.RequestParam;

import javax.swing.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;

public class preGame {

    @JsonProperty
    Integer width;
    @JsonProperty
    Integer height;
    @JsonProperty
    Integer maxPlayers;

    ArrayList<Player> players = new ArrayList<>();
    Boolean isReady = false;
    Long gameID;


    public preGame(@NotNull Integer height,
                   @NotNull Integer width,
                   @NotNull Integer maxPlayers) {
        this.width = width;
        this.height = height;
        this.maxPlayers = maxPlayers;
        isReady = false;
    }

    public preGame() { }

    public Integer addPlayer(UserModel userModel) {
        final Player player = new Player(userModel);
        return addPlayer(player);
    }

    @Nullable
    public Integer addBot() {
        final Player player = new BotPlayer();
        return addPlayer(player);
    }

    @Nullable
    private Integer addPlayer(Player player) {
        if (isReady) {
            return null;
        }
        players.add(player);
        final Integer index = players.indexOf(player);
        if (index < maxPlayers) {
            players.get(index).setPlayerID(index);
            if (index + 1 == maxPlayers) {
                isReady = true;
            }
            return index;
        } else {
            players.remove(player);
            return null;
        }
    }

    public Long getGameID() {
        return gameID;
    }

    public void setGameID(Long gameID) {
        this.gameID = gameID;
    }

    public Integer getWidth() {
        return width;
    }

    public Integer getHeight() {
        return height;
    }

    public Integer getMaxPlayers() {
        return maxPlayers;
    }

    public Boolean isReady() {
        return isReady;
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }
}
