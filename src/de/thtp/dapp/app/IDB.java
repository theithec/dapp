package de.thtp.dapp.app;

import java.util.Map;

public interface IDB {

	int insertSession(String sessionName);

	Player updateOrCreatePlayer(BasePlayer bp, Session session, int pos);

	int writeGame(Game g);

	PlayerList getKnownPlayers();

	Map<String, Integer> getSessionIdsByName();

	// Map<String, Integer> getPlayerIdsByName();

	void loadSession(int id, GameList games, PlayerList players);

	boolean deleteSession(int sessionId);

	void updateGame(Game game);

	void renamePlayer(int playerId, String name);

	Map<String, Integer> getSessionIdsByNamesWithPlayer(int playerId);

	void deletePlayer(int playerId);

}
