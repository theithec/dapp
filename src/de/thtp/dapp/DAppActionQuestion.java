package de.thtp.dapp;

//Map
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

class DAppActionQuestion {
	final DAppAction _action;
	boolean confirmed = false;

	public DAppActionQuestion(Context context, DAppAction action) {
		this._action = action;
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setMessage(_action.question)
				.setPositiveButton(R.string.ok,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int id) {
								confirmed = true;
								_action.execute();
							}
						})
				.setNegativeButton(R.string.cancel,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();

							}
						});
		AlertDialog alert = builder.create();
		alert.show();
	}
}
