package de.thtp.dapp.app;

import java.util.ArrayList;

public class PlayerList extends ArrayList<Player> {

	public PlayerList() {
		super();
	}

	// for copy
	public PlayerList(PlayerList players) {
		super(players);
	}

	public ArrayList<String> getNames() {
		ArrayList<String> names = new ArrayList<String>();
		for (Player p : this) {
			names.add(p.name);
		}
		return names;
	}

	public Player getByName(String name) {
		Player found = null;
		for (Player p : this) {
			if (name.equals(p.name)) {
				found = p;
			}
		}

		return found;
	}

	public int indexOf(Player p){
		int index = -1;
		for (int i=0;i<this.size();i++){
			Player o = this.get(i);
			if (o.equals(p)){
				index = i;
			}
		}
		return index;

	}

	public Player getById(int id) {
		for (Player p : this) {
			if (p.id == id) {
				return p;
			}
		}
		return null;
	}
	
	public String toString(){
		String str = "";
		for (Player p : this) {
			str += " " + p.name;
		}
		return str;
	}

}
