package de.thtp.dapp;


import java.util.HashMap;
import java.util.Set;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import de.thtp.dapp.app.Player;
import de.thtp.dapp.app.PlayerList;
import de.thtp.dapp.app.Session;

public class PlayerListActivity extends DappListViewActivity {

	String selectedPlayerName;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		setTitle(getString(R.string.playersCtrl));
		map = new HashMap<String, Integer>();
		PlayerList pl = Session.getKnownPlayers();
		for (Player p: pl){
			map.put(p.name, p.id);
		}
		super.onCreate(savedInstanceState);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		final int playerId = selectedPlayerName!=null?map.get(selectedPlayerName):-1;
		switch (item.getItemId()) {
		
		case R.id.itemPlayerRename:
			new PlayerNameEditDialog(this, playerId,
					selectedPlayerName).show();
			return true;
		case R.id.itemPlayerDelete:
			Set<String> sessionNames = Session.getSessionIdsByNamesWithPlayer(playerId).keySet();
			String foundSessions = "";
			String sep="";
			for (String s:sessionNames){
				foundSessions += sep +  s;
				sep = ", ";
			}
			
			new DAppActionQuestion(
					this,
					new DAppAction(
							"Wirklich löschen?\nFolgende Sessions werden auch gelöscht:" + foundSessions) {
						@Override
						void execute() {
							Session.deletePlayer(playerId);
							endclick();
						}
					});
			//endclick();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/*@Override
	protected boolean onListItemLongClick(AdapterView l, View v, int position, long id) {
		selectedPlayerName = ((TextView) v).getText().toString();
		return true; //openOptionsMenu();
	}*/
}
