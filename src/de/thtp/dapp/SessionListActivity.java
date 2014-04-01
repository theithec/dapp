package de.thtp.dapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
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
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.sessionlistoptionsmenu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
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
						Toast.makeText(a, "can't delete current Session",
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

	@Override
	protected void onListItemClick(AdapterView l, View v, int position, long id) {
		selectedSession = ((TextView) v).getText().toString();
		openOptionsMenu();
	}
}
