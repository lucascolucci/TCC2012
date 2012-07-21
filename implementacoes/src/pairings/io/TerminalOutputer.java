package pairings.io;

import pairings.Pairing;

public class TerminalOutputer implements Outputable {
	private int numberOfPairings;
	private boolean showEnabled;
	
	@Override
	public int getNumberOfPairings() {
		return numberOfPairings;
	}
	
	public boolean showEnabled() {
		return showEnabled;
	}

	public void setShowEnabled(boolean showEnabled) {
		this.showEnabled = showEnabled;
	}

	public TerminalOutputer() {
		numberOfPairings = 0;
		showEnabled = true;
	}
	
	@Override
	public void output(Pairing pairing) {
		++numberOfPairings;
		if (showEnabled())
			show(pairing);
	}
	
	private void show(Pairing pairing) {
		System.out.println("Pairing " + numberOfPairings);
		System.out.print(pairing.toString());
	}
}
