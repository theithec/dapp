package de.thtp.dapp;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import de.thtp.dapp.app.Player;
import de.thtp.dapp.app.PlayerList;
import de.thtp.dapp.app.Session;

public abstract class GameResultActivity extends DappActivity implements
		PlayerCheckBoxCheckable {

	PlayerCheckBox[] playerCheckBoxes;
	private Button addGameBtn;// , addGameAsBtn;
	Button PointPlusBtn, PointMinusBtn;
	Button bockPlusBtn, bockMinusBtn;
	EditText[] editTexts;
	final int editTextsPoints = 0;
	final int editTextsBoecke = 1;
	PlayerList players;

	int initialPoints;
	int suggestedBoecke;

	int boeckeForThisGame;

	Bundle data;

	static final int PICK_PLAYER_REQUEST = 1;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gameresult);
		setTitle(getString(R.string.addResult));
		initialPoints = 0;
		data = this.getIntent().getExtras();

		PlayerList sessionPlayers = Session.getVisibleSessionPlayers();
		if (sessionPlayers.size() > DAppPrefs.MIN_PLAYERS) {
			pickPlayers();

		} else {
			players = new PlayerList(sessionPlayers);
			initWithPlayers();
		}

	}

	void initWithPlayers() {
		int ps = players.size();
		playerCheckBoxes = new PlayerCheckBox[ps];
		LinearLayout ll = (LinearLayout) findViewById(R.id.verLayout1);

		int[] editIds = new int[] { R.id.editPoints, R.id.editBoecke };
		editTexts = new EditText[editIds.length];
		int[][] PlusMinusButtonIds = new int[][] {
				{ R.id.pointPlusBtn, R.id.pointMinusBtn },
				{ R.id.bockPlusBtn, R.id.bockMinusBtn } };
		EditText et;
		for (int i = 0; i < 2; i++) {
			et = (EditText) findViewById(editIds[i]);
			et.addTextChangedListener(new TextWatcher() {

				@Override
				public void onTextChanged(CharSequence s, int start,
						int before, int count) {
					checkChecked();

				}

				@Override
				public void beforeTextChanged(CharSequence s, int start,
						int count, int after) {
					// TODO Auto-generated method stub

				}

				@Override
				public void afterTextChanged(Editable s) {
					// TODO Auto-generated method stub

				}
			});
			et.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					((EditText) v).selectAll();
				}
			});
			editTexts[i] = et;
			int[] pmBtnIds = PlusMinusButtonIds[i];
			Button pBtn = (Button) findViewById(pmBtnIds[0]);
			pBtn.setOnClickListener(new PMButtonClickListener(et, 1));
			Button mBtn = (Button) findViewById(pmBtnIds[1]);
			mBtn.setOnClickListener(new PMButtonClickListener(et, -1));
		}
		addGameBtn = (Button) findViewById(R.id.addGameBtn);
		addGameBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				putGame();
				// finish();

			}
		});

		for (int i = 0; i < ps; i++) {
			Player p = players.get(i);
			PlayerCheckBox pcb = new PlayerCheckBox(this, p);
			ll.addView(playerCheckBoxes[i] = pcb);
		}

		editTexts[0].setText("" + initialPoints);
		editTexts[1].setText("" + suggestedBoecke);

		if (DAppPrefs.MAX_BOECKE > 0) {
			int[] ids = new int[] { R.id.editBoecke, R.id.bockPlusBtn,
					R.id.bockMinusBtn, R.id.lblBoecke };
			for (int i : ids) {
				findViewById(i).setVisibility(View.VISIBLE);
			}
		}
	}

	@Override
	public void checkChecked() {
		int cnt = 0;
		for (CheckBox cb : playerCheckBoxes) {
			if (cb != null && cb.isChecked()) {
				cnt++;
			}
		}
		EditText pointsEdit = editTexts[editTextsPoints];
		String strPoints = pointsEdit.getText().toString();
		int pint = -1;
		if (!strPoints.equals("")) {
			pint = Integer.parseInt(strPoints);
		}
		boolean isEnabled = cnt > 0 && cnt < DAppPrefs.MIN_PLAYERS && pint > 0
				|| cnt == DAppPrefs.MIN_PLAYERS && pint == 0;
		addGameBtn.setEnabled(isEnabled);

		TextView textViewPoints = (TextView) findViewById(R.id.text_viewPoints);
		String textViewPointsText = getString(R.string.points) + "";

		if (boeckeForThisGame > 0) {
			int bp = pint;
			for (int i = 0; i < boeckeForThisGame; i++) {
				bp *= 2;
			}
			textViewPointsText += " (" + getString(R.string.bock) + ": " + bp
					+ ")";
		}
		textViewPoints.setText(textViewPointsText);
	}

	private void putGame() {
		final PlayerList winners = new PlayerList();
		for (PlayerCheckBox pcb : playerCheckBoxes) {
			if (pcb.isChecked()) {
				winners.add(pcb.player);
			}
		}
		final int points = Integer.parseInt(editTexts[editTextsPoints]
				.getText().toString());
		String strBoecke = editTexts[editTextsBoecke].getText().toString();
		final int boecke = strBoecke.equals("") ? 0 : Integer.parseInt(strBoecke);
		if (winners.size() == 1) {
			final GameResultActivity copyThis = this;
			new DAppActionQuestion(this, new DAppAction(
					getString(R.string.oneWinnerOnlyQ)) {
				@Override
				void execute() {
					putGameToData(winners, points, boecke);
					startActivity(new Intent(copyThis,
							SessionResultActivity.class));
					finish();
				}
			});
		} else {
			SessionResultActivity.newBoeckeFromGame = 0;
			putGameToData(winners, points, boecke);
			startActivity(new Intent(this, SessionResultActivity.class));
			finish();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		// data= intent.getExtras();
		Bundle data = null;
		boolean err = intent == null;
		List<Integer> playerIDs = null;
		if (!err) {
			data = intent.getExtras();
			err = data == null;
		}
		if (!err) {
			playerIDs = data.getIntegerArrayList(Const.K_PICKED_PLAYERS);
			err = playerIDs == null;
		}

		if (err) {
			finish();
		} else {
			this.players = Session.getPlayersbyIDs(playerIDs);
			initWithPlayers();
		}
	}

	class PMButtonClickListener implements OnClickListener {
		final EditText et;
		final int diff;

		public PMButtonClickListener(EditText et, int incr) {
			this.et = et;
			this.diff = incr;
		}

		@Override
		public void onClick(View v) {
			int val = 0;
			try {
				val = Integer.parseInt(et.getText().toString());
			} catch (Exception e) {
				//
			}
			et.setText("" + (val + diff));
			checkChecked();
		}
	}

	abstract void pickPlayers();

	abstract void putGameToData(PlayerList winners, int points, int boecke);

}
