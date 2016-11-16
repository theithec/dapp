package de.thtp.dapp.app;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import de.thtp.dapp.DAppPrefs;

public class Session {

	private static Session instance;
	public static final PlayerList EMPTY_PLAYERLIST = new PlayerList();
	private final GameList gameList;
	private PlayerList players;
	private static IDB idb;
	private int id;

	private Session(String name) {
		Session.instance = this;
		this.players = new PlayerList();
		this.gameList = new GameList(name);
	}

	public static void start(String name, PlayerList basePlayers) {
		Session.instance = new Session(name);
		Session.instance.id = idb.insertSession(name);
		updatePlayers(basePlayers);
	}

	public static void load(int id, String name) {
		Session.instance = new Session(name);
		idb.loadSession(id, instance.gameList, instance.players);
		Session.instance.id = id;
		//Collections.sort(instance.players);

	}

	public static PlayerList getPlayersbyIDs(List<Integer> ids) {
		PlayerList allPlayers = getKnownPlayers();
		PlayerList pl = new PlayerList();
		for (Player player : allPlayers) {
			if (ids.contains(player.id)) {
				pl.add(player);
			}
		}
		return pl;
	}

	public static PlayerList getKnownPlayers() {
		return idb.getKnownPlayers();
	}

	public static String getName() {
		if (instance == null) {
			return "";
		}
		return instance.gameList.name;
	}

	public static boolean isReady() {
		return instance != null;
	}

	public static PlayerList getSessionPlayers() {
		if (null == instance) {
			return new PlayerList();
		}
		return instance.players;
	}

	public static PlayerList getVisibleSessionPlayers() {
		if (DAppPrefs.HIDE_INACTIVE_PLAYERS) {
			return getActivePlayers();
		}
		return getSessionPlayers();
	}

	public static PlayerList getActivePlayers() {
		PlayerList actives = new PlayerList();
		for (Player p : getSessionPlayers()) {
			if (p.isActive) {
				actives.add(p);
			}
		}
		return actives;
	}

	public static int getSessionsCount() {
		return idb.getSessionIdsByName().size();
	}

	public static ResultList getResultList() {
		// @todo used cached Resultlist (and check if its clean)
		return new ResultList(instance.gameList);
	}

	public static void addGame(PlayerList players, PlayerList winners,
			int points, int boecke, int size) {
		Game g = new Game(players, winners, points, boecke, size);
		g.id = idb.writeGame(g);
		instance.gameList.add(g);

	}

	public static void updatePlayers(PlayerList basePlayers) {
		int cnt = 0;
		//instance.players = new PlayerList();
		for (Player bp : basePlayers) {
			bp.pos = cnt++;
			idb.updateOrCreatePlayer(bp, instance);
		}
		//Collections.sort(basePlayers);
		instance.players = basePlayers;
	}

	public static void setIDB(IDB db) {
		Session.idb = db;

	}

	public static boolean hasIDB() {
		return idb != null;
	}

	public static Map<String, Integer> getSessionIdsByName() {
		return idb.getSessionIdsByName();
	}

	public static boolean deleteSession(int sessionID) {
		if (sessionID == Session.instance.id) {
			return false;
		}
		return idb.deleteSession(sessionID);
	}

	public static void clear() {
		instance = null;
	}

	public static Game getGame(int gamePos) {
		return instance.gameList.get(gamePos);
	}

	public static void editGame(int gamePos, PlayerList players,
			PlayerList winners, int points, int boecke) {
		Game g = getGame(gamePos);
		g.players = players;
		g.winners = winners;
		g.points = points;
		g.boeckeCreated = boecke;
		idb.updateGame(g);
	}

	public static void renamePlayer(int playerId, String name) {
		Player player = idb.getKnownPlayers().getById(playerId);
		player.name = name;
		idb.renamePlayer(playerId, name);

	}

	public static Map<String, Integer> getSessionIdsByNamesWithPlayer(
			int playerId) {
		return idb.getSessionIdsByNamesWithPlayer(playerId);
	}

	public static void deletePlayer(int playerId) {
		idb.deletePlayer(playerId);

	}

	public static PlayerList getPlayersSuggestionForNextGame() {
		PlayerList sug = new PlayerList();
		int gamesSize = instance.gameList.size();
		if (gamesSize > 0) {
			Game lastGame = instance.gameList.get(gamesSize - 1);
			PlayerList players = getVisibleSessionPlayers();
			for (int i = 0; i < players.size(); i++) {
				Player p = players.get(i);
				if (lastGame.players.contains(p)) {
					Player nextPlayer = players.get((i + 1) % players.size());
					sug.add(nextPlayer);
				}
			}
		}
		return sug;
	}

	public static boolean hasGames() {
		return instance.gameList.size() > 0;
	}
}
