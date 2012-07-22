package pairings.io;

import pairings.Pairing;

public class TerminalOutputer extends BasicOutputer {
	private boolean printEnabled;
	
	public boolean getPrintEnabled() {
		return printEnabled;
	}

	public void setPrintEnabled(boolean printEnabled) {
		this.printEnabled = printEnabled;
	}

	public TerminalOutputer() {
		super();
		printEnabled = true;
	}
	
	@Override
	public void output(Pairing pairing) {
		if (getPrintEnabled())
			print(pairing);
	}
	
	private void print(Pairing pairing) {
		System.out.println("Pairing " + numberOfPairings);
		System.out.print(pairing.toString());
	}
}
