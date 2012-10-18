package tcc.pairings.solvers.heuristics.genetic;

import java.text.DecimalFormat;
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
		int i;
		for (i = 0; i < individues.size(); i++)
			if (individues.get(i).getFitness() > individue.getFitness())
				break;
		individues.add(i, individue);
	}
	
	public Individue[] getParents() {
		Individue parent1 = getFittest();
		Individue parent2 = getFittest();
		while (parent1 == parent2)
			parent2 = getFittest();
		return new Individue[] { parent1, parent2 };
	}
	
	private Individue getFittest() { 
		int n = individues.size();
		int tot = n * (n + 1) / 2; 
		double r = GeneticSolver.random.nextDouble();
		double pk = 0.0;
		int k;
		for (k = 1; k <= n; k++) {
			pk += (double) (n - k + 1) / tot;
			if (r < pk)
				break;
		}
		return individues.get(k - 1);
	}
	
	public Individue getTheFittest() {
		return individues.get(0);
	}
	
	public void replace(Individue child) {
		individues.remove(getRandomAboveAverageIndex());
		add(child);
	}
	
	private int getRandomAboveAverageIndex() {
		double average = getAverageFitness();
		int size = individues.size();
		int i;
		for (i = 0; i < size; i++) 
			if (individues.get(i).getFitness() >= average) 
				break;
		if (i >= size)
			return size - 1;
		return i + GeneticSolver.random.nextInt(size - i);
	}

//	private Individue getWeakest() { 
//		int n = individues.size();
//		int tot = n * (n + 1) / 2; 
//		double r = random.nextDouble();
//		double pk = 0.0;
//		int k;
//		for (k = 1; k <= n; k++) {
//			pk += (double) k / tot;
//			if (r < pk)
//				break;
//		}
//		return individues.get(k - 1);
//	}
	
	public boolean contains(Individue individue) {
		for (Individue populationIndividue: individues)
			if (populationIndividue.isDuplicate(individue))
				return true;
		return false;
	}
	
	public double getAverageFitness() {
		double total = 0.0;
		for (Individue individue: individues)
			total += individue.getFitness();
		return total / individues.size();
	}
	
	@Override
	public String toString() {
		DecimalFormat df = new DecimalFormat("#.###");
		StringBuilder sb = new StringBuilder("Popula‹o:\n");
		for (Individue individue: individues)
			sb.append(individue).append('\n');
		sb.append("Fitness mŽdio = ").append(df.format(getAverageFitness()));
		return sb.toString();
	}
}
