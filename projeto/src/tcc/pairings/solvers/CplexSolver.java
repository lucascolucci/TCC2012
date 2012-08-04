package tcc.pairings.solvers;

import ilog.concert.*;
import ilog.cplex.*;

import java.util.ArrayList;
import java.util.List;

import tcc.pairings.Pairing;

public class CplexSolver implements Solvable {
	private IloCplex model;
	private IloLPMatrix matrix;
	private double solutionTime;

	public IloCplex getModel() {
		return model;
	}
	
	@Override
	public double getSolutionTime() {
		return solutionTime;
	}
	
	public CplexSolver(IloCplex model) {
		this.model = model;
		matrix = (IloLPMatrix) model.LPMatrixIterator().next();
		solutionTime = -1;
	}
	
	public CplexSolver(String mpsFile) {
		setUpModelFromFile(mpsFile);
		matrix = (IloLPMatrix) model.LPMatrixIterator().next();
		solutionTime = -1;
	}

	private void setUpModelFromFile(String mpsFile) {
		try {
			tryToSetUpModelFromFile(mpsFile);	
		} catch (IloException e) {
			System.err.println("Error: " + e.getMessage());
		}
	}

	private void tryToSetUpModelFromFile(String mpsFile) throws IloException {
		model = new IloCplex();
		model.importModel(mpsFile);
	}
	
	@Override
	public boolean solve() {
		try {
			return tryToSolve();
		} catch (IloException e) {
			System.err.println("Error: " + e.getMessage());
		}
		return false;
	}

	private boolean tryToSolve() throws IloException {
		if (model != null)
			// TODO setSolutionTime()
			return model.solve();
		return false;
	}
	
	@Override
	public List<Pairing> getSolution(String pairingsFile) {
		// TODO
		return null;
	}
	
	@Override
	public List<Pairing> getSolution(List<Pairing> pairings) {
		try {
			return tryToGetSolution(pairings);
		} catch (IloException e) {
			System.err.println("Error: " + e.getMessage());
			return null;
		}        
	}

	private List<Pairing> tryToGetSolution(List<Pairing> pairings) throws IloException {
		List<Pairing> solution = new ArrayList<Pairing>(); int ncols = matrix.getNcols();
		for (int i = 0; i < ncols; i++)
			if ((int) model.getValue(matrix.getNumVar(i)) == 1)
				solution.add(pairings.get(i));
		return solution;
	}
	
	@Override
	public int getSolutionCost() {
		try {
			return tryToGetSolutionCost();
		} catch (IloException e) {
			System.err.println("Error: " + e.getMessage());
			return -1;
		}
	}

	private int tryToGetSolutionCost() throws IloException {
		if (model != null)
			return (int) model.getObjValue();
		return -1;
	}
	
	@Override
	public int getSolutionSize() {
		try {
			return tryToGetSolutionSize();
		} catch (IloException e) {
			System.err.println("Error: " + e.getMessage());
			return -1;
		}
	}

	private int tryToGetSolutionSize() throws IloException {
		int count = 0; int ncols = matrix.getNcols();
		for (int i = 0; i < ncols; i++)
			if (model.getValue(matrix.getNumVar(i)) == 1)
				count++;
		return count;
	}
	
	public void endModel() {
		model.end();
	}
}
