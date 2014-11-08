package de.thtp.dapp;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import de.thtp.dapp.app.BasePlayer;
import de.thtp.dapp.app.Game;
import de.thtp.dapp.app.GameList;
import de.thtp.dapp.app.IDB;
import de.thtp.dapp.app.Player;
import de.thtp.dapp.app.PlayerList;
import de.thtp.dapp.app.Session;


public class DB extends SQLiteOpenHelper implements IDB {
	public static final PlayerList EMPTY_PLAYER_LIST = null;

		private static final String DATABASE_NAME = "Dapp2DB";
		private static final int DATABASE_VERSION = 1;
		
		private int currentSessionID =  0;

		public DB(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {

			db.execSQL("CREATE TABLE sessions ( _id INTEGER PRIMARY KEY AUTOINCREMENT,"
					+ "name TEXT NOT NULL,"
					+ "started datetime default current_timestamp)");
			db.execSQL("CREATE TABLE games ( _id INTEGER PRIMARY KEY AUTOINCREMENT,"
					+ "session_id INTEGER NOT NULL,"
					+ "points INTEGER NOT NULL,"
					+ "current_players_length INTEGER NOT NULL,"
					+ "boecke_created INTEGER NOT NULL)");
			db.execSQL("CREATE TABLE players ( _id INTEGER PRIMARY KEY AUTOINCREMENT,"
					+ "name TEXT NOT NULL UNIQUE)");
			db.execSQL("CREATE TABLE games_players ( _id INTEGER PRIMARY KEY AUTOINCREMENT,"
					+ "game_id INTEGER NOT NULL,"
					+ "player_id INTEGER NOT NULL,"
					+ "is_winner BOOLEAN)");
			db.execSQL("CREATE TABLE sessions_players ( _id INTEGER PRIMARY KEY AUTOINCREMENT,"
					+ "session_id INTEGER NOT NULL, player_id INTEGER NOT NULL, pos INTEGER NOT NULL, is_active BOOLEAN, diff INTEGER default 0,"
					+ " UNIQUE(session_id, player_id) ON CONFLICT REPLACE)");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub
		}
		
		private int playerIdFromName(String name){
			int id = -1;
			SQLiteDatabase rdb = getReadableDatabase();
			Cursor cur = rdb.query("players", new String[] { "_id" }, "name=\""+name+"\"",
					null, null, null, null);
			boolean any = cur.getCount() > 0;
			while (any && cur.moveToNext()) {
				id = cur.getInt(0);
			}
			rdb.close();
			return id;
		}
		
		@Override
		public Player updateOrCreatePlayer(BasePlayer bp, Session session, int pos) {
			
			int id = playerIdFromName(bp.name);
			ContentValues cv = new ContentValues();
			SQLiteDatabase wdb = getWritableDatabase();
			cv.put("name", bp.name);
			id = (int)wdb.insertWithOnConflict("players", null, cv, SQLiteDatabase.CONFLICT_IGNORE );
			id = playerIdFromName(bp.name);
			wdb.close();
			wdb = getWritableDatabase();
			Player found = session.players.getByName(bp.name);
			cv = new ContentValues();
			cv.put("diff", bp.diff);
			cv.put("is_active", bp.isActive);
			cv.put("pos", pos);
			if (found == null){
				cv.put("session_id", currentSessionID);
				cv.put("player_id", id);
				long r = wdb.insertWithOnConflict("sessions_players", null, cv, SQLiteDatabase.CONFLICT_IGNORE);
				found = new Player(id, pos, bp);
				session.players.add(found);
			}else {
				//cv = new ContentValues();
				wdb.update("sessions_players", cv, "player_id="+found.id, null);
			}
			
			wdb.close();
			found.isActive = bp.isActive;
			found.diff = bp.diff;
			return found;
		}

		@Override
		public void insertSession(String sessionName) {
			SQLiteDatabase wdb = getWritableDatabase();
			ContentValues cv = new ContentValues();
			cv.put("name", sessionName);
			currentSessionID = (int) wdb.insert("sessions", null, cv);
			wdb.close();
			
		}
		
		public int writeGame(Game g) {
			int currentPlayersLength= g.currentPlayersSize; //_currSession.getActivePlayers().size();
			int gameId;
			SQLiteDatabase wdb = getWritableDatabase();
			ContentValues cv = new ContentValues();
			cv.put("session_id", currentSessionID);
			cv.put("points", g.points);
			cv.put("boecke_created", g.boeckeCreated);
			cv.put("current_players_length", currentPlayersLength);
			gameId = (int) wdb.insert("games", null, cv);

			PlayerList players = g.players;
			PlayerList winners = g.winners;
			for (Player player : players) {
				cv = new ContentValues();
				cv.put("player_id", player.id);
				cv.put("is_winner", winners.contains(player) ? 1 : 0);
				cv.put("game_id", gameId);
				wdb.insert("games_players", null, cv);
			}

			wdb.close();
			return gameId;
		}

		@Override
		public PlayerList getKnownPlayers() {
			SQLiteDatabase rdb = getReadableDatabase();
			Cursor cur = rdb.query("players", new String[] { "_id", "name" }, null,
					null, null, null, null);
			boolean any = cur.getCount() > 0;
			PlayerList pl = new PlayerList();
			while (any && cur.moveToNext()) {
				Player p = new Player(cur.getInt(0), cur.getInt(1), new BasePlayer(cur.getString(1)));
				pl.add(p);
				
			}
			return pl;
		}
		
		private Map<String, Integer> idsByNameFor(String name, String[] fields) {
			Map<String, Integer> map = new HashMap<String, Integer>();
			SQLiteDatabase rdb = getReadableDatabase();
			Cursor cur = rdb.query(name, fields, null, null, null, null, null);
			while (cur.moveToNext()) {
				map.put(cur.getString(1), cur.getInt(0));
			}
			cur.close();
			rdb.close();
			return map;
		}

		@Override
		public Map<String, Integer> getSessionIdsByName() {
			return idsByNameFor("sessions", new String[] { "_id", "started" });
		}

		@Override
		public Map<String, Integer> getPlayerIdsByName() {
			return idsByNameFor("players", new String[] { "_id", "name" });
		}
		
		@Override
		public void loadSession(int id, GameList games, PlayerList players) {
			SQLiteDatabase rdb = getReadableDatabase();
			Cursor cur = rdb.query("sessions", new String[] { "name" },
					"_id=" + id, null, null, null, null);
			cur.moveToFirst();
			currentSessionID = id;
			cur = rdb.query(
				"sessions_players",
				new String[] { "player_id" , "pos", "diff", "is_active"},
				"session_id=" + id, 
				null, null, null, "pos DESC"
			);
			
			while (cur.moveToNext()) {
				int pid = cur.getInt(0);
				Cursor cur2 = rdb.query("players", new String[] { "name" }, "_id="
						+ pid, null, null, null, null);
				cur2.moveToFirst();
				Player p = new Player(
					pid, 
					cur.getInt(1),
					new BasePlayer(cur2.getString(0),
						cur.getInt(2),
						cur.getInt(3) == 1
					)
				);
				players.add(p);
			}
			
			
			cur = rdb.query(
				"games",
				new String[] { "_id", "points",	"boecke_created", "current_players_length" },
				"session_id=" + id,
				null, null, null, null
			);
			boolean any = cur.getCount() > 0;
			PlayerList gPlayers, winners;
			Cursor cur2;
			while (any && cur.moveToNext()) {
				gPlayers = new PlayerList();
				winners = new PlayerList();
				cur2 = rdb.query("games_players", new String[] { "player_id",
						"is_winner" }, "game_id=" + cur.getInt(0), null, null,
						null, null);
				boolean any2 = cur2.getCount() > 0;
				while (any2 && cur2.moveToNext()) {
					Player p = players.getById(cur2.getInt(0));
					gPlayers.add(p);
					if (cur2.getInt(1) == 1) {
						winners.add(p);
					}
				}
				Game g = new Game(cur.getInt(0), gPlayers, winners, cur.getInt(1), cur.getInt(2),
						cur.getInt(3));
				games.add(g);
			}
		}

		@Override
		public boolean deleteSession(int sessionId) {
			SQLiteDatabase wdb = getWritableDatabase();
			boolean delSession = wdb.delete("sessions", "_id" + "=" + sessionId, null) > 0;
			Cursor cur = wdb.query("games", new String[]{"_id"} , "session_id=" + sessionId, null, null,null,null);
			boolean any = cur.getCount() > 0;
			while(any && cur.moveToNext()){
				wdb.delete("games_players", "game_id="+cur.getInt(0),null);
			}
			wdb.delete("games", "session_id" + "=" + sessionId, null);
			wdb.delete("sessions_players", "session_id" + "=" + sessionId, null);
			
			return delSession;
		}

		@Override
		public void updateGame( Game game) {
			SQLiteDatabase wdb = getWritableDatabase();
			ContentValues cv = new ContentValues();
			cv.put("points", game.points);
			cv.put("boecke_created", game.boeckeCreated);
			cv.put("current_players_length", game.currentPlayersSize);
			wdb.update("games", cv, "_id="+game.id, null );
			
			wdb.delete("games_players", "game_id="+game.id, null);
			
			for (Player p: game.players){
				cv = new ContentValues();
				cv.put("game_id", game.id);
				cv.put("player_id", p.id);
				cv.put("is_winner", game.winners.contains(p));
				wdb.insert("games_players", null, cv);
			}
			wdb.close();
			
		}

		@Override
		public void renamePlayer(int playerId, String name) {
			SQLiteDatabase wdb = getWritableDatabase();
			ContentValues cv = new ContentValues();
			cv.put("name", name);
			wdb.update("players", cv, "_id="+playerId,null);
			wdb.close();
			
		}

		@Override
		public Map<String, Integer> getSessionIdsByNamesWithPlayer(int playerId) {
			Map<String, Integer> namesById = new TreeMap<String, Integer>();
			SQLiteDatabase rdb = getReadableDatabase();
			Set<Integer> sessionIds = new HashSet<Integer>();
			String sessionIdsStr = "(";
			String sep = "";
			Cursor cur = rdb.query(
					true, //distinct
					"sessions_players",
					new String[]{"session_id"}, 
					"player_id="+playerId, 
					null,null, null, null,null);
			boolean any = cur.getCount() > 0;
			while (any && cur.moveToNext()) {
				int id = cur.getInt(0);
				sessionIds.add(id);
				sessionIdsStr += sep + id;
				sep=",";
			}
			sessionIdsStr += ")";
			cur = rdb.query(
				"sessions",
				new String[]{"_id", "name"},
				"_id in " + sessionIdsStr,
				null, null,null,null,null
			);
			any = cur.getCount() > 0;
			while (any && cur.moveToNext()) {
				namesById.put(cur.getString(1), cur.getInt(0));
			}
			rdb.close();
			return namesById;
		}

		@Override
		public void deletePlayer(int playerId) {
			Map<String, Integer> sessions= getSessionIdsByNamesWithPlayer(playerId);
			String sessionsWhere ="(";
			String sep = "";
			for (String sname: sessions.keySet()){
				int sessionId = sessions.get(sname);
				sessionsWhere += sep + sessionId;
				sep = ",";
			}
			sessionsWhere+=")";
			SQLiteDatabase wdb = getWritableDatabase();
			wdb.delete("sessions", "_id in "+sessionsWhere, null);
			wdb.delete("sessions_players", "session_id in "+sessionsWhere, null);
			wdb.delete("players", "_id="+playerId, null);
			wdb.close();
		}
}
