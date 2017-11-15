package application.functional;

import application.models.user.UserSignUp;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Random;

@Component
public class TestUtils {

    static class TestPlayer {
        @JsonProperty
        Integer width;
        @JsonProperty
        Integer height;
        @JsonProperty
        Integer maxPlayers;

        TestPlayer(Integer width, Integer height, Integer maxPlayers) {
            this.width = width;
            this.height = height;
            this.maxPlayers = maxPlayers;
        }

        TestPlayer() {
            this.width = 6;
            this.height = 6;
            this.maxPlayers = 2;
        }

        public Integer getWidth() {
            return width;
        }

        public void setWidth(Integer width) {
            this.width = width;
        }

        public Integer getHeight() {
            return height;
        }

        public void setHeight(Integer height) {
            this.height = height;
        }

        public Integer getMaxPlayers() {
            return maxPlayers;
        }

        public void setMaxPlayers(Integer maxPlayers) {
            this.maxPlayers = maxPlayers;
        }
    }

    public static final Random RANDOM = new Random(new Date().getTime());


    public static final ObjectMapper MAPPER = new ObjectMapper();

    public static UserSignUp getRandomUser() {
        return new UserSignUp(
                "TestUsername_" + RANDOM.nextGaussian(),
                "TestEmail_" + RANDOM.nextGaussian(),
                "TestPassword_" + RANDOM.nextGaussian()
        );
    }

    public static String toJson(Object o) throws JsonProcessingException {
        return MAPPER.writeValueAsString(o);
    }

    static class TestPlaystep {

        @JsonProperty
        Integer x1;
        @JsonProperty
        Integer x2;
        @JsonProperty
        Integer y1;
        @JsonProperty
        Integer y2;
        @JsonProperty
        Long gameID;
        @JsonProperty
        Integer playerID;
        @JsonProperty
        Integer currentPlayerID;

        TestPlaystep(Integer x1, Integer y1, Integer x2, Integer y2,
                     Long gameID, Integer playerID) {

            this.x1 = x1;
            this.x2 = x2;
            this.y1 = y1;
            this.y2 = y2;
            this.gameID = gameID;
            this.playerID = playerID;
        }
        TestPlaystep(Long gameID, Integer playerID, Integer currentPlayerID) {

            this.gameID = gameID;
            this.playerID = playerID;
            this.currentPlayerID = currentPlayerID;
        }
    }
}
