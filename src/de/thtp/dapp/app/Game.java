package de.thtp.dapp.app;

import java.util.HashMap;
import java.util.Map;

import de.thtp.dapp.DAppPrefs;

public class Game {
	public PlayerList players;
	public PlayerList winners;
	public int points;
	public int boeckeCreated;

	public final int currentPlayersSize;
	public int id;

	public Game(PlayerList players, PlayerList winners, int points,
			int boeckeCreated, int size) {
		this.players = players;
		this.winners = winners;
		this.points = points;
		this.boeckeCreated = boeckeCreated;
		this.currentPlayersSize = size;
	}

	public Game(int id, PlayerList players, PlayerList winners, int points,
			int boeckeCreated, int size) {
		this(players, winners, points, boeckeCreated, size);
		this.id = id;
	}

	public Map<Player, Integer> gameResult(int boeckeInThisGame) {
		Map<Player, Integer> res = new HashMap<Player, Integer>();
		int winnerPoints = points;
		for (int i = 0; i < boeckeInThisGame; i++) {
			winnerPoints *= 2;
		}
		int looserPoints = DAppPrefs.COUNT_MINUS ? -winnerPoints : 0;


		int winnersSize = winners.size();
		if (winnersSize == 1) {
			winnerPoints *= 3;
		} else if (winnersSize == 3) {
			looserPoints *= 3;
		}
		for (Player p : players) {
			int playerPoints = winners.contains(p) ? winnerPoints : looserPoints;
			res.put(p, playerPoints);
		}
		return res;
	}
}
