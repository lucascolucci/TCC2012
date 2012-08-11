package tcc.pairings.solvers;

import java.util.ArrayList;
import java.util.List;

import tcc.pairings.Pairing;

public class Solution {
	private List<Pairing> pairings;

	public List<Pairing> getPairings() {
		return pairings;
	}

	public Solution() {
		pairings = new ArrayList<Pairing>();
	}
	
	public Solution(List<Pairing> pairings) {
		this.pairings = pairings;
	}
	
	public void addPairing(Pairing pairing) {
		pairings.add(pairing);
	}
	
	public void removePairing(Pairing pairing) {
		pairings.remove(pairing);
	}
	
	public void clear() {
		pairings.clear();
	}
	
	public double getCost() {
		double total = 0.0;
		for (Pairing pairing: pairings)
			total += pairing.getCost();
		return total;
	}
	
	public int getSize() {
		return pairings.size();
	}
}
