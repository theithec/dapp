package de.thtp.dapp;

import java.util.ArrayList;
import java.util.Map;



import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
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
		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				onListItemClick(parent, view, position, id);
			}
		});
	}

	protected void endclick() {
		startActivity(getIntent());
		finish();
	}

	abstract protected void onListItemClick(AdapterView l, View v,
			int position, long id);
}
