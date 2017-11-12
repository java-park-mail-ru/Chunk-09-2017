package application.models.game;

import application.services.game.GameTools;

import javax.validation.constraints.NotNull;

public class Field {

    private final Integer[/*x*/][/*y*/] field;

    private final Integer maxX;
    private final Integer maxY;


    public Field(@NotNull Integer maxX, @NotNull Integer maxY) {
        this.maxX = maxX;
        this.maxY = maxY;

        field = new Integer[maxX][maxY];
        for (int i = 0; i < maxX; ++i) {
            for (int j = 0; j < maxY; ++j) {
                field[i][j] = GameTools.EMPTY_CELL;
            }
        }
    }

    public void simpleInitializeField() {
        field[0][maxY - 1] = field[maxX - 1][0] = GameTools.PLAYER_1;
        field[0][0] = field[maxX - 1][maxY - 1] = GameTools.PLAYER_2;
    }

    public boolean step(Integer x1, Integer y1, Integer x2, Integer y2) {

        if ( x2 < 0 || x2 >= maxX || y2 < 0 || y2 >= maxY) {
            // TODO throw exception OUT_OF_RANGE
            return false;
        }

        switch (getRange(x1, y1, x2, y2)) {
            case 1:
                field[x2][y2] = field[x1][y1];
                break;
            case 2:
                field[x2][y2] = field[x1][y1];
                field[x1][y1] = GameTools.EMPTY_CELL;
                break;
            default:
                //TODO throw exception
                return false;
        }
        consumeAround(x2, y2);

        return isGameOver();
    }

    public Integer[][] getField() {
        return field;
    }

    public boolean isGameOver() {
        for (int i = 0; i < maxX; ++i) {
            for (int j = 0; j < maxY; ++j) {
                if ( field[i][j] == GameTools.EMPTY_CELL ) {
                    return false;
                }
            }
        }
        return true;
    }

    @NotNull
    private Integer getRange(Integer x1, Integer y1, Integer x2, Integer y2) {
        return Math.max(Math.abs(x2 - x1), Math.abs(y2 - y1));
    }

    private void consumeAround(Integer x, Integer y) {

        for (int i = x - 1; i <= x + 1; ++i) {
            if ( i < 0 || i >= maxX) {
                continue;
            }
            for (int j = y - 1; j <= y + 1; ++j) {
                if ( j < 0 || j >= maxY) {
                    continue;
                }
                if ( GameTools.PLAYER_1 <= field[i][j] && field[i][j] < GameTools.PLAYER_MAX ) {
                    field[i][j] = field[x][y];
                }
            }
        }
    }


    // Getter & Setters
    public Integer getMaxX() {
        return maxX;
    }

    public Integer getMaxY() {
        return maxY;
    }
}
