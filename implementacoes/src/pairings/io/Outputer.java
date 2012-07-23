package pairings.io;

import java.util.List;

import pairings.Pairing;

public abstract class Outputer {
	protected int numberOfPairings;
	
	public int getNumberOfPairings() {
		return numberOfPairings;
	}

	public void setNumberOfPairings(int numberOfPairings) {
		this.numberOfPairings = numberOfPairings;
	}
	
	public Outputer() {
		numberOfPairings = 0;
	}
	
	public void incrementNumberOfPairings() {
		numberOfPairings++;
	}
	
	public void output(List<Pairing> pairings) {
		for (Pairing pairing: pairings) {
			incrementNumberOfPairings();
			output(pairing);
		}
	}
	
	public abstract void output(Pairing pairing);
}
