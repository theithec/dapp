package de.thtp.dapp;

import de.thtp.dapp.app.PlayerList;
import de.thtp.dapp.app.Session;


public class GamePlayersAddGameActivity extends GamePlayersActivity {

	@Override
	protected PlayerList getSuggestedPlayers() {
		return Session.getPlayersSuggestionForNextGame();
	}


}
