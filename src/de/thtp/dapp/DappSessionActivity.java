package de.thtp.dapp;

import android.content.Intent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import de.thtp.dapp.app.Session;

public abstract class DappSessionActivity extends DappActivity{
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.sessionmenu, menu);
		return true;
	}
	
	
	
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.mainmenu:
			startActivity(new Intent(this, MainActivity.class));
			finish();
		case R.id.sessionplayers:
			Intent i = new Intent(this, SessionPlayersActivity.class);
			i.putExtra(Const.K_SESSION_NAME, Session.getName());

			startActivity(i);
			finish();

		default:
			return super.onOptionsItemSelected(item);
		}
	}


}
