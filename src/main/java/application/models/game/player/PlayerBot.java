package application.models.game.player;

import application.models.game.field.Field;
import application.models.game.field.Step;
import application.services.game.BotLogic;
import application.services.game.GameTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public final class PlayerBot extends PlayerAbstractActive {

    private final Integer level;
    private Long botID;
    private static final Logger LOGGER = LoggerFactory.getLogger(PlayerBot.class);

    public PlayerBot(Integer level) {
        super(GameTools.getBotName());
        this.level = level;
    }

    public synchronized Step generateStep(final Field field) {

        switch (level) {
            case GameTools.BOT_LEVEL_LOW:
                return BotLogic.lowLogic(field, getPlayerID());
            case GameTools.BOT_LEVEL_MEDIUM:
                return BotLogic.mediumLogic(field, getPlayerID());
            case GameTools.BOT_LEVEL_HIGH:
                return BotLogic.highLogic(field, getPlayerID());
            default:
                LOGGER.error("Level " + level + " of the bot is not recognized");
                return null;
        }
    }

    public Integer getLevel() {
        return level;
    }

    public Long getBotID() {
        return botID;
    }

    public void setBotID(Long botID) {
        this.botID = botID;
    }
}