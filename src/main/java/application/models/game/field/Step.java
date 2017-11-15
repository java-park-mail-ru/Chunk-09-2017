package application.models.game.field;

public class Step {

	public Spot src;
	public Spot dst;

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
