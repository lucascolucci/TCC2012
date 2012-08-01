package tcc.pairings.solvers;

import ilog.concert.*;
import ilog.cplex.*;

import java.util.List;

import tcc.pairings.Pairing;

public class CplexSolver implements Solvable {
	private IloCplex model;
	
	public CplexSolver(IloCplex model) {
		this.model = model;
	}
	
	@Override
	public boolean solve() {
		if (model != null)
			try {
				return model.solve();
			} catch (IloException e) {
				System.err.println(e.getMessage());
			}
		return false;
	}
	
	@Override
	public List<Pairing> getSolution(String pairingsFile) {
		// TODO
		return null;
	}
	
	@Override
	public List<Pairing> getSolution(List<Pairing> pairings) {
		// TODO
		return null;
	}
	
	@Override
	public int getSolutionCost() {
		// TODO
		return 0;
	}
}
