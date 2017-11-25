package application.models.game.player;

import application.models.game.field.Field;
import application.models.game.field.Spot;
import application.models.game.field.Step;
import application.services.game.GameTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public final class PlayerBot extends PlayerAbstractActive {

    private final Random random;
    private final Integer level;
    private static final Logger LOGGER = LoggerFactory.getLogger(PlayerBot.class);

    public PlayerBot(Integer level) {
        super(GameTools.getBotName());
        this.level = level;
        this.random = new Random(new Date().getTime());
    }

    public synchronized Step generateStep(final Field field) {

        switch (level) {
            case GameTools.BOT_LEVEL_LOW:
                return low(field);
            case GameTools.BOT_LEVEL_MEDIUM:
                return medium(field);
            case GameTools.BOT_LEVEL_HIGH:
                return high(field);
            default:
                LOGGER.error("Level " + level + " of the bot is not recognized");
                return null;
        }
    }

    private Step low(final Field field) {

        final ArrayList<Spot> sourceSpots = field.getPlayerSpots(getPlayerID());

        Spot src;
        ArrayList<Spot> destinationSpots;
        do {
            src = sourceSpots.get(random.nextInt(sourceSpots.size()));
            destinationSpots = field.getPossiblePoints(src);
        }
        while (destinationSpots.isEmpty());

        final Spot dst = destinationSpots.get(random.nextInt(destinationSpots.size()));
        return new Step(src, dst);
    }

    private Step medium(final Field field) {

        class StepAnalyze {

            StepAnalyze(Spot src, Spot dst) {
                this.src = src;
                this.dst = dst;
                this.assumed = field.getAssumedCount(dst, getPlayerID());
                if (Math.abs(src.getCstX() - dst.getCstX()) < 2
                        && Math.abs(src.getCstY() - dst.getCstY()) < 2) {
                    ++this.assumed;
                }
            }

            private Spot src;
            private Spot dst;
            // Кол-во захваченных фигур
            private Integer assumed;

            public Integer getAssumed() {
                return assumed;
            }
        }

        final ArrayList<Spot> sourceSpots = field.getPlayerSpots(getPlayerID());
        final ArrayList<StepAnalyze> steps = new ArrayList<>();

        for (Spot src : sourceSpots) {
            for (Spot dst : field.getPossiblePoints(src)) {
                steps.add(new StepAnalyze(src, dst));
            }
        }

        Collections.shuffle(steps, GameTools.RANDOM);
        final StepAnalyze max = steps.stream()
                .max(Comparator.comparing(StepAnalyze::getAssumed)).get();

        return new Step(max.src, max.dst);
    }

    private Step high(final Field field) {
        return null;
    }
}
