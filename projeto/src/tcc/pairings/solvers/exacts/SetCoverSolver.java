package tcc.pairings.solvers.exacts;

import java.util.List;

import tcc.pairings.Leg;
import tcc.pairings.costs.CostCalculator;
import tcc.pairings.io.outputers.DHCplexOutputer;
import tcc.pairings.io.outputers.MemoryOutputer;
import tcc.pairings.io.outputers.Outputer;
import tcc.pairings.io.outputers.TerminalOutputer;
import tcc.pairings.optimizers.CplexOptimizer;
import tcc.pairings.solvers.BasicSolver;
import tcc.pairings.solvers.Solution;

public class SetCoverSolver extends BasicSolver {
	private DHCplexOutputer cplex;
	
	public SetCoverSolver(List<Leg> legs) {
		super(legs);
	}
	
	public SetCoverSolver(List<Leg> legs, CostCalculator calculator) {
		super(legs, calculator);
	}
	
	public SetCoverSolver(String timeTable) {
		super(timeTable);
	}
	
	public SetCoverSolver(String timeTable, CostCalculator calculator) {
		super(timeTable, calculator);
	}
	
	@Override
	protected void setOutputers() {
		memory = new MemoryOutputer(); 
		cplex = new DHCplexOutputer(getLegs(), calculator);
		cplex.addRows();
		outputers = new Outputer[] { memory, cplex, new TerminalOutputer() }; 
	}

	@Override
	protected void setOptimizer() {
		cplex.addDHVariables();
		//cplex.getModel().setOut(null);
		optimizer = new CplexOptimizer(cplex.getModel());	
	}
	
	@Override
	protected Solution getOptimalSolution() {
		Solution solution = super.getOptimalSolution();
		if (solution != null) {
			setDeadHeads(solution);
			setCostsWithDeadHeads(solution.getPairings());
		}
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