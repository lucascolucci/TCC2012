package tcc.pairings.optimizers;

import java.util.List;

public interface Optimizer {
	double getOptimizationTime();
	boolean optimize();
	List<Integer> getOptimalVariables();
	List<Integer> getArtificialValues();
	double getObjectiveValue();
}
