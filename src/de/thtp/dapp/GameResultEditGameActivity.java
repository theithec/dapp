package de.thtp.dapp;

import android.content.Intent;
import android.os.Bundle;
//import android.util.Log;
import de.thtp.dapp.app.Game;
import de.thtp.dapp.app.PlayerList;
import de.thtp.dapp.app.ResultList;
import de.thtp.dapp.app.Session;


public class GameResultEditGameActivity extends GameResultActivity {

	PlayerList suggWinners;
	int gamePos;
	Game game;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		gamePos = data.getInt(Const.K_GAME_POS);
		/*suggWinners = data.containsKey(Const.K_SUGG_WINNERS) ? dappData
				.getCurrentSession().getPlayersbyIDs(
						data.getIntegerArrayList(Const.K_SUGG_WINNERS))
				: new PlayerList();
	*/
		

	}

	@Override
	void initWithPlayers() {
		//Log.d("dapp", "GAMEID: " + gameId);
		game = Session.getGame(gamePos);
		ResultList rl = Session.getResultList();
		boeckeForThisGame = rl.get(gamePos).getBoecke();
		suggWinners = game.winners;
		initialPoints= game.points;
		super.initWithPlayers();
		for (PlayerCheckBox pbox : playerCheckBoxes) {
			pbox.setChecked(suggWinners.contains(pbox.player));
		}
		this.editTexts[editTextsPoints].setText("" + game.points);
		this.editTexts[editTextsBoecke].setText("" + game.boeckeCreated);

	}
	

	/*
	 * @Override Intent getPickPlayerIntent(){ Intent i = new
	 * Intent(this,SessionPlayersActivity.class); i.putExtra(Const.K_GAME_ID,
	 * data.getInt(Const.K_GAME_ID)); return i; }
	 */
	/*
	@Override
	void putGameToData(PlayerList winners, int points, int boecke) {
		GameList curr = dappData.getCurrentSession();
		Game g = new Game(players, winners, points, boecke, curr.getActivePlayers().size() );
		//dappData.get() = g;
		curr.set(data.getInt(Const.K_GAME_ID), g);
		//dappData.getCurrentSession().set(data.getInt(Const.K_GAME_ID), g);//updateCurrentSessionGame(data.getInt(Const.K_GAME_ID),
		//		players, winners, points, boecke);

	}
*/
	
	@Override
	void pickPlayers() {
		Intent intent = new Intent(this, GamePlayersEditGameActivity.class);
		intent.putExtra(Const.K_GAME_POS, gamePos);
		startActivityForResult(intent, PICK_PLAYER_REQUEST);
	} 

	@Override
	void putGameToData(PlayerList winners, int points, int boecke) {
		// TODO Auto-generated method stub
		Session.editGame(gamePos, players, winners, points, boecke);
		
	}



}
