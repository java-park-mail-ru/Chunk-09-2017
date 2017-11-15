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
			case 2: {
				array[0][0] = GameTools.PLAYER_1;
				array[maxX - 1][maxY - 1] = GameTools.PLAYER_2;
				return;
			}
			case 3: {
				array[0][0] = GameTools.PLAYER_1;
				array[maxX - 1][maxY - 1] = GameTools.PLAYER_2;
				array[maxX - 1][0] = GameTools.PLAYER_3;
				return;
			}
			case 4: {
				array[0][0] = GameTools.PLAYER_1;
				array[maxX - 1][maxY - 1] = GameTools.PLAYER_2;
				array[maxX - 1][0] = GameTools.PLAYER_3;
				array[0][maxY - 1] = GameTools.PLAYER_4;
				return;
			}
			default: {
				System.err.println("Too many players");
			}
		}
	}

	public boolean isGameOver() {

		for (int x = 0; x < maxX; ++x) {
			for (int y = 0; y < maxY; ++y) {
				if ( array[x][y].equals(GameTools.EMPTY_CELL) ) {
					return false;
				}
			}
		}
		return true;
	}

	public boolean isBlocked(Integer playerID) {

		for (int x = 0; x < maxX; ++x) {
			for (int y = 0; y < maxY; ++y) {
				if ( array[x][y].equals(playerID) ) {
					if ( !this.getPossiblePoints(new Spot(x, y)).isEmpty() ) {
						return false;
					}
				}
			}
		}
		return true;
	}

	public synchronized boolean makeStep(Step step) {

		// Валидация
		if ( array[step.src.x][step.src.y].equals(GameTools.EMPTY_CELL) ) {
			return false;
		}
		if ( !array[step.dst.x][step.dst.y].equals(GameTools.EMPTY_CELL) ) {
			return false;
		}
			if ( step.src.equals(step.dst) ) {
			return false;
		}
		if ( Math.abs(step.dst.x - step.src.x) > 2 ||
				Math.abs(step.dst.y - step.src.y) > 2 ) {
			return false;
		}

		// Ход
		array[step.dst.x][step.dst.y] = array[step.src.x][step.src.y];
		if ( Math.abs(step.dst.x - step.src.x) == 2 ||
				Math.abs(step.dst.y - step.src.y) == 2 ) {
			array[step.src.x][step.src.y] = GameTools.EMPTY_CELL;
		}

		this.assumedAround(step.dst);
		return true;
	}

	public Integer getPlayerInPoint(Spot spot) {
		return array[spot.x][spot.y];
	}

	private synchronized void assumedAround(Spot spot) {

		final Integer playerID = array[spot.x][spot.y];
		for (int x = spot.x - 1; x <= spot.x + 1; ++x) {
			for (int y = spot.y - 1; y <= spot.y + 1; ++y) {

				if ( !this.isValid(new Spot(x, y)) ) {
					continue;
				}
				if ( array[x][y] == GameTools.EMPTY_CELL ) {
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
				if ( array[x][y].equals(playerID) ) {
					spots.add(new Spot(x, y));
				}
			}
		}
		return spots;
	}

	public ArrayList<Spot> getPossiblePoints(Spot spot) {

		final ArrayList<Spot> possibleSpots = new ArrayList<>();
		for (int x = spot.x - 2; x <= spot.x + 2; ++x) {
			for (int y = spot.y - 2; y <= spot.y + 2; ++y) {

				if ( !this.isValid(new Spot(x, y)) ) {
					continue;
				}
				if ( array[x][y] == GameTools.EMPTY_CELL ) {
					possibleSpots.add(new Spot(x, y));
				}
			}
		}
		return possibleSpots;
	}

	public Integer getAssumedCount(Spot spot, Integer playerID) {

		Integer count = 0;
		for (int x = spot.x - 1; x <= spot.x + 1; ++x) {
			for (int y = spot.y - 1; y <= spot.y + 1; ++y) {

				if ( !this.isValid(new Spot(x, y)) ) {
					continue;
				}
				if ( array[x][y] == GameTools.EMPTY_CELL ) {
					continue;
				}
				++count;
			}
		}
		return count;
	}

	private Boolean isValid(Spot spot) {
		if (spot.x < 0 || spot.y < 0) {
			return false;
		}
		if (spot.x >= maxX || spot.y >= maxY) {
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
}
