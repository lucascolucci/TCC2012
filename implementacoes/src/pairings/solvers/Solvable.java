package pairings.solvers;

import java.util.List;

import pairings.Pairing;

public interface Solvable {
	boolean solve();
	List<Pairing> getSolution(String pairingsFile);
	List<Pairing> getSolution(List<Pairing> pairings);
	int getSolutionCost();
}
