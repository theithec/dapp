package de.thtp.dapp;

import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.widget.EditText;
import de.thtp.dapp.app.Session;

class PlayerNameDialog extends Builder {
	final EditText input;

	public PlayerNameDialog(DappActivity dappActivity, String title, String name) {
		super(dappActivity);
		DappActivity dappActivity1 = dappActivity;
		setTitle(title);
		setMessage("Name");
		// final String val;
		input = new EditText(dappActivity);
		input.setText(name);
		setView(input);

		setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int whichButton) {
			}
		});
	}
}

class PlayerNameEditDialog extends PlayerNameDialog {

	public PlayerNameEditDialog(final DappActivity dappActivity,
			final int playerId, String name) {
		super(dappActivity, "Edit Player", name);

		setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int whichButton) {

				String name = input.getText().toString();
				if (!Session.getKnownPlayers().getNames().contains(name)) {
					Session.renamePlayer(playerId, name);
					dappActivity.startActivity(dappActivity.getIntent());
					dappActivity.finish();
				}
			}
		});
	}
}
