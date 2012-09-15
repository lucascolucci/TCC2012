package tcc.pairings.solvers;

import java.util.List;

import tcc.pairings.Base;
import tcc.pairings.Leg;

public interface Solver {
	public List<Leg> getLegs();
	public int getNumberOfPairings();
	public Solution getSolution(Base... bases);
}
