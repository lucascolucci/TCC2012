package pairings.io;

import pairings.Pairing;

public abstract class BasicOutputer {
	protected int numberOfPairings;
	
	public int getNumberOfPairings() {
		return numberOfPairings;
	}

	public void setNumberOfPairings(int numberOfPairings) {
		this.numberOfPairings = numberOfPairings;
	}
	
	public BasicOutputer() {
		numberOfPairings = 0;
	}
	
	public void incrementNumberOfPairings() {
		numberOfPairings++;
	}
	
	public abstract void output(Pairing pairing);
}
