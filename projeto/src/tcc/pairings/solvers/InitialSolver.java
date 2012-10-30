package tcc.pairings.solvers;

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
	protected Solution tryToGetSolution() {
		setLegs();
		buildFlightNetwork();
		return getInitialSolution();
	}
	
	@Override
	protected void setOutputers() {
		outputers = null;
	}

	@Override
	protected void setOptimizer() {
		optimizer = null;
	}

	private Solution getInitialSolution() {		
		InitialGenerator generator = new InitialGenerator(net, calculator);
		generator.generate(bases);
		numberOfPairings = generator.getNumberOfPairings();
		if (!generator.isAllLegsCovered())
			return null;
		Solution solution = new Solution(generator.getPairings());
		setCostsWithDeadHeads(solution.getPairings());
		setSolutionCost(solution);
		return solution;
	}
}
