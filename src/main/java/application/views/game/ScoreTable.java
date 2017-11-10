package application.views.game;

import application.views.user.UserScore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class ScoreTable {

	@JsonProperty
	private List<UserScore> players;

	@JsonProperty(value = "numberOfRecords")
	private Long numberOfPlayers;

	public ScoreTable(List<UserScore> players, Long numberOfPlayers) {
		this.players = players;
		this.numberOfPlayers = numberOfPlayers;
	}

	public List<UserScore> getPlayers() {
		return players;
	}

	public Long getNumberOfPlayers() {
		return numberOfPlayers;
	}
}
