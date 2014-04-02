package de.thtp.dapp.app;


public class Player {
	public int id;
	public String name;
	public int diff;
	public Player(int id, String name){
		int diff = 0;
		this.id = id;
		this.name = name;
	}
	
	@Override
	public boolean equals(Object o) {
		Player op = (Player) o;
		return (op.id == this.id);
	}

	@Override
	public int hashCode() {
		return id;
	}





}
