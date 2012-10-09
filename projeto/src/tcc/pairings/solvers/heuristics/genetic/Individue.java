package tcc.pairings.solvers.heuristics.genetic;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import tcc.pairings.Leg;
import tcc.pairings.Pairing;

public class Individue {
	private int size;
	private List<Leg> toCoverLegs;
	private List<Pairing> pairings;
	private List<Pairing> chromosome;
	private double fitness;
	private static long number = 0;
	private static Random random = new Random(0);
	
	public List<Pairing> getChromosome() {
		return chromosome;
	}

	public void setChromosome(List<Pairing> chromosome) {
		this.chromosome = chromosome;
	}
	
	public double getFitness() {
		return fitness;
	}
		
	public Individue(List<Leg> toCoverLegs, List<Pairing> pairings) {
		this.toCoverLegs = toCoverLegs;
		this.pairings = pairings;
		size = pairings.size();
		chromosome = new ArrayList<Pairing>();
		number++;
	}
	
	public void generateChromosome() {
		HashMap<Leg, Integer> count = new HashMap<Leg, Integer>();
		for (Leg leg: toCoverLegs)
			count.put(leg, 0);
		
		for (Leg leg: toCoverLegs) {
			int size = Math.min(5, GeneticSolver.getHash().get(leg).size());
			int randomIndex = random.nextInt(size);
			Pairing selected = GeneticSolver.getHash().get(leg).get(randomIndex);
			chromosome.add(selected);
			for (Leg selectedLeg: selected.getLegs())
				for (Leg countLeg: count.keySet())
					if (countLeg.isDuplicate(selectedLeg)) {
						int value = count.get(countLeg);
						count.put(countLeg, value + 1);
						break;
					}
		}
		
		List<Pairing> T = new ArrayList<Pairing>(chromosome);
		while (!T.isEmpty()) {
			int randomIndex = random.nextInt(T.size());
			Pairing selected = T.get(randomIndex);
			T.remove(selected);
			boolean isDuplicated = true;
			for (Leg selectedLeg: selected.getLegs())
				for (Leg countLeg: count.keySet())
					if (countLeg.isDuplicate(selectedLeg))
						if (count.get(countLeg) < 2) {
							isDuplicated = false;
							break;
						}
			if (isDuplicated) {
				chromosome.remove(selected);
				for (Leg selectedLeg: selected.getLegs())
					for (Leg countLeg: count.keySet())
						if (countLeg.isDuplicate(selectedLeg)) {
							int value = count.get(countLeg);
							count.put(countLeg, value - 1);
						}
			}
		}	
	}
		
	public void turnFeasible() {
		HashMap<Leg, Integer> count = new HashMap<Leg, Integer>();
		for (Leg leg: toCoverLegs) {
			int value = 0;
			for (Pairing pairing: chromosome)
				if (pairing.contains(leg))
					value++;
			count.put(leg, value);
		}
		
		List<Leg> uncoveredLegs = new ArrayList<Leg>();
		for (Leg leg: count.keySet()) 
			if (count.get(leg) == 0)
				uncoveredLegs.add(leg);
		
		while (!uncoveredLegs.isEmpty()) {
			Leg leg = uncoveredLegs.get(0);
			Pairing selected = null;
			double min = Double.MAX_VALUE;
			for (Pairing pairing: GeneticSolver.getHash().get(leg)) {
				int numberOfCoveredLegs = getNumberOfCoveredLegs(pairing, uncoveredLegs);
				double ratio = pairing.getCost() / numberOfCoveredLegs;
				if (ratio < min) {
					selected = pairing;
					min = ratio;
				}
			}
			
			chromosome.add(selected);
			for (Leg selectedLeg: selected.getLegs())
				for (Leg countLeg: count.keySet())
					if (countLeg.isDuplicate(selectedLeg)) {
						int value = count.get(countLeg);
						count.put(countLeg, value + 1);
						break;
					}
			updateUncoveredLegs(selected, uncoveredLegs);
			
			List<Pairing> cloned = new ArrayList<Pairing>(chromosome);
			for (int i = cloned.size() - 1; i >= 0; i--) {
				boolean isDuplicated = true;
				for (Leg clonedLeg: cloned.get(i).getLegs())
					for (Leg countLeg: count.keySet())
						if (countLeg.isDuplicate(clonedLeg))
							if (count.get(countLeg) < 2) {
								isDuplicated = false;
								break;
							}
				if (isDuplicated) {
					chromosome.remove(cloned.get(i));
					for (Leg clonedLeg: cloned.get(i).getLegs())
						for (Leg countLeg: count.keySet())
							if (countLeg.isDuplicate(clonedLeg)) {
								int value = count.get(countLeg);
								count.put(countLeg, value - 1);
							}
				}	
			}
		}
	}
	
	private int getNumberOfCoveredLegs(Pairing pairing, List<Leg> uncoveredLegs) {
		int count = 0;
		for (Leg leg: uncoveredLegs)
			if (pairing.contains(leg))
				count++;
		return count;
	}
	
	private void updateUncoveredLegs(Pairing pairing, List<Leg> uncoveredLegs) {
		List<Leg> cloneLegs = new ArrayList<Leg>(uncoveredLegs);
		for (Leg leg: cloneLegs)
			if (pairing.contains(leg))
				uncoveredLegs.remove(leg);
	}
				
	public Individue doCrossover(Individue other) {
		Individue individue = new Individue(toCoverLegs, pairings);
		individue.setChromosome(getCrossoverChromosome(other));
		return individue;
	}

	private List<Pairing> getCrossoverChromosome(Individue other) {
		List<Pairing> crossover = new ArrayList<Pairing>(); 
		for (Pairing pairing: chromosome)
			if (other.getChromosome().contains(pairing))
				crossover.add(pairing);
			else
				if (random.nextBoolean())
					crossover.add(pairing);
		for (Pairing pairing: other.getChromosome())
			if (!chromosome.contains(pairing))
				if (random.nextBoolean())
					crossover.add(pairing);
		return crossover;
	}
	
	public void doMutation(Individue theFittest) {
		double prob = theFittest.getOnesDensity();
		for (int i = 0; i < GeneticSolver.getMutationSize(); i++)
			mutatePairing(pairings.get(random.nextInt(size)), prob);
	}

	private void mutatePairing(Pairing pairing, double prob) {
		double r = random.nextDouble();
		if (r < prob) {
			if (!chromosome.contains(pairing))
				chromosome.add(pairing);
		} else
			if (chromosome.contains(pairing))
				chromosome.remove(pairing);
	}
	
	public double getOnesDensity() {
		return (double) chromosome.size() / size;
	}
	
	public void calculateFitness() {
		fitness = 0.0;
		for (Pairing pairing: chromosome)
			fitness += pairing.getCost(); 
		fitness += GeneticSolver.getDeadheadingPenalty() * getNumberOfDeadheadedFlights(); 
	}
	
	private int getNumberOfDeadheadedFlights() {
		int total = 0;
		for (Leg leg: toCoverLegs)
			for (Pairing pairing: chromosome)
				if (pairing.contains(leg))
					total++;			
		return total - toCoverLegs.size();
	}
		
	@Override
	public String toString() {
		DecimalFormat df = new DecimalFormat("#.###");
		StringBuilder sb = new StringBuilder();
		sb.append("Indiv’duo ").append(number).append('\n');
		sb.append("- Colunas = ").append(chromosome.size()).append('\n');
		sb.append("- Fitness = ").append(df.format(getFitness()));
		return sb.toString(); 
	}
}
