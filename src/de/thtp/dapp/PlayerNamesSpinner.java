package de.thtp.dapp;

import java.util.List;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;



public class PlayerNamesSpinner extends Spinner {
	public static List<String> allPlayerNames;
	public static List<String> selectedPlayerNames;
	int index;
	SessionPlayersActivity context;
	public PlayerNamesSpinner(final SessionPlayersActivity context, int index) {
		super(context);
		this.index = index;

		this.context = context;
		
		//update();
		
		
		this.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parentView,
					View selectedItemView, int position, long id) {
				if (position > 0) {
					//context.updateSpinners();
				}
				//context.check();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parentView) {
			}
		});
		
		
	}
	


	public void update(List<String> playerNames) {
		long id = this.getSelectedItemId();
		int position = this.getSelectedItemPosition();
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,
				android.R.layout.simple_spinner_item, playerNames);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		this.setAdapter(adapter);
		this.setSelection(position);
		
	}

}
