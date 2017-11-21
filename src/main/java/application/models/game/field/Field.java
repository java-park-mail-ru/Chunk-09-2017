package application.models.game.field;

import application.services.game.GameTools;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

public class Field {

    @JsonProperty(value = "field")
    private Integer[][] array;
    private Integer maxX;
    private Integer maxY;

    public Field(Integer maxX, Integer maxY) {
        this.maxX = maxX;
        this.maxY = maxY;
        array = new Integer[maxX][maxY];

        for (int x = 0; x < maxX; ++x) {
            for (int y = 0; y < maxY; ++y) {
                array[x][y] = GameTools.EMPTY_CELL;
            }
        }
    }


    public void initialize(Integer numberOfPlayers) {

        switch (numberOfPlayers) {
            case TWO_PLAYERS:
                array[0][0] = GameTools.PLAYER_1;
                array[0][maxY - 1] = GameTools.PLAYER_2;
                array[maxX - 1][0] = GameTools.PLAYER_2;
                array[maxX - 1][maxY - 1] = GameTools.PLAYER_1;
                return;

            case THREE_PLAYERS:
                array[0][0] = GameTools.PLAYER_1;
                array[maxX - 1][maxY - 1] = GameTools.PLAYER_2;
                array[maxX - 1][0] = GameTools.PLAYER_3;
                return;

            case FOUR_PLAYERS:
                array[0][0] = GameTools.PLAYER_1;
                array[maxX - 1][maxY - 1] = GameTools.PLAYER_2;
                array[maxX - 1][0] = GameTools.PLAYER_3;
                array[0][maxY - 1] = GameTools.PLAYER_4;
                return;

            default:
                System.err.println("Too many players");
        }
    }

    public boolean isGameOver() {

        for (int x = 0; x < maxX; ++x) {
            for (int y = 0; y < maxY; ++y) {
                if (array[x][y].equals(GameTools.EMPTY_CELL)) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean isBlocked(Integer playerID) {

        for (int x = 0; x < maxX; ++x) {
            for (int y = 0; y < maxY; ++y) {
                if (array[x][y].equals(playerID)) {
                    if (!this.getPossiblePoints(new Spot(x, y)).isEmpty()) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public synchronized boolean makeStep(Step step) {

        // Валидация
        if (array[step.getSrc().getCstX()][step.getSrc().getCstY()].equals(GameTools.EMPTY_CELL)) {
            return false;
        }
        if (!array[step.getDst().getCstX()][step.getDst().getCstY()].equals(GameTools.EMPTY_CELL)) {
            return false;
        }
        if (step.getSrc().equals(step.getDst())) {
            return false;
        }
        if (Math.abs(step.getDst().getCstX() - step.getSrc().getCstX()) > 2
                || Math.abs(step.getDst().getCstY() - step.getSrc().getCstY()) > 2) {
            return false;
        }

        // Ход
        array[step.getDst().getCstX()][step.getDst().getCstY()] = array[step.getSrc().getCstX()][step.getSrc().getCstY()];
        if (Math.abs(step.getDst().getCstX() - step.getSrc().getCstX()) == 2
                || Math.abs(step.getDst().getCstY() - step.getSrc().getCstY()) == 2) {
            array[step.getSrc().getCstX()][step.getSrc().getCstY()] = GameTools.EMPTY_CELL;
        }

        this.assumedAround(step.getDst());
        return true;
    }

    public Integer getPlayerInPoint(Spot spot) {
        return array[spot.getCstX()][spot.getCstY()];
    }

    private synchronized void assumedAround(Spot spot) {

        final Integer playerID = array[spot.getCstX()][spot.getCstY()];
        for (int x = spot.getCstX() - 1; x <= spot.getCstX() + 1; ++x) {
            for (int y = spot.getCstY() - 1; y <= spot.getCstY() + 1; ++y) {

                if (!this.isValid(new Spot(x, y))) {
                    continue;
                }
                if (array[x][y] == GameTools.EMPTY_CELL) {
                    continue;
                }
                array[x][y] = playerID;
            }
        }
    }

    public ArrayList<Spot> getPlayerSpots(Integer playerID) {

        final ArrayList<Spot> spots = new ArrayList<>();
        for (int x = 0; x < maxX; ++x) {
            for (int y = 0; y < maxY; ++y) {
                if (array[x][y].equals(playerID)) {
                    spots.add(new Spot(x, y));
                }
            }
        }
        return spots;
    }

    public ArrayList<Spot> getPossiblePoints(Spot spot) {

        final ArrayList<Spot> possibleSpots = new ArrayList<>();
        for (int x = spot.getCstX() - 2; x <= spot.getCstX() + 2; ++x) {
            for (int y = spot.getCstY() - 2; y <= spot.getCstY() + 2; ++y) {

                if (!this.isValid(new Spot(x, y))) {
                    continue;
                }
                if (array[x][y] == GameTools.EMPTY_CELL) {
                    possibleSpots.add(new Spot(x, y));
                }
            }
        }
        return possibleSpots;
    }

    public Integer getAssumedCount(Spot spot, Integer playerID) {

        Integer count = 0;
        for (int x = spot.getCstX() - 1; x <= spot.getCstX() + 1; ++x) {
            for (int y = spot.getCstY() - 1; y <= spot.getCstY() + 1; ++y) {

                if (!this.isValid(new Spot(x, y))) {
                    continue;
                }
                if (array[x][y] == GameTools.EMPTY_CELL) {
                    continue;
                }
                ++count;
            }
        }
        return count;
    }

    private Boolean isValid(Spot spot) {
        if (spot.getCstX() < 0 || spot.getCstY() < 0) {
            return false;
        }
        if (spot.getCstX() >= maxX || spot.getCstY() >= maxY) {
            return false;
        }
        return true;
    }


    public Integer getMaxX() {
        return maxX;
    }

    public Integer getMaxY() {
        return maxY;
    }

    public Integer[][] getArray() {
        return array;
    }

    static final int TWO_PLAYERS = 2;
    static final int THREE_PLAYERS = 3;
    static final int FOUR_PLAYERS = 4;
}
