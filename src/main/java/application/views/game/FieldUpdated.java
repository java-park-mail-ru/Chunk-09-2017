package application.views.game;

import com.fasterxml.jackson.annotation.JsonProperty;

public class FieldUpdated {

    @JsonProperty
    private Integer[][] fieled;

    public FieldUpdated(Integer[][] fieled) {
        this.fieled = fieled;
    }

    public Integer[][] getFieled() {
        return fieled;
    }

    public void setFieled(Integer[][] fieled) {
        this.fieled = fieled;
    }
}
