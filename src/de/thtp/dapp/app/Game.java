package de.thtp.dapp.app;

import java.util.HashMap;
import java.util.Map;

import de.thtp.dapp.DAppPrefs;

public class Game {
	public PlayerList players;
	public PlayerList winners;
	public int points;
	public int boeckeCreated;
	
	public int currentPlayersSize;
	public int id;
	public Game(PlayerList players, PlayerList winners, int points, int boeckeCreated, int size) {
		this.players = players;
		this.winners = winners;
		this.points = points;
		this.boeckeCreated = boeckeCreated;
		this.currentPlayersSize = size;
	}
	
	public Game(int id, PlayerList players, PlayerList winners, int points, int boeckeCreated, int size) {
		this(players, winners, points, boeckeCreated, size);
		this.id = id;
	}
	
	public Map<Player, Integer> gameResult(int boeckeInThisGame) {
		Map<Player, Integer> res = new HashMap<Player, Integer>();
		int wpoints = points;
		for (int i = 0; i < boeckeInThisGame; i++) {
			wpoints *= 2;
		}
		int lpoints = DAppPrefs.COUNT_MINUS ? -wpoints : 0;

		int wsize = winners.size();
		if (wsize == 1) {
			wpoints *= 3;
		} else if (wsize == 3) {
			lpoints *= 3;
		}
		for (Player p : players) {
			int ppoints = winners.contains(p) ? wpoints : lpoints;
			res.put(p, ppoints);
		}
		return res;
	}
}
