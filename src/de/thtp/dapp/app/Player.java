package de.thtp.dapp.app;


public class Player  implements Comparable<Player> {
	public String name;
	public int id;
	public int pos;
	public int diff;
	public boolean isActive;
	

	public Player(String name, int pos, int diff, boolean isActive) {
		this.pos = pos;
		this.diff = diff;
		this.name = name;
		this.isActive = isActive;
	}

	public Player(String name) {
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

	@Override
	public int compareTo(Player other) {
		int r = 0;
		if (this.pos < other.pos) {
			r = -1;
		} else {
			if (this.pos > other.pos) {
				r = 1;
			}
		}
		return r;
	}
}
