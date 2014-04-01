package de.thtp.dapp;

public abstract class DAppAction {
	String question;

	public DAppAction(String question) {
		this.question = question;
	}

	
	void execute() {
	}
}
