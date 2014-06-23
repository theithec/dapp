package de.thtp.dapp;

import java.util.ArrayList;
import java.util.Map;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

abstract public class DappListViewActivity extends DappActivity {
	Map<String, Integer> map;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_objectlist);
		setListAdapter();
	}

	protected void setListAdapter() {

		ArrayList<String> names = new ArrayList<String>();
		for (String name : map.keySet()) {
			names.add(name);
		}
		ListView lv = (ListView) this.findViewById(R.id.objectlist);
		ArrayAdapter<String> aad = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, names);
		lv.setAdapter(aad);
		registerForContextMenu(lv);
		
		
		
	}

	protected void endclick() {
		startActivity(getIntent());
		finish();
	}

	//abstract protected boolean onListItemLongClick(AdapterView l, View v,
	//		int position, long id);
}
