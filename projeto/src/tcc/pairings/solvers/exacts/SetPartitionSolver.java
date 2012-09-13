package tcc.pairings.solvers.exacts;

import tcc.pairings.costs.CostCalculator;
import tcc.pairings.io.outputers.CplexOutputer;
import tcc.pairings.io.outputers.MemoryOutputer;
import tcc.pairings.io.outputers.Outputer;
import tcc.pairings.optimizers.CplexOptimizer;
import tcc.pairings.solvers.BasicSolver;

public class SetPartitionSolver extends BasicSolver {
	private CplexOutputer cplex;
	
	public SetPartitionSolver(String timeTable) {
		super(timeTable);
	}
	
	public SetPartitionSolver(String timeTable, CostCalculator calculator) {
		super(timeTable, calculator);
	}
	
	protected void setOutputers() {
		memory = new MemoryOutputer(); 
		cplex = new CplexOutputer(legs);
		cplex.addRows();
		outputers = new Outputer[] { memory, cplex }; 
	}

	@Override
	protected void setOptimizer() {
		optimizer = new CplexOptimizer(cplex.getModel());
	}
}