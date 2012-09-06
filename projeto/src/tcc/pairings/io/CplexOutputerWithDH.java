package tcc.pairings.io;

import ilog.concert.IloColumn;
import ilog.concert.IloException;

import java.util.List;

import tcc.pairings.Leg;
import tcc.pairings.Pairing;

public class CplexOutputerWithDH extends CplexOutputer {

	public CplexOutputerWithDH(List<Leg> legs) {
		super(legs);
	}
	
	@Override
	protected void tryToOutput(Pairing pairing) throws IloException {
		IloColumn col = model.column(obj, pairing.getCost());
		for (int i = 0; i < legs.size(); i++){
			col = col.and(model.column(range[i], 1));
			matrix.addColumn(model.boolVar(col, "X" + pairing.getNumber()));
		}
	}
}
