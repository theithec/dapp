package de.thtp.dapp;

import android.os.Bundle;
import de.thtp.dapp.app.PlayerList;
import de.thtp.dapp.app.Session;

public class GamePlayersEditGameActivity extends GamePlayersActivity {
	private int gamePos;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		gamePos = data.getInt(Const.K_GAME_POS);
	}

	@Override
	protected PlayerList getSuggestedPlayers() {
		return Session.getGame(gamePos).players;
	}

}
