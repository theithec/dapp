package de.thtp.dapp;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import de.thtp.dapp.app.Player;
import de.thtp.dapp.app.PlayerList;
import de.thtp.dapp.app.Session;

public abstract class GamePlayersActivity extends DappActivity implements
		PlayerCheckBoxCheckable {
	List<PlayerCheckBox> cbs;
	Button btn;
	PlayerList players;
	Bundle data;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		data = this.getIntent().getExtras();
		players = Session.getSessionPlayers();
		PlayerList suggPlayers = getSuggestedPlayers();
		setTitle(getString(R.string.choosePlayers));
		ScrollView sv = new ScrollView(this);
		LinearLayout ll = new LinearLayout(this);
		ll.setOrientation(LinearLayout.VERTICAL);
		sv.addView(ll);
		cbs = new ArrayList<PlayerCheckBox>();

		btn = new Button(this);
		btn.setText(getString(R.string.btnContinueInsertGame));
	
		for (Player p : players) {
			PlayerCheckBox cb = new PlayerCheckBox(this, p);
			cb.setChecked(suggPlayers.contains(p));
			cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView,
						boolean isChecked) {
					checkChecked();
				}
			});
			cbs.add(cb);
			ll.addView(cb);
		}
		
		btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Bundle data = new Bundle();
			    ArrayList<Integer> ids = new ArrayList<Integer>();
			    for (PlayerCheckBox cb: cbs){
			    	if (cb.isChecked()){
			    		ids.add(cb.player.id);
			    	}
			    }
			    data.putIntegerArrayList(Const.K_PICKED_PLAYERS, ids);
			    Intent intent = new Intent();
			    intent.putExtras(data);
			    setResult(RESULT_OK, intent);
			    finish();
			}
		});
		ll.addView(btn);
		setContentView(sv);
		checkChecked();
	}



	@Override
	public void checkChecked() {
		int cnt = 0;
		for (CheckBox cb : cbs) {
			if (cb.isChecked())
				cnt++;
		}
		btn.setEnabled(cnt == DAppPrefs.MIN_PLAYERS);
	}

	protected abstract de.thtp.dapp.app.PlayerList getSuggestedPlayers();

}
