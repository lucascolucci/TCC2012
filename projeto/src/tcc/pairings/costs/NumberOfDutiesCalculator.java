package tcc.pairings.costs;

import tcc.pairings.Leg;
import tcc.pairings.Pairing;
import tcc.pairings.graph.networks.FlightNetworkPath;
import tcc.pairings.rules.Rules;

public class NumberOfDutiesCalculator implements CostCalculator {
	@Override
	public void setCost(Pairing pairing) {
		double number = (double) pairing.getNumberOfDuties();
		pairing.setCost(number * number);
	}

	@Override
	public double getDeadHeadingCost(Leg leg) {
		return Rules.DH_PENALTY_FACTOR;
	}

	@Override
	public void setReducedCost(FlightNetworkPath path) {
		Pairing dummy = new Pairing(0, path);
		double number = (double) dummy.getNumberOfDuties(); 
		path.setReducedCost(number * number - path.sumOfDuals());
	}
}
