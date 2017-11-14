package application.models.game.field;

public class Step {

	public Point src;
	public Point dst;

	public Step(Point src, Point dst) {
		this.src = src;
		this.dst = dst;
	}

	public Point getSrc() {
		return src;
	}

	public Point getDst() {
		return dst;
	}
}
