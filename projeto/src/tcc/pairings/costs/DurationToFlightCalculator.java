package tcc.pairings.costs;

import tcc.pairings.Leg;
import tcc.pairings.Pairing;
import tcc.pairings.graph.networks.FlightNetworkPath;
import tcc.pairings.rules.Rules;

public class DurationToFlightCalculator implements CostCalculator {
	@Override
	public void setCost(Pairing pairing) {
		int duration = pairing.getDuration();
		int flight = pairing.getFlightTime();
		double cost = (double) duration / flight;
		pairing.setCost(cost);
	}

	@Override
	public double getDeadHeadingCost(Leg leg) {
		return Rules.DH_PENALTY_FACTOR;
	}
	
	@Override
	public void setReducedCost(FlightNetworkPath path) {
		// TODO Auto-generated method stub
	}
}
