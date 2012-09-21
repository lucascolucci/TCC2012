package tcc.pairings.costs;

import tcc.pairings.Leg;
import tcc.pairings.Pairing;
import tcc.pairings.rules.Rules;

public class DutyToFlightCalculator implements CostCalculator {
	@Override
	public void setCost(Pairing pairing) {
		double duty = (double) pairing.getDutyTime();
		double flight = (double) pairing.getFlightTime();
		pairing.setCost(duty / flight);
	}

	@Override
	public double getDeadHeadingCost(Leg leg) {
		return Rules.DH_PENALTY_FACTOR;
	}
}
