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
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;
import de.thtp.dapp.app.Player;
import de.thtp.dapp.app.PlayerList;
import de.thtp.dapp.app.ResultList;
import de.thtp.dapp.app.Session;

public class SessionResultActivity extends DappSessionActivity {

	private PlayerList players;
	private int rowcnt = 0;
	private int tmp_rid = -1;
	private int[] columWidths;
	public  static int newBoeckeFromGame = 0;
	private Button[] bockBtns;
	private static final int bockBtnsAddBtn = 0;
	private static final int bockBtnsRMBtn = 1;
	private TextView textViewBoeckeNextGame;
	private int boeckeForNextGame;
	private static final int MENU_DIA = 99;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sessionresult);

		setTitle(R.string.SessionResults);
		Button btnAddGame = (Button) findViewById(R.id.btnAddGame);
		textViewBoeckeNextGame = (TextView) findViewById(R.id.textviewBoeckeNextGame);
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
			bockBtns = new Button[2];
			for (int i = 0; i < 2; i++) {
				Button btn = (Button) findViewById(bockBtnIds[i]);
				btn.setVisibility(View.VISIBLE);
				bockBtns[i] = btn;
				btn.setOnClickListener(new BockClickListener(bockBtnParams[i]));
			}
			textViewBoeckeNextGame.setVisibility(View.VISIBLE);


		}
		players = Session.getVisibleSessionPlayers();
		setRowSizes(players.size(), DAppPrefs.MAX_BOECKE);
		TableRow tr = (TableRow) findViewById(R.id.fullresultheaderrow);

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

		TableLayout tl = (TableLayout) findViewById(R.id.fullresulttablelayoutcontent);
		ResultList results = Session.getResultList(); // new
														// ResultList(Session.getCurrentSession());
		int[][] rsts = results.valuesTable();
		int rID;
		int cnt = 0;
		for (rID = 0; rID < rsts.length; rID++) {
			tr = new TableRow(this);
			text2row("" + (cnt + 1), tr).setOnClickListener(
					new GameRowClickListener(rowcnt++));

			int cID;
			for (cID = 0; cID < rsts[rID].length - 2; cID++) {
				text2row("" + rsts[rID][cID], tr);
			}

			String pointsWithBoecke;
			int points = rsts[rID][cID++];
			int boecke = rsts[rID][cID];
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
			checkBockBtns();
		}
	}


	private void startAddGameActivity() {
		Intent intent = new Intent(this, GameResultAddGameActivity.class);
		intent.putExtra(Const.K_BOECKE_FOR_THIS_GAME, boeckeForNextGame);
		intent.putExtra(Const.K_SUGG_BOECKE, newBoeckeFromGame);
		startActivity(intent);
		finish();
	}

	private void setRowSizes(int playersLength, int maxBoecke) {
		int columns = playersLength + (DAppPrefs.MAX_BOECKE > 0 ? 3 : 2);
		Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE))
				.getDefaultDisplay();
		int width = display.getWidth();
		int avW = width / columns;
		columWidths = new int[columns];
		columWidths[0] = avW / 2;
		for (int i = 1; i < columns; i++) {
			columWidths[i] = avW;
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
		int trcc = tr.getChildCount();
		tv.setMinWidth(columWidths[trcc]);
		tv.setMaxWidth(columWidths[trcc]);
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
			checkBockBtns();

		}
	}

	private void checkBockBtns() {
		boolean hasBoecke = newBoeckeFromGame > 0;
		textViewBoeckeNextGame.setText(getString(
				R.string.newBoeckeToAdd, newBoeckeFromGame) );
		bockBtns[bockBtnsAddBtn]
				.setEnabled(newBoeckeFromGame < DAppPrefs.MAX_BOECKE);
		bockBtns[bockBtnsRMBtn].setEnabled(hasBoecke);
	}
}
