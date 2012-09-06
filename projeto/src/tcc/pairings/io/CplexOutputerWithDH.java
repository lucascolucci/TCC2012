package tcc.pairings.io;

import ilog.concert.IloColumn;
import ilog.concert.IloException;

import java.util.List;

import tcc.pairings.Leg;
import tcc.pairings.Rules;

public class CplexOutputerWithDH extends CplexOutputer {
	public CplexOutputerWithDH(List<Leg> legs) {
		super(legs);
	}
	
	public void addDHVariables() throws IloException {
		for (int i = 0; i < legs.size(); i++) {
			IloColumn col = model.column(obj, Rules.DH_PENALTY_FACTOR * legs.get(i).getFlightTime());
			col = col.and(model.column(range[i], 1));
			matrix.addColumn(model.intVar(col, 0, Integer.MAX_VALUE, "Y" + (i + 1)));
		}
	}
}
