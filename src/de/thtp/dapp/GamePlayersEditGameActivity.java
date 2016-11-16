package de.thtp.dapp;

import de.thtp.dapp.app.PlayerList;
import de.thtp.dapp.app.Session;

public class GamePlayersEditGameActivity extends GamePlayersActivity {

	@Override
	protected PlayerList getSuggestedPlayers() {
        int gamePos = data.getInt(Const.K_GAME_POS);
        return Session.getGame(gamePos).players;
    }

}
