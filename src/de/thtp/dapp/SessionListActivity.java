package de.thtp.dapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.TextView;
import android.widget.Toast;
import de.thtp.dapp.app.Session;

public class SessionListActivity extends DappListViewActivity {

	String selectedSession;
	private static final int MENU_DEL=90;
	private static final int MENU_OPEN=91;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		map = Session.getSessionIdsByName();
		super.onCreate(savedInstanceState);
		setTitle(R.string.SessionsCtrl);
	}

	/*
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		if (selectedSession!=null){
			menu.clear();
			menu.add(0, MENU_DEL, Menu.NONE, R.string.titleSessionDelete);
			menu.add(0, MENU_OPEN, Menu.NONE, R.string.titleSessionOpen);
		}
		return true;
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		return true; //super.onCreateOptionsMenu(menu);
	}
	*/
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
	    ContextMenuInfo menuInfo) {
		
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.sessionlistoptionsmenu, menu);
		return;
		/*
		menu.clear();
		menu.add(0, MENU_OPEN, Menu.NONE, R.string.titleSessionOpen);
		menu.add(0, MENU_DEL, Menu.NONE, R.string.titleSessionDelete);
		*/
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		//info.targetView.toString();
		selectedSession = (String) ((TextView)info.targetView).getText();
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

	
	

	
}
