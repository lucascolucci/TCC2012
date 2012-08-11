package tcc.pairings.optimizers;

import java.util.List;

import tcc.pairings.Pairing;

public interface Optimizer {
	double getOptimizationTime();
	boolean optimize();
	List<Pairing> getOptimalPairings(List<Pairing> pairings);
	double getOptimalCost();
	int getOptimalSize();
}
