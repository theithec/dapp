package de.thtp.dapp;

abstract class DAppAction {
	final String question;

	public DAppAction(String question) {
		this.question = question;
	}

	void execute() {
	}
}
