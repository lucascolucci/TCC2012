package tcc.pairings.costs;

import tcc.pairings.Leg;
import tcc.pairings.Pairing;
import tcc.pairings.graph.networks.FlightNetworkPath;
import tcc.pairings.rules.Rules;

public class ExcessCalculator implements CostCalculator {
	@Override
	public void setCost(Pairing pairing) {
		double excess = (double) pairing.getExcessTime();
		pairing.setCost(excess);
	}

	@Override
	public double getDeadHeadingCost(Leg leg) {
		return Rules.DH_PENALTY_FACTOR * leg.getFlightTime();
	}

	@Override
	public void setReducedCost(FlightNetworkPath path) {
		// TODO Auto-generated method stub
	}
}
