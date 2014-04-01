package de.thtp.dapp.app;

import java.util.ArrayList;
import java.util.List;

public class PlayerList extends ArrayList<Player> {

	public List<String> getNames() {
		List<String> names = new ArrayList<String>();
		for (Player p: this) {
			names.add(p.name);
		}
		return names;
	}

	public Player getByName(String name) {
		for (Player p: this) {
			if (p.name==name){
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
