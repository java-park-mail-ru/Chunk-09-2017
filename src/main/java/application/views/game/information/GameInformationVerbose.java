package application.views.game.information;

import application.models.game.game.GamePrepare;
import application.models.game.player.PlayerBot;
import application.models.game.player.PlayerGamer;

import java.util.Collection;


@SuppressWarnings("unused")
public final class GameInformationVerbose extends GameInformation {
    private final Long masterID;
    private final Collection<PlayerGamer> realPlayers;
    private final Collection<PlayerBot> botPlayers;

    public GameInformationVerbose(GamePrepare game) {
        super(game);
        masterID = game.getMasterID();
        realPlayers = game.getGamers().values();
        botPlayers = game.getBots().values();
    }


    public Long getMasterID() {
        return masterID;
    }

    public Collection<PlayerGamer> getRealPlayers() {
        return realPlayers;
    }

    public Collection<PlayerBot> getBotPlayers() {
        return botPlayers;
    }
}
