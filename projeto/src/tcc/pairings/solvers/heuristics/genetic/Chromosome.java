package tcc.pairings.solvers.heuristics.genetic;

import tcc.pairings.Pairing;
import java.util.ArrayList;
import java.util.Collection;

public class Chromosome extends ArrayList<Pairing> implements Cloneable {
	private static final long serialVersionUID = 1L;
	
	public Chromosome() {
		super();
	}
	
	public Chromosome(Collection<? extends Pairing> collection) {
		super(collection);
	}
	
	@Override
	public boolean addAll(Collection<? extends Pairing> collection) {
		for (Pairing pairing: collection)
			this.add(pairing);
		return true;
	}
	
	@Override
	public boolean add(Pairing pairing) {
		int i;
		for (i = 0; i < size(); i++)
			if (get(i).getCost() > pairing.getCost())
				break;
		add(i, pairing);
		return true;
	}
	
	@Override
	public Chromosome clone() {
		Chromosome clone = new Chromosome(this);
		return clone;
	}
}
