package tcc.pairings.costs;

import tcc.pairings.Pairing;

public class ExcessCalculator implements CostCalculator {
	@Override
	public void setCost(Pairing pairing) {
		double excess = (double) pairing.getExcessTime();
		pairing.setCost(excess);
	}
}
