package de.thtp.dapp.app;

import java.util.ArrayList;
import java.util.List;

public class ResultList extends ArrayList<Result> {

	private static final long serialVersionUID = 1L;
	private final ArrayList<Integer> boeckeForGames;
	private final GameList games;

	private int lowest;
	private int highest;

	public ResultList(GameList games) {// DApp dApp){
		PlayerList players = Session.getSessionPlayers();
		boeckeForGames = new ArrayList<Integer>();
		lowest = 0;
		highest = 0;
		this.games = games;
		Result rst = new Result(players);
		int upTo = games.size();
		for (int i = 0; i < upTo; i++) {
			Game g = games.get(i);
			rst = new Result(rst, g, i, boeckeForGames);
			int rl = rst.getLowest();
			int rh = rst.getHighest();
			if (rl < lowest)
				lowest = rl;
			if (rh > highest)
				highest = rh;
			add(rst);
		}
	}

	public int[] getOutstandingBoecke() {
		int gs = games.size();
		int bs = boeckeForGames.size();
		int d = bs - gs;
		int[] ob = new int[d];

		for (int i = 0; i < ob.length; i++) {
			ob[i] = boeckeForGames.get(i + gs);
		}
		return ob;
	}

	public int[][] valuesTable() {
		int rowSize = size();
		List<Player> visP = Session.getVisibleSessionPlayers();
		int visPSize = visP.size();
		int colSize = visPSize + 2;
		int[][] t = new int[rowSize][colSize];
		for (int rID = 0; rID < rowSize; rID++) {
			Result res = get(rID);
			int pID;
			for (pID = 0; pID < visPSize; pID++) {
				t[rID][pID] = res.get(visP.get(pID));
			}
			t[rID][pID] = res.gamePoints();
			t[rID][++pID] = res.getBoecke();
		}
		return t;
	}

	public int[] resultPeaks() {
		return new int[] { lowest, highest };
	}

}
