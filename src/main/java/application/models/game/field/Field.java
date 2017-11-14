package application.models.game.field;

import java.util.ArrayList;

public class Field {

	private Integer maxX;
	private Integer maxY;
	private Integer[][] array;

	public Field(Integer maxX, Integer maxY) {
		this.maxX = maxX;
		this.maxY = maxY;
		array = new Integer[maxX][maxY];
	}


	public boolean isGameOver() {
		// TODO Field::isGameOver;
		return false;
	}

	public boolean isBlocked(Long playerID) {
		// TODO Field::isBlocked;
		return false;
	}

	public boolean makeStep(Step step) {
		// TODO Field::makeStep;
		return false;
	}


	private void assumedAround(Point point) {
		// TODO Field::assumedAround;
	}

	private ArrayList<Point> getPossiblePoints(Point point) {
		// TODO Field::getPossiblePoints;
		return null;
	}
}
