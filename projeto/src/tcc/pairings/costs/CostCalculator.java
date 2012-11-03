package tcc.pairings.costs;

import tcc.pairings.Leg;
import tcc.pairings.Pairing;
import tcc.pairings.graph.networks.FlightNetworkPath;

public interface CostCalculator {
	void setCost(Pairing pairing);
	double getDeadHeadingCost(Leg leg);
	void setReducedCost(FlightNetworkPath path);
}