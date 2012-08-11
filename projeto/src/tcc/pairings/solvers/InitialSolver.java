package tcc.pairings.solvers;

import java.util.List;

import tcc.pairings.Base;
import tcc.pairings.Leg;

public class InitialSolver implements Solver {
	private List<Leg> legs;

	public List<Leg> getLegs() {
		return legs;
	}

	public InitialSolver(List<Leg> legs) {
		this.legs = legs;
	}
	
	@Override
	public Solution getSolution(Base... bases) {
		return null;
	}
}
