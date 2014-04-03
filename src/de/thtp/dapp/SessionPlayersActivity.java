package de.thtp.dapp;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
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
import de.thtp.dapp.app.BasePlayer;
import de.thtp.dapp.app.Player;
import de.thtp.dapp.app.PlayerList;
import de.thtp.dapp.app.Session;

public class SessionPlayersActivity extends DappActivity {

	String sessionName;
	List<PlayerNamesSpinner> spinners;
	List <EditText> diffTexts;
	List<CheckBox> activeCheckBoxes;
	
	PlayerList allPlayers;
	PlayerList sessionPlayers; 
	List<String> allPlayerNames;
	Button okBtn;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sessionplayers);
		
		setTitle(R.string.players);
		allPlayers = Session.getKnownPlayers();

		spinners = new ArrayList<SessionPlayersActivity.PlayerNamesSpinner>();
		activeCheckBoxes =new ArrayList<CheckBox>();
		diffTexts = new ArrayList<EditText>();
		
		sessionPlayers = Session.getSessionPlayers();
		allPlayerNames = Session.getKnownPlayers().getNames();
		allPlayerNames.add(0, "-");
		
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
			PlayerNamesSpinner spinner = spinners.get(i);
			int pos = ((ArrayAdapter<String>) spinner.getAdapter())
					.getPosition(p.name);
			spinner.setSelection(pos);
			diffTexts.get(i).setText(p!=null?""+p.diff:"0");
			activeCheckBoxes.get(i).setChecked(p!=null && p.isActive);
		}
	}

	public void updateSpinners() {
		List<String> cpy = new ArrayList<String>(allPlayerNames);
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
			spinner.update(cpy2);
		}
		
		for (PlayerNamesSpinner spinner : spinners) {
			spinner.setOnItemSelectedListener();
		}
		okBtn.setEnabled(cntPlayers>=DAppPrefs.MIN_PLAYERS);
	}

	public void addSessionPlayerName(View v) {
		new PlayerNameAddDialog(this).show();
	}

	public void startSessionWithPlayers(View v) {
		List<BasePlayer> basePlayers =new ArrayList<BasePlayer>();
		for (int i=0; i<spinners.size(); i++) {
			PlayerNamesSpinner spinner = spinners.get(i);
			String name = spinner.getSelectedItem().toString();
			
			if (!name.equals("-")) {
				BasePlayer bp = new BasePlayer(
					name,
					Integer.parseInt(diffTexts.get(i).getText().toString()),
					activeCheckBoxes.get(i).isChecked()
				);
				basePlayers.add(bp);
			}
		}
		
		if (!Session.isReady()){
			Session.setIDB( new DB(this));
			Session.start( sessionName, basePlayers);
		} else {
			Session.updatePlayers(basePlayers);
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
		ps.update(allPlayerNames);
		tr.addView(ps);
		
		CheckBox cb = new CheckBox(this);
		activeCheckBoxes.add(cb);
		tr.addView(cb);
		
		EditText et = new EditText(this);
		et.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
		Object selItem = ps.getSelectedItem();
		

		et.setText("0"); 
		diffTexts.add(et);
		tr.addView(et);
		
		return tr;
	}

	class PlayerNamesSpinner extends Spinner {

		private int index;

		public PlayerNamesSpinner(Context context, int index) {
			super(context);
			this.index=index;
			setOnItemSelectedListener();
		}

		public void setOnItemSelectedListener() {
			this.setOnItemSelectedListener(new OnItemSelectedListener() {
				@Override
				public void onItemSelected(AdapterView<?> parentView,
						View selectedItemView, int position, long id) {
					if (position > 0) {
						TextView tv = (TextView) selectedItemView;
						String name =tv.getText().toString();
						Player p = null;
						if (name !="-"){
							p =  sessionPlayers.getByName(name);
						}
						diffTexts.get(index).setText(p!=null?""+p.diff:"0");
						activeCheckBoxes.get(index).setChecked(p!=null?p.isActive:true);
						//String tv.getT
						/*String name = selItem!=null?selItem.toString():null;
						Player foundInSession = name !=null?sessionPlayers.getByName(name):null;
						foundInSession !=null?""+foundInSession.diff:"0");
						*/
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
					allPlayerNames.add(name);
					updateSpinners();
				}
			});
		}
	}
}
