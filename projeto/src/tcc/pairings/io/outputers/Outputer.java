package tcc.pairings.io.outputers;

import java.util.List;

import tcc.pairings.Pairing;

public abstract class Outputer {
	public void output(List<Pairing> pairings) {
		for (Pairing pairing: pairings)
			output(pairing);
	}
	
	public abstract void output(Pairing pairing);
}
