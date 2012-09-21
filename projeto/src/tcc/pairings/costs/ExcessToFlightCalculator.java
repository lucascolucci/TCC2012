package tcc.pairings.costs;

import tcc.pairings.Leg;
import tcc.pairings.Pairing;
import tcc.pairings.rules.Rules;

public class ExcessToFlightCalculator implements CostCalculator {
	@Override
	public void setCost(Pairing pairing) {
		double excess = (double) pairing.getExcessTime();
		double flight = (double) pairing.getFlightTime();
		pairing.setCost(excess / flight);
	}

	@Override
	public double getDeadHeadingCost(Leg leg) {
		return Rules.DH_PENALTY_FACTOR;
	}
}
