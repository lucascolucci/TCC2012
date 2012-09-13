package tcc.pairings.io.outputers;

import ilog.concert.IloColumn;
import ilog.concert.IloException;

import java.util.List;

import tcc.pairings.Leg;
import tcc.pairings.rules.Rules;

public class DHCplexOutputer extends CplexOutputer {
	public DHCplexOutputer(List<Leg> legs) {
		super(legs);
	}
	
	public void addDHVariables() {
		try {
			tryToAddDHVariables();
		} catch (IloException e) {
			System.err.println("Error: " + e.getMessage());
		}
	}

	private void tryToAddDHVariables() throws IloException {
		for (int i = 0; i < legs.size(); i++) {
			IloColumn col = model.column(obj, Rules.getDeadHeadCost(legs.get(i)));
			col = col.and(model.column(range[i], -1));
			matrix.addColumn(model.intVar(col, 0, Integer.MAX_VALUE, "Y" + (i + 1)));
		}
	}
}
