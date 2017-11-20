package application.functional;

import application.models.user.UserSignUp;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class TestUtils {

    @Autowired
    public final Logger logger = LoggerFactory.getLogger("test");

    @Autowired
    private final Random random = new Random();

    @Autowired
    @Qualifier("mymapper")
    private final ObjectMapper mapper = new ObjectMapper();

    public String toJson(Object o) throws JsonProcessingException {
        return mapper.writeValueAsString(o);
    }

    public UserSignUp getRandomUser() {
        return new UserSignUp(
                "TestUsername_" + random.nextInt(),
                "TestEmail_" + random.nextInt(),
                "TestPassword_" + random.nextInt()
        );
    }

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

    static class TestPlaystep {

        Integer x1;
        Integer x2;
        Integer y1;
        Integer y2;
        Long gameID;
        Integer playerID;
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
