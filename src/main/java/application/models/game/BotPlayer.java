package application.models.game;

public class BotPlayer extends Player {

    public BotPlayer() { }
    public BotPlayer(Integer playerID) {
        super(playerID,"BotPlayer " + playerID,null);
    }

    @Override
    public void setPlayerID(Integer botID) {
        this.playerID = botID;
        this.username = "BotPlayer " + botID;
        this.userID = null;
    }
}
