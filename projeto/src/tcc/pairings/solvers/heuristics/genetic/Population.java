package tcc.pairings.solvers.heuristics.genetic;

import java.util.ArrayList;
import java.util.List;

public class Population {
	private List<Individue> individues;

	public Population() {
		individues = new ArrayList<Individue>();
	}
	
	public int getSize() {
		return individues.size();
	}
	
	public void add(Individue individue) {
		individues.add(individue);
	}
	
	public void remove(Individue individue) {
		individues.remove(individue);
	}
}
