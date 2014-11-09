package de.thtp.dapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import de.thtp.dapp.app.Session;

abstract public class DappActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (!Session.hasIDB()) {
			Session.setIDB(new DB(this));
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.defaultmenu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.mainmenu:
			startActivity(new Intent(this, MainActivity.class));
			finish();

		default:
			return super.onOptionsItemSelected(item);
		}
	}

}
