package application.functional;

import application.models.user.UserModel;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Random;

@Component
public class TestUtils {

    public static final Integer MAX_GAME_FIELD_WIDTH = 10;
    public static final Integer MAX_GAME_FIELD_HEIGHT = 10;

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
            this.width = RANDOM.nextInt(MAX_GAME_FIELD_WIDTH) + 2;
            this.height = RANDOM.nextInt(MAX_GAME_FIELD_HEIGHT) + 2;
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

    public static UserModel getRandomUser() {
        return new UserModel(
                "TestUsername_" + RANDOM.nextGaussian(),
                "TestEmail_" + RANDOM.nextGaussian(),
                "TestPassword_" + RANDOM.nextGaussian()
        );
    }

    public static String toJson(Object o) throws JsonProcessingException {
        return MAPPER.writeValueAsString(o);
    }
}
