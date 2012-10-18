package tcc.pairings.solvers.heuristics;

import java.util.List;

import tcc.pairings.Leg;

public class Subproblem {
	private List<Leg> legs;
	
	public List<Leg> getLegs() {
		return legs;
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
