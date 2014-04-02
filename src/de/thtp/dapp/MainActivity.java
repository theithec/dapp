package de.thtp.dapp;

import java.util.Date;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import de.thtp.dapp.app.Session;



public class MainActivity extends DappActivity {

	Button 	btnContinueSession,
			btnSessionsCtrl,
			btnPlayersCtrl;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		btnContinueSession = (Button) findViewById(R.id.btnContinueSession);
		btnSessionsCtrl = (Button) findViewById(R.id.btnSessionsCtrl);
		btnPlayersCtrl = (Button) findViewById(R.id.btnPlayersCtrl);
	}
	@Override
	public void onResume() {
		super.onResume();
		btnContinueSession.setEnabled(Session
				.isReady());
		btnSessionsCtrl.setEnabled(Session.getSessionsCount()>0);
		btnPlayersCtrl.setEnabled(Session.getKnownPlayers().size()>0);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
		
	}
	
	public void  continueSession(View v) {
		Intent i = new Intent(this, SessionResultActivity.class);
		startActivity(i);
	}
	
	public void startNewSession(View v) {
		
		final Intent i = new Intent(this, SessionPlayersActivity.class);
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		
		alert.setTitle("New Session");
		alert.setMessage("Name");
		final EditText input = new EditText(this);
		Date d = new Date();
		input.setText(DateFormat.getDateFormat(this).format(d) + "-"
				+ DateFormat.getTimeFormat(this).format(d));
		input.selectAll();
		alert.setView(input);

		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int whichButton) {
				Session.clear();
				i.putExtra(Const.K_SESSION_NAME, input.getText().toString());
				//Session.startSession(sessionName, activeByNames)
				startActivity(i);
			}
		});

		alert.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int whichButton) {
					}
				});
		alert.show();

	}
	
	public void settingsClick(View v) {
		startActivity(new Intent(this, DappPreferencesActivity.class));

	}
	public void SessionsCtrlClick(View v) {
		startActivity(new Intent(this, SessionListActivity.class));
	}
	
	public void playersCtrlClick(View v){
		startActivity(new Intent(this, PlayerListActivity.class));
	}

}
