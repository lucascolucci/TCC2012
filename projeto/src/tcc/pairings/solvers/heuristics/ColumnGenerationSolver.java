package tcc.pairings.solvers.heuristics;

import java.util.List;

import tcc.pairings.Pairing;
import tcc.pairings.costs.CostCalculator;
import tcc.pairings.io.outputers.CGCplexOutputer;
import tcc.pairings.optimizers.CplexOptimizer;
import tcc.pairings.solvers.BasicSolver;
import tcc.pairings.solvers.InitialSolver;
import tcc.pairings.solvers.Solution;

public class ColumnGenerationSolver extends BasicSolver {
	private InitialSolver initialSolver;
	private Solution initialSolution;
	private CGCplexOutputer cplex;
	private List<Pairing> pairings;
	
	
	public ColumnGenerationSolver(String timeTable, CostCalculator calculator) {
		super(timeTable, calculator);
	}

	@Override
	protected Solution tryToGetSolution() {
		initialSolver = new InitialSolver(timeTable);
		initialSolution = initialSolver.getSolution(bases);
		if (initialSolution == null)
			return null;
		legs = initialSolver.getLegs();
		net = initialSolver.getFlightNetwork();
		setOutputers();
		generatePairings();
		setOptimizer();
		return getOptimalSolution();
	}
	
	@Override
	protected void setOutputers() {
		memory = null; 
		cplex = new CGCplexOutputer(legs, calculator);
		cplex.addRows();
		outputers = null; 
	}

	@Override
	protected void generatePairings() {
		pairings = initialSolution.getPairings();
		numberOfPairings = initialSolver.getNumberOfPairings();
	}
	
	@Override
	protected void setOptimizer() {
		cplex.addDHVariables();
		cplex.output(pairings);
		//cplex.getModel().setOut(null);
		optimizer = new CplexOptimizer(cplex.getModel());
	}
	
	@Override
	protected Solution getOptimalSolution() {
		if (optimizer.optimize()) {
			double[] duals = optimizer.getDuals();
			net.setDuals(duals);
		}
		return null;
	}
}
