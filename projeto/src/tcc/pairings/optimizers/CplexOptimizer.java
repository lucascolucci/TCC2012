package tcc.pairings.optimizers;

import ilog.concert.*;
import ilog.cplex.*;

import java.util.ArrayList;
import java.util.List;

public class CplexOptimizer implements Optimizer {
	private IloCplex model;
	private IloLPMatrix matrix;
	private double optimizationTime;
	
	public IloCplex getModel() {
		return model;
	}
	
	@Override
	public double getOptimizationTime() {
		return optimizationTime;
	}
		
	public CplexOptimizer(IloCplex model) {
		this.model = model;
		matrix = (IloLPMatrix) model.LPMatrixIterator().next();
		optimizationTime = -1;
	}
	
	public CplexOptimizer(String mpsFile) {
		setUpModelFromFile(mpsFile);
		matrix = (IloLPMatrix) model.LPMatrixIterator().next();
		optimizationTime = -1;
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
			optimizationTime = after - before;
			return status;
		}
		return false;
	}
	
	@Override
	public List<Integer> getOptimalVariables() {
		try {
			return tryToGetVariables();
		} catch (IloException e) {
			System.err.println("Error: " + e.getMessage());
			return null;
		}        
	}

	private List<Integer> tryToGetVariables() throws IloException {
		List<Integer> list = new ArrayList<Integer>(); 
		int last =  model.getNbinVars();
		for (int i = 0; i < last; i++)
			if (Math.round(model.getValue(matrix.getNumVar(i))) == 1)
				list.add(i + 1);
		return list;
	}
	
	@Override
	public List<Integer> getArtificialValues() {
		try {
			return tryToGetArtificialValues();
		} catch (IloException e) {
			System.err.println("Error: " + e.getMessage());
			return null;
		}
	}

	private List<Integer> tryToGetArtificialValues() throws IloException {
		List<Integer> list = new ArrayList<Integer>();
		int first = model.getNbinVars(); int last = model.getNcols();
		for (int i = first; i < last; i++)			
			list.add((int) Math.round(model.getValue(matrix.getNumVar(i))));
		return list;
	}
		
	@Override
	public double getObjectiveValue() {
		try {
			return tryToGetObjectiveValue();
		} catch (IloException e) {
			System.err.println("Error: " + e.getMessage());
			return -1;
		}
	}

	private double tryToGetObjectiveValue() throws IloException {
		if (model != null)
			return model.getObjValue();
		return -1;
	}
	
	public void endModel() {
		model.end();
	}
}
