package tcc.pairings.costs;

import tcc.pairings.Leg;
import tcc.pairings.Pairing;
import tcc.pairings.graph.networks.FlightNetworkPath;
import tcc.pairings.rules.Rules;

public class MeanFlightPerDutyCalculator implements CostCalculator {
	@Override
	public void setCost(Pairing pairing) {
		int flight = pairing.getFlightTime();
		int duties = pairing.getNumberOfDuties();
		double cost = (double) duties / flight;
		pairing.setCost(cost);
	}

	@Override
	public double getDeadHeadingCost(Leg leg) {
		return Rules.DH_PENALTY_FACTOR / leg.getFlightTime();
	}

	@Override
	public void setReducedCost(FlightNetworkPath path) {
		// TODO Auto-generated method stub
	}
}
