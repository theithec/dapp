package de.thtp.dapp.app;


public class Player extends BasePlayer implements Comparable<Player> {
	public int id;
	public int pos;
	
	public Player(int id, int pos, BasePlayer bp){
		this.id = id;
		this.pos = pos;
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
	
	@Override
    public int compareTo(Player other) {
		int r = 0;
		if (this.pos < other.pos){
			r =  -1;
		} else {
			if (this.pos > other.pos){
				r = 1;
			}
		}
		return r;
    }
}
