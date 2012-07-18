package pairings.generators;

import java.util.ArrayList;
import java.util.List;

import pairings.Pairing;
import pairings.graph.networks.FlightNetwork;

public class MemoryPairingsGenerator extends PairingsGenerator {
	private List<Pairing> pairings;
	
	public MemoryPairingsGenerator(FlightNetwork net) {
		super(net);
	}
	
	public List<Pairing> getPairings() {
		return pairings;
	}
	
	@Override
	public void generate(String base) {
		this.base = base;
		pairings = new ArrayList<Pairing>();
		super.generate(base);
	}
	
	@Override
	protected void generateOutput(Pairing pairing) {
		pairings.add(pairing);
	}
}
