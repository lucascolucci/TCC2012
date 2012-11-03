package tcc.pairings.solvers;

import tcc.pairings.costs.CostCalculator;
import tcc.pairings.generators.InitialGenerator;
import tcc.pairings.io.outputers.MemoryOutputer;
import tcc.pairings.io.outputers.Outputer;

public class InitialSolver extends BasicSolver {
	private InitialGenerator generator;
	
	public InitialSolver(String timeTable) {
		this(timeTable, null);
	}
	
	public InitialSolver(String timeTable, CostCalculator calculator) {
		super(timeTable, calculator);
	}
	
	@Override
	protected void setOutputers() {
		memory = new MemoryOutputer();
		outputers = new Outputer[] { memory };
	}
	
	@Override
	protected void generatePairings() {
		generator = new InitialGenerator(net, outputers, calculator);
		generator.generate(bases);
		numberOfPairings = generator.getNumberOfPairings();
	}

	@Override
	protected void setOptimizer() {
		optimizer = null;
	}

	@Override
	protected Solution getSolution() {		
		if (!generator.isAllLegsCovered())
			return null;
		Solution solution = new Solution(memory.getPairings());
		setCostsWithDeadHeads(solution.getPairings());
		setSolutionCost(solution);
		return solution;
	}
}
