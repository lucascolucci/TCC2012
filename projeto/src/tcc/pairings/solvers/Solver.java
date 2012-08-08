package tcc.pairings.solvers;

import java.util.List;

import tcc.pairings.Pairing;

public interface Solver {
	double getSolutionTime();
	boolean solve();
	List<Pairing> getSolution(String pairingsFile);
	List<Pairing> getSolution(List<Pairing> pairings);
	double getSolutionCost();
	int getSolutionSize();
}
