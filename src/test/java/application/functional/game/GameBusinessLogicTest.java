package application.functional.game;

import application.models.game.field.Field;
import application.models.game.game.GamePrepare;
import application.models.game.player.PlayerBot;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import static org.junit.Assert.*;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class GameBusinessLogicTest {

    @Rule
    public ExpectedException expected = ExpectedException.none();


    @Test
    public void testCreateGame() throws Exception {

        final Integer maxX = 10;
        final Integer maxY = 10;
        final Long gameID = gameGeneratorID++;
        final Integer numberOfPlayers = 4;
        final Long masterID = 0L;

        final Field field = new Field(maxX, maxY);
        assertNotNull(field.getArray());
        assertEquals(field.getMaxX(), maxX);
        assertEquals(field.getMaxY(), maxY);

        final GamePrepare gamePrepare = new GamePrepare(
                field, gameID, numberOfPlayers, masterID);


        assertEquals(gamePrepare.getMasterID(), masterID);
        assertEquals(gamePrepare.getNumberOfPlayer(), numberOfPlayers);
        assertEquals(gamePrepare.getGameID(), gameID);
        assertEquals(gamePrepare.getField(), field);
    }

    @Test
    public void testAddBotInPrepareGame() throws Exception {

        final GamePrepare gamePrepare = new GamePrepare(
                new Field(5, 5), gameGeneratorID++,
                4, 0L);

        final PlayerBot bot = new PlayerBot(1);
        gamePrepare.addBot(bot);

        assertTrue(gamePrepare.getBots().contains(bot));
        assertEquals(gamePrepare.getGamers().size(), 0);
    }

    @Test
    public void testOverflowBotsInPrepareGame() throws Exception {

        final Integer numberOfPlayers = 4;
        final GamePrepare gamePrepare = new GamePrepare(
                new Field(5, 5), gameGeneratorID++,
                numberOfPlayers, 0L);

        gamePrepare.addBot(new PlayerBot(1));
        gamePrepare.addBot(new PlayerBot(1));
        gamePrepare.addBot(new PlayerBot(1));
        gamePrepare.addBot(new PlayerBot(1));
        gamePrepare.addBot(new PlayerBot(1));

        System.out.println(gamePrepare.getBots().size());
        System.out.println(gamePrepare.isReady());
        assertEquals(gamePrepare.getBots().size(), numberOfPlayers.intValue());
        assertTrue(gamePrepare.isReady());
    }

    @Test
    public void testGameIsReady() throws Exception {

        final Integer numberOfPlayers = 2;
        final GamePrepare gamePrepare = new GamePrepare(
                new Field(5, 5), gameGeneratorID++,
                numberOfPlayers, 0L);

        gamePrepare.addBot(new PlayerBot(1));
        assertFalse(gamePrepare.isReady());
        gamePrepare.addBot(new PlayerBot(1));
        assertTrue(gamePrepare.isReady());
    }

    private Long gameGeneratorID = 0L;
}
