package de.thtp.dapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.TextView;
import android.widget.Toast;
import de.thtp.dapp.app.Session;

public class SessionListActivity extends DappListViewActivity {

	String selectedSession;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		map = Session.getSessionIdsByName();
		super.onCreate(savedInstanceState);
		setTitle(R.string.SessionsCtrl);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {

		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.sessionlistoptionsmenu, menu);
		return;
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
		// info.targetView.toString();
		selectedSession = (String) ((TextView) info.targetView).getText();
		final int sessionID = map.get(selectedSession);
		final Activity a = this;
		switch (item.getItemId()) {

		case R.id.itemSessionOpen:
			Session.load(sessionID, selectedSession);
			Toast.makeText(this, "loaded: " + selectedSession,
					Toast.LENGTH_LONG).show();
			startActivity(new Intent(this, SessionResultActivity.class));
			finish();
			return true;
		case R.id.itemSessionDelete:
			new DAppActionQuestion(this, new DAppAction("Wirklich löschen?") {
				@Override
				void execute() {

					if (Session.deleteSession(sessionID)) {
						Toast.makeText(a, "deleted: " + selectedSession,
								Toast.LENGTH_LONG).show();
						setListAdapter();
					} else {
						Toast.makeText(a, R.string.sessionCantDeleteCurrent,
								Toast.LENGTH_LONG).show();
					}
					endclick();
				}
			});
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}

	}
}
