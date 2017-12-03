package application.models.game.game;

import application.models.game.field.Field;
import application.models.game.player.PlayerAbstract;
import application.views.game.StatusCode;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.TextMessage;

import javax.validation.constraints.NotNull;
import java.io.IOException;


public abstract class GameAbstract {

    private final Long gameID;
    private final Field field;
    private final Integer numberOfPlayers;
    @JsonIgnore
    private final ObjectMapper mapper = new ObjectMapper();
    @JsonIgnore
    private final Logger gameLogger = LoggerFactory.getLogger(GameAbstract.class);


    protected GameAbstract(@NotNull Long gameID, @NotNull Field gameField,
                           @NotNull Integer numberOfPlayers) {

        this.gameID = gameID;
        this.field = gameField;
        this.numberOfPlayers = numberOfPlayers;
    }

    protected final synchronized void sendMessageToPlayer(PlayerAbstract player,
                                                    StatusCode statusCode) {
        try {
            player.getSession().sendMessage(
                    new TextMessage(mapper.writeValueAsString(statusCode)));
        } catch (IOException e) {
            gameLogger.error(e.getMessage(), e.getCause());
        }
    }

    // Оповещение участников о каком-либо событии
    abstract void notifyPlayers(StatusCode code);

    public Long getGameID() {
        return gameID;
    }

    public Integer getNumberOfPlayers() {
        return numberOfPlayers;
    }

    public Field getField() {
        return field;
    }
}
