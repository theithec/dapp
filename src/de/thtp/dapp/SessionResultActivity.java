package de.thtp.dapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;
import de.thtp.dapp.app.Player;
import de.thtp.dapp.app.PlayerList;
import de.thtp.dapp.app.ResultList;
import de.thtp.dapp.app.Session;

public class SessionResultActivity extends DappSessionActivity {

	private int rowCount = 0;
	private int tmp_rid = -1;
	private int[] columnWidths;
	public  static int newBoeckeFromGame = 0;
	private Button[] bockButtons;
	private static final int bockButtonsAddBtn = 0;
	private static final int bockButtonsRMBtn = 1;
	private TextView textViewBoeckeNextGame;
	private int boeckeForNextGame;
	private static final int MENU_DIA = 99;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sessionresult);

		setTitle(R.string.SessionResults);
		Button btnAddGame = (Button) findViewById(R.id.btnAddGame);
		textViewBoeckeNextGame = (TextView) findViewById(R.id.text_view_boecke_next_game);
		btnAddGame.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startAddGameActivity();
			}
		});

		btnAddGame.setEnabled(Session.isReady());
		if (DAppPrefs.MAX_BOECKE > 0) {
			int[] bockBtnIds = new int[] { R.id.btnAddBockForNextGame,
					R.id.btnRMBockForNextGame };
			final int[] bockBtnParams = new int[] { 1, -1 };
			bockButtons = new Button[2];
			for (int i = 0; i < 2; i++) {
				Button btn = (Button) findViewById(bockBtnIds[i]);
				btn.setVisibility(View.VISIBLE);
				bockButtons[i] = btn;
				btn.setOnClickListener(new BockClickListener(bockBtnParams[i]));
			}
			textViewBoeckeNextGame.setVisibility(View.VISIBLE);


		}
		PlayerList players = Session.getVisibleSessionPlayers();
		setRowSizes(players.size());
		TableRow tr = (TableRow) findViewById(R.id.full_result_header_row);

		text2row("", tr);
		for (Player p : players) {
			text2row(p.name, tr);
		}

		String[] resultFields;
		if (DAppPrefs.MAX_BOECKE > 0) {
			resultFields = new String[] { getString(R.string.points),
					getString(R.string.boecke), };
		} else {
			resultFields = new String[] { getString(R.string.points) };
		}
		for (String title : resultFields) {
			text2row(title, tr);
		}

		TableLayout tl = (TableLayout) findViewById(R.id.full_result_table_layout);
		ResultList results = Session.getResultList(); // new
														// ResultList(Session.getCurrentSession());
		int[][] values = results.valuesTable();
		int rID;
		int cnt = 0;
		for (rID = 0; rID < values.length; rID++) {
			tr = new TableRow(this);
			text2row("" + (cnt + 1), tr).setOnClickListener(
					new GameRowClickListener(rowCount++));

			int cID;
			for (cID = 0; cID < values[rID].length - 2; cID++) {
				text2row("" + values[rID][cID], tr);
			}

			String pointsWithBoecke;
			int points = values[rID][cID++];
			int boecke = values[rID][cID];
			if (boecke != 0) {
				int bockPoints = points;
				for (int i = 0; i < boecke; i++) {
					bockPoints *= 2;
				}
				pointsWithBoecke = "(" + points + ") " + bockPoints;
			} else {
				pointsWithBoecke = "" + points;
			}
			text2row("" + pointsWithBoecke, tr);
			if (DAppPrefs.MAX_BOECKE > 0) {
				text2row("" + boecke, tr);
			}
			tl.addView(tr);
			cnt++;
		}
		int[] outStandingBoecke = results.getOutstandingBoecke();
		int additionalRows = outStandingBoecke.length > 0 ? outStandingBoecke.length
				: 1;
		boeckeForNextGame = outStandingBoecke.length > 0 ? outStandingBoecke[0]
				: 0;
		for (int i = 0; i < additionalRows; i++) {
			tr = new TableRow(this);
			text2row("", tr); // +(rID+i+1), tr);
			for (int j = 0; j < players.size(); j++) {
				String s = "";
				if (i == 0
						&& (rID + i + DAppPrefs.DEALER_POS_DIFF)
								% players.size() == j) {
					s = ".";
				}
				text2row(s, tr);
			}

			text2row("", tr);
			if (DAppPrefs.MAX_BOECKE > 0) {
				String bockStr = i < outStandingBoecke.length ? ""
						+ outStandingBoecke[i] : "";
				text2row(bockStr, tr);
			}

			tl.addView(tr);
		}
		if (DAppPrefs.MAX_BOECKE > 0) {
			checkBockButtons();
		}
	}


	private void startAddGameActivity() {
		Intent intent = new Intent(this, GameResultAddGameActivity.class);
		intent.putExtra(Const.K_BOECKE_FOR_THIS_GAME, boeckeForNextGame);
		intent.putExtra(Const.K_SUGGESTED_BOECKE, newBoeckeFromGame);
		startActivity(intent);
		finish();
	}

	private void setRowSizes(int playersLength) {
		int columns = playersLength + (DAppPrefs.MAX_BOECKE > 0 ? 3 : 2);
		Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE))
				.getDefaultDisplay();
		int width = display.getWidth();
		int avW = width / columns;
		columnWidths = new int[columns];
		columnWidths[0] = avW / 2;
		for (int i = 1; i < columns; i++) {
			columnWidths[i] = avW;
		}
	}

	private TextView text2row(String txt, TableRow tr) {
		TextView tv = new TextView(this);
		LayoutParams lp = new LayoutParams(
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
				android.view.ViewGroup.LayoutParams.FILL_PARENT);
		lp.setMargins(1, 1, 1, 1);
		int cc = tr.getChildCount();
		tv.setText(txt);
		tv.setLayoutParams(lp);
		tv.setPadding(1, 1, 5, 1);
		tv.setGravity(Gravity.END);
		tv.setBackgroundColor(cc % 2 == 0 ? Color.parseColor("#343434") : Color
				.parseColor("#454545"));
		tv.setTextColor(Color.parseColor("#EFEFEF"));
		int childCount = tr.getChildCount();
		tv.setMinWidth(columnWidths[childCount]);
		tv.setMaxWidth(columnWidths[childCount]);
		tr.addView(tv);
		return tv;
	}

	private void rowClick(int rid) {
		tmp_rid = rid;
		new DAppActionQuestion(this, new DAppAction(getString(
				R.string.editGameById, tmp_rid + 1)) {
			@Override
			void execute() {
				editGame(tmp_rid);
			}
		});

	}

	private void editGame(int gamePos) {
		Intent intent = new Intent(this, GameResultEditGameActivity.class);
		intent.putExtra(Const.K_GAME_POS, gamePos);
		startActivity(intent);
		finish();

	}

	class GameRowClickListener implements View.OnClickListener {
		final int rid;

		public GameRowClickListener(int rid) {
			this.rid = rid;
		}

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			rowClick(rid);

		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		boolean result = super.onCreateOptionsMenu(menu);
		if (Session.hasGames()) {
			menu.add(0, MENU_DIA, Menu.NONE, R.string.titleDiagram);
		}
		return result;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MENU_DIA:
			startActivity(new Intent(this, DiagramActivity.class));
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	class BockClickListener implements OnClickListener {
		final int val;

		public BockClickListener(int val) {
			this.val = val;
		}

		@Override
		public void onClick(View v) {
			newBoeckeFromGame += val;
			checkBockButtons();

		}
	}

	private void checkBockButtons() {
		boolean hasBoecke = newBoeckeFromGame > 0;
		textViewBoeckeNextGame.setText(getString(
				R.string.newBoeckeToAdd, newBoeckeFromGame) );
		bockButtons[bockButtonsAddBtn]
				.setEnabled(newBoeckeFromGame < DAppPrefs.MAX_BOECKE);
		bockButtons[bockButtonsRMBtn].setEnabled(hasBoecke);
	}
}
