package pairings.io;

import java.util.ArrayList;
import java.util.List;

import pairings.Pairing;

public class MemoryOutputer implements Outputable {
	private List<Pairing> pairings;
	
	public MemoryOutputer() {
		pairings = new ArrayList<Pairing>();
	}
	
	public List<Pairing> getPairings() {
		return pairings;
	}
	
	public int getNumberOfPairings() {
		return pairings.size();
	}
	
	public void clear() {
		pairings.clear();
	}
	
	@Override
	public void output(Pairing pairing) {
		pairings.add(pairing);
	}
}
