package de.thtp.dapp;

import java.util.Set;
import java.util.TreeMap;

import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.TextView;
import de.thtp.dapp.app.Player;
import de.thtp.dapp.app.PlayerList;
import de.thtp.dapp.app.Session;

public class PlayerListActivity extends DappListViewActivity {

	private String selectedPlayerName;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		setTitle(getString(R.string.playersCtrl));
		map = new TreeMap<String, Integer>();
		PlayerList pl = Session.getKnownPlayers();
		for (Player p : pl) {
			map.put(p.name, p.id);
		}
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {

		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.playerlistoptionsmenu, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
		// info.targetView.toString();
		selectedPlayerName = (String) ((TextView) info.targetView).getText();
		final int playerId = selectedPlayerName != null ? map
				.get(selectedPlayerName) : -1;
		switch (item.getItemId()) {

		case R.id.itemPlayerRename:
			new PlayerNameEditDialog(this, playerId, selectedPlayerName).show();
			return true;
		case R.id.itemPlayerDelete:
			Set<String> sessionNames = Session.getSessionIdsByNamesWithPlayer(
					playerId).keySet();
			String foundSessions = "";
			String sep = "";
			for (String s : sessionNames) {
				foundSessions += sep + s;
				sep = ", ";
			}

			new DAppActionQuestion(this, new DAppAction(
					getString(R.string.deletePlayerQ) + foundSessions) {
				@Override
				void execute() {
					Session.deletePlayer(playerId);
					endclick();
				}
			});
			// endclick();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/*
	 * @Override protected boolean onListItemLongClick(AdapterView l, View v,
	 * int position, long id) { selectedPlayerName = ((TextView)
	 * v).getText().toString(); return true; //openOptionsMenu(); }
	 */
}
