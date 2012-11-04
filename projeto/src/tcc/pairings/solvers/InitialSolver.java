package tcc.pairings.solvers;

import tcc.pairings.costs.CostCalculator;
import tcc.pairings.generators.InitialGenerator;
import tcc.pairings.graph.networks.FlightNetwork;
import tcc.pairings.io.outputers.MemoryOutputer;
import tcc.pairings.io.outputers.Outputer;
import tcc.pairings.rules.Rules;

public class InitialSolver extends BasicSolver {
	private int maxDuties = Rules.MAX_DUTIES;
	private int tempMaxDuties = Rules.MAX_DUTIES;
	private InitialGenerator generator;
	
	public int getMaxDuties() {
		return maxDuties;
	}

	public void setMaxDuties(int maxDuties) {
		this.maxDuties = maxDuties;
	}

	public InitialSolver(String timeTable) {
		this(timeTable, null);
	}
	
	public InitialSolver(String timeTable, CostCalculator calculator) {
		super(timeTable, calculator);
	}
	
	protected void buildFlightNetwork() {
		tempMaxDuties = Rules.MAX_DUTIES;
		Rules.MAX_DUTIES = maxDuties;
		net = new FlightNetwork(legs);
		net.build();
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
		Rules.MAX_DUTIES = tempMaxDuties;
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
