package de.thtp.dapp;

import android.content.Intent;

import de.thtp.dapp.app.PlayerList;
import de.thtp.dapp.app.Session;

public class GameResultAddGameActivity extends GameResultActivity {

	//@Override
	//protected void onCreate(Bundle savedInstanceState) {
	//	super.onCreate(savedInstanceState);
	//}

	@Override
	void putGameToData(PlayerList winners, int points, int boecke) {
		Session.addGame(players, winners, points, boecke, players.size());
	}

	@Override
	void initWithPlayers() {
		this.suggestedBoecke = null != data && data.containsKey(Const.K_SUGGESTED_BOECKE) ? data
				.getInt(Const.K_SUGGESTED_BOECKE) : 0;
		boeckeForThisGame = null != data
				&& data.containsKey(Const.K_BOECKE_FOR_THIS_GAME) ? data
				.getInt(Const.K_BOECKE_FOR_THIS_GAME) : 0;
		super.initWithPlayers();

	}

	@Override
	void pickPlayers() {
		Intent intent = new Intent(this, GamePlayersAddGameActivity.class);
		startActivityForResult(intent, PICK_PLAYER_REQUEST);
		// Object y =((Activity) intent.getComponent()).finish();

	}

}
