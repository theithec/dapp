package de.thtp.dapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

public class DappPreferencesActivity extends PreferenceActivity implements
		OnSharedPreferenceChangeListener {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle(getString(R.string.dappSettings));
		addPreferencesFromResource(R.xml.preferences);
		getPreferenceScreen().getSharedPreferences()
				.registerOnSharedPreferenceChangeListener(this);
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences prefs, String arg1) {
		DAppPrefs.updateDroikoSettings(prefs);
		Toast.makeText(this, getText(R.string.settingsUpdated), Toast.LENGTH_LONG).show();
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.defaultmenu, menu);
		return true;
	}
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.mainmenu:
			startActivity(new Intent(this, MainActivity.class));
			finish();
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}
