package tcc.pairings.solvers.exacts;

import java.util.List;

import tcc.pairings.Leg;
import tcc.pairings.costs.CostCalculator;
import tcc.pairings.io.CplexOutputer;
import tcc.pairings.io.MemoryOutputer;
import tcc.pairings.solvers.BasicSolver;

public class SetCoverSolver extends BasicSolver {
	private List<Leg> legs;
	private CplexOutputer cplex;
	
	public SetCoverSolver(String timeTable) {
		super(timeTable);
	}
	
	public SetCoverSolver(String timeTable, CostCalculator calculator) {
		super(timeTable, calculator);
	}
	
	protected void setOutputers() {
		memory = new MemoryOutputer(); 
		cplex = new CplexOutputer(legs);
		cplex.addRows();
	}


	@Override
	protected void setOptimizer() {
		// TODO Auto-generated method stub
		
	}
}