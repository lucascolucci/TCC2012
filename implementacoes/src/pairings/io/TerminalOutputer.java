package pairings.io;

import pairings.Pairing;

public class TerminalOutputer extends Outputer {
	public TerminalOutputer() {
		super();
	}
		
	@Override
	public void output(Pairing pairing) {
		System.out.print(pairing.toString());
	}
}
