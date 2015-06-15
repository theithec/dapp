package de.thtp.dapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
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

import java.util.ArrayList;
import java.util.List;

import de.thtp.dapp.app.Player;
import de.thtp.dapp.app.PlayerList;
import de.thtp.dapp.app.Session;

public class SessionPlayersActivity2 extends DappActivity {
/*
	String sessionName;
	List<PlayerNumSpinner> spinners;
	List<EditText> diffTexts;
	List<CheckBox> activeCheckBoxes;

	PlayerList knownPlayers;
	PlayerList sessionPlayers;
	//List<String> availPlayerNames;
	Button okBtn;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sessionplayers);

		setTitle(R.string.players);
		knownPlayers = Session.getKnownPlayers();

		spinners = new ArrayList<SessionPlayersActivity.PlayerNumSpinner>();
		activeCheckBoxes = new ArrayList<CheckBox>();
		diffTexts = new ArrayList<EditText>();

		sessionPlayers = Session.getSessionPlayers();
		ArrayList<String> availPlayerNames = Session.getKnownPlayers().getNames();
		availPlayerNames.add(0, "-");

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
		PlayerList players = Session.getSessionPlayers();
		for (int i = 0; i < players.size(); i++) {
			Player p = players.get(i);
			PlayerNumSpinner spinner = spinners.get(i);
			int pos = ((ArrayAdapter<String>) spinner.getAdapter())
					.getPosition(p.name);
			spinner.setSelection(pos);
			diffTexts.get(i).setText(p != null ? "" + p.diff : "0");
			activeCheckBoxes.get(i).setChecked(p != null && p.isActive);
		}
	}

	public void updateSpinners() {
		return;
		/*
		List<String> cpy = new ArrayList<String>(availPlayerNames);
		for (PlayerNumSpinner spinner : spinners) {
			spinner.setOnItemSelectedListener(null);
			Object obj = spinner.getSelectedItem();
			if (obj != null && obj != "-") {
				cpy.remove(obj);
			}
		}
		int cntPlayers = 0;
		for (PlayerNumSpinner spinner : spinners) {
			Object obj = spinner.getSelectedItem();
			List<String> cpy2 = new ArrayList<String>(cpy);
			if (obj != "-") {
				cpy2.add(0, obj.toString());
				cntPlayers++;
			}
			spinner.setSelection(0);
			spinner.update(cpy2);
		}

		for (PlayerNumSpinner spinner : spinners) {
			spinner.setOnItemSelectedListener();
		}
		okBtn.setEnabled(cntPlayers >= DAppPrefs.MIN_PLAYERS);
		*/
	/*
	}

	public void addSessionPlayerName(View v) {
		new PlayerNameAddDialog(this).show();
	}

	public void startSessionWithPlayers(View v) {
		List<Player> basePlayers = new ArrayList<Player>();
		for (int i = 0; i < spinners.size(); i++) {
			PlayerNumSpinner spinner = spinners.get(i);
			String name = spinner.getSelectedItem().toString();

			if (!name.equals("-")) {
				Player bp = new Player(
						name, 
						i,
						Integer.parseInt(diffTexts.get(i).getText().toString()), 
						activeCheckBoxes.get(i).isChecked());
				basePlayers.add(bp);
			}
		}

		if (!Session.isReady()) {
			Session.setIDB(new DB(this));
			Session.start(sessionName, basePlayers);
		} else {
			Session.updatePlayers(basePlayers);
		}
		Intent i = new Intent(this, SessionResultActivity.class);
		startActivity(i);
		finish();
	}

	private View getPlayerRow(int index) {
		TableRow tr = new TableRow(this);

		PlayerNumSpinner ps = new PlayerNumSpinner(this, index);
		spinners.add(ps);
		ArrayList<String> numStrings = new ArrayList<String>();
		for(int i=0;i<sessionPlayers.size();i++){
			numStrings.add("" + i);
		}

		ps.update(numStrings);
		tr.addView(ps);
		TextView tv = new TextView(this);
		if (index < sessionPlayers.size()){

			tv.setText(sessionPlayers.get(index).name);
		}else {
			tv.setText("");

		}
		tr.addView(tv);
		CheckBox cb = new CheckBox(this);
		activeCheckBoxes.add(cb);
		tr.addView(cb);

		EditText et = new EditText(this);
		et.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
		// Object selItem = ps.getSelectedItem();

		et.setText("0");
		diffTexts.add(et);
		tr.addView(et);

		return tr;
	}

	class PlayerNumSpinner extends Spinner {

		private int index;

		public PlayerNumSpinner(Context context, int index) {
			super(context);
			this.index = index;
			setOnItemSelectedListener();
		}

		public void setOnItemSelectedListener() {
			this.setOnItemSelectedListener(new OnItemSelectedListener() {
				@Override
				public void onItemSelected(AdapterView<?> parentView,
						View selectedItemView, int position, long id) {
					if (position > 0) {
						TextView tv = (TextView) selectedItemView;
						String name = tv.getText().toString();
						Player p = null;
						if (name != "-") {
							p = sessionPlayers.getByName(name);
						}
						diffTexts.get(index).setText(
								p != null ? "" + p.diff : "0");
						activeCheckBoxes.get(index).setChecked(
								p != null ? p.isActive : true);
						// String tv.getT
						/*
						 * String name = selItem!=null?selItem.toString():null;
						 * Player foundInSession = name
						 * !=null?sessionPlayers.getByName(name):null;
						 * foundInSession !=null?""+foundInSession.diff:"0");
						 */
	/*
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
					//Session.a
					//availPlayerNames.add(name);
					updateSpinners();
				}
			});
		}
	}
	*/
}
