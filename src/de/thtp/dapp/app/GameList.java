package de.thtp.dapp.app;

import java.util.ArrayList;

public class GameList extends ArrayList<Game> {

	private static GameList instance;
	public final String name;

	public GameList(String name) {
		GameList.instance = this;
		this.name = name;
	}

	@Override
	public boolean add(Game g) {
		return super.add(g);
	}

}
