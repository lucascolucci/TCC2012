package tcc.pairings.solvers;

import java.util.List;

import tcc.pairings.Base;
import tcc.pairings.Pairing;
import tcc.pairings.costs.CostCalculator;
import tcc.pairings.generators.InitialGenerator;

public class InitialSolver extends BasicSolver {
	public InitialSolver(String timeTable) {
		this(timeTable, null);
	}
	
	public InitialSolver(String timeTable, CostCalculator calculator) {
		super(timeTable, calculator);
	}
	
	@Override
	protected Solution tryToGetSolution(Base... bases) {
		setLegs();
		buildFlightNetwork();
		return getInitialSolution(bases);
	}

	private Solution getInitialSolution(Base... bases) {		
		InitialGenerator generator = new InitialGenerator(net, calculator);
		generator.generate(bases);
		if (!generator.isAllLegsCovered())
			return null;
		numberOfPairings = generator.getNumberOfPairings();
		Solution solution = new Solution(generator.getPairings());
		setCostsWithDeadHeads(solution.getPairings());
		setSolutionCost(solution);
		return solution;
	}
	
	private void setSolutionCost(Solution solution) {
		List<Pairing> pairings = solution.getPairings();
		double cost = 0.0;
		for (Pairing pairing: pairings)
			cost += pairing.getCostWithDeadHeads();
		solution.setCost(cost);
	}

	@Override
	protected void setOutputers() {
		outputers = null;
	}

	@Override
	protected void setOptimizer() {
		optimizer = null;
	}
}
