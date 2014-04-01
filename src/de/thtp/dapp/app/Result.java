package de.thtp.dapp.app;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.thtp.dapp.DAppPrefs;

public class Result extends HashMap<Player, Integer> {

	int points = 0;
	int boeckeInThisGame = 0;

	int lowest;
	int highest;

	public Result(PlayerList playerList) {
		lowest = 0;
		highest = 0;
		for (Player p : playerList) {
			put(p, checked(p.diff));
		}
	}

	public Result(Result rst, Game g, int gIndex, List<Integer> boeckeForGames) {
		points = g.points;
		Set<Player> players = rst.keySet();
		for (Player p : players) {
			put(p, rst.get(p));
		}
		while (boeckeForGames.size() <= gIndex) {
			boeckeForGames.add(0);
		}
		boeckeInThisGame = boeckeForGames.get(gIndex);

		Map<Player, Integer> gRes = g.gameResult(boeckeInThisGame);
		for (Player p : gRes.keySet()) {
			int ppoints = containsKey(p) ? get(p) : 0;
			ppoints += gRes.get(p);
			put(p, checked(ppoints));
		}

		int newBoecke = g.boeckeCreated;
		for (int b = 0; b < newBoecke; b++) {
			int insIndex = gIndex + 1;
			for (int m = 0; m < g.activePlayersSize; m++) {
				while (boeckeForGames.size() <= insIndex) {
					boeckeForGames.add(0);
				}
				int ob = boeckeForGames.get(insIndex);
				if (ob < DAppPrefs.MAX_BOECKE) {
					boeckeForGames.set(insIndex, ob + 1);
				} else if (!DAppPrefs.CUT_BOECKE && DAppPrefs.MAX_BOECKE != 0) {
					m--;
				}
				insIndex++;
			}
		}
	}

	protected int getLowest() {
		return lowest;
	}

	protected int getHighest() {
		return highest;
	}

	private int checked(int i) {
		if (i > highest)
			highest = i;
		if (i < lowest)
			lowest = i;
		return i;
	}

	public int getBoecke() {
		return boeckeInThisGame;
	}

	public int gamePoints() {
		return points;
	}
}
