package application.models.game.player;

import application.models.game.field.Field;
import application.models.game.field.Spot;
import application.models.game.field.Step;
import application.services.game.GameTools;

import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;


public final class PlayerBot extends PlayerAbstractActive {

    private static AtomicLong generatorBotName = new AtomicLong();
    private final Random random = new Random(new Date().getTime());
    private final Integer level;

    public PlayerBot(Integer level) {
        // TODO сделать осмысленные имена, добавить botID
        super("Bot_" + generatorBotName.getAndIncrement());
        this.level = level;
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
                dst = destinationSpots.get(random.nextInt(sourceSpots.size()));

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
                // TODO hashmap все дела
                return null;


            // TODO generateStep in bot;
            default:
                System.err.println("Level of the bot is nor recognized");
                return null;

        }
    }
}
