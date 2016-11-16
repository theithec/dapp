package de.thtp.dapp;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import de.thtp.dapp.app.Player;

public class PlayerCheckBox extends CheckBox {

	public Player player;

	public PlayerCheckBox(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	private void init() {
		setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				((PlayerCheckBoxCheckable) getContext()).checkChecked();
			}
		});
	}

	public PlayerCheckBox(Context context, Player player) {
		super(context);
		setPlayer(player);
		init();

	}

	private void setPlayer(Player player) {
		this.player = player;
		setText(player.name);
	}

}
