package tcc.pairings.costs;

import tcc.pairings.Leg;
import tcc.pairings.Pairing;

public interface CostCalculator {
	void setCost(Pairing pairing);
	double getDeadHeadingCost(Leg leg);
}