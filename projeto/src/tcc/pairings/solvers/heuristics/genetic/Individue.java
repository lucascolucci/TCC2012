package tcc.pairings.solvers.heuristics.genetic;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import com.sun.tools.javac.util.Pair;

import tcc.pairings.DutyLeg;
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
		List<Pairing> possiblePairings = new ArrayList<Pairing>(pairings);
		while (!possiblePairings.isEmpty()) {
			int size = possiblePairings.size();
			Pairing selected = possiblePairings.get(random.nextInt(size));
			chromosome.add(selected);
			trimPossiblePairings(possiblePairings, selected.getLegs());
		}
	}
	
	private void trimPossiblePairings(List<Pairing> possiblePairings, List<DutyLeg> legs) {
		List<Pairing> toBeRemoved = new ArrayList<Pairing>();
		for (Pairing pairing: possiblePairings)
			if (pairing.containsAny(legs))
				toBeRemoved.add(pairing);
		possiblePairings.removeAll(toBeRemoved);
	}

	public void turnFeasible() {
		List<Leg> uncoveredLegs = getUncoveredLegs();
		for (Leg uncoveredLeg: uncoveredLegs) {
			List<Pairing> pairings = GeneticSolver.getHash().get(uncoveredLeg);
			Pairing selected = selectPairingToCoverLeg(pairings, uncoveredLegs);
			chromosome.add(selected);
		}
	}
	
	public List<Leg> getUncoveredLegs() {
		List<Leg> uncoveredLegs = new ArrayList<Leg>();
		for (Leg leg: toCoverLegs) {
			boolean isCovered = false;
			for (Pairing pairing: chromosome)
				if (pairing.contains(leg)) {
					isCovered = true;
					break;
				}
			if (!isCovered)
				uncoveredLegs.add(leg);
		}
		return uncoveredLegs;
	}
	
	private Pairing selectPairingToCoverLeg(List<Pairing> pairings, List<Leg> uncoveredLegs) {
		List<Pair<Pairing, Integer>> list = getPairsList(pairings);
		sortPairs(list); 	
		cutoffPairs(list);                    
		return getPairingThatCoversMostLegs(list, uncoveredLegs);		
	}

	private List<Pair<Pairing, Integer>> getPairsList(List<Pairing> pairings) {
		List<Pair<Pairing, Integer>> list = new ArrayList<Pair<Pairing, Integer>>();
		for (Pairing pairing: pairings)
			list.add(new Pair<Pairing, Integer>(pairing, getNumberOfRepeatedLegs(pairing)));
		return list;
	}
		
	private int getNumberOfRepeatedLegs(Pairing pairing) {
		int total = 0;
		for (Leg leg: pairing.getLegs())
			for (Pairing chromosomePairing: chromosome)
				if (chromosomePairing.contains(leg))
					total++;
		return total;
	}
	
	private void sortPairs(List<Pair<Pairing, Integer>> list) {
		Collections.sort(list, new Comparator<Pair<Pairing, Integer>>() {  
            public int compare(Pair<Pairing, Integer> p1, Pair<Pairing, Integer> p2) {  
                return p1.snd < p2.snd ? -1 : 1;  
            }  
        });
	}
	
	private void cutoffPairs(List<Pair<Pairing, Integer>> list) {
		int cutoffSize = (int) Math.round(list.size() * GeneticSolver.CUTOFF_FACTOR);
		int size = Math.min(list.size() - 1, cutoffSize);
		for (int i = 0; i < size; i++)
			list.remove(list.size() - 1);
	}
	
	private Pairing getPairingThatCoversMostLegs(List<Pair<Pairing, Integer>> list, List<Leg> uncoveredLegs) {
		Pair<Pairing, Integer> maxPair = new Pair<Pairing, Integer>(null, 0);
		for (Pair<Pairing, Integer> pair: list) {
			int count = 0;
			for (Leg leg: uncoveredLegs)
				if (pair.fst.contains(leg))
					count++;
			if (count > maxPair.snd)
				maxPair = new Pair<Pairing, Integer>(pair.fst, count);
		}
		return maxPair.fst;
	}
		
	public void calculateFitness() {
		fitness = 0.0;
		for (Pairing pairing: chromosome)
			fitness += pairing.getCost(); 
		fitness += GeneticSolver.DEADHEADING_PENALTY * getNumberOfDeadheadedFlights(); 
	}
	
	private int getNumberOfDeadheadedFlights() {
		int total = 0;
		for (Leg leg: toCoverLegs)
			for (Pairing pairing: chromosome)
				if (pairing.contains(leg))
					total++;
		return total - toCoverLegs.size();
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
		for (int i = 0; i < GeneticSolver.MUTATION_SIZE; i++)
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
	
	@Override
	public String toString() {
		DecimalFormat df = new DecimalFormat("#.###");
		StringBuilder sb = new StringBuilder();
		sb.append("Indiv’duo ").append(number).append('\n');
		sb.append("- Cromossomo = ").append(chromosome.size()).append('\n');
		sb.append("- Fitness = ").append(df.format(getFitness()));
		return sb.toString(); 
	}
}
