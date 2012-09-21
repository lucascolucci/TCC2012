package tcc.pairings.io.outputers;

import ilog.concert.IloColumn;
import ilog.concert.IloException;

import java.util.List;

import tcc.pairings.Leg;
import tcc.pairings.costs.CostCalculator;

public class DHCplexOutputer extends CplexOutputer {
	private CostCalculator calculator;
	
	public DHCplexOutputer(List<Leg> legs, CostCalculator calculator) {
		super(legs);
		this.calculator = calculator;
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
			IloColumn col = getColWithObjSet(i);
			col = col.and(model.column(range[i], -1));
			matrix.addColumn(model.intVar(col, 0, Integer.MAX_VALUE, "Y" + (i + 1)));
		}
	}

	private IloColumn getColWithObjSet(int index) throws IloException {
		if (calculator != null)
			return model.column(obj, calculator.getDeadHeadingCost(legs.get(index)));
		return model.column(obj, 1.0);
	}
}
