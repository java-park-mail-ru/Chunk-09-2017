package application.models.game.field;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Spot {

    @JsonProperty(value = "x")
    private Integer cstX;
    @JsonProperty(value = "y")
    private Integer cstY;

    public Spot(Integer cstX, Integer cstY) {
        this.cstX = cstX;
        this.cstY = cstY;
    }

    public Integer getCstX() {
        return cstX;
    }

    public Integer getCstY() {
        return cstY;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Spot)) {
            return false;
        }

        final Spot spot = (Spot) obj;

        if (!cstX.equals(spot.cstX)) {
            return false;
        }
        return cstY.equals(spot.cstY);
    }

    @Override
    public int hashCode() {
        int result = cstX.hashCode();
        result = HASH_NUMBER * result + cstY.hashCode();
        return result;
    }

    static final int HASH_NUMBER = 31;
}
