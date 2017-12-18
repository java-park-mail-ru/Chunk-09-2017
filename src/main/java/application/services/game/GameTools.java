package application.services.game;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class GameTools {

    public static final int EMPTY_CELL = 0;
    
    public static final int PLAYER_1 = 1;
    public static final int PLAYER_2 = 2;
    public static final int PLAYER_3 = 3;
    public static final int PLAYER_4 = 4;

    public static boolean isPlayer(int cellID) {
        return PLAYER_1 <= cellID && cellID <= PLAYER_4;
    }

    public static final int SPOT_OF_PLAYER_1 = 11;
    public static final int SPOT_OF_PLAYER_2 = 12;
    public static final int SPOT_OF_PLAYER_3 = 13;
    public static final int SPOT_OF_PLAYER_4 = 14;


    public static final List<String> BOTS_NAMES = Arrays.asList(
            "Bob", "John", "Foo", "Bar", "Doctor", "Master",
            "Loki", "Thor", "Batman", "Gandalf", "Dahaka",
            "Java-Man", "Spider-Man", "Tony Stark", "Pennywise",
            "Lucky", "Grandmaster", "Dr.Strange", "I am Groot",
            "Star Lord", "Rocket", "Thanos", "Ostapenko"
    );

    public static final int BOT_LEVEL_LOW = 1;
    public static final int BOT_LEVEL_MEDIUM = 2;
    public static final int BOT_LEVEL_HIGH = 3;

    public static final String NUMBER_OF_PLAYERS = "numberOfPlayers";
    public static final String GAME_ID_ATTR = "gameID";
    public static final String KICK_USER_ATTR = "userID";
    public static final String KICK_BOT_ATTR = "botID";
    public static final String BOT_LEVEL_ATTR = "lvlbot";
    public static final String STEP_ATTR = "step";
    public static final String MAX_X_ATTR = "maxX";
    public static final String MAX_Y_ATTR = "maxY";

    public static final long ROUND_TIME_SEC = 3;
    public static final long FIRST_ROUND_TIME_SEC = 45;
    public static final long TIME_BETWEEN_CHECKS_MIN = 1;

    public static final Random RANDOM = new Random(new Date().getTime());

    public static synchronized String getBotName() {
        return BOTS_NAMES.get(RANDOM.nextInt(BOTS_NAMES.size()));
    }
}
