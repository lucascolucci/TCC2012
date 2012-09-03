package tcc.pairings.optimizers;

import ilog.concert.*;
import ilog.cplex.*;

import java.util.ArrayList;
import java.util.List;

import tcc.pairings.Pairing;

public class CplexOptimizer implements Optimizer {
	private IloCplex model;
	private IloLPMatrix matrix;
	private double solutionTime;

	public IloCplex getModel() {
		return model;
	}
	
	@Override
	public double getOptimizationTime() {
		return solutionTime;
	}
	
	public CplexOptimizer(IloCplex model) {
		this.model = model;
		matrix = (IloLPMatrix) model.LPMatrixIterator().next();
		solutionTime = -1;
	}
	
	public CplexOptimizer(String mpsFile) {
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
	public boolean optimize() {
		try {
			return tryToOptimize();
		} catch (IloException e) {
			System.err.println("Error: " + e.getMessage());
		}
		return false;
	}

	private boolean tryToOptimize() throws IloException {
		if (model != null) {
			long before = System.currentTimeMillis();
			boolean status = model.solve();
			long after = System.currentTimeMillis();
			solutionTime = after - before;
			return status;
		}
		return false;
	}
	
	@Override
	public List<Pairing> getOptimalPairings(List<Pairing> pairings) {
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
	public double getOptimalCost() {
		try {
			return tryToGetSolutionCost();
		} catch (IloException e) {
			System.err.println("Error: " + e.getMessage());
			return -1;
		}
	}

	private double tryToGetSolutionCost() throws IloException {
		if (model != null)
			return model.getObjValue();
		return -1;
	}
	
	@Override
	public int getOptimalSize() {
		try {
			return tryToGetOptimalSize();
		} catch (IloException e) {
			System.err.println("Error: " + e.getMessage());
			return -1;
		}
	}

	private int tryToGetOptimalSize() throws IloException {
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
