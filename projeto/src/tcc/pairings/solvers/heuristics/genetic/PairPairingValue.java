package tcc.pairings.solvers.heuristics.genetic;

import tcc.pairings.Pairing;

public class PairPairingValue {
	private Pairing pairing;
	private int value;
	
	public Pairing getPairing() {
		return pairing;
	}
	
	public void setPairing(Pairing pairing) {
		this.pairing = pairing;
	}
	
	public int getValue() {
		return value;
	}
	
	public void setValue(int value) {
		this.value = value;
	}	
	
	public PairPairingValue(Pairing pairing, int value) {
		this.pairing = pairing;
		this.value = value;
	}
}
