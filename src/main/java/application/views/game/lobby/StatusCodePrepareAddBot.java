package application.views.game.lobby;

import application.models.game.player.PlayerBot;
import application.services.game.GameSocketStatusCode;
import application.views.game.StatusCode;


@SuppressWarnings("unused")
public final class StatusCodePrepareAddBot extends StatusCode {

    private final Long botID;
    private final Integer botlvl;
    private final String botname;
    private final Integer botsCount;

    public StatusCodePrepareAddBot(PlayerBot bot, Integer count) {
        super(GameSocketStatusCode.ADD_BOT);
        botID = bot.getBotID();
        botlvl = bot.getLevel();
        botname = bot.getUsername();
        botsCount = count;
    }


    public Long getBotID() {
        return botID;
    }

    public Integer getBotlvl() {
        return botlvl;
    }

    public String getBotname() {
        return botname;
    }

    public Integer getBotsCount() {
        return botsCount;
    }
}
