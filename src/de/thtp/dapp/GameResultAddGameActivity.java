package de.thtp.dapp;

import android.content.Intent;
import android.os.Bundle;
import de.thtp.dapp.app.PlayerList;
import de.thtp.dapp.app.Session;

public class GameResultAddGameActivity extends GameResultActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	void putGameToData(PlayerList winners, int points, int boecke) {
		Session.addGame(players, winners, points, boecke, players.size());
	}
	
	@Override
	void initWithPlayers() {
		this.suggBoecke = null != data && data.containsKey(Const.K_SUGG_BOECKE) ? data
				.getInt(Const.K_SUGG_BOECKE) : 0;
		boeckeForThisGame = null != data
			&& data.containsKey(Const.K_BOECKE_FOR_THIS_GAME) ? data
				.getInt(Const.K_BOECKE_FOR_THIS_GAME) : 0;
		super.initWithPlayers();
		
	}

	@Override
	void pickPlayers() {
		Intent intent  = new Intent(this,GamePlayersAddGameActivity.class);
		startActivityForResult(intent, PICK_PLAYER_REQUEST);
	}

}
