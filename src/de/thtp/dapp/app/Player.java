package de.thtp.dapp.app;


public class Player extends BasePlayer {
	public int id;
	
	public Player(int id, BasePlayer bp){
		this.id = id;
		this.diff = bp.diff;
		this.name = bp.name;
		this.isActive =bp.isActive;
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
