package pairings.io;

import java.util.ArrayList;
import java.util.List;

import pairings.Pairing;

public class MemoryOutputer implements Outputable {
	private int numberOfPairings;
	private List<Pairing> pairings;
	
	@Override
	public int getNumberOfPairings() {
		return numberOfPairings;
	}
	
	public MemoryOutputer() {
		pairings = new ArrayList<Pairing>();
	}
	
	public List<Pairing> getPairings() {
		return pairings;
	}
	
	public void clear() {
		pairings.clear();
	}
	
	@Override
	public void output(Pairing pairing) {
		++numberOfPairings;
		pairings.add(pairing);
	}
}
