package tcc.pairings.io;

import java.util.ArrayList;
import java.util.List;

import tcc.pairings.Pairing;

public class MemoryOutputer extends Outputer {
	private List<Pairing> pairings;
	
	public List<Pairing> getPairings() {
		return pairings;
	}
	
	public MemoryOutputer() {
		super();
		pairings = new ArrayList<Pairing>();
	}
	
	@Override
	public void output(Pairing pairing) {
		pairings.add(pairing);
	}
	
	public void clear() {
		pairings.clear();
	}
}
