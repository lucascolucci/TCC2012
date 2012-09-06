package tcc.pairings.solvers.exacts;

import tcc.pairings.costs.CostCalculator;
import tcc.pairings.io.CplexOutputerWithDH;
import tcc.pairings.io.MemoryOutputer;
import tcc.pairings.io.Outputer;
import tcc.pairings.optimizers.CplexOptimizer;
import tcc.pairings.solvers.BasicSolver;

public class SetCoverSolver extends BasicSolver {
	private CplexOutputerWithDH cplex;
	
	public SetCoverSolver(String timeTable) {
		super(timeTable);
	}
	
	public SetCoverSolver(String timeTable, CostCalculator calculator) {
		super(timeTable, calculator);
	}
	
	protected void setOutputers() {
		memory = new MemoryOutputer(); 
		cplex = new CplexOutputerWithDH(legs);
		cplex.addRows();
		outputers = new Outputer[] { memory, cplex }; 
	}

	@Override
	protected void setOptimizer() {
		cplex.addDHVariables();
		optimizer = new CplexOptimizer(cplex.getModel());
	}
}