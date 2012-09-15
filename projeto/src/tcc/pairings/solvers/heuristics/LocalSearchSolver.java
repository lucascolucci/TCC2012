package tcc.pairings.solvers.heuristics;

import java.util.List;

import tcc.pairings.Base;
import tcc.pairings.Leg;
import tcc.pairings.Pairing;
import tcc.pairings.costs.CostCalculator;
import tcc.pairings.solvers.InitialSolver;
import tcc.pairings.solvers.Solution;
import tcc.pairings.solvers.Solver;

public class LocalSearchSolver implements Solver {
	private CostCalculator calculator;
	private InitialSolver initialSolver;
	private Solution currentSolution;
	
	public CostCalculator getCalculator() {
		return calculator;
	}

	public void setCalculator(CostCalculator calculator) {
		this.calculator = calculator;
	}

	@Override
	public List<Leg> getLegs() {
		return initialSolver.getLegs();
	}
	
	@Override
	public int getNumberOfPairings() {
		return initialSolver.getNumberOfPairings();
	}
	
	public LocalSearchSolver(String timeTable) {
		this(timeTable, null);
	}
	
	public LocalSearchSolver(String timeTable, CostCalculator calculator) {
		initialSolver = new InitialSolver(timeTable);
		this.calculator = calculator;
	}


	@Override
	public Solution getSolution(Base... bases) {
		currentSolution = initialSolver.getSolution(bases);
		
		List<Pairing> pairings = currentSolution.getPairings();
		if (calculator != null)
			for (Pairing pairing: pairings) 
				calculator.setCost(pairing);
		
		return currentSolution;
	}
}
