package application.models.game.field;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Spot {

    @JsonProperty(value = "x")
    Integer codestyleX;
    @JsonProperty(value = "y")
    Integer codestyleY;

    public Spot(Integer codestyleX, Integer codestyleY) {
        this.codestyleX = codestyleX;
        this.codestyleY = codestyleY;
    }

    public Integer getCodestyleX() {
        return codestyleX;
    }

    public Integer getCodestyleY() {
        return codestyleY;
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

        if (!codestyleX.equals(spot.codestyleX)) {
            return false;
        }
        return codestyleY.equals(spot.codestyleY);
    }

    @Override
    public int hashCode() {
        int result = codestyleX.hashCode();
        result = HASH_NUMBER * result + codestyleY.hashCode();
        return result;
    }

    static final int HASH_NUMBER = 31;
}
