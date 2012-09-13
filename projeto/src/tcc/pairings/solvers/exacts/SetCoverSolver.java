package tcc.pairings.solvers.exacts;

import java.util.List;

import tcc.pairings.costs.CostCalculator;
import tcc.pairings.io.DHCplexOutputer;
import tcc.pairings.io.MemoryOutputer;
import tcc.pairings.io.Outputer;
import tcc.pairings.optimizers.CplexOptimizer;
import tcc.pairings.solvers.BasicSolver;
import tcc.pairings.solvers.Solution;

public class SetCoverSolver extends BasicSolver {
	private DHCplexOutputer cplex;
	
	public SetCoverSolver(String timeTable) {
		super(timeTable);
	}
	
	public SetCoverSolver(String timeTable, CostCalculator calculator) {
		super(timeTable, calculator);
	}
	
	protected void setOutputers() {
		memory = new MemoryOutputer(); 
		cplex = new DHCplexOutputer(legs);
		cplex.addRows();
		outputers = new Outputer[] { memory, cplex }; 
	}

	@Override
	protected void setOptimizer() {
		cplex.addDHVariables();
		optimizer = new CplexOptimizer(cplex.getModel());	
	}
	
	@Override
	protected Solution getOptimalSolution() {
		Solution solution = super.getOptimalSolution();
		if (solution != null) 
			setDeadHeads(solution);
		return solution;
	}

	private void setDeadHeads(Solution solution) {
		List<Integer> artificials = optimizer.getArtificialValues();
		for (int i = 0; i < legs.size(); i++) {
			int numberOfDeadHeads = artificials.get(i);
			if (numberOfDeadHeads > 0)
				solution.setDeadHeads(legs.get(i), numberOfDeadHeads);
		}
	}
}