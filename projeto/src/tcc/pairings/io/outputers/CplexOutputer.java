package tcc.pairings.io.outputers;

import ilog.concert.IloColumn;
import ilog.concert.IloException;
import ilog.concert.IloLPMatrix;
import ilog.concert.IloObjective;
import ilog.concert.IloRange;
import ilog.cplex.IloCplex;

import java.util.List;

import tcc.pairings.Leg;
import tcc.pairings.Pairing;

public class CplexOutputer extends Outputer {
	protected List<Leg> legs;
	protected IloCplex model;
	protected IloLPMatrix matrix;
	protected IloObjective obj;
	protected IloRange[] range;
	
	public IloCplex getModel() {
		return model;
	}
	
	public CplexOutputer(List<Leg> legs) {
		super();
		this.legs = legs;
		setUp();
	}

	private void setUp() {
		try {
			tryToSetUp();
		} catch (IloException e) {
			System.err.println("Error: " + e.getMessage());
			model = null;
		}
	}

	private void tryToSetUp() throws IloException {
		model = new IloCplex();
		model.setName("CPP");
		matrix = model.addLPMatrix();
		obj = model.addMinimize();
		range = new IloRange[legs.size()];
	}
	
	public void addRows() {
		try {
			tryToAddRows();
		} catch (IloException e) {
			model = null;
			System.err.println("Error: " + e.getMessage());
		}
	}

	private void tryToAddRows() throws IloException {
		for (int i = 0; i < legs.size(); i++) {
			range[i] = model.addRange(1, 1, "F" + legs.get(i).getNumber());
			matrix.addRow(range[i]);
		}
	}
		
	@Override
	public void output(Pairing pairing) {
		try {
			tryToOutput(pairing);
		} catch (IloException e) {
			System.err.println("Error: " + e.getMessage());
		}
	}

	protected void tryToOutput(Pairing pairing) throws IloException {
		IloColumn col = model.column(obj, pairing.getCost());
		for (int i = 0; i < legs.size(); i++)
			if (pairing.contains(legs.get(i)))
				col = col.and(model.column(range[i], 1));
		matrix.addColumn(model.boolVar(col, "X" + pairing.getNumber()));
	}
	
	public void exportModel(String file) {
		try {
			model.exportModel(file);
		} catch (IloException e) {
			System.err.println("Error: " + e.getMessage());
		}
	}
}
