package tcc.pairings.solvers.heuristics.genetic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class Population {
	private List<Individue> individues;
	private static Random random = new Random(0);

	public Population() {
		individues = new ArrayList<Individue>();
	}
	
	public int getSize() {
		return individues.size();
	}
	
	public void add(Individue individue) {
		individues.add(individue);
	}
	
	public Individue[] getParents() {
		Individue parent1 = getFittest();
		Individue parent2 = getFittest();
		while (parent1 == parent2)
			parent2 = getFittest();
		return new Individue[] { parent1, parent2 };
	}
	
	public void sort() {
		Collections.sort(individues, new Comparator<Individue>() {  
            public int compare(Individue i1, Individue i2) {  
                return i1.getFitness() < i2.getFitness() ? -1 : 1;  
            }  
        });  
	}
	
	private Individue getFittest() { 
		int n = individues.size();
		int tot = n * (n + 1) / 2; 
		double r = random.nextDouble();
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
		int i;
		for (i = 0; i < individues.size(); i++)
			if (individues.get(i).getFitness() > child.getFitness())
				break;
		individues.add(i, child);
	}
	
	private int getRandomAboveAverageIndex() {
		double average = getAverageFitness();
		int i;
		for (i = 0; i < individues.size(); i++) 
			if (individues.get(i).getFitness() >= average) 
				break;
		return i + random.nextInt(individues.size() - i);
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
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (Individue individue: individues)
			sb.append(individue).append('\n');
		sb.deleteCharAt(sb.length() - 1);
		return sb.toString();
	}

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
}
