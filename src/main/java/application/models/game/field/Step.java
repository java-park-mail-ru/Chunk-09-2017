package application.models.game.field;

public class Step {

    private Spot src;
    private Spot dst;

    public Step() {
    }

    public Step(Spot src, Spot dst) {
        this.src = src;
        this.dst = dst;
    }

    public Spot getSrc() {
        return src;
    }

    public Spot getDst() {
        return dst;
    }
}
