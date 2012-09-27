package tcc.pairings.costs;

import tcc.pairings.Leg;
import tcc.pairings.Pairing;
import tcc.pairings.rules.Rules;

public class MeanLegsPerDutyCalculator implements CostCalculator {
	@Override
	public void setCost(Pairing pairing) {
		int legs = pairing.getNumberOfLegs();
		int duties = pairing.getNumberOfDuties();
		double cost = (double) duties / legs;
		pairing.setCost(cost);
	}

	@Override
	public double getDeadHeadingCost(Leg leg) {
		return Rules.DH_PENALTY_FACTOR;
	}
}
