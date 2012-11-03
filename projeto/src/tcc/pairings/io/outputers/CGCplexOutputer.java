package tcc.pairings.io.outputers;

import ilog.concert.IloColumn;
import ilog.concert.IloException;

import java.util.List;

import tcc.pairings.Leg;
import tcc.pairings.Pairing;
import tcc.pairings.costs.CostCalculator;

public class CGCplexOutputer extends DHCplexOutputer {
	private static int column = 0;
	
	public CGCplexOutputer(List<Leg> legs, CostCalculator calculator) {
		super(legs, calculator);
	}
	
	@Override
	protected void tryToOutput(Pairing pairing) throws IloException {
		IloColumn col = getColumn(pairing);
		matrix.addColumn(model.numVar(col, 0, 1, "X" + (++column)));
	}
	
	@Override
	protected void tryToAddDHVariables() throws IloException {
		for (int i = 0; i < legs.size(); i++) { 
			IloColumn col = getColWithObjSet(i);
			col = col.and(model.column(range[i], -1));
			matrix.addColumn(model.numVar(col, 0, Double.MAX_VALUE, "Y" + (i + 1)));
		}
	}
}
