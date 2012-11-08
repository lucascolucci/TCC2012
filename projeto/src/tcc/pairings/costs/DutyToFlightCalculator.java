package tcc.pairings.costs;

import tcc.pairings.Leg;
import tcc.pairings.Pairing;
import tcc.pairings.graph.networks.FlightNetworkPath;
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

	@Override
	public void setReducedCost(FlightNetworkPath path) {
		Pairing dummy = new Pairing(0, path);
		int duty = dummy.getDutyTime();
		int flight = dummy.getFlightTime();
		double cost = (double) duty / flight;
		path.setReducedCost(cost - path.sumOfDuals());
	}
}
