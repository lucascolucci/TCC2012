package tcc.pairings.costs;

import tcc.pairings.Pairing;

public class DutyToFlightCalculator implements CostCalculator {
	@Override
	public void setCost(Pairing pairing) {
		double duty = (double) pairing.getDutyTime();
		double flight = (double) pairing.getFlightTime();
		pairing.setCost(duty / flight);
	}
}
