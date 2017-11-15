package application.models.game.field;

public class Spot {

	public Integer x;
	public Integer y;

	public Spot(Integer x, Integer y) {
		this.x = x;
		this.y = y;
	}

	public Integer getX() {
		return x;
	}

	public Integer getY() {
		return y;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Spot)) return false;

		Spot spot = (Spot) o;

		if (!x.equals(spot.x)) return false;
		return y.equals(spot.y);
	}

	@Override
	public int hashCode() {
		int result = x.hashCode();
		result = 31 * result + y.hashCode();
		return result;
	}
}
