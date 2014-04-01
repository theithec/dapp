package de.thtp.dapp;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import de.thtp.dapp.app.PlayerList;
import de.thtp.dapp.app.Session;

public class SessionPlayersActivity extends DappActivity {

	CheckBox[] playerActiveCheckBoxes;
	EditText[] playerDiffEditTexts;
	String sessionName;
	List<PlayerNamesSpinner> spinners;

	List<String> allPlayers;
	Button okBtn;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sessionplayers);
		setTitle(R.string.players);
		allPlayers = Session.getKnownPlayers().getNames();
		allPlayers.add(0, "-");
		spinners = new ArrayList<SessionPlayersActivity.PlayerNamesSpinner>();
		sessionName = this.getIntent().getExtras()
				.getString(Const.K_SESSION_NAME);

		TableLayout tl = (TableLayout) findViewById(R.id.playerstablelayout);
		for (int i = 0; i < DAppPrefs.MAX_PLAYERS; i++) {
			tl.addView(getPlayerRow(i));
		}
		loadPlayers();
		okBtn = (Button) findViewById(R.id.btnSessionPlayersDone);
		
	
	}

	private void loadPlayers() {
		PlayerList players = Session.getActivePlayers();
		for (int i = 0; i < players.size(); i++) {
			PlayerNamesSpinner spinner = spinners.get(i);
			int pos = ((ArrayAdapter<String>) spinner.getAdapter())
					.getPosition(players.get(i).name);
			spinner.setSelection(pos);
		}
	}

	public void updateSpinners() {
		// Log.d("DEBUG", "updateSpinners");
		List<String> cpy = new ArrayList<String>(allPlayers);
		for (PlayerNamesSpinner spinner : spinners) {
			spinner.setOnItemSelectedListener(null);
			Object obj = spinner.getSelectedItem();
			if (obj != null && obj != "-") {
				cpy.remove(obj);
			}
		}
		int cntPlayers = 0;
		for (PlayerNamesSpinner spinner : spinners) {
			Object obj = spinner.getSelectedItem();
			List<String> cpy2 = new ArrayList<String>(cpy);
			if (obj != "-") {
				cpy2.add(0, obj.toString());
				cntPlayers++;
			}
			spinner.setSelection(0);
			// Log.d("DEBUG", "cpy2: "+ cpy2);

			spinner.update(cpy2);
		}
		
		for (PlayerNamesSpinner spinner : spinners) {
			spinner.setOnItemSelectedListener();
		}
		okBtn.setEnabled(cntPlayers>=DAppPrefs.MIN_PLAYERS);
	}

	public void addSessionPlayerName(View v) {
		new PlayerNameAddDialog(this).show();
		// allPlayers.add(v.toString());
	}

	public void startSessionWithPlayers(View v) {
		Map<String, Boolean> activeByNames = new LinkedHashMap<String, Boolean>();
		for (PlayerNamesSpinner spinner : spinners) {
			String name = spinner.getSelectedItem().toString();
			if (!name.equals("-")) {
				activeByNames.put(name, true);
			}
		}
		
		if (!Session.isReady()){
			Session.setIDB( new DB(this));
			Session.start( sessionName, activeByNames);
		} else {
			Session.updatePlayers(activeByNames);
		}
		Intent i = new Intent(this, SessionResultActivity.class);
		startActivity(i);
		finish();
	}

	private View getPlayerRow(int index) {
		TableRow tr = new TableRow(this);
		TextView tv = new TextView(this);
		tv.setText("" + (index + 1));
		tr.addView(tv);
		PlayerNamesSpinner ps = new PlayerNamesSpinner(this, index);
		spinners.add(ps);
		ps.update(allPlayers);
		tr.addView(ps);
		// tr.addView(playerActiveCheckBoxes[index] = new CheckBox(this));

		return tr;
	}

	class PlayerNamesSpinner extends Spinner {

		public PlayerNamesSpinner(Context context, int index) {
			super(context);
			setOnItemSelectedListener();
		}

		public void setOnItemSelectedListener() {
			this.setOnItemSelectedListener(new OnItemSelectedListener() {
				@Override
				public void onItemSelected(AdapterView<?> parentView,
						View selectedItemView, int position, long id) {
					if (position > 0) {
						updateSpinners();
					}
				}

				@Override
				public void onNothingSelected(AdapterView<?> parentView) {
				}
			});
		}

		public void update(List<String> playerNames) {
			int position = this.getSelectedItemPosition();
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(
					this.getContext(), android.R.layout.simple_spinner_item,
					playerNames);
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			this.setAdapter(adapter);
			this.setSelection(position);

		}

	}

	class PlayerNameAddDialog extends PlayerNameDialog {

		public PlayerNameAddDialog(final SessionPlayersActivity dappActivity) {
			super(dappActivity, "Add Player", "");
			setPositiveButton("Ok", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int whichButton) {
					String name = input.getText().toString();
					allPlayers.add(name);
					updateSpinners();
				}
			});
		}
	}
}
