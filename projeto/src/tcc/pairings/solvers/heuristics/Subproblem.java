package tcc.pairings.solvers.heuristics;

import java.util.List;

import tcc.pairings.Leg;
import tcc.pairings.solvers.Solution;

public class Subproblem {
	private List<Leg> legs;
	private Solution solution;
	
	public List<Leg> getLegs() {
		return legs;
	}
	
	public Solution getSolution() {
		return solution;
	}
	
	public void setSolution(Solution solution) {
		this.solution = solution;
	}
	
	public Subproblem(List<Leg> legs) {
		this.legs = legs;
	}
	
	@Override
	public boolean equals(Object obj) {
		Subproblem other = (Subproblem) obj;
		if (legs.size() != other.getLegs().size())
			return false;
		for (Leg thisLeg: legs)
			if (!other.getLegs().contains(thisLeg))
				return false;
		return true;
	}
}
