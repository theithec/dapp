package de.thtp.dapp.app;

public class BasePlayer {
	public String name;
	public int diff;
	public boolean isActive;

	public BasePlayer() {
	};

	public BasePlayer(String name, int diff, boolean isActive) {
		this.diff = diff;
		this.isActive = isActive;
		this.name = name;
	}

	public BasePlayer(String name) {
		this(name, 0, true);
	}
}
