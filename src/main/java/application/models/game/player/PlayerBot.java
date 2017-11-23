package application.models.game.player;

import application.models.game.field.Field;
import application.models.game.field.Spot;
import application.models.game.field.Step;
import application.services.game.GameTools;

import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

public final class PlayerBot extends PlayerAbstractActive {

    private final Random random;
    private final Integer level;

    public PlayerBot(Integer level) {
        super(GameTools.getBotName());
        this.level = level;
        this.random = new Random(new Date().getTime());
    }

    public synchronized Step generateStep(final Field field) {

        final ArrayList<Spot> sourceSpots;
        final ArrayList<Spot> destinationSpots;
        final Spot src;
        final Spot dst;

        switch (level) {
            case GameTools.BOT_LEVEL_LOW:
                sourceSpots = field.getPlayerSpots(getPlayerID());
                src = sourceSpots.get(random.nextInt(sourceSpots.size()));

                destinationSpots = field.getPossiblePoints(src);
                dst = destinationSpots.get(random.nextInt(destinationSpots.size()));

                return new Step(src, dst);

            case GameTools.BOT_LEVEL_MEDIUM:
                sourceSpots = field.getPlayerSpots(getPlayerID());
                destinationSpots = new ArrayList<>();

                for (Spot spot : sourceSpots) {
                    destinationSpots.addAll(field.getPossiblePoints(spot));
                }

                final ArrayList<Integer> count = new ArrayList<>(destinationSpots.size());
                for (Spot spot : destinationSpots) {
                    count.add(field.getAssumedCount(spot, getPlayerID()));
                }
                return null;


            // TODO generateStep in bot;
            default:
                System.err.println("Level of the bot is not recognized");
                return null;
        }
    }
}
