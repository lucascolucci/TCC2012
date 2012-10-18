package tcc.pairings.solvers.heuristics;

import java.util.ArrayList;
import java.util.List;

public class History {
	private List<Subproblem> subproblems;
	
	public History() {
		subproblems = new ArrayList<Subproblem>();
	}
	
	public void add(Subproblem subproblem) {
		subproblems.add(subproblem);
	}
	
	public boolean contains(Subproblem other) {
		return subproblems.contains(other);
	}
	
	public int size() {
		return subproblems.size();
	}
	
	public void clear() {
		subproblems.clear();
	}
}
