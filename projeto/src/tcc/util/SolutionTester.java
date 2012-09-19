package tcc.util;

import java.util.ArrayList;
import java.util.List;

import tcc.pairings.DutyLeg;
import tcc.pairings.Leg;
import tcc.pairings.Pairing;
import tcc.pairings.solvers.Solution;

public class SolutionTester {
	public static boolean isAllLegsCovered(List<Leg> legs, Solution solution) {
		List<DutyLeg> nonDHLegs = getNonDHLegs(solution);
		if (legs.size() != nonDHLegs.size())
			return false;
		for (Leg leg: legs) {
			boolean legFound = false;
			for (DutyLeg dutyLeg: nonDHLegs) 
				if (dutyLeg.isDuplicate(leg)) {
					legFound = true;
					break;
				}
			if (!legFound)
				return false;
		}
		return true;
	}
		
	private static List<DutyLeg> getNonDHLegs(Solution solution) {
		List<DutyLeg> nonDHLegs = new ArrayList<DutyLeg>();
		for (Pairing pairing: solution.getPairings())
			for (DutyLeg leg: pairing.getLegs()) 
				if (!leg.isDeadHead())
					nonDHLegs.add(leg);
		return nonDHLegs;
	}
	
	public static boolean isCostRight(Solution solution) {
		double plainCost = 0.0;
		double costWithDeadHeads = 0.0;
		for (Pairing pairing: solution.getPairings()) {
			plainCost += pairing.getCost();
			costWithDeadHeads += pairing.getCostWithDeadHeads();
		}
		return (Math.abs(plainCost -  solution.getPairingsCost()) <=  0.001) 
				&& (Math.abs(costWithDeadHeads - solution.getCost()) <= 0.001);
	}
}
