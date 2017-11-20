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

    public static final int SPOT_OF_PLAYER_1 = 10;
    public static final int SPOT_OF_PLAYER_2 = 11;
    public static final int SPOT_OF_PLAYER_3 = 12;
    public static final int SPOT_OF_PLAYER_4 = 13;
    public static final int SPOT_OF_PLAYER_5 = 14;


    public static final List<String> BOTS_NAMES = Arrays.asList(
            "Bob", "John", "Doctor", "Master", "Tony Stark",
            "Loki", "Thor", "Batman", "Gandalf", "Dahaka", "TARDIS",
            "Java-Man", "Spider-Man", "Pennywise", "The Dancing clown",
            "Джевгенец", "Лапища", "Spring", "п/п-к Пряхин", "Foo", "Bar",
            "Титаевский", "Арсюка", "Grandmaster", "Lucky", "Bauman"
    );


    public static final long TIME_BETWEEN_BLOCKED = 5000L;
    public static final long TIME_BEFORE_BOTS_STEP = 2000L;
    public static final int BOT_LEVEL_LOW = 1;
    public static final int BOT_LEVEL_MEDIUM = 2;
    public static final int BOT_LEVEL_HIGH = 3;

    public static final String NUMBER_OF_PLAYERS = "numberOfPlayers";
    public static final String GAME_ID_ATTR = "gameID";
    public static final String BOT_LEVEL_ATTR = "lvlbot";
    public static final String STEP_ATTR = "step";
    public static final String MAX_X_ATTR = "maxX";
    public static final String MAX_Y_ATTR = "maxY";


    public static final Random RANDOM = new Random(new Date().getTime());

    public static synchronized String getBotName() {
        return BOTS_NAMES.get(RANDOM.nextInt(BOTS_NAMES.size()));
    }
}
