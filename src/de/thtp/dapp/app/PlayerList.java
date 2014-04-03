package de.thtp.dapp.app;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;

public class PlayerList extends ArrayList<Player> {

	
	public PlayerList() {
		super();
	}
	//for copy
	public PlayerList(PlayerList players) {
		super(players);
	}

	public List<String> getNames() {
		List<String> names = new ArrayList<String>();
		for (Player p: this) {
			names.add(p.name);
		}
		return names;
	}

	public Player getByName(String name) {
		Log.d("DAPP name", name);
		Log.d("DAPP nameTS", name.toString());
		for (Player p: this) {
			Log.d("DAPP playername", p.name);
			Log.d("DAPP playernameTS", p.name.toString());
			if (name == p.name){
				Log.d("DAPP found1", p.name);
				return p;
			}
			if (name.toString() == p.name.toString()){
				Log.d("DAPP found2", p.name);
				return p;
			}
			if (name.toString().equals(p.name.toString())){
				Log.d("DAPP found3", p.name);
				return p;
			}
			if (name.equals(p.name)){
				Log.d("DAPP found4", p.name);
				return p;
			}
		}
		
		return null;
	}

	public Player getById(int id) {
		for (Player p: this) {
			if (p.id==id){
				return p;
			}
		}
		return null;
	}

}
