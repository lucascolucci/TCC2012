package tcc.pairings.costs;

import tcc.pairings.Pairing;

public class ExcessToFlightCalculator implements CostCalculator {
	@Override
	public void setCost(Pairing pairing) {
		double excess = (double) pairing.getExcessTime();
		double flight = (double) pairing.getFlightTime();
		pairing.setCost(excess / flight);
	}
}
