package pairings.io;

import pairings.Pairing;

public class CplexOutputer implements Outputable {
	private int numberOfPairings;
	
	@Override
	public int getNumberOfPairings() {
		return numberOfPairings;
	}
	
	public CplexOutputer() {
		numberOfPairings = 0;
	}
		
	@Override
	public void output(Pairing pairing) {
		++numberOfPairings;
	}
}
