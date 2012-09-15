package tcc.pairings.solvers.heuristics;

import java.util.List;

import tcc.pairings.Base;
import tcc.pairings.Leg;
import tcc.pairings.solvers.InitialSolver;
import tcc.pairings.solvers.Solution;
import tcc.pairings.solvers.Solver;

public class LocalSearchSolver implements Solver {
	private InitialSolver initialSolver;
	private Solution currentSolution;
	
	@Override
	public List<Leg> getLegs() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public int getNumberOfPairings() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	public LocalSearchSolver(String timeTable) {
		initialSolver = new InitialSolver(timeTable);
	}

	@Override
	public Solution getSolution(Base... bases) {
		// TODO Auto-generated method stub
		return null;
	}
}
