package tcc.pairings.costs;

import tcc.pairings.Pairing;

public class ExcessCalculator implements CostCalculator {
	@Override
	public void setCost(Pairing pairing) {
		double flight = (double) pairing.getFlightTime();
		pairing.setCost(flight);
	}
}
