package application.views.game.information;

import application.models.game.game.GamePrepare;


@SuppressWarnings("unused")
public final class GameInformationCompact extends GameInformation {
    private final String masterUsername;
    private final Integer realSize;
    private final Integer botSize;

    public GameInformationCompact(GamePrepare game) {
        super(game);
        realSize = game.getGamers().size();
        botSize = game.getBots().size();
        masterUsername = realSize != 0
                ? game.getGamers().get(game.getMasterID()).getUsername() : "none";
    }


    public String getMasterUsername() {
        return masterUsername;
    }

    public Integer getRealSize() {
        return realSize;
    }

    public Integer getBotSize() {
        return botSize;
    }
}
